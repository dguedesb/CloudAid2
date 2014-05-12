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

public class ELECTRE {
	
	private static final String[] ws_electre = {"ElectreConcordance-J-MCDA.py","ElectreDiscordances-J-MCDA.py","ElectreOutranking-J-MCDA.py","cutRelation-J-MCDA.py","alternativesRankingViaQualificationDistillation-ITTB.py"};
	
	public static XMCDA solve(ArrayList<XMCDA> files,String dir)
	{
		DecisionDeckSOAPClient client = new DecisionDeckSOAPClient(dir);

		// run every steps
		XMCDA conc = null;
		int controller = 0 ;
		String com = "[ELECTRE-Step1] Request is taking a while. Waiting.";
		do{
			if(controller == 0)
			{
				conc = step1(files, client,false);
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
				conc = step1(files, client,true);
			}
		}while(conc == null);
		
		controller=0;
		System.out.println();
		/////////////////////////////////////////////////////////////////////////
		XMCDA disc = null;
		com = "[ELECTRE-Step2] Request is taking a while. Waiting.";
		do{
			if(controller == 0)
			{
				disc = step2(files, client,false);
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
				conc = step2(files, client,true);
			}
		}while(disc == null);
		
		controller=0;
		System.out.println();
		
		///////////////////////////////////////////////////////
		XMCDA outranking = null;
		com = "[ELECTRE-Step3] Request is taking a while. Waiting.";
		if(conc != null && disc != null)
		{
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(files.get(1));
			cont.add(conc);
			cont.add(disc);
			do{
				if(controller == 0)
				{
					outranking = step3(cont, client,false);
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
					outranking = step3(cont, client,true);
				}
			}while(outranking == null);
		}
		
		controller=0;
		System.out.println();
		///////////////////////////////////////////////////////////////////////////
		XMCDA binaryRel=null;
		com = "[ELECTRE-Step4] Request is taking a while. Waiting.";
		if (outranking != null) {
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(outranking);
			cont.add(XMCDAConverter.createMethodParameters("0.7",1));
			
			do{
				if(controller == 0)
				{
					binaryRel = step4(cont, client,false);
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
					binaryRel = step4(cont, client,true);
				}
			}while(binaryRel == null);
		}
		controller=0;
		System.out.println();
		///////////////////////////////////////////////////////////
		XMCDA intersectionDistillation=null;
		com = "[ELECTRE-Step5] Request is taking a while. Waiting.";
		if (binaryRel != null) {
			ArrayList<XMCDA> cont = new ArrayList<XMCDA>();
			cont.add(files.get(0));
			cont.add(binaryRel);
			
			do{
				if(controller == 0)
				{
					intersectionDistillation = step5(cont, client,false);
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
					intersectionDistillation = step5(cont, client,true);
				}
			}while(intersectionDistillation == null);
		}
		
		controller=0;
		System.out.println();
		
		/////////////////////FINISHED//////////////////////////////
		
		if(intersectionDistillation != null)
		{
			System.out.println("---------------------------Final Result---------------------------------");
			
//			System.out.println(intersectionDistillation.toString());
			XMCDAConverter.export(intersectionDistillation, client.getDir()+"/intersectionDistillation.xml");
		}
		return intersectionDistillation;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step1(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatReqAnswer)
	{
		XMCDA conc = null;
		try {
			if(repeatReqAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria", "weights","performances"));
				client.sendProblem(files,alias,ws_electre[0],Controller.ELECTRE);
			}
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[0]);
			SOAPMessage response = client.getProblemResponse(ws_electre[0]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("concordance"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 conc = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[ELECTRE-Step1]Messages:\n" + tt.getTextContent());
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
		
		return conc;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step2(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatRequestAnswer)
	{
		XMCDA disc = null;
		try {
			if(repeatRequestAnswer==false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria", "weights","performances"));
				client.sendProblem(files,alias,ws_electre[1],Controller.ELECTRE);
			}
			
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[1]);
			SOAPMessage response = client.getProblemResponse(ws_electre[1]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("discordances"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 disc = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[ELECTRE-Step2]Messages:\n" + tt.getTextContent());
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("ticket"))
            			 {
//            				 System.out.println("[ELECTRE-Step2]Request took longer than expected:\nTicket->"+tt.getTextContent());
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
		
		return disc;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step3(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatRequestAnswer)
	{
		XMCDA outranking = null;
		try {
			if(repeatRequestAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "criteria", "concordance","discordances"));
				client.sendProblem(files,alias,ws_electre[2],Controller.ELECTRE);
			}
			
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[2]);
			SOAPMessage response = client.getProblemResponse(ws_electre[2]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("outranking"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 outranking = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[ELECTRE-Step3]Messages:\n" + tt.getTextContent());
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("ticket"))
            			 {
//            				 System.out.println("[ELECTRE-Step3]Request took longer than expected:\nTicket->"+tt.getTextContent());
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
		
		return outranking;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step4(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatRequestAnswer)
	{
		XMCDA binary = null;
		try {
			if(repeatRequestAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "relation", "cut_threshold"));
				client.sendProblem(files,alias,ws_electre[3],Controller.ELECTRE);
			}
			
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[3]);
			SOAPMessage response = client.getProblemResponse(ws_electre[3]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("binary_relation"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 binary = XMCDADoc.Factory.parse(tt.getTextContent()).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[ELECTRE-Step4]Messages:\n" + tt.getTextContent());
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("ticket"))
            			 {
//            				 System.out.println("[ELECTRE-Step4]Request took longer than expected:\nTicket->"+tt.getTextContent());
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
		
		return binary;
	}
	
	@SuppressWarnings("unchecked")
	public static XMCDA step5(ArrayList<XMCDA> files,DecisionDeckSOAPClient client,boolean repeatRequestAnswer)
	{
		XMCDA intersectionDistillation = null;
		try {
			if(repeatRequestAnswer == false)
			{
				ArrayList<String> alias = new ArrayList<String>(Arrays.asList("alternatives", "outrankingRelation"));
				client.sendProblem(files,alias,ws_electre[4],Controller.ELECTRE);
			}
			
//			System.out.println("[ELECTRE]Requesting response from "+ws_electre[4]);
			SOAPMessage response = client.getProblemResponse(ws_electre[4]);
			
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
            			 if(tt.getLocalName().equalsIgnoreCase("intersectionDistillation"))
    	            	 {
//            				 System.out.println(tt.getTextContent());
            				 String content = tt.getTextContent().replace("http://www.decision-deck.org/2009/XMCDA-2.0.0", "http://www.decision-deck.org/2009/XMCDA-2.1.0");
            				 intersectionDistillation = XMCDADoc.Factory.parse(content).getXMCDA();
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("messages"))
    	            	 {
            				 System.out.println( "[ELECTRE-Step5]Messages:\n" + tt.getTextContent());
    	            	 }
            			 if(tt.getLocalName().equalsIgnoreCase("ticket"))
            			 {
//            				 System.out.println("[ELECTRE-Step5]Request took longer than expected:\nTicket->"+tt.getTextContent());
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
		
		return intersectionDistillation;
	}
	
	
}
