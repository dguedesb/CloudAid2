package searchDataModels;

public class PriceVariable {
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private double val=-1;
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
	
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	private String details;
}
