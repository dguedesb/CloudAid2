package aggregationEngine;

import java.util.ArrayList;
import csadata.Requirement;
import decisionDataModels.GNode;

public class AggChecker {

	
	public static boolean checkAdmissability(ArrayList<Requirement> reqs, ArrayList<GNode> solution){
		boolean admissable = false;
		
		for(Requirement req : reqs){
			if(req.getType() == 1){
				System.out.println("Checking a qualitative requirement");
			}else if(req.getType() == 0){
				System.out.println("Checking a quantitative requirement");
			}else{
				admissable = checkPrice(solution, req);
			}
		}
		return admissable;
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
