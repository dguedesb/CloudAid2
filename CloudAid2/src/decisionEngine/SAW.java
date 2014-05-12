package decisionEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.soap.SOAPMessage;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Controller;

public class SAW {
	
	private static final String[] ws_saw = {"weightedSum-PyXMCDA.py"};
	
	public static XMCDA solve(ArrayList<XMCDA> files,String dir)
	{
		DecisionDeckSOAPClient client = new DecisionDeckSOAPClient(dir);
		// run every steps
		XMCDA alternativesValues = null;
		int controller = 0 ;
		String com = "[SAW-Step1] Request is taking a while. Waiting.";
		do{
			if(controller == 0)
			{
				alternativesValues = step1(files, client,false);
				controller++;
			}
			else
			{
				if(controller == 1)
				{
					System.out.print(com + ".");
					controller++;
				}
				else
					System.out.print(".");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				alternativesValues = step1(files, client,true);
			}
		}while(alternativesValues == null);
		
		System.out.println();
		controller=0;
		/////////////////////FINISHED//////////////////////////////

		if (alternativesValues != null) {
			System.out.println("---------------------------Final Result---------------------------------");

//			System.out.println(alternativesValues.toString());
			XMCDAConverter.export(alternativesValues, client.getDir()+ "/alternativesValues.xml");
		}
		return alternativesValues;
	}
	
	
	@SuppressWarnings("unchecked")
	public static XMCDA step1(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA alternativesValues = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria", "criteriaWeights","performanceTable"));
				client.sendProblem(files,alias,ws_saw[0],Controller.SAW);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_saw[0]);
			
			Iterator<Node> returnListbb = response.getSOAPBody().getChildElements();
             while(returnListbb.hasNext())
             {
            	 Node t = returnListbb.next();
            	 
            	 if(t.getLocalName().equalsIgnoreCase("requestSolutionResponse"))
            	 {
            		 NodeList nl = t.getChildNodes();
            		 
            		for(int k =0 ; k<nl.getLength();k++)
            		{
            			 Node tt = nl.item(k);
            			 if(tt.getLocalName().equalsIgnoreCase("alternativesValues"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 String content = tt.getTextContent().replace("http://www.decision-deck.org/2009/XMCDA-2.0.0", "http://www.decision-deck.org/2009/XMCDA-2.1.0");
            				 alternativesValues = XMCDADoc.Factory.parse(content).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[SAW-Step1]Messages:\n" + tt.getTextContent());
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("ticket"))
            			 {
//            				 System.out.println("[ELECTRE-Step1]Request took longer than expected:\nTicket->"+tt.getTextContent());
            				 //place on the MySQL DB, method, stage, ticket,directory
            			 }
            		}
            	 }
             }
			
			//do something with response
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alternativesValues;
	}
}
