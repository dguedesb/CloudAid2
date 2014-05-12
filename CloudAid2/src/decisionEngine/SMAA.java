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

public class SMAA {
	
	private static final String[] ws_smaa = {"smaa2-jsmaa.py"};
	

	public static XMCDA solve(ArrayList<XMCDA> files,String dir)
	{
		DecisionDeckSOAPClient client = new DecisionDeckSOAPClient(dir);
		// run every steps
		XMCDA rankAcceptabilities = null;
		int controller = 0 ;
		String com = "[SMAA-Step1] Request is taking a while. Waiting.";
		do{
			if(controller == 0)
			{
				rankAcceptabilities = step1(files, client,false);
				controller++;
			}
			else
			{
				if(controller == 1)
				{
					System.out.println(com + ".");
					controller++;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rankAcceptabilities = step1(files, client,true);
			}
		}while(rankAcceptabilities == null);
		
		System.out.println();
		controller=0;
		/////////////////////FINISHED//////////////////////////////

		if (rankAcceptabilities != null) {
			System.out.println("---------------------------Final Result---------------------------------");

//			System.out.println(rankAcceptabilities.toString());
			XMCDAConverter.export(rankAcceptabilities, client.getDir()+ "/rankAcceptabilities.xml");
		}
		
		return rankAcceptabilities;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step1(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA rankAcceptabilities = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria","performanceTable"));
				client.sendProblem(files,alias,ws_smaa[0],Controller.SMAA2);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_smaa[0]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("rankAcceptabilities"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 String content = tt.getTextContent().replace("http://www.decision-deck.org/2009/XMCDA-2.0.0", "http://www.decision-deck.org/2009/XMCDA-2.1.0");
            				 rankAcceptabilities = XMCDADoc.Factory.parse(content).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[SMAA-Step1]Messages:\n" + tt.getTextContent());
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
		return rankAcceptabilities;
	}
}
