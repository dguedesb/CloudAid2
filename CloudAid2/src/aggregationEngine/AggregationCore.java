package aggregationEngine;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import csadata.CSAData;
import csadata.Requirement;
import decisionDataModels.DecisionResult;
import decisionDataModels.GNode;




public class AggregationCore {
	
	private double init = 0;
	private double end = 0;
	private double ncomb = 0;
	public ArrayList<ArrayList<GNode>> computeAggregation(CSAData data,ArrayList<DecisionResult> decisionResults) {
		for (DecisionResult res : decisionResults) {
			this.printAdjacencyList(res.getAdjacencyList());
		}
		
		ArrayList<ArrayList<GNode>> admissable =this.getAdmissables(decisionResults,data);
		
		System.out.println("[AggregationModule]Possible Admissable Aggregations (ungrouped): " +admissable.size());
		
		return admissable;
	}


	private ArrayList<ArrayList<GNode>> getAdmissables(ArrayList<DecisionResult> decisionResults,CSAData userData) {
		ArrayList<ArrayList<GNode>> admissables = new ArrayList<ArrayList<GNode>>();
		ArrayList<ArrayList<GNode>> tested = new ArrayList<ArrayList<GNode>>();
		ArrayList<int[][]> adjMatrixes = new ArrayList<int[][]>();
		ArrayList<GNode> solution = new ArrayList<GNode>();

		for(DecisionResult res: decisionResults) {//the least dominated on the graph
			GNode n = res.getAdjacencyList().get(0);
			solution.add(n);
			adjMatrixes.add(res.getAdjacencyMatrix());
		}
		
		Queue<ArrayList<GNode>> queue = (Queue<ArrayList<GNode>>) new LinkedList<ArrayList<GNode>>();
		queue.add(solution);
		
		ArrayList<Requirement> aggReqs= this.getAggregationValidRequirements(userData,decisionResults);
		
		if(aggReqs.size()==0){
			System.out.println("[AggregationCore] Aggregation requirements not valid! Alternatives don't share the same feature(s) to perform the aggregation!");
			return null;
		}
		else {
			System.out.println("[AggregationCore] Defined aggregation requirements: "+userData.getRequirements().size() + "\tAggregation requirements being used: "+aggReqs.size());
		}
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
					if (newSol.size() >= 1) {
						if(!queue.contains(newSol) && !tested.contains(newSol)) {
							queue.add(newSol);
						}
					}
				}
			}
		}
		System.out.println("-----------");
		this.init = System.currentTimeMillis();
		double ncombinations = 0;
		while(!queue.isEmpty())//bfs transversal
		{
			ArrayList<GNode> sol = queue.poll();
			tested.add(sol);
			ncombinations++;
			this.printSolution(sol);
			if(AggChecker.checkAdmissability(aggReqs, sol)) {
				if(!checkDominatedOrIncomparable(sol,admissables,adjMatrixes)) {
					admissables.add(sol);	
				}
			}
			else {
				for(int i=0;i<sol.size();i++) {
					ArrayList<ArrayList<GNode>> newSol = moveForward(sol.get(i),sol,i);//move forward in the graph by choosing the next less dominated alternative. It also considers the incomparable alternatives with the chosen alternative
					for(ArrayList<GNode> newSolution : newSol) {
						if(!queue.contains(newSolution) && !tested.contains(newSolution)) {
							if(!checkDominatedOrIncomparable(newSolution,admissables,adjMatrixes)) {
								queue.add(newSolution);
							}
						}
					}
				}
			}
		}
		this.end = System.currentTimeMillis();
		this.setNcomb(ncombinations);
		return admissables;
	}
	
