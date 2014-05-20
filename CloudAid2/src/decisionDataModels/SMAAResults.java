package decisionDataModels;

import java.util.ArrayList;


public class SMAAResults {
	private ArrayList<SMAAResult> smaaResults;
	
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

}
