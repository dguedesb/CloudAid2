package csadata;

import java.util.ArrayList;
import java.util.List;

public class CSAData {
	
	private List<ServiceTemplate> serviceTemplates;
	private List<Requirement> requirements;
	private int evalCode; // 0 = OK, 1 = Not enough components
	private int method;
	private String name="";
	


	public CSAData(){
		this.serviceTemplates = new ArrayList<ServiceTemplate>();
		this.requirements = new ArrayList<Requirement>();
	}
	
	public CSAData(ArrayList<ServiceTemplate> serviceTemplates, ArrayList<Requirement> requirements, ArrayList<Criterion> criteria) {
		super();
		this.serviceTemplates = serviceTemplates;
		this.requirements = requirements;
	}
	
	public List<ServiceTemplate> getServiceTemplates() {
		return serviceTemplates;
	}
	public void setServiceTemplates(ArrayList<ServiceTemplate> components) {
		this.serviceTemplates = components;
	}
	public List<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	
	public void addComponent(ServiceTemplate comp){
		this.serviceTemplates.add(comp);
	}
	
	public void addReq(Requirement req){
		this.requirements.add(req);
	}

	public int getEvalResult() {
		return evalCode;
	}

	public void setEvalResult(int evalCode) {
		this.evalCode = evalCode;
	}
	
	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
