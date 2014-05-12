package frontend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import searchDataModels.FiltRes;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import csadata.CSAData;
import csadata.Requirement;
import csadata.ServiceTemplate;

@SuppressWarnings("resource")

public class ComInterface {
	
	public ComInterface(){}
	
	
	public boolean propmtToExit(){
		while(true){
			System.out.println("Do you want to exit? (Y/N)");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			if(s.equalsIgnoreCase("y")){
				return true;		
			}else if(s.equalsIgnoreCase("n")){
				return false;
			}else{
				System.out.println("Please answer only with Y/N");
			}
		}
	}
	
	public String askforCriterionWeight(String criterionName, String serviceTemplateName){
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.println("Please specify the decision weight of criterion: "+ criterionName +" in Service Template: "+ serviceTemplateName);
			String weight = in.nextLine();
			try {
				Float weightValue = Float.parseFloat(weight);
				return weightValue.toString();
			} catch (NumberFormatException e) {
				System.out.println("Please insert only numerical values!");
			}
		}
	}
	
	public String askforCritPrefDirection(String criterionName, String serviceTemplateName){
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.println("Do you want to maximize the value of criterion: "+ criterionName +" in Service Template: "+ serviceTemplateName+ "? (y/n)");
			String s = in.nextLine();
			if(s.equalsIgnoreCase("y")){
				return s;
			}else if (s.equalsIgnoreCase("n")){
				return s;
			}else{
				System.out.println("Please answere only with Y/N");
			}
		}
	}
	
	public String askforPreferenceValue(String criterionName){
		System.out.println("Please insert the preferable value for criterion: "+ criterionName+ ":");
		Scanner in3 = new Scanner(System.in);
		String s3 = in3.nextLine();
		return s3;
	}
	
	public String askforDistance(String tempName, String preference){
		double tempValue;
		while(true){
			System.out.println("What is a distance between the value "+tempName + "(a) and the preferable value "+preference+ "(b) ?");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try{
				tempValue = Double.parseDouble(s);
				if(tempValue >= 0)
					return Double.toString(tempValue);
				else
					System.out.println("Please use positive values.");
			}catch(Exception ex){
				System.out.println("Please use numerical values.");
			}
			
		}
	}
	
	public String promptYesNo(String msg){
		while(true){
			System.out.println(msg);
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			if(s.equalsIgnoreCase("y")){
				return s;		
			}else if(s.equalsIgnoreCase("n")){
				return s;
			}else{
				System.out.println("Please answer only with Y/N");
			}
		}
	}
	
	
	public void prompt(String msg){
		System.out.println(msg);
	}
	
	public void printAlternatives(ArrayList<FiltRes> alternatives){
		
		for(FiltRes alt : alternatives){
			System.out.println("  - ALTERNATIVE:" + alt.getMyOff().getName());
			Iterator<Entry<String, String>> it = alt.getCritAttr().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
		        System.out.println("        - "+ pairs.getKey() + " = " + pairs.getValue());
		    }
		    System.out.println();
	
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void printAlternativesData(ArrayList<FiltRes> alternatives){
		
		for(FiltRes alt : alternatives){
			System.out.println("  - ALTERNATIVE: " + alt.getMyOff().getName());
			Iterator<Entry<String,String>> it = alt.getCritAttr().entrySet().iterator();
			System.out.println("      - Qualitative Features");
			for(QualitativeValue qualF : alt.getMyOff().getIncludes().get(0).getQualfeatures()){
				System.out.println("         - " +qualF.toString());
			}
			System.out.println("      - Quantitative Features");
			for(QuantitativeValue quantF : alt.getMyOff().getIncludes().get(0).getQuantfeatures()){
				System.out.println("         - " +quantF.toString());
			}
			System.out.println("      - Attributes");
		    while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println("        - "+ pairs.getKey() + " = " + pairs.getValue());
		    }
		    System.out.println("      - Attribute Weights");
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println("        - "+ pairs.getKey() + " = " + pairs.getValue());
		    }
		    System.out.println();
	
		}
	}

	public void printData(CSAData data){
		List<ServiceTemplate> comps;
		List<Requirement> reqs;
		
		comps = data.getServiceTemplates();
		System.out.println("CSA SERVICE TEMPLATEs: ");
		for(int i=0; i < comps.size(); i++){
			System.out.println(comps.get(i).toString());
		}
		
		reqs = data.getRequirements();
		System.out.println("CSA GENERAL REQUIREMENTS: ");
		for(int i=0; i < reqs.size(); i++){
			System.out.println(reqs.get(i).toString());
		}
		
	}
	
//	public void printResults(CSAData data){
//		int index = 1;
//		System.out.println("CSA RESULTS: ");
//		for(ServiceTemplate comp : data.getServiceTemplates()){
//			System.out.println("SERVICE TEMPLATE: " + comp.getId());
//			if(comp.getResultList() != null){
//				for(Result res : comp.getResultList()){			
//					System.out.println("    ALTERNATIVE"+ index++ +":" + res.getService().getName() + "--->" + res.getPerformance());
//				}
//			}
//			index = 1;
//		}
//	}
//	
//	public void printResultList(String msg, ArrayList<Result> results){
//		int index = 1;
//		System.out.println(msg);
//		for(Result res : results){			
//			System.out.println("    ALTERNATIVE"+ index++ +":" + res.getService().getName() + "--->" + res.getPerformance());
//		}
//	}
}
