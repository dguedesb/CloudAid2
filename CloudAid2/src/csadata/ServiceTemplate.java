package csadata;

import java.util.ArrayList;
import java.util.HashMap;

import searchDataModels.FiltRes;

public class ServiceTemplate {
	
	private static int componentCount = 0;
	private final String id;
	private String name;
	private String description;
	private ArrayList<Requirement> requirements;
//	private ArrayList<Result> resultList;
	private ArrayList<Criterion> criteria;
	private float weight;
	private ArrayList<FiltRes> foundAlternatives=null;
	private HashMap<String,FiltRes> foundAlternativesMap = null;




	public ServiceTemplate(){
		this.id = "ST"+componentCount++;
		requirements = new ArrayList<Requirement>();
		criteria = new ArrayList<Criterion>();
	}
	
	public ServiceTemplate(String id, String name, String description) {
		super();
		requirements = new ArrayList<Requirement>();
		criteria = new ArrayList<Criterion>();
		this.id = "ST"+componentCount++;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	} 
	
	public void addReq(Requirement req){
		this.requirements.add(req);
	}
	
//	public ArrayList<Result> getResultList() {
//		return resultList;
//	}
//	public void setResultList(ArrayList<Result> resultList) {
//		this.resultList = resultList;
//	}
//	
//	public void addResult(Result res){
//		this.resultList.add(res);
//	}
	
	public ArrayList<Criterion> getCriteria() {
		return criteria;
	}
	public void setCriteria(ArrayList<Criterion> criteria) {
		this.criteria = criteria;
	}
	
	public void addCrit(Criterion crit){
		this.criteria.add(crit);
	}
	public String getId() {
		return id;
	}
	
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public ArrayList<FiltRes> getFoundAlternatives() {
		return foundAlternatives;
	}

	public void setFoundAlternatives(ArrayList<FiltRes> foundAlternatives) {
		this.foundAlternativesMap = new HashMap<String,FiltRes>();
		this.foundAlternatives = foundAlternatives;
		
		for(FiltRes alt : foundAlternatives)
			foundAlternativesMap.put(alt.getMyID(), alt);
	}
	
	public HashMap<String, FiltRes> getFoundAlternativesMap() {
		return foundAlternativesMap;
	}


	@Override
	public String toString() {
		return "ServiceTemplate [id=" + id + ", name=" + name
				+ ", description=" + description + ", requirements="
				+ requirements +  ", criteria="
				+ criteria + ", weight=" + weight + "]";
	}

	

}

