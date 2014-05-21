package decisionEngine;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import controller.Controller;
import searchDataModels.FiltRes;
import csadata.Criterion;
import csadata.ServiceTemplate;
import de.normalisiert.utils.graphs.ElementaryCyclesSearch;
import decisionDataModels.ComparabilityResult;
import decisionDataModels.DecisionResult;
import decisionDataModels.GNode;
import decisionDataModels.SAWResult;
import decisionDataModels.SAWResults;
import decisionDataModels.SMAAResult;
import decisionDataModels.SMAAResults;


public class DecisionCore {
	
	
	private XMCDAConverter converter;
	private Normalizer normalizer;
//	private static final String[] methods = {"SAW","ELECTRE","PROMETHEE","SMAA"};

	
	public DecisionCore(){//receive the client's interface in order to communicate with it
		this.converter = new XMCDAConverter();
		this.normalizer = new Normalizer();
	}
	
	public DecisionResult decide(ServiceTemplate comp,  int method,String dir) throws FileNotFoundException{
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		System.out.println("STATUS: Decision for Service Template: "+ comp.getName());

		if(method == Controller.SAW || method == Controller.ELECTRE || method == Controller.PROMETHEE){
			files = WeightXMCDA(comp, comp.getFoundAlternatives(), method);
		}else if(method == Controller.SMAA2){
			files = WeightlessXMCDA(comp, comp.getFoundAlternatives());
		}else{
			return null;
		}
		
		return postXMCDA(files,comp, method,dir);

	}
	
	
	private DecisionResult postXMCDA(ArrayList<XMCDA> files, ServiceTemplate comp, int method,String dir) throws FileNotFoundException{
		//attach timestamp in the method messages
		
		if(method == Controller.SAW)
		{
//			ArrayList<Result> results =SAW.solve(files);
			XMCDA results = SAW.solve(files,dir);
			SAWResults sawResults =XMCDAConverter.processSAWResults(results,comp);
			
			Collections.sort(sawResults.getSAWResults(), Collections.reverseOrder(new Comparator<SAWResult>() {
			    @Override
			    public int compare(SAWResult c1, SAWResult c2) {
			        return Double.compare(c1.getPerformance(), c2.getPerformance());
			    }
			}));
			
			System.out.println("----------[DecisionModule] Ranked List from DDWS-----------------");
			for(SAWResult s: sawResults.getSAWResults())
			{
				System.out.println(s.getService().getMyID() + " - " +s.getPerformance());
			}
			System.out.println("----------/[DecisionModule] Ranked List from DDWS-----------------");
			return this.getSAWGraphSolution(sawResults,dir);
		}
		else if(method == Controller.PROMETHEE)
		{
			XMCDA results = PROMETHEE.solve(files,dir);
			ComparabilityResult prometheeResults =XMCDAConverter.processComparabilityResults(results,comp);

			return this.getComparabilityGraphSolution(prometheeResults,dir);
		}
		else if(method == Controller.ELECTRE)
		{
			XMCDA results = ELECTRE.solve(files,dir);
			ComparabilityResult electreResults =XMCDAConverter.processComparabilityResults(results,comp);

			return this.getComparabilityGraphSolution(electreResults,dir);
		}
		else if(method == Controller.SMAA2)
		{
			XMCDA results = SMAA.solve(files,dir);
			SMAAResults smaaResults =XMCDAConverter.processSMAAResults(results,comp);

			return this.getSMAAGraphSolution(smaaResults,dir);
		}
		
		return null;
	}
	
