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

public class PROMETHEE {
	
	private static final String[] ws_promethee = {"PrometheePreference-J-MCDA.py","PrometheeFlows-J-MCDA.py","Promethee1Ranking-RXMCDA.py"};
	
	public static XMCDA solve(ArrayList<XMCDA> files,String dir)
	{

		DecisionDeckSOAPClient client = new DecisionDeckSOAPClient(dir);
		// run every steps
		XMCDA preference = null;
		int controller = 0 ;
		String com = "[PROMETHEE-Step1] Request is taking a while. Waiting.";
		do{
			if(controller == 0)
			{
				preference = step1(files, client,false);
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
				preference = step1(files, client,true);
			}
		}while(preference == null);
		
		controller=0;
		/////////////////////////////////////////////////////////////////////////

		XMCDA positiveFlow = null;
		
		com = "[PROMETHEE-Step2] Request is taking a while. Waiting.";
		if (preference != null) {
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(preference);
			cont.add(XMCDAConverter.createMethodParameters("POSITIVE",0));
			
			do{
				if(controller == 0)
				{
					positiveFlow = step2(cont, client,false);
					controller++;
				}
				else
				{
					if(controller == 1)
					{
						System.out.print(com + ".");
						controller++;
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					positiveFlow = step2(cont, client,true);
				}
			}while(positiveFlow == null);
		}
		
		System.out.println();
		controller=0;
		///////////////////////////////////////////////////////////
		XMCDA negativeFlow = null;
		
		com = "[PROMETHEE-Step3] Request is taking a while. Waiting.";
		if (positiveFlow != null) {
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(preference);
			cont.add(XMCDAConverter.createMethodParameters("NEGATIVE",0));
			
			do{
				if(controller == 0)
				{
					negativeFlow = step2(cont, client,false);
					controller++;
				}
				else
				{
					if(controller == 1)
					{
						System.out.print(com + ".");
						controller++;
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					negativeFlow = step2(cont, client,true);
				}
			}while(negativeFlow == null);
		}
		System.out.println();
		controller=0;
		///////////////////////////////////////////////////////////
		
		XMCDA promethee1Ranking = null;
		
		com = "[PROMETHEE-Step4] Request is taking a while. Waiting.";
		if (positiveFlow != null && negativeFlow != null) {
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(positiveFlow);
			cont.add(negativeFlow);
			
			do{
				if(controller == 0)
				{
					promethee1Ranking = step3(cont, client,false);
					controller++;
				}
				else
				{
					if(controller == 1)
					{
						System.out.print(com + ".");
						controller++;
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					promethee1Ranking = step3(cont, client,true);
				}
			}while(promethee1Ranking == null);
		}
		controller=0;
		System.out.println();
		// ///////////////////FINISHED//////////////////////////////

		if (promethee1Ranking != null) {
			System.out.println("---------------------------Final Result---------------------------------");

//			System.out.println(promethee1Ranking.toString());
			XMCDAConverter.export(promethee1Ranking, client.getDir()+ "/promethee1.xml");
		}
		return promethee1Ranking;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step1(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA flow = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria", "weights","performances"));
				client.sendProblem(files,alias,ws_promethee[0],Controller.PROMETHEE);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_promethee[0]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("preference"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 flow = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[PROMETHEE-Step1]Messages:\n" + tt.getTextContent());
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
		
		return flow;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step2(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA preference = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "preference", "flow_type"));
				client.sendProblem(files,alias,ws_promethee[1],Controller.PROMETHEE);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_promethee[1]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("flows"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 preference = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[PROMETHEE-Step2]Messages:\n" + tt.getTextContent());
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
		
		return preference;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step3(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA promethee1Ranking = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "negativeFlows", "positiveFlows"));
				client.sendProblem(files,alias,ws_promethee[2],Controller.PROMETHEE);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_promethee[2]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("promethee1"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 String content = tt.getTextContent().replace("http://www.decision-deck.org/2009/XMCDA-2.0.0", "http://www.decision-deck.org/2009/XMCDA-2.1.0");
            				 promethee1Ranking = XMCDADoc.Factory.parse(content).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[PROMETHEE-Step3]Messages:\n" + tt.getTextContent());
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
		
		return promethee1Ranking;
	}
}
