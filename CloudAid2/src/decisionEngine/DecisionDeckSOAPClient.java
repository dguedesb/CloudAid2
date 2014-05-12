package decisionEngine;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;
import org.w3c.dom.NodeList;

import com.google.common.io.Files;

public class DecisionDeckSOAPClient {
	
	
	
	private static final String url = "http://webservices.decision-deck.org/soap/";
	
		
	private static int timeout=0;
	private static String ticketId = null;
	private static String ticketNumber = null;
	private String dir=null;
	
	
	DecisionDeckSOAPClient(String directory)
	{
		dir = directory;
	}
	
    public  void sendProblem(ArrayList<XMCDA> files,ArrayList<String> alias, String ws, int method) throws Exception {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        URL endpoint = new URL(null,url+ws,
              new URLStreamHandler() { // Anonymous (inline) class
              @Override
              protected URLConnection openConnection(URL url) throws IOException {
              URL clone_url = new URL(url.toString());
              HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();
              // TimeOut settings
              clone_urlconnection.setConnectTimeout(timeout);
              clone_urlconnection.setReadTimeout(timeout);
              return(clone_urlconnection);
              }
          });
        
        SOAPMessage soapResponse=null;
        try {
        	 soapResponse = soapConnection.call(createSOAPRequest(files,alias,method), endpoint);
        }
        catch(Exception e) {
            if ((e instanceof com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl) && (e.getCause()!=null) && (e.getCause().getCause()!=null) && (e.getCause().getCause().getCause()!=null)) {
                System.err.println("[" + e + "] Error sending SOAP message. Initial error cause = " + e.getCause().getCause().getCause());
            }
            else if(e instanceof SocketTimeoutException) {
                System.out.println("I'm sorry but the data is taking too long to process. Check your email later for the results!");
                //do something about the request, get ticket to the database;
            }
        }
       
        //here
        parseSubmitResponse(soapResponse);

        soapConnection.close();
        
    }
    
	public SOAPMessage getProblemResponse(String ws) throws Exception
    {
    	
    	 // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        URL endpoint = new URL(null,url+ws,
              new URLStreamHandler() { // Anonymous (inline) class
              @Override
              protected URLConnection openConnection(URL url) throws IOException {
              URL clone_url = new URL(url.toString());
              HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();
              // TimeOut settings
              clone_urlconnection.setConnectTimeout(timeout);
              clone_urlconnection.setReadTimeout(timeout);
              return(clone_urlconnection);
              }
          });
    	
    	 //check if the webservice is done
        SOAPMessage soapResponseb = null;
        if(ticketNumber != null && ticketId!=null)
        {
        	 soapResponseb = soapConnection.call(getProblemSolutionMessage(ticketId,ticketNumber), endpoint);
        }
        
        soapConnection.close();
        return soapResponseb;
    }

    private SOAPMessage createSOAPRequest(ArrayList<XMCDA> files,ArrayList<String> alias,int method) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        
        // SOAP Envelope, add namespaces
        SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
        soapEnv.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        soapEnv.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/" );
        soapEnv.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/" );
        soapEnv.addNamespaceDeclaration("ZSI", "http://www.zolera.com/schemas/ZSI/" );
        soapEnv.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema" );

        // SOAP Body
        SOAPBody soapBody = soapMessage.getSOAPBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("submitProblem");
        

        new File(dir).mkdirs();
        for(int i=0;i<files.size();i++)
        {
			SOAPElement t = null;
			XMCDAWriteUtils writer = new XMCDAWriteUtils();// write to disk
			XMCDADoc file = XMCDADoc.Factory.newInstance();

			File result = new File(dir+"/" + alias.get(i) +".xml");
			file.setXMCDA(files.get(i));

			try {
				writer.write(file, Files.newOutputStreamSupplier(result));
			} catch (IOException e) {
				System.out.println("Unable to create file!!!");
				e.printStackTrace();
			}
	
			t = soapBodyElem.addChildElement(alias.get(i));
			t.setAttribute("xsi:type", "xsd:string");
			t.addTextNode(file.toString());

        	soapBodyElem.addChildElement(t);
        }
        soapMessage.saveChanges();
//        System.out.println("\n********************************************:\n");
//        System.out.println("\nSubmit Problem:\n");
//        soapMessage.writeTo(System.out);
//        System.out.println("\n********************************************:\n");
        return soapMessage;
    }
    
    private SOAPMessage getProblemSolutionMessage(String id,String number) throws Exception {
    	

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
    
        // SOAP Envelope
        SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
        soapEnv.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        soapEnv.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/" );
        soapEnv.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/" );
        soapEnv.addNamespaceDeclaration("ZSI", "http://www.zolera.com/schemas/ZSI/" );
        soapEnv.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema" );

        // SOAP Body
        SOAPBody soapBody = soapMessage.getSOAPBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("requestSolution");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("ticket");
        QName attributeName = new QName("id");
   
       
        soapBodyElem1.addAttribute(attributeName, id);
        soapBodyElem1.addTextNode(number);

        soapMessage.saveChanges();
//        System.out.println("\n********************************************:\n");
//        System.out.println("\nRequest Solution Message:\n");
//        soapMessage.writeTo(System.out);
//        System.out.println("\n********************************************:\n");
        return soapMessage;
    }
    
    public void parseSubmitResponse(SOAPMessage response) throws SOAPException
    {
    	boolean success = false;
    	
		SOAPBody soapBody = response.getSOAPBody();
		NodeList returnList = soapBody.getElementsByTagName("submitProblemResponse");
		for (int k = 0; k < returnList.getLength(); k++) {
			NodeList innerResultList = returnList.item(k).getChildNodes();
			for (int l = 0; l < innerResultList.getLength(); l++) {
				if (innerResultList.item(l).getNodeName().equalsIgnoreCase("ticket")) {
					ticketId = innerResultList.item(l).getAttributes().getNamedItem("id").getTextContent();
					ticketNumber = innerResultList.item(l).getTextContent().trim();
					// System.out.println("Ticket from the Submit:\n"+ innerResultList.item(l).getAttributes().getNamedItem("id").getTextContent()+ ":" +innerResultList.item(l) .getTextContent().trim());
				}
				if (innerResultList.item(l).getNodeName().equalsIgnoreCase("message")) {
					System.out.println("Problem submission status:\n"+ innerResultList.item(l).getTextContent().trim());
					if (innerResultList.item(l).getTextContent().trim().contains("unsuccessful"))
						success = false;
					else if (innerResultList.item(l).getTextContent().trim().contains("successful"))
						success = true;
				}
			}
		}
    	if(!success)
    		ticketId=ticketNumber=null;
    }

	public static String getTicketId() {
		return ticketId;
	}

	public static void setTicketId(String ticketId) {
		DecisionDeckSOAPClient.ticketId = ticketId;
	}

	public static String getTicketNumber() {
		return ticketNumber;
	}

	public static void setTicketNumber(String ticketNumber) {
		DecisionDeckSOAPClient.ticketNumber = ticketNumber;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
    
}