	private DecisionResult getSAWGraphSolution(SAWResults sawResults,String dir) throws FileNotFoundException
	{
		ArrayList<ArrayList<FiltRes>> rankedList = new ArrayList<ArrayList<FiltRes>>();
		for (int i=0; i < sawResults.getSAWResults().size(); i++) {
			rankedList.add(new ArrayList<FiltRes>());
		}
		
		for(int k = 0 ; k<sawResults.getSAWResults().size();k++)
		{
			rankedList.get(k).add(sawResults.getSAWResults().get(k).getService());
		}
		
		ArrayList<GNode> graph = new ArrayList<GNode>();
		
		for (int i = 0; i < sawResults.getSAWResults().size(); i++) {
			GNode n = new GNode();
			n.setData(sawResults.getSAWResults().get(i).getService());
			graph.add(n);
		}
		
		int[][] matrix = new int[sawResults.getSAWResults().size()][sawResults.getSAWResults().size()];
		
		for (int l = 0; l < rankedList.size(); l++) {//create the adjency matrix from the ranked list
			ArrayList<FiltRes> t = rankedList.get(l);
			for (int k = 0; k < t.size(); k++) {
				FiltRes alt = t.get(k);
				int id = Integer.parseInt(alt.getMyID().substring(3)) - 1;
				for (int next = l + 1; next < rankedList.size(); next++) {
					ArrayList<FiltRes> nextRank = rankedList.get(next);
					for (FiltRes inferior : nextRank) {
						int id_inf = Integer.parseInt(inferior.getMyID().substring(3)) - 1;
						matrix[id][id_inf] = 1;
						matrix[id_inf][id] = -1;
					}
				}
			}
		}
		
		
		StringBuilder graphb = new StringBuilder();
		graphb.append("digraph G {\n");
		GNode n=null;
		for (int i = 0; i <sawResults.getSAWResults().size(); i++) {
			
			for(GNode node : graph){
				if(node.getData().getMyID().substring(3).equals("" + (i+1)))
					n=node;
			}
			
			for (int j = 0; j < sawResults.getSAWResults().size(); j++) {
				if (i < j && i != j) {
					
					GNode son = null;
					for(GNode node : graph){
						if(node.getData().getMyID().substring(3).equals("" + (j+1)))
							son=node;
					}
					
					if (matrix[i][j] == 1) {
						graphb.append("" + n.getData().getMyID() + " -> "+ son.getData().getMyID() + ";\n");
						// compResults.getServices().get(i).inferior++;
						// compResults.getServices().get(i).preferableTo.add(compResults.getServices().get(j));

						n.setOut(n.getOut() + 1);
						n.getPreferableTo().add(son);
						son.setIn(son.getIn() + 1);
					} else if (matrix[i][j] == -1) {

						graphb.append("" + son.getData().getMyID() + " -> "+ n.getData().getMyID() + ";\n");
						// compResults.getServices().get(j).inferior++;
						// compResults.getServices().get(j).preferableTo.add(compResults.getServices().get(i));

						n.setIn(n.getIn() + 1);
						son.getPreferableTo().add(n);
						son.setOut(son.getOut() + 1);
					} else if (matrix[i][j] == 0) {
						n.getIncomparableWith().add(son);
						son.getIncomparableWith().add(n);
					}
				}
			}
		}
		
		//fix cycles
		
		for (GNode res : graph) {
			Collections.sort(res.getIncomparableWith(), new CustomComparator());
			Collections.sort(res.getPreferableTo(), new CustomComparator());
		}

		graphb.append("}\n");

		Collections.sort(graph, new CustomComparator());
		
		this.exportDotFile(graphb,dir);
		this.printAdjancyList(graph);
		
		DecisionResult result = new DecisionResult(graph,matrix);
		this.frcycles(result);
		
		return result;
//		sawResults.setRankedList(rankedList);
	}
	
