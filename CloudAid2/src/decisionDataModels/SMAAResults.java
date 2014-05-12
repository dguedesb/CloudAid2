package decisionDataModels;

import java.util.ArrayList;

import searchDataModels.FiltRes;

public class SMAAResults {
	private ArrayList<SMAAResult> smaaResults;
	
	private ArrayList<ArrayList<FiltRes>> rankedList;
	
	public SMAAResults(ArrayList<SMAAResult> results)
	{
		this.setSmaaResults(results);
	}
	

	public ArrayList<SMAAResult> getSmaaResults() {
		return smaaResults;
	}

	public void setSmaaResults(ArrayList<SMAAResult> smaaResults) {
		this.smaaResults = smaaResults;
	}


	public ArrayList<ArrayList<FiltRes>> getRankedList() {
		return rankedList;
	}


	public void setRankedList(ArrayList<ArrayList<FiltRes>> rankedList) {
		this.rankedList = rankedList;
	}
}
