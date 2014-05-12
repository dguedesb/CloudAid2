package decisionDataModels;

import searchDataModels.FiltRes;

public class SAWResult implements Comparable<SAWResult>{
	
	private FiltRes service;
	private double performance;
	
	public SAWResult(FiltRes serv, double perf){
		this.service = serv;
		this.performance = perf;
	}

	public FiltRes getService() {
		return service;
	}

	public void setService(FiltRes service) {
		this.service = service;
	}

	public double getPerformance() {
		return performance;
	}

	public void setPerformance(double performance) {
		this.performance = performance;
	}

	@Override
	public String toString() {
		return "Result [service=" + service + ", performance=" + performance
				+ "]";
	}

	@Override
	public int compareTo(SAWResult r) {
		return new Double(this.performance).compareTo(new Double(r.performance));
	}

	
}