	private DecisionResult getSMAAGraphSolution(SMAAResults smaaResults,String dir) throws FileNotFoundException {
		
		ArrayList<ArrayList<FiltRes>> rankedList = new ArrayList<ArrayList<FiltRes>>();
		for (int i=0; i < smaaResults.getSmaaResults().size(); i++) {
			rankedList.add(new ArrayList<FiltRes>());
		}
		
		for(SMAAResult res : smaaResults.getSmaaResults())
		{
			double[] ranks = res.getRanks();
			System.out.println(res.getService().getMyID());
			double max = ranks[0];
			int index = 0;
			for(int i = 1; i<ranks.length;i++)//find the rank with highest probability 
			{
				if(ranks[i] > max)
					index = i;
			}
			rankedList.get(index).add(res.getService());//place it on rankedList
		}
			
			////////////////////////////////
			
		ArrayList<GNode> graph = new ArrayList<GNode>();
			
		for (int i = 0; i < smaaResults.getSmaaResults().size(); i++) {
			GNode n = new GNode();
			n.setData(smaaResults.getSmaaResults().get(i).getService());
			graph.add(n);
		}

		int[][] matrix = new int[smaaResults.getSmaaResults().size()][smaaResults.getSmaaResults().size()];

		for (int l = 0; l < rankedList.size(); l++) {//create the adjency matrix from the ranked list
			ArrayList<FiltRes> t = rankedList.get(l);
			for (int k = 0; k < t.size(); k++) {
				FiltRes alt = t.get(k);
				int id = Integer.parseInt(alt.getMyID().substring(3)) - 1;
				for (int next = l + 1; next < rankedList.size(); next++) {
					ArrayList<FiltRes> nextRank = rankedList.get(next);
					for (FiltRes inferior : nextRank) {
						int id_inf = Integer.parseInt(inferior.getMyID().substring(3)) - 1;
						matrix[id][id_inf] = 1;
						matrix[id_inf][id] = -1;
					}
				}
			}
		}

		StringBuilder graphb = new StringBuilder();
		graphb.append("digraph G {\n");
		GNode n;
		for (int i = 0; i < smaaResults.getSmaaResults().size(); i++) {
			n = graph.get(i);
			for (int j = 0; j < smaaResults.getSmaaResults().size(); j++) {
				if (i < j && i != j) {
					GNode son = graph.get(j);
					if (matrix[i][j] == 1) {
						graphb.append("" + n.getData().getMyID() + " -> "+ son.getData().getMyID() + ";\n");
						// compResults.getServices().get(i).inferior++;
						// compResults.getServices().get(i).preferableTo.add(compResults.getServices().get(j));

						n.setOut(n.getOut() + 1);
						n.getPreferableTo().add(son);
						son.setIn(son.getIn() + 1);
					} else if (matrix[i][j] == -1) {

						graphb.append("" + son.getData().getMyID() + " -> "+ n.getData().getMyID() + ";\n");
						// compResults.getServices().get(j).inferior++;
						// compResults.getServices().get(j).preferableTo.add(compResults.getServices().get(i));

						n.setIn(n.getIn() + 1);
						son.getPreferableTo().add(n);
						son.setOut(son.getOut() + 1);
					} else if (matrix[i][j] == 0) {
						n.getIncomparableWith().add(son);
						son.getIncomparableWith().add(n);
					}
				}
			}
		}

		for (GNode res : graph) {
			Collections.sort(res.getIncomparableWith(), new CustomComparator());
			Collections.sort(res.getPreferableTo(), new CustomComparator());
		}

		graphb.append("}\n");

		Collections.sort(graph, new CustomComparator());
		
		this.exportDotFile(graphb,dir);
		this.printAdjancyList(graph);

		// /////////////////////////////
		
		DecisionResult result = new DecisionResult(graph,matrix);
		this.frcycles(result);
		
		return result;
	}

