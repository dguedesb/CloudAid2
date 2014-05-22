package aggregationEngine;

import java.util.ArrayList;

import csadata.CSAData;
import decisionDataModels.GNode;

public class AggChecker {

	
	public static boolean checkAdmissability(CSAData data, ArrayList<GNode> solution){
		
		//check if the solution is admissable
		int[] control = new int[solution.size()];
		for(int k=0;k<solution.size();k++){
			if(solution.get(k).getPreferableTo().size() == 0)
				control[k]=1;
		}
		
		int sum = 0;

		for (int i : control)
		    sum += i;
		if(sum == solution.size())
			return true;
		else
			return false;
	}
}
