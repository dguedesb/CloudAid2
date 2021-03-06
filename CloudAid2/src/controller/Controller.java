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
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import aggregationDataModels.AggregatedSolution;
import aggregationDataModels.AggregationComponent;
import aggregationDataModels.AggregationSolutions;
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
	private SearchCore searchModule;
	private DecisionCore decisionModule;
	private AggregationCore aggregationModule;
	private CSAEvaluator CSAEval;
	// decision Method codes
	public static final int SAW = 0;
	public static final int ELECTRE = 1;
	public static final int PROMETHEE = 2;
	public static final int SMAA2 = 3;
	
	private static String ClientPath="C:/Users/daniel/git/CloudAid2-GUI/CloudAid2-GUI";
	private static final String[] methods = {"SAW","ELECTRE","PROMETHEE","SMAA"};
	public Controller(String cp)
	{
		searchModule = new SearchCore();
		decisionModule = new DecisionCore();
		aggregationModule = new AggregationCore();
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
					sendResults(aggregatedSolutions);
				}
				else {
					System.out.println("[Controller] No Aggregated Solutions found, please reconsider your parameters..!");
					sendResults(null);
				}
			}
			else if(data.getEvalResult() == CSAEvaluator.ERROR_1){
				System.out.println("There are no Service Templates in your cloud architecture. Please reconcider your options.");
			}
		}
	}	

	public static void main(String[] args) throws InterruptedException
	{
		Controller cont = new Controller("");
		try {
			cont.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void sendResults(ArrayList<ArrayList<GNode>> aggregatedSolutions) {
		// TODO Auto-generated method stub
		AggregationSolutions res=new AggregationSolutions();
		if(aggregatedSolutions != null)
			res = createAggregationJSONModels(aggregatedSolutions);
		
		String directoryToWrite = ClientPath+"/JSONRequests-Results";
//		String directoryToWrite = "./[Client]JSONRequests-Results";
		
		Gson gson = new Gson();

		// called toJson() method and passed student object as parameter
		// print generated json to console

		String json = gson.toJson(res);
		System.out.println(json);
		
		try {
			FileUtils.writeStringToFile(new File(directoryToWrite + "/Results"+"-"+System.nanoTime()+".json"), json);
			System.out.println("[Controller] Wrote JSON Results!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private AggregationSolutions createAggregationJSONModels(ArrayList<ArrayList<GNode>> aggregatedSolutions) {
		
		AggregationSolutions solsj = new AggregationSolutions();
		
		List<AggregatedSolution> solutionContainer = new ArrayList<AggregatedSolution>();
		
		for(ArrayList<GNode> sol : aggregatedSolutions) {
			
			List<AggregationComponent> aggregatedSol = new ArrayList<AggregationComponent>();
			for(GNode alt : sol) {
				AggregationComponent comp = new AggregationComponent();
				List<String> features = new ArrayList<String>();
				List<String> fvals = new ArrayList<String>();
				
				features.add("Name");
				fvals.add(alt.getData().getMyOff().getName().replaceAll("TIME\\d+.*", "") );
				
				features.add("Comment");
				fvals.add(alt.getData().getMyOff().getComment() );
				
				features.add("Price");
				fvals.add(String.valueOf(alt.getData().getMyPrice()));
				
				for(QuantitativeValue v : alt.getData().getMyOff().getIncludes().get(0).getQuantfeatures()){
					features.add(v.getTypes().get(0));
					if(v.getValue() >= 0)
						fvals.add(""+v.getValue());
					else if(v.getMaxValue() >= 0)
						fvals.add(""+v.getMaxValue());
					else if(v.getMinValue() >= 0)
						fvals.add(""+v.getMinValue());
				}
				
				for(QualitativeValue v : alt.getData().getMyOff().getIncludes().get(0).getQualfeatures()){
					features.add(v.getTypes().get(0));
					fvals.add(v.getHasValue());
				}
				
				comp.setFeatures(features);
				comp.setFeatureValues(fvals);
				aggregatedSol.add(comp);
			}
			
			AggregatedSolution solution = new AggregatedSolution();
			solution.setComponents(aggregatedSol);
			solutionContainer.add(solution);
		}
		solsj.setSolutions(solutionContainer);
		
		return solsj;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public static PricingVariables requestVariablesInfo(PricingVariables variables) {
		String directoryToWrite = ClientPath+"/JSONRequests-Variables";
//		String directoryToWrite = "./[Client]JSONRequests-Variables";
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
//		String directoryToWrite = "./[Client]JSONRequests-ConceptDistances";
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

