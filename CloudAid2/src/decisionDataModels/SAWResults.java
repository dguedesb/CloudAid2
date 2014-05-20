package decisionDataModels;

import java.util.ArrayList;


public class SAWResults {
	private ArrayList<SAWResult> sawResults;

	public SAWResults(ArrayList<SAWResult> results)
	{
		this.setSAWResults(results);
	}

	public ArrayList<SAWResult> getSAWResults() {
		return sawResults;
	}

	public void setSAWResults(ArrayList<SAWResult> sawResults) {
		this.sawResults = sawResults;
	}

}
