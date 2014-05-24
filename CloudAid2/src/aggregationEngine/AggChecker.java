package aggregationEngine;

import java.util.ArrayList;

import csadata.CSAData;
import csadata.Requirement;
import decisionDataModels.GNode;

public class AggChecker {

	
	public static boolean checkAdmissability(CSAData data, ArrayList<GNode> solution){
		
//		check if the solution is admissable
//		int[] control = new int[solution.size()];
//		for(int k=0;k<solution.size();k++){
//			if(solution.get(k).getPreferableTo().size() == 0)
//				control[k]=1;
//		}
//		
//		int sum = 0;
//
//		for (int i : control)
//		    sum += i;
//		if(sum == solution.size())
//			return true;
//		else
//			return false;
//		
		boolean admissable = false;
		
		for(Requirement req : data.getRequirements()){
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
