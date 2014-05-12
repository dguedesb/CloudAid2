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
import java.util.Arrays;



import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import aggregationEngine.AggregationCore;
import csadata.CSAData;
import csadata.Criterion;
import csadata.ServiceTemplate;
import csaevaluator.CSAEvaluator;
import decisionDataModels.ComparabilityResult;
import decisionDataModels.DistancesContainer;
import decisionDataModels.SAWResult;
import decisionDataModels.SAWResults;
import decisionDataModels.SMAAResult;
import decisionDataModels.SMAAResults;
import decisionEngine.DecisionCore;
import exceptions.InvalidLinkedUSDLModelException;
import searchDataModels.FiltRes;
import searchDataModels.PricingVariables;
import searchEngine.SearchCore;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;

public class Controller {
	
	// User Interface Data Codes
	public static final int GET_WEIGHT = 100;
	public static final int GET_PREFERENCE_DIRECTION = 101;
	public static final int GET_PREFERENCE_VALUE = 102;
	public static final int GET_DISTANCE_VALUE = 103;
	public static final int GET_YESNO_ANSWER = 200;
	public static final int PROMPT = 300;
	public static final int PRINTCSA = 301;
	public static final int PRINTALTDATA = 302;
	public static final int PRINTRESULTLIST = 303;

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
	public Controller()
	{
		searchModule = new SearchCore();
		decisionModule = new DecisionCore();
		aggregationModule = new AggregationCore();
//		searchModule.searchy();
	}
	
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
				
				ArrayList<ComparabilityResult> decisionComparabilityResults = null;
				ArrayList<SAWResults> decisionSAWResults = null;
				ArrayList<SMAAResults> decisionSMAAResults = null;
				
				if(data.getMethod() == Controller.ELECTRE ||  data.getMethod() == Controller.PROMETHEE)
				{
					decisionComparabilityResults =  new ArrayList<ComparabilityResult>();
				}
				else if(data.getMethod() == Controller.SMAA2)
				{
					decisionSMAAResults = new ArrayList<SMAAResults>();
				}
				else if(data.getMethod() == Controller.SAW)
				{
					decisionSAWResults = new ArrayList<SAWResults>();
				}
				
				String dir = "./XMCDA/"+methods[data.getMethod()]+"/"+System.currentTimeMillis()+"/";
				for(ServiceTemplate st : data.getServiceTemplates())//Search Module
				{
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
					//DECISION MODULE
					st.setFoundAlternatives(offs);
					Object decisionResult = decisionModule.decide(st, data.getMethod(),dir+st.getName());
					
					if(data.getMethod() == Controller.ELECTRE || data.getMethod() == Controller.PROMETHEE)
					{
						ComparabilityResult compResults =(ComparabilityResult)decisionResult;
						decisionComparabilityResults.add(compResults);
						
					}
					else if(data.getMethod() == Controller.SMAA2)
					{
						SMAAResults smaaResults = (SMAAResults)decisionResult;
						decisionSMAAResults.add(smaaResults);
						for(SMAAResult res : smaaResults.getSmaaResults())
							System.out.println(res.getService().getMyOff().getName() + "\nRanks:  "+Arrays.toString(res.getRanks()));
						
					}
					else if(data.getMethod() == Controller.SAW)
					{
						SAWResults sawResults = (SAWResults)decisionResult;
						decisionSAWResults.add(sawResults);
						for(SAWResult res : sawResults.getSAWResults())
							System.out.println(res.getService().getMyOff().getName() + " - " + res.getPerformance() + " - "  + res.getService().getMyPrice());
					}
				}
				
				if(error == false)
				{
					if(data.getMethod() == Controller.ELECTRE || data.getMethod() == Controller.PROMETHEE)
						aggregationModule.computeAggregation(data,decisionComparabilityResults);
					else if(data.getMethod() == Controller.SMAA2)
						aggregationModule.computeAggregation(data,decisionSMAAResults);
					else if(data.getMethod() == Controller.SAW)
						aggregationModule.computeAggregation(data,decisionSAWResults);
				}
			}
			else if(data.getEvalResult() == CSAEvaluator.ERROR_1){
				System.out.println("There are no Service Templates in your cloud architecture. Please reconcider your options.");
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public static String askData(int code, String[] msg, Object data){
//		switch(code){
//		case PROMPT:
//			frontend.getCi().prompt(msg[0]);
//			return null;
////		case PRINTCSA:
////			frontend.getCi().printResults((CSAData) data);
////			return null;
//		case GET_WEIGHT:
//			return frontend.getCi().askforCriterionWeight(msg[0], msg[1]);
//		case GET_PREFERENCE_DIRECTION:
//			return frontend.getCi().askforCritPrefDirection(msg[0], msg[1]);
//		case GET_YESNO_ANSWER:
//			return frontend.getCi().promptYesNo(msg[0]);
//		case GET_PREFERENCE_VALUE:
//			return frontend.getCi().askforPreferenceValue(msg[0]);
//		case GET_DISTANCE_VALUE:
//			return frontend.getCi().askforDistance(msg[0], msg[1]);
//		case PRINTALTDATA:
//			frontend.getCi().printAlternativesData((ArrayList<FiltRes>)data);
//			return null;
////		case PRINTRESULTLIST:
////			frontend.getCi().printResultList(msg[0], (ArrayList<Result>) data);
////			return null;
//		default:
//			System.out.println("ERROR: Unrecognized interface code!!!");
//			return null;
//		}
//	}
	
	
	public static void main(String[] args) throws InterruptedException
	{
		Controller cont = new Controller();
		try {
			cont.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static PricingVariables requestVariablesInfo(PricingVariables variables) {
		String directoryToWrite = "C:/Users/daniel/workspace/CloudAid2-Client/JSONRequests-Variables";
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

	public static DistancesContainer requestDistancesInfo(DistancesContainer distancesRequest) {
		
		String directoryToWrite = "C:/Users/daniel/workspace/CloudAid2-Client/JSONRequests-ConceptDistances";
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

