package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import aggregationEngine.AggregationCore;
import csadata.CSAData;
import csadata.Criterion;
import csadata.ServiceTemplate;
import csaevaluator.CSAEvaluator;
import decisionDataModels.DecisionResult;
import decisionDataModels.DistancesContainer;
import decisionDataModels.GNode;
import decisionEngine.DecisionCore;
import exceptions.InvalidLinkedUSDLModelException;
import searchDataModels.FiltRes;
import searchDataModels.PricingVariables;
import searchEngine.SearchCore;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;

public class Controller {
	public static String ClientPath="";
	private SearchCore searchModule;
	private DecisionCore decisionModule;
	private AggregationCore aggregationModule;
	private CSAEvaluator CSAEval;
	// decision Method codes
	public static final int SAW = 0;
	public static final int ELECTRE = 1;
	public static final int PROMETHEE = 2;
	public static final int SMAA2 = 3;
	private static final String[] methods = {"SAW","ELECTRE","PROMETHEE","SMAA"};
	public Controller(String cp)
	{
		searchModule = new SearchCore();
		decisionModule = new DecisionCore();
		aggregationModule = new AggregationCore();
		ClientPath = cp;
//		searchModule.searchy();
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private String readJSONRequests() throws IOException, InterruptedException
	{

		Path faxFolder = Paths.get("./JSONRequests/");
	    WatchService watchService = FileSystems.getDefault().newWatchService();
	    faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
	 
	    System.out.println("Waiting for requests!");
	    boolean valid = true;
	    do {
	      WatchKey watchKey = watchService.take();
	      for (WatchEvent<?> event : watchKey.pollEvents()) {
			WatchEvent.Kind kind = event.kind();
	        
	        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
	          String fileName = event.context().toString();
	          System.out.println("File Created:" + fileName);
	          return FileUtils.readFileToString(new File("./JSONRequests/" + fileName));
	        }
	      }
	      System.out.println(valid);
	      valid = watchKey.reset();
	      
	    } while (valid);
	    
	    return null;
	}
	
