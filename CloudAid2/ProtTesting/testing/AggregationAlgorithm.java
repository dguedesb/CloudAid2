package testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import csadata.CSAData;
import searchDataModels.FiltRes;
import aggregationEngine.AggregationCore;
import decisionDataModels.DecisionResult;
import decisionDataModels.SMAAResult;
import decisionDataModels.SMAAResults;
import decisionEngine.DecisionCore;

public class AggregationAlgorithm {
	
	private static int NALTERNATIVES = 10;
	private static int MINCOST = 50;
	private static int MAXCOST = 500;
	private static int NST = 3;
	
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		DecisionCore decisionModule = new DecisionCore();
		AggregationCore aggregationModule = new AggregationCore();
		ArrayList<DecisionResult> sts = new ArrayList<DecisionResult>();
		String dir = "./Testing/TestingResults"+"/"+System.currentTimeMillis()+"/";
		for(int k=0;k<NST;k++) {
			SMAAResults fict = getFictSMAARes();
			FiltRes.resetIDCounter();
			double delta = 4;
			
			new File(dir +k + "/").mkdirs();
			DecisionResult dr = decisionModule.getSMAAGraphSolution(fict, delta, dir + k + "/");
			sts.add(dr);
		}
		
		String filename = "CSA_GR-PRICE.json";
		
		CSAData csa =  new Gson().fromJson(FileUtils.readFileToString(new File("./Testing/TestingData/" + filename)), CSAData.class);
		
		aggregationModule.computeAggregation(csa,sts);
		
		System.out.println("Time: " + aggregationModule.AlgorithmRunningTime() * 0.001 + "  seconds.");
		System.out.println("Nº of total combinations:  "+aggregationModule.getPerformedCombinations());
	}


	private static SMAAResults getFictSMAARes() {
		ArrayList<SMAAResult> list = new ArrayList<SMAAResult>();
		
		for(int i=0;i<NALTERNATIVES;i++) {
			
			double[] prob = new double[NALTERNATIVES];
			for(int k=0;k<NALTERNATIVES;k++) {
				Random r = new Random();
				prob[k]=r.nextFloat() * 100;
			}
			
			FiltRes alt = new FiltRes(null,new Random().nextInt(MAXCOST)+MINCOST);
			alt.getCritAttr().put("price", Double.toString(alt.getMyPrice()));
			
			list.add(new SMAAResult(alt,prob));
		}
		
		return new SMAAResults(list);
	}
}
