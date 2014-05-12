package csadata;

public class Criterion{
	
	private static int criterionCount = 0;
	
	private final String id;
	private String name;
	private double weight;
	private String preferenceDirection;
	private String preference;
	private int type = -1;
	private double indifference_threshold=-1;
	private double preference_threshold=-1;
	private double veto_threshold=-1;
	
	public Criterion(){
		this.id = "Crit"+criterionCount++;
	}
	
	public Criterion(String name){
		this.id = "Crit"+criterionCount++;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double wheight) {
		this.weight = wheight;
	}
	

	public String getPreferenceDirection() {
		return preferenceDirection;
	}

	public void setPreferenceDirection(String preferenceDirection) {
		this.preferenceDirection = preferenceDirection;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Criterion [id=" + id + ", name=" + name + ", wheight="
				+ weight + ", preferenceDirection=" + preferenceDirection
				+ ", preference=" + preference + ", type=" + type + "]";
	}
	
	public double getIndifference_threshold() {
		return indifference_threshold;
	}

	public void setIndifference_threshold(double indifference_threshold) {
		this.indifference_threshold = indifference_threshold;
	}

	public double getPreference_threshold() {
		return preference_threshold;
	}

	public void setPreference_threshold(double preference_threshold) {
		this.preference_threshold = preference_threshold;
	}

	public double getVeto_threshold() {
		return veto_threshold;
	}

	public void setVeto_threshold(double veto_threshold) {
		this.veto_threshold = veto_threshold;
	}

	public boolean isNumerical(){
		if(this.type == 0)
			return true;
		
		return false;
	}
	
	public boolean hasPreference(){
		if(this.preference != null)
			return true;
		
		return false;
	}

}

