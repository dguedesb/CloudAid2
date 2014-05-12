package decisionDataModels;

import java.util.ArrayList;

import searchDataModels.FiltRes;

public class SAWResults {
	private ArrayList<SAWResult> sawResults;

	private ArrayList<ArrayList<FiltRes>> rankedList;

	
	
	public SAWResults(ArrayList<SAWResult> results)
	{
		this.setSAWResults(results);
	}
	
	public ArrayList<ArrayList<FiltRes>> getRankedList() {
		return rankedList;
	}

	public void setRankedList(ArrayList<ArrayList<FiltRes>> rankedList) {
		this.rankedList = rankedList;
	}

	public ArrayList<SAWResult> getSAWResults() {
		return sawResults;
	}

	public void setSAWResults(ArrayList<SAWResult> sawResults) {
		this.sawResults = sawResults;
	}
}
