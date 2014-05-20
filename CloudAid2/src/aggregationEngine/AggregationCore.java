package aggregationEngine;


import java.util.ArrayList;

import csadata.CSAData;
import decisionDataModels.DecisionResult;
import decisionDataModels.GNode;




public class AggregationCore {

	public void computeAggregation(CSAData data,ArrayList<DecisionResult> decisionResults) {
		for (DecisionResult res : decisionResults) {
			this.printAdjancyList(res.getAdjancyList());
		}
	}


	public void printAdjancyList(ArrayList<GNode> graph)
	{
		System.out.println("------------------------[AggregationModule] ADJANCY LIST  ------------------------------");
		int rank = 1;
		for(GNode so : graph)
		{
			String  s=  rank +" - {";
			s+=so.getData().getMyID() + "["+so.getIn()+"]" ;
			int k;
			for(k=0;k<so.getIncomparableWith().size();k++)
			{
					s = s + ",";
					s+= so.getIncomparableWith().get(k).getData().getMyID()+ "["+so.getIncomparableWith().get(k).getIn()+"]";
			}
			
			s+="} ";
			System.out.println(s);
			rank++;
		}
		System.out.println("------------------------[AggregationModule] /ADJANCY LIST  ------------------------------");
	}
}
