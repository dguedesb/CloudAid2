package decisionDataModels;



import searchDataModels.FiltRes;

public class SMAAResult{
	
	private FiltRes service;
	private double[] ranks;


	public SMAAResult(FiltRes serv, double[] ranks){
		this.service = serv;
		this.ranks = ranks;
	}

	public FiltRes getService() {
		return service;
	}

	public void setService(FiltRes service) {
		this.service = service;
	}
	
	public double[] getRanks() {
		return ranks;
	}

	public void setRanks(double[] ranks) {
		this.ranks = ranks;
	}


	@Override
	public String toString() {
		return "Result [service=" + service + ", performance=" + ranks.toString()
				+ "]";
	}
}

