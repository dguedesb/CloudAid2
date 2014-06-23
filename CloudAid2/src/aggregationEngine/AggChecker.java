package aggregationEngine;

import java.util.ArrayList;

import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import csadata.Requirement;
import decisionDataModels.GNode;

public class AggChecker {

	
	public static boolean checkAdmissability(ArrayList<Requirement> reqs, ArrayList<GNode> solution){
		boolean admissable = false;

		for(Requirement req : reqs){
			
			if(req.getType() == 1){
//					System.out.println("Checking a qualitative requirement");
				admissable = checkQualitative(solution,req);
			}else if(req.getType() == 0){
//					System.out.println("Checking a quantitative requirement");
				admissable = checkQuantitative(solution,req);
			}else{
				admissable = checkPrice(solution, req);
			}
			
			if(admissable == false)//if the solutions fails to comply with one requirement it's no longer admissable, no point in checking for admissibility with the other aggregation requirements
				break;
		}

		
		return admissable;
	}
	
	
	//TO TEST
	private static boolean checkQualitative(ArrayList<GNode> solution,Requirement req) {//checks if the alternative's qualitativevalue contains the value/feature defined on the aggregation requirement
		
		int n=0;
		for(GNode off : solution) {
			for(QualitativeValue qv : off.getData().getMyOff().getIncludes().get(0).getQualfeatures()) {//modify in case Offering includes more than one Service
				for(String type : qv.getTypes()) {
					if(type.contains(req.getCloudtype().replaceAll("cloudtaxonomy:", ""))) {
						if(req.getQualValue() != null) {//if the qualitative aggregation requirement has a defined value
							if(!(req.getQualValue().equals(""))) {
								if(   (qv.getHasValue().toLowerCase().contains(req.getQualValue().toLowerCase())  )      ){
									n++;
								}
							}
						}
					}
				}
			}
		}
		
		if (n >= req.getMaxAgST())
			return true;
		else
			return false;
	}

	
	//TO TEST
	private static boolean checkQuantitative(ArrayList<GNode> solution,Requirement req) {
		double total = 0;
		for(GNode off : solution) {
			for(QuantitativeValue qv : off.getData().getMyOff().getIncludes().get(0).getQuantfeatures()) {//modify in case Offering includes more than one Service
				for(String type : qv.getTypes()) {
					if(type.contains(req.getCloudtype().replaceAll("cloudtaxonomy:", ""))) {
							total+=total+req.getMax();
					}
				}
			}
		}
		
		if(req.isExclusivityMax()){
			if(total <= req.getMax())
				return true;
		}else{
			if(total >= req.getMin())
				return true;
		}
		
		return true;
	}

	private static boolean checkPrice(ArrayList<GNode> solution, Requirement req) {
		double total = 0;

		for(GNode off : solution){
			total = total + Double.parseDouble((String) off.getData().getCritAttr().get("price"));
		}

		if(req.isExclusivityMax()){
			if(total <= req.getMax())
				return true;
		}else{
			if(total >= req.getMin())
				return true;
		}
		
		return false;
	}
}