//	private void printGroupedAdmissables(ArrayList<ArrayList<ArrayList<GNode>>> admissables) {
//		
//		System.out.println("[Aggregation Module] Grouped admissables");
//		
//		for(ArrayList<ArrayList<GNode>>group : admissables) {
//			for(ArrayList<GNode> set : group) {
//				printSolution(set);
//			}
//			System.out.println("         *********            ");
//		}
//	}

	private ArrayList<Requirement> getAggregationValidRequirements(CSAData userData, ArrayList<DecisionResult> decisionResults) {//get the features in common. only aggregate based on the features that every offering in every ST has in common
		// TODO Auto-generated method stub
		
		ArrayList<Requirement> req = new ArrayList<Requirement>();
		for(Requirement r : userData.getRequirements()) {
			int st=0;
			for(DecisionResult dres : decisionResults) {
				int n=0;
				for(GNode node : dres.getAdjacencyList()) {
					if(r.getType() == 0) {//quantitative
						nextNode:
						for(QuantitativeValue qv : node.getData().getMyOff().getIncludes().get(0).getQuantfeatures()) {//each offering only includes one service. this is pre-defined on the data set, in case there are more than one, necessary to modify to iterate over every service 
							for(String type : qv.getTypes()) {
								if(type.contains(r.getCloudtype().replaceAll("cloudtaxonomy:", ""))) {
									n++;
									break nextNode;
								}
							}
						}
					}
					else if(r.getType() == 1) {//qualitative
						nextNode:
						for(QualitativeValue qv : node.getData().getMyOff().getIncludes().get(0).getQualfeatures()) {//each offering only includes one service. this is pre-defined on the data set, in case there are more than one, necessary to modify to iterate over every service 
							for(String type : qv.getTypes()) {
//								System.out.println(type + "   ->    "+r.getCloudtype().replaceAll("cloudtaxonomy:", ""));
								if(type.contains(r.getCloudtype().replaceAll("cloudtaxonomy:", ""))) {
									n++;
									break nextNode;
								}
							}
						}
					}
					else if(r.getType() == 2)
						n++;
				}
				
				if(n==dres.getAdjacencyList().size())
					st++;
			}
			if(st == decisionResults.size())
				req.add(r);
		}
		
		return req;
	}


	public void printSolution(ArrayList<GNode> sol) {
		String solution = "";
		for(GNode g : sol) {
			solution += g.getData().getMyID()+"["+g.getIn()+"]  "+"["+g.getOut()+"]  |";
		}
		
		int total = 0;
		for(GNode g : sol)
			total += g.getData().getMyPrice();
		
		System.out.println(solution + " -- total cost:" + total);
	}


	private ArrayList<ArrayList<GNode>> moveForward(GNode gNode,ArrayList<GNode> sol, int gNodePos) {
		ArrayList<ArrayList<GNode>> newPossibilities = new ArrayList<ArrayList<GNode>>();//copies of the original solution but replace gNode by the best of his children and its incomparabilities
		
		ArrayList<GNode> possibleChoices = new ArrayList<GNode>();
		
		if(gNode.getPreferableTo().size() >= 1) {
			possibleChoices.add(gNode.getPreferableTo().get(0));//choose his best child, the one least dominated
//			System.out.println("From "+gNode.getData().getMyID() + "chose its best: "+gNode.getPreferableTo().get(0).getData().getMyID());
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


	private boolean checkDominatedOrIncomparable(ArrayList<GNode> toTest,ArrayList<ArrayList<GNode>> admissables,ArrayList<int[][]> matrixes) {
		boolean dominated = true;
		if(admissables.size() == 0)
			return false;
		else{
			for(ArrayList<GNode> fsol : admissables) {
				for(int i=0;i<fsol.size();i++) {
					int[][] m = matrixes.get(i);
					int toTestindex=Integer.parseInt(toTest.get(i).getData().getMyID().substring(3))-1;
					int Solindex=Integer.parseInt(fsol.get(i).getData().getMyID().substring(3))-1;
					if(m[toTestindex][Solindex] == 1 || m[toTestindex][Solindex] == 0) {
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
	
	public double AlgorithmRunningTime(){
		return this.end - this.init;
	}


	public double getPerformedCombinations() {
		return ncomb;
	}


	public void setNcomb(double ncomb) {
		this.ncomb = ncomb;
	}
}
