package aggregationEngine;


import java.util.ArrayList;

import searchDataModels.FiltRes;
import controller.Controller;
import csadata.CSAData;
import decisionDataModels.ComparabilityResult;
import decisionDataModels.SAWResults;
import decisionDataModels.SMAAResults;



public class AggregationCore {

	@SuppressWarnings("unchecked")
	public void computeAggregation(CSAData data,Object decisionResults) {
		
		ArrayList<ComparabilityResult> decisionComparabilityResults = null;
		ArrayList<SAWResults> decisionSAWResults = null;
		ArrayList<SMAAResults> decisionSMAAResults = null;
		
		if(data.getMethod() == Controller.ELECTRE ||  data.getMethod() == Controller.PROMETHEE)
		{
			decisionComparabilityResults =  (ArrayList<ComparabilityResult>) decisionResults;
			
			for(ComparabilityResult cr : decisionComparabilityResults)
			{
				this.printRankedList(cr.getRankedList());
			}
		}
		else if(data.getMethod() == Controller.SMAA2)
		{
			decisionSMAAResults =  (ArrayList<SMAAResults>) decisionResults;
			
			for(SMAAResults smaar : decisionSMAAResults)
			{
				this.printRankedList(smaar.getRankedList());
			}
		}
		else if(data.getMethod() == Controller.SAW)
		{
			decisionSAWResults = (ArrayList<SAWResults>) decisionResults;
			
			for(SAWResults sawr : decisionSAWResults)
			{
				this.printRankedList(sawr.getRankedList());
			}
		}
		
	}
	
	private void printRankedList(ArrayList<ArrayList<FiltRes>> rankedList)
	{
		System.out.println("------------------------ [AggregationModule] - LIST  ------------------------------");
		int rank=1;
		for(ArrayList<FiltRes> r : rankedList)
		{
			if(r.size() >= 1)
			{
				String alts = "{ " + r.get(0).getMyID();
				for(int l =1; l<r.size();l++)
				{
					alts = alts + ",";
					alts+= r.get(l).getMyID();
				}
				alts=alts+" }";
				System.out.println(rank++ + " - " + alts);
			}
		}
		System.out.println("------------------------ [AggregationModule] - /LIST  ------------------------------");
	}


}
