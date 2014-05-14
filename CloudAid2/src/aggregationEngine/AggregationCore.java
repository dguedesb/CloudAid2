package aggregationEngine;


import java.util.ArrayList;
import java.util.List;

import searchDataModels.FiltRes;
import controller.Controller;
import csadata.CSAData;
import de.normalisiert.utils.graphs.ElementaryCyclesSearch;
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
				
				//////////////////////////////////////
				int[][] m =cr.getCompareMatrix();
				
				boolean[][] mcopy = new boolean[cr.getServices().size()][cr.getServices().size()];
				
				for(int i=0;i<cr.getServices().size();i++)
				{
					for(int j = 0;j<cr.getServices().size();j++)
					{
						if(m[i][j]==1)
							mcopy[i][j]=true;
//						else if(m[i][j] == 0)
//						{
//							mcopy[i][j]=true;
//							mcopy[j][i]=true;
//						}
					}
				}
				
				String nodes[] = new String[cr.getServices().size()];

				for (int i = 0; i < cr.getServices().size(); i++) {
					nodes[i] = "Alt " + (i+1);
				}
				
				
				ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(mcopy, nodes);
				List cycles = ecs.getElementaryCycles();
				for (int i = 0; i < cycles.size(); i++) {
					List cycle = (List) cycles.get(i);
					for (int j = 0; j < cycle.size(); j++) {
						String node = (String) cycle.get(j);
						if (j < cycle.size() - 1) {
							System.out.print(node + " -> ");
						} else {
							System.out.print(node);
						}
					}
					System.out.print("\n");
				}
				
				
				
				////////////////////////////////////////
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
				String alts = "{ " + r.get(0).getMyID() + "["+r.get(0).inferior+"]";
				for(int l =1; l<r.size();l++)
				{
					alts = alts + ",";
					alts+= r.get(l).getMyID()+ "["+r.get(l).inferior+"]";
				}
				alts=alts+" }";
				System.out.println(rank++ + " - " + alts);
			}
		}
		System.out.println("------------------------ [AggregationModule] - /LIST  ------------------------------");
	}


}
