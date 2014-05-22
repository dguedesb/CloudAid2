package aggregationEngine;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import csadata.CSAData;
import decisionDataModels.DecisionResult;
import decisionDataModels.GNode;




public class AggregationCore {

	public void computeAggregation(CSAData data,ArrayList<DecisionResult> decisionResults) {
		for (DecisionResult res : decisionResults) {
			this.printAdjacencyList(res.getAdjacencyList());
		}
		
		ArrayList<ArrayList<GNode>> admissable =this.getAdmissables(decisionResults,data);
		
		System.out.println("Aggregations: " +admissable.size());
		for(ArrayList<GNode> solution : admissable) {
			printSolution(solution);
		}
	}


	private ArrayList<ArrayList<GNode>> getAdmissables(ArrayList<DecisionResult> decisionResults,CSAData userData) {
		ArrayList<ArrayList<GNode>> admissables = new ArrayList<ArrayList<GNode>>();
		ArrayList<ArrayList<GNode>> tested = new ArrayList<ArrayList<GNode>>();
		ArrayList<int[][]> adjMatrixes = new ArrayList<int[][]>();
		ArrayList<GNode> solution = new ArrayList<GNode>();
		
		for(DecisionResult res: decisionResults) {//the least dominated on the graph
			solution.add(res.getAdjacencyList().get(0));
			adjMatrixes.add(res.getAdjacencyMatrix());
		}
		
		
		
		Queue<ArrayList<GNode>> queue = (Queue<ArrayList<GNode>>) new LinkedList<ArrayList<GNode>>();
		queue.add(solution);
		
		//if there's alternatives not comparable with the least dominated of the graphs, we have to consider them as well
		for (int q = 0; q < solution.size(); q++) {
			if (solution.get(q).getIncomparableWith().size() >= 1) {
				ArrayList<GNode> possibleChoices = new ArrayList<GNode>();
				
				getIncomparable(possibleChoices, solution.get(q));
				// replace index q on solution with one of the not comparable
				// alternatives
				for (GNode newAlt : possibleChoices) {
					ArrayList<GNode> newSol = new ArrayList<GNode>();
					for (int h = 0; h < solution.size(); h++) {
						if (q != h) {
							newSol.add(solution.get(h));
						} else {
							newSol.add(newAlt);
						}
					}
					if (newSol.size() >= 1)
						queue.add(newSol);
				}
			}
		}
		
		while(!queue.isEmpty())//bfs transversal
		{
			ArrayList<GNode> sol = queue.poll();
			tested.add(sol);
			this.printSolution(sol);
			if(AggChecker.checkAdmissability(userData, sol)) {
				if(!checkInAdmissable(sol,admissables,adjMatrixes)) {
					admissables.add(sol);
				}
			}
			else {
				for(int i=0;i<sol.size();i++) {
					ArrayList<ArrayList<GNode>> newSol = moveForward(sol.get(i),sol,i);//move forward in the graph by choosing the next less dominated alternative. It also considers the incomparable alternatives with the chosen alternative
					for(ArrayList<GNode> newSolution : newSol) {
						if(!queue.contains(newSolution) && !tested.contains(newSolution)) {
							if(!checkInAdmissable(newSolution,admissables,adjMatrixes)) {
								queue.add(newSolution);
							}
						}
					}
				}
			}
		}

		return admissables;
	}


	private void printSolution(ArrayList<GNode> sol) {
		String solution = "";
		for(GNode g : sol) {
			solution += g.getData().getMyID()+"["+g.getIn()+"]  "+"["+g.getOut()+"]  |";
		}
		
		System.out.println(solution);
	}


	private ArrayList<ArrayList<GNode>> moveForward(GNode gNode,ArrayList<GNode> sol, int gNodePos) {
		ArrayList<ArrayList<GNode>> newPossibilities = new ArrayList<ArrayList<GNode>>();//copies of the original solution but replace gNode by the best of his children and its incomparabilities
		
		ArrayList<GNode> possibleChoices = new ArrayList<GNode>();
		
		if(gNode.getPreferableTo().size() >= 1) {
			possibleChoices.add(gNode.getPreferableTo().get(0));//choose his best child, the one least dominated
		
			getIncomparable(possibleChoices,gNode.getPreferableTo().get(0));//fetch the childs incomparabilities
		}
		for(GNode possChoice : possibleChoices) {//create the new solutions
			ArrayList<GNode> newSolution = new ArrayList<GNode>();
			for(int k=0;k<sol.size();k++) {
				if(k!=gNodePos)
					newSolution.add(sol.get(k));
				else
					newSolution.add(possChoice);
			}
			if(newSolution.size() >= 1)
				newPossibilities.add(newSolution);
		}
		
		return newPossibilities;
	}


	private void getIncomparable(ArrayList<GNode> possibleChoices, GNode gNode) {
		for(GNode inc : gNode.getIncomparableWith()) {
			if(!possibleChoices.contains(inc)) {
				possibleChoices.add(inc);
				if(inc.getIncomparableWith().size() >= 1)
					getIncomparable(possibleChoices,inc);
			}
		}
	}


	private boolean checkInAdmissable(ArrayList<GNode> toTest,ArrayList<ArrayList<GNode>> admissables,ArrayList<int[][]> matrixes) {
		boolean dominated = true;
		
		if(admissables.size() == 0)
			return false;
		else{
			for(ArrayList<GNode> fsol : admissables) {
				for(int i=0;i<fsol.size();i++) {
					int[][] m = matrixes.get(i);
					int toTestindex=Integer.parseInt(toTest.get(i).getData().getMyID().substring(3))-1;
					int Solindex=Integer.parseInt(fsol.get(i).getData().getMyID().substring(3))-1;
					if(m[Solindex][toTestindex] == 1 || m[Solindex][toTestindex] == 0) {
						dominated=false;
						break;
					}
					dominated=true;
				}
				if(dominated)
					return true;
			}
		}
		
		return dominated;
	}


	public void printAdjacencyList(ArrayList<GNode> graph)
	{
		System.out.println("------------------------[AggregationModule] ADJACENCY LIST  ------------------------------");
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
		System.out.println("------------------------[AggregationModule] /ADJACENCY LIST  ------------------------------");
	}
}