	private DecisionResult getComparabilityGraphSolution(ComparabilityResult compResults,String dir) throws FileNotFoundException {
		
		int[][] matrix = compResults.getCompareMatrix();
		
		
		System.out.println("------------------------ Comparisons Matrix ------------------------------");
		for (int i = 0; i < compResults.getServices().size(); i++) {
			for (int j = 0; j < compResults.getServices().size(); j++) {
				System.out.printf("%d ", matrix[i][j]);
			}
			System.out.println();
		}
		System.out.println("------------------------ /Comparisons Matrix ------------------------------"); 
		
		
		

		StringBuilder graphb = new StringBuilder();
		graphb.append("digraph G {\n");
		
		ArrayList<GNode> graph = new ArrayList<GNode>();
		
		for(int i = 0;i< compResults.getServices().size();i++)
		{
			GNode n = new GNode();
			n.setData(compResults.getServices().get(i));
			graph.add(n);
		}
		
		GNode n;
		for (int i = 0; i < compResults.getServices().size(); i++) {
			n=graph.get(i);
			for (int j = 0; j < compResults.getServices().size(); j++) {
				if(i<j && i != j)
				{
					GNode son = graph.get(j);
					if(matrix[i][j] == 1){
						graphb.append("" + n.getData().getMyID() + " -> " + son.getData().getMyID() + ";\n" );
//						compResults.getServices().get(i).inferior++;
//						compResults.getServices().get(i).preferableTo.add(compResults.getServices().get(j));
						
						n.setOut(n.getOut()+1);
						n.getPreferableTo().add(son);
						son.setIn(son.getIn() + 1);
					}
					else if(matrix[i][j] == -1){
					
						graphb.append("" + son.getData().getMyID() + " -> " + n.getData().getMyID() + ";\n" );
//						compResults.getServices().get(j).inferior++;
//						compResults.getServices().get(j).preferableTo.add(compResults.getServices().get(i));
						
						n.setIn(n.getIn()+1);
						son.getPreferableTo().add(n);
						son.setOut(son.getOut()+1);
					}
					else if(matrix[i][j] == 0)
					{
//						graphb.append("" + compResults.getServices().get(i).getMyID() + " ->" + compResults.getServices().get(j).getMyID() + ";\n" );
//						graphb.append("" + compResults.getServices().get(j).getMyID() + " ->" + compResults.getServices().get(i).getMyID() + ";\n" );
						
						n.getIncomparableWith().add(son);
						son.getIncomparableWith().add(n);
					}
				}	
			}
		}
		
		for(GNode res : graph)
		{
			Collections.sort(res.getIncomparableWith(), new CustomComparator());
			Collections.sort(res.getPreferableTo(), new CustomComparator());
		}
		
		graphb.append("}\n");
		
		Collections.sort(graph,new CustomComparator());
		
		this.exportDotFile(graphb,dir);
		this.printAdjancyList(graph);
		
		DecisionResult result = new DecisionResult(graph,matrix);
		this.frcycles(result);
		
		return result;
		
//		HashSet<ArrayList<GNode>> rankedList = new HashSet<ArrayList<GNode>>();
//		
//		/////////////EVERY DIFFERENT POSSIBLE PATH - DFS BASED //////////////////////////
//		
//		
//		int[] memb = new int[compResults.getServices().size()];
//		
//		dfs_paths(rankedList,memb,graph.get(0),new ArrayList<GNode>(),0);

	}
	
