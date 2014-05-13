package decisionEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import controller.Controller;
import csadata.Criterion;
import decisionDataModels.ConceptDistance;
import decisionDataModels.DistancesContainer;
import frontend.CSABuilder;
import searchDataModels.FiltRes;
import usdl.servicemodel.Offering;

@SuppressWarnings("unused")
public class Normalizer {
	private boolean print=true;
	public Normalizer(){}
	
	public ArrayList<FiltRes> normalize(ArrayList<FiltRes> alt, ArrayList<Criterion> crit){
		ArrayList<FiltRes> normalized = alt;
		String preference;

		for(Criterion criterion : crit){
			//check criterion type
			//normalize according to the criterion type
			if( criterion.getType()  == 0 || criterion.getType()==2){
				//numerical
				normalized = this.calculateDistances(normalized, criterion);
				normalized = this.normalizeNumerical(alt, criterion);
			}else if(criterion.getType() == 1){
				//non-numerical
				//System.out.println(criterion.getName() + " ->non-numerical");
				normalized = this.normalizeNonNumerical(alt, criterion);
			}
			if(print)
				this.printCriterion(criterion);
		}
//		this.printData(normalized);
		//this.printUnprocessedData(normalized);
		return normalized;	
	}
	
	//mode == 0 - get maximum from the original values || mode == 1 - get maximum from the already calculated weighted values
	private double getMaximum(ArrayList<FiltRes> alternatives, String criterionName, int mode){
		double max = 0;
		
		for(FiltRes alt : alternatives){
			double temp = 0;
			if(mode == 0){
				temp = Double.parseDouble((String) alt.getCritAttr().get(criterionName));
			}else if(mode == 1){
				temp = alt.getNormalizedAttributes().get(criterionName);
			}
			if( temp > max)
				max = temp;
			
			
		}
		
		return max;
		
	}
	
	//mode == 0 - get maximum from the original values || mode == 1 - get maximum from the already calculated weighted values
	private double getMinimum(ArrayList<FiltRes> alternatives, String criterionName, int mode){
		double min = Double.MAX_VALUE;

		for(FiltRes alt : alternatives){
			double temp = 0;
			if(mode == 0){
				temp = Double.parseDouble((String) alt.getCritAttr().get(criterionName));
			}else if(mode == 1){
				temp = alt.getNormalizedAttributes().get(criterionName);
			}
			if( temp < min)
				min = temp;
		}
		
		return min;
		
	}
	
	private ArrayList<FiltRes> calculateDistances(ArrayList<FiltRes> alt, Criterion criterion){
		ArrayList<FiltRes> normalized = alt;
		double preference; // the value to compare
		
		//establish the preference depending on the preference direction
		if(criterion.getPreference().equalsIgnoreCase("max"))
			preference = this.getMaximum(normalized, criterion.getName(), 0); 
		else if(criterion.getPreference().equalsIgnoreCase("min"))
			preference = this.getMinimum(normalized, criterion.getName(), 0);
		else 
			preference = Double.parseDouble(criterion.getPreference());
		
		criterion.setPreference(Double.toString(preference));
		for(FiltRes alternative : normalized){
			double res = Math.abs(preference - Double.parseDouble((String) alternative.getCritAttr().get(criterion.getName())));
			alternative.getNormalizedAttributes().put(criterion.getName(), res);
			if(print)
				System.out.println("DecisionModule-Normalizer:  "+alternative.getMyOff().getName() + " : " + criterion.getName() + "="+res);
		}
		
		
		
		double min = this.getMinimum(alt, criterion.getName(), 0);
		double max = this.getMaximum(alt, criterion.getName(), 0);
		
		if(criterion.getPreference_threshold() >= 0)
		{
			double res = 0;
			res = Math.min( Math.max(criterion.getPreference_threshold()/(double)(max-min) , 0),1 );
			criterion.setPreference_threshold(res);
		}
		
		if(criterion.getIndifference_threshold() >= 0)
		{
			double res = 0;
			res = Math.min( Math.max(criterion.getIndifference_threshold()/(double)(max-min) , 0),1 );
			criterion.setIndifference_threshold(res);
		}
		
		if(criterion.getVeto_threshold() >= 0)
		{
			double res = 0;
			res = Math.min( Math.max(criterion.getVeto_threshold()/(double)(max-min) , 0),1 );
			criterion.setVeto_threshold(res);
		}
		
		return normalized;
	}
	
