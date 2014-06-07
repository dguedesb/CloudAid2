package decisionDataModels;

import java.util.List;

public class DistancesContainer {
	
	private String prefered;
	private List<ConceptDistance> concepts = null;
	private String CriterionType;
	public String getCriterionType() {
		return CriterionType;
	}
	public void setCriterionType(String criterionType) {
		CriterionType = criterionType;
	}
	public String getPrefered() {
		return prefered;
	}
	public void setPrefered(String prefered) {
		this.prefered = prefered;
	}
	public List<ConceptDistance> getConcepts() {
		return concepts;
	}
	public void setConcepts(List<ConceptDistance> concepts) {
		this.concepts = concepts;
	}

}