	public void run() throws IOException, InterruptedException
	{
		// obtained Gson object   
		Gson gson = new Gson();  

		String json = this.readJSONRequests();
		CSAData   data = gson.fromJson(json, CSAData.class);
		
		for(ServiceTemplate st : data.getServiceTemplates())
		{
			System.out.println("C:  "+st.getCriteria().size());
			for(Criterion c : st.getCriteria())
			{
				System.out.println(c.getName());
			}
			System.out.println("R:  "+st.getRequirements().size());
		}
		CSAEval = new CSAEvaluator(data.getMethod());
		
		data = CSAEval.evaluator(data);
		
		
		
		boolean error = false;
		if(data != null){
			if(data.getEvalResult() == CSAEvaluator.OK){
				ArrayList<ArrayList<FiltRes>> foundOffs = new ArrayList<ArrayList<FiltRes>>();//save the found offerings of each service template
				
				ArrayList<DecisionResult> STGraphs = new ArrayList<DecisionResult>();
				String STName="";
				String dir = "./XMCDA/"+methods[data.getMethod()]+"/"+System.currentTimeMillis()+"/";
				for(ServiceTemplate st : data.getServiceTemplates())//Search Module
				{
					STName = st.getName();
					System.out.println("#######################################################----"+st.getName()+"----################################################################");
					ArrayList<FiltRes> offs=null;
					try {
						offs = searchModule.searchOfferings(st,data.getRequirements());
					} catch (InvalidLinkedUSDLModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(offs == null || offs.size() == 0)
					{
						error = true;
						break;
					}
					
					foundOffs.add(offs);
					System.out.println("----------------------------------Found "+offs.size() + " possible offerings!  ----------------------------------");
					for(FiltRes r : offs)
					{
						System.out.println(r.getMyOff().getName().replaceAll("TIME\\d+.*", "") + " - " + r.getMyPrice());
						for(QuantitativeValue qv : r.getMyOff().getIncludes().get(0).getQuantfeatures())
						{
							System.out.println(qv.getTypes().get(0) + " --->" +qv.getValue());
						}
						for(QualitativeValue qv : r.getMyOff().getIncludes().get(0).getQualfeatures())
						{
							System.out.println(qv.getTypes().get(0) + " --->" +qv.getHasValue());
						}
					}
					
					st.setFoundAlternatives(offs);
					DecisionResult  decisionResult = decisionModule.decide(st, data.getMethod(),dir+st.getName());
					
					STGraphs.add(decisionResult);
				}
				
				ArrayList<ArrayList<GNode>> aggregatedSolutions = new ArrayList<ArrayList<GNode>>();
				if(error == false)
				{
						aggregatedSolutions = aggregationModule.computeAggregation(data,STGraphs);
				}
				else if(error == true)
					System.out.println("[Controller] Service Template: " + STName + " returned 0 results. Please, reconsider your parameters.");
				
				if(aggregatedSolutions.size() >= 1) {
					System.out.println("[Controller] Aggregated Solutions found:");
					for(ArrayList<GNode> solution : aggregatedSolutions) {
						aggregationModule.printSolution(solution);
					}
				}
				else {
					System.out.println("[Controller] No Aggregated Solutions found, please reconsider your parameters..!");
				}
			}
			else if(data.getEvalResult() == CSAEvaluator.ERROR_1){
				System.out.println("There are no Service Templates in your cloud architecture. Please reconcider your options.");
			}
		}
	}	
	
	public static void main(String[] args) throws InterruptedException
	{
		String ClientPath="C:/Users/daniel/workspace/CloudAid2-GUI";
		Controller cont = new Controller(ClientPath);
		try {
			cont.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	public static PricingVariables requestVariablesInfo(PricingVariables variables) {
		String directoryToWrite = ClientPath+"/JSONRequests-Variables";
		// obtained Gson object
		Gson gson = new Gson();

		// called toJson() method and passed student object as parameter
		// print generated json to console

		String json = gson.toJson(variables);
		System.out.println(json);
		
		try {
			FileUtils.writeStringToFile(new File(directoryToWrite + "/Variables"+"-"+System.nanoTime()+".json"), json);
			System.out.println("[Controller] Wrote request for variables!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Path faxFolder = Paths.get("./JSONPricingVariables/");
	    WatchService watchService = null;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	    System.out.println("[Controller] Waiting for variables values!");
	    boolean valid = true;
	    do {
	      WatchKey watchKey = null;
		try {
			watchKey = watchService.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      for (WatchEvent<?> event : watchKey.pollEvents()) {
			WatchEvent.Kind kind = event.kind();
	        
	        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
	          String fileName = event.context().toString();
	          System.out.println("File Created:" + fileName);
	          try {
				return gson.fromJson(FileUtils.readFileToString(new File("./JSONPricingVariables/" + fileName)), PricingVariables.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      }
	      valid = watchKey.reset();
	      
	    } while (valid);
		
		
	    return null;
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	public static DistancesContainer requestDistancesInfo(DistancesContainer distancesRequest) {
		
		String directoryToWrite = ClientPath+"/JSONRequests-ConceptDistances";
		// obtained Gson object
		Gson gson = new Gson();

		// called toJson() method and passed student object as parameter
		// print generated json to console

		String json = gson.toJson(distancesRequest);
		System.out.println(json);
		
		try {
			FileUtils.writeStringToFile(new File(directoryToWrite + "/Concepts"+"-"+System.nanoTime()+".json"), json);
			System.out.println("[Controller] Wrote request for concept distances!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Path faxFolder = Paths.get("./JSONConceptDistances/");
	    WatchService watchService = null;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	    System.out.println("[Controller] Waiting for concept distances!");
	    boolean valid = true;
	    do {
	      WatchKey watchKey = null;
		try {
			watchKey = watchService.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      for (WatchEvent<?> event : watchKey.pollEvents()) {
			WatchEvent.Kind kind = event.kind();
	        
	        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
	          String fileName = event.context().toString();
	          System.out.println("File Created:" + fileName);
	          try {
				return gson.fromJson(FileUtils.readFileToString(new File("./JSONConceptDistances/" + fileName)), DistancesContainer.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      }
	      valid = watchKey.reset();
	      
	    } while (valid);

		return null;
	}
}