	private ArrayList<FiltRes> normalizeNumerical(ArrayList<FiltRes> alt, Criterion criterion){
		ArrayList<FiltRes> normalized = alt;
		
		double min = this.getMinimum(alt, criterion.getName(), 1);
		double max = this.getMaximum(alt, criterion.getName(), 1);
		

		System.out.println("DecisionModule-Normalizer: NORMALIZING---  " + criterion.getName());
		for(FiltRes alternative : normalized){
			double res = 0;
			if(max != min)
				res = (((alternative.getNormalizedAttributes().get(criterion.getName())) - min) / (max-min));
			res = (res * -1) + 1; 
			if(print)
			{
				System.out.println("DecisionModule-Normalizer: min: "+min+ "|| max: "+ max + "|| value: "+alternative.getNormalizedAttributes().get(criterion.getName())+"|| res: "+ res);
			}
			alternative.getNormalizedAttributes().put(criterion.getName(), res);
			//System.out.println(alternative.getName() + " : " + criterion.getName() + "="+res);
		}
	
		return normalized;
	}
	
	
	private ArrayList<FiltRes> normalizeNonNumerical(ArrayList<FiltRes> alternatives, Criterion criterion){
		ArrayList<FiltRes> normalized = alternatives;
		HashMap<String,Double> temps = new HashMap<String,Double>();
		Double tempValue;
		String preference = criterion.getPreference();
		
		DistancesContainer distancesRequest = new DistancesContainer();
		distancesRequest.setPrefered(preference);
		
		List<ConceptDistance> concepts = new ArrayList<ConceptDistance>();
		
		for(FiltRes alt : normalized){			
			String tempName = (String) alt.getCritAttr().get(criterion.getName());
			//System.out.println("Normalizing alternative: "+tempName);
			if(!temps.containsKey(tempName)){
				//System.out.println("contains");
//				tempValue = (Double)temps.get(tempName);
				ConceptDistance cd = new ConceptDistance();
				cd.setName(tempName);
				concepts.add(cd);
				temps.put(tempName, -1.0);
			}
//			else{
//				String[] msg = {tempName, preference};
//				tempValue = Double.parseDouble(Controller.askData(Controller.GET_DISTANCE_VALUE, msg, null));
//				temps.put(tempName, tempValue);
//				}
//				alt.getNormalizedAttributes().put(criterion.getName(), tempValue);
//			//System.out.println(alt.getName() + " : " + criterion.getName() + "="+tempValue);
			}
		
			distancesRequest.setConcepts(concepts);
			
			distancesRequest = Controller.requestDistancesInfo(distancesRequest);
		
			for(FiltRes alt : normalized)
			{
				String tempName = (String) alt.getCritAttr().get(criterion.getName());
				for(ConceptDistance cd : distancesRequest.getConcepts())
				{
					if(cd.getName().equals(tempName))
						alt.getNormalizedAttributes().put(criterion.getName(), cd.getDistVal());
				}
			}
			
			normalized = this.normalizeNumerical(normalized, criterion);

		return normalized;
	}
	
	private void printData(ArrayList<FiltRes> normalized){
		
		for(FiltRes serv : normalized){
			System.out.println(serv.getMyOff().getName());
			Iterator<Entry<String, Double>> it = serv.getNormalizedAttributes().entrySet().iterator();
			Iterator<Entry<String, String>> itc = serv.getCritAttr().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,Double> pairs = (Map.Entry<String,Double>)it.next();
		        Map.Entry<String,String> pairsb = (Map.Entry<String,String>)itc.next();
		        System.out.print("DecisionModule-Normalizer: "+pairs.getKey() + " = "+pairsb.getValue() + "  =   "+ pairs.getValue()+"    ");
		    }
		    System.out.println();
		}
	}
	
private void printUnprocessedData(ArrayList<FiltRes> normalized){
		
		for(FiltRes serv : normalized){
			System.out.println(serv.getMyOff().getName());
			Iterator<Entry<String, String>> it = serv.getCritAttr().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
		        System.out.print("DecisionModule-Normalizer: "+pairs.getKey() + " = " + pairs.getValue()+"    ");
		    }
		    System.out.println();
		}
	}
	
	private void printCriterion(Criterion crit){
		System.out.println("DecisionModule-Normalizer: "+crit.toString());
		
	}

}