	public void printAdjancyList(ArrayList<GNode> graph)
	{
		System.out.println("------------------------ ADJANCY LIST  ------------------------------");
		int rank = 1;
		for(GNode so : graph)
		{
			String  s=  rank +" - {";
			s+=so.getData().getMyID() + "["+so.getIn()+"]" ;
//			System.out.println(so.getMyID());
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
		System.out.println("------------------------ /ADJANCY LIST  ------------------------------");
	}
	
	private void dfs_paths(HashSet<ArrayList<GNode>> paths,int[] visited, GNode source,ArrayList<GNode> container,int level)
	{
		int ind=Integer.parseInt(source.getData().getMyID().substring(3)) - 1;
		
		visited[ind]=1;

		container.add(source);

		for (GNode inc : source.getIncomparableWith()) {
			int indb = Integer.parseInt(inc.getData().getMyID().substring(3)) - 1;
			inc.setLevel(level);
			if (visited[indb] == 0) {
				ArrayList<GNode> newPath = new ArrayList<GNode>();

				for (int g = 0; g < container.size() - 1; g++) {
					newPath.add(container.get(g));
				}
				visited[indb] = 1;
				dfs_paths(paths, visited.clone(), inc, newPath, level);
			}
		}

		if (source.getPreferableTo().size() >= 1) {
			int idc = Integer.parseInt(source.getPreferableTo().get(0).getData().getMyID().substring(3)) - 1;
			if (visited[idc] == 0) {
				dfs_paths(paths, visited, source.getPreferableTo().get(0),container, level + 1);
			}
		}
		else {
			if (container.size() >= 1) {
				ArrayList<GNode> cp = new ArrayList<GNode>();
				for (int j = 0; j < container.size(); j++) {
					cp.add(container.get(j));
				}
				// cp.add(source);
				paths.add(cp);
			}
			container.clear();
		}
	}
	

//	private void bfs(GNode root,int NNodes)
//	{
//		// BFS uses Queue data structure
//		int level = 0;
//		Queue<ArrayList<GNode>> queue = new LinkedList<ArrayList<GNode>>();
//		Queue<ArrayList<GNode>> queueb = new LinkedList<ArrayList<GNode>>();
//		int[] visited =new int[NNodes];
//		int[] visitedInc =new int[NNodes];
//	
//		ArrayList<GNode> p = new ArrayList<GNode>();
//		p.add(root);
//
//		queue.add(p);
//		while (!queue.isEmpty()) {
//			ArrayList<GNode> path = queue.remove();
//
//			GNode node = path.get(path.size()-1);
//			if(node.getPreferableTo().size() >= 1)
//			{
//				if(level > 0)
//				{
//					for(GNode inc : node.getIncomparableWith())
//					{
//						int inc_id=Integer.parseInt(inc.getData().getMyID().substring(3)) - 1;
//						int source=Integer.parseInt(node.getData().getMyID().substring(3)) - 1;
//						if(visitedInc[inc_id]==0)
//						{
//							visitedInc[inc_id] = 1;
//							visitedInc[source] = 1;
//							inc.setLevel(level);
//							ArrayList<GNode> pathcp = new ArrayList<GNode>();
//							
//							for(int h =0;h<path.size()-1;h++)
//								pathcp.add(path.get(h));
//							
//							pathcp.add(inc);
//							System.out.println("ALT");
//							for(GNode gf : pathcp)
//								System.out.print(gf.getData().getMyID() + "["+gf.getIn()+"] ,");
//							System.out.println("\n/ALT");
//							queue.add(pathcp);
//						}
//					}
//				}
//			
//			
//				GNode nodeb= node.getPreferableTo().get(0);
//				int cid=Integer.parseInt(nodeb.getData().getMyID().substring(3)) - 1;
////				if(visited[cid]==0)
////				{
////					visited[cid]=1;
////				System.out.println("[" + level++ + "]"+ node.getData().getMyID() + "[" + node.getIn() + "]");
//				level++;
//				nodeb.setLevel(level);
//				path.add(nodeb);
//				queue.add(path);
////				}
//			}
//			else
//				queueb.add(path);
//			
//			
//			for(GNode gf : path)
//				System.out.print(gf.getData().getMyID() + "["+gf.getIn()+"] ,");
//			System.out.println();
//		}
//		System.out.println("POSSIBLE PATHS: "+queueb.size());
//		
//		while(!queueb.isEmpty())
//		{
//			for(GNode s : queueb.remove())
//			{
//				System.out.print(s.getData().getMyID() + "["+s.getLevel()+"] ,");
//			}
//			System.out.println();
//		}
//	}
//	
	

	private void exportDotFile(StringBuilder graphb, String dir) throws FileNotFoundException {
		System.out.println("------------------------ DOT-LANGUAGE ------------------------------");
		System.out.println(graphb.toString());
		
		System.out.println("------------------------ /DOT-LANGUAGE ------------------------------");
		
		File file = new File(dir+"/" + "graph.dot");
		String content = graphb.toString();
 
		try (FileOutputStream fop = new FileOutputStream(file)) {
 
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<XMCDA> WeightXMCDA(ServiceTemplate comp, ArrayList<FiltRes> alternatives, int method){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		//normalize alternatives
		this.normalizer.normalize(alternatives, comp.getCriteria());
		System.out.println("DecisionModule-Normalizer: Creating XMCDA with "+alternatives.size() +" alternatives.");
		XMCDA alte = converter.createAlternatives(alternatives);
		files.add(alte);
		XMCDA crit = converter.createCriteria(comp);
		files.add(crit);
		XMCDA weights = converter.createWeights(comp);
		files.add(weights);
		
		HashMap<String,String> critIDs = new HashMap<String,String>();
		
		for(Criterion c : comp.getCriteria())
			critIDs.put(c.getName(), c.getId());

		XMCDA alternativeValues = converter.createAlternativeValues(alternatives,critIDs,method);
		files.add(alternativeValues);
		
		return files;
	}
	
	private ArrayList<XMCDA> WeightlessXMCDA(ServiceTemplate comp, ArrayList<FiltRes> alternatives){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		this.normalizer.normalize(alternatives, comp.getCriteria());
		System.out.println("SYSTEM: Creating Weightless XMCDA with "+alternatives.size() +" alternatives.");
		XMCDA alte = converter.createAlternatives(alternatives);
		files.add(alte);
		XMCDA crit = converter.createCriteria(comp);
		files.add(crit);
		
		HashMap<String,String> critIDs = new HashMap<String,String>();
		for(Criterion c : comp.getCriteria())
			critIDs.put(c.getName(), c.getId());
		
		XMCDA alternativeValues = converter.createAlternativeValues(alternatives,critIDs, Controller.SMAA2);
		files.add(alternativeValues);
		
		return files;
	}
	
	
	@SuppressWarnings("rawtypes")
	private void frcycles(DecisionResult res) {
		// TODO Auto-generated method stub
		int[][] m = res.getAdjacencyMatrix();

		boolean[][] mcopy = new boolean[res.getAdjacencyList().size()][res.getAdjacencyList().size()];

		for (int i = 0; i < res.getAdjacencyList().size(); i++) {
			for (int j = 0; j < res.getAdjacencyList().size(); j++) {
				if (m[i][j] == 1)
					mcopy[i][j] = true;
				else if (m[i][j] == 0) {
					mcopy[i][j] = true;
					mcopy[j][i] = true;
				}
			}
		}
		

		String nodes[] = new String[res.getAdjacencyList().size()];

		for (int i = 0; i < res.getAdjacencyList().size(); i++) {
			nodes[i] = "Alt" + (i + 1);
		}
		
		
		ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(mcopy, nodes);
		List cycles = ecs.getElementaryCycles();
		for (int i = 0; i < cycles.size(); i++) {
			List cycle = (List) cycles.get(i);
			ArrayList<GNode> newInc = new ArrayList<GNode>();
			if (cycle.size() > 1) {
				for (int j = 0; j < cycle.size(); j++) {
					String node = (String) cycle.get(j);
					if (j < cycle.size() - 1) {
						System.out.print(node + " -> ");
					} else {
						System.out.print(node);
					}
					for (GNode gn : res.getAdjacencyList()) {
						if (gn.getData().getMyID().equals(node))
							newInc.add(gn);
					}
				}
				for(int q = 0;q<newInc.size();q++) {
					GNode inc = newInc.get(q);
					int index1= Integer.parseInt(inc.getData().getMyID().substring(3)) - 1;
					for(int w = 0;w< newInc.size();w++ ) {
						if(w != q) {//make the two of them incomparable
							GNode inc2 = newInc.get(w);
							int index2= Integer.parseInt(inc2.getData().getMyID().substring(3)) - 1;
							if(m[index1][index2] != 0) { // if they're not incompatible already
								m[index1][index2] = 0;
								inc.getIncomparableWith().add(inc2);
							}
						}
					}
				}
				System.out.print("\n");
			}
			else
				System.out.println((String) cycle.get(i));
		}
	}

}

class CustomComparator implements Comparator<GNode> {
    @Override
    public int compare(GNode o1, GNode o2) {
		return o1.getIn().compareTo(o2.getIn());
    }
}
