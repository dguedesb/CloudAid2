package decisionEngine;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import controller.Controller;
import searchDataModels.FiltRes;
import csadata.Criterion;
import csadata.ServiceTemplate;
import decisionDataModels.ComparabilityResult;
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
	
	public Object decide(ServiceTemplate comp,  int method,String dir) throws FileNotFoundException{
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
	
	
	private Object postXMCDA(ArrayList<XMCDA> files, ServiceTemplate comp, int method,String dir) throws FileNotFoundException{
		//attach timestamp in the method messages

		if(method == Controller.SAW)
		{
//			ArrayList<Result> results =SAW.solve(files);
			XMCDA results = SAW.solve(files,dir);
			SAWResults sawResults =XMCDAConverter.processSAWResults(results,comp);
			
			Collections.sort(sawResults.getSAWResults(), new Comparator<SAWResult>() {
			    @Override
			    public int compare(SAWResult c1, SAWResult c2) {
			        return Double.compare(c1.getPerformance(), c2.getPerformance());
			    }
			});
			
			this.getSAWRankedList(sawResults);
			
			this.printRankedList(sawResults.getRankedList());
			
			return sawResults;
		}
		else if(method == Controller.PROMETHEE)
		{
			XMCDA results = PROMETHEE.solve(files,dir);
			ComparabilityResult prometheeResults =XMCDAConverter.processComparabilityResults(results,comp);
			
			this.getComparabilityRankedList(prometheeResults,dir);
			
//			this.printRankedList(prometheeResults.getRankedList());
			
			return prometheeResults;
		}
		else if(method == Controller.ELECTRE)
		{
			XMCDA results = ELECTRE.solve(files,dir);
			ComparabilityResult electreResults =XMCDAConverter.processComparabilityResults(results,comp);
			
			this.getComparabilityRankedList(electreResults,dir);
//			this.printRankedList(electreResults.getRankedList());
			
			return electreResults;
		}
		else if(method == Controller.SMAA2)
		{
			XMCDA results = SMAA.solve(files,dir);
			SMAAResults smaaResults =XMCDAConverter.processSMAAResults(results,comp);
			
			this.getSMAARankedList(smaaResults);
//			this.printRankedList(smaaResults.getRankedList());
			return smaaResults;
		}
		
		return null;
	}
	
	private void getSAWRankedList(SAWResults sawResults)
	{
		ArrayList<ArrayList<FiltRes>> rankedList = new ArrayList<ArrayList<FiltRes>>();
		for (int i=0; i < sawResults.getSAWResults().size(); i++) {
			rankedList.add(new ArrayList<FiltRes>());
		}
		
		for(int k = 0 ; k<sawResults.getSAWResults().size();k++)
		{
			rankedList.get(k).add(sawResults.getSAWResults().get(k).getService());
		}
		
		sawResults.setRankedList(rankedList);
	}
	
	private void getSMAARankedList(SMAAResults smaaResults) {
		
		ArrayList<ArrayList<FiltRes>> rankedList = new ArrayList<ArrayList<FiltRes>>();
		for (int i=0; i < smaaResults.getSmaaResults().size(); i++) {
			rankedList.add(new ArrayList<FiltRes>());
		}
		
		for(SMAAResult res : smaaResults.getSmaaResults())
		{
			double[] ranks = res.getRanks();
			
			double max = ranks[0];
			int index = 0;
			for(int i = 1; i<ranks.length;i++)//find the rank with highest probability 
			{
				if(ranks[i] > max)
					index = i;
			}
			rankedList.get(index).add(res.getService());//place it on rankedList
		}
		
		smaaResults.setRankedList(rankedList);
	}

	private void getComparabilityRankedList(ComparabilityResult compResults,String dir) throws FileNotFoundException {
		
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
		for (int i = 0; i < compResults.getServices().size(); i++) {
			for (int j = 0; j < compResults.getServices().size(); j++) {
				if(i<j )
				{
					if(matrix[i][j] == 1){
					
						graphb.append("" + compResults.getServices().get(i).getMyID() + " -> " + compResults.getServices().get(j).getMyID() + ";\n" );
						compResults.getServices().get(i).inferior++;
					}
					else if(matrix[i][j] == -1){
					
						graphb.append("" + compResults.getServices().get(j).getMyID() + " -> " + compResults.getServices().get(i).getMyID() + ";\n" );
						compResults.getServices().get(j).inferior++;
					}
					else if(matrix[i][j] == 0)
					{
//						graphb.append("" + compResults.getServices().get(i).getMyID() + " ->" + compResults.getServices().get(j).getMyID() + ";\n" );
//						graphb.append("" + compResults.getServices().get(j).getMyID() + " ->" + compResults.getServices().get(i).getMyID() + ";\n" );
						compResults.getServices().get(i).incomparableWith.add(compResults.getServices().get(j));
						compResults.getServices().get(j).incomparableWith.add(compResults.getServices().get(i));
					}
				}	
			}
		}
		
		for(FiltRes res : compResults.getServices())
			Collections.sort(res.incomparableWith, Collections.reverseOrder(new CustomComparator()));
		
		
		graphb.append("}\n");
		
		Collections.sort(compResults.getServices(), Collections.reverseOrder(new CustomComparator()));
		
		this.exportDotFile(graphb,dir);
		
		
		System.out.println("------------------------ RANKING  ------------------------------");
		int rank = 1;
		for(FiltRes so : compResults.getServices())
		{
			String  s=  rank +" - {";
			s+=so.getMyID() + "["+so.inferior+"]" ;
//			System.out.println(so.getMyID());
			int k;
			for(k=0;k<so.incomparableWith.size();k++)
			{
					s = s + ",";
					s+= so.incomparableWith.get(k).getMyID()+ "["+so.incomparableWith.get(k).inferior+"]";
					
			}
			
			s+="} ";
			System.out.println(s);
			rank++;
		}
		System.out.println("------------------------ /RANKING  ------------------------------");
		
		ArrayList<ArrayList<FiltRes>> rankedList = new ArrayList<ArrayList<FiltRes>>();
		for (int i=0; i < compResults.getServices().size(); i++) {
			rankedList.add(new ArrayList<FiltRes>());
		}
		
		int[] mem = new int[compResults.getServices().size()];
		
		for(int z=0;z<compResults.getServices().size();z++)
		{
			int ID =Integer.parseInt(compResults.getServices().get(z).getMyID().substring(3)) - 1;
			if(mem[ID] == 0 )
			{
				rankedList.get(z).add(compResults.getServices().get(z));
				mem[ID]=1;
			}
			for(int k=0;k<compResults.getServices().get(z).incomparableWith.size();k++)
			{
				int IDinc =Integer.parseInt(compResults.getServices().get(z).incomparableWith.get(k).getMyID().substring(3)) - 1;
				if (mem[IDinc]==0) {
					rankedList.get(z).add(compResults.getServices().get(z).incomparableWith.get(k));
					mem[IDinc]=1;
				}
			}
		}
		
		compResults.setRankedList(rankedList);
	}
	
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

	private void printRankedList(ArrayList<ArrayList<FiltRes>> rankedList)
	{
		System.out.println("------------------------ LIST  ------------------------------");
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
		System.out.println("------------------------ /LIST  ------------------------------");
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
}

class CustomComparator implements Comparator<FiltRes> {
    @Override
    public int compare(FiltRes o1, FiltRes o2) {
		return o1.inferior.compareTo(o2.inferior);
    }
}
