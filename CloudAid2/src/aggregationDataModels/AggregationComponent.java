package aggregationDataModels;


import java.util.List;

public class AggregationComponent {
	private List<String> features;
	private List<String> featureValues;
	
	public List<String> getFeatureValues() {
		return featureValues;
	}
	public void setFeatureValues(List<String> featureValues) {
		this.featureValues = featureValues;
	}
	public List<String> getFeatures() {
		return features;
	}
	public void setFeatures(List<String> features) {
		this.features = features;
	}

}
