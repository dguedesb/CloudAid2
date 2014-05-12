/* Copyright (C) 2014 Daniel Barrigas - danielgbarrigas@hotmail.com/danielgbarrigas@gmail.com. All rights reserved.
 * Based on the work realized by Jorge Araújo during the development of CloudAid1.
*/

package csadata;


public class Requirement {
	
	
	private static int count=0;
	
	private float min=-1;
	private float max=-1;
	private boolean needed; //true if the must exist this feature, false if the absense of this feature is not exclusive.
	private boolean positive = true; // true (default) if the user wants to have this feature in the service, false if he does not want this feature in the service.
	private String qualValue;
	private boolean criterion; // if true means that this requirement is also on criterion. If false means that this requirement is exclusive.
	private boolean exclusive = false; // this field is automatically set when one of <min, max, positive> fields are set
	private boolean exclusivityMax; //true if we want to use the max field, false if we want to use the min field
	private String id; 
	private int priority; // from 1 to 5, 1 being high priority and 5 being low priority
	private String description; 
	private String Cloudtype=null;
	private int type;//0 - Quantitative, 1-Qualitative, 2 - Price
	public Requirement(){
		count++;
		setId("Req"+count);
	}
	
	public Requirement(String description,String type){
		count++;
		setId("Req"+count);
		CLOUDEnumMiddleMan mm = new CLOUDEnumMiddleMan();
		if(mm.getQuantMiddleMan().get(type) != null)
		{
			this.setType(0);
			this.setCloudtype(type);
		}
		else if(mm.getQualMiddleMan().get(type) != null)
		{
			this.setType(1);
			this.setCloudtype(type);
		}
		else if(type.equals("price"))
		{
			this.setType(2);
			System.out.println("price req.");
		}
		else
			System.out.println("Concept not recgnized by the CloudTaxonomy.");
		
		this.setDescription(description);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isExclusivityMax() {
		return exclusivityMax;
	}

	public void setExclusivityMax(boolean exclusivityMax) {
		this.exclusivityMax = exclusivityMax;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean isCriterion() {
		return criterion;
	}

	public void setCriterion(boolean criterion) {
		this.criterion = criterion;
	}

	public String getQualValue() {
		return qualValue;
	}

	public void setQualValue(String qualValue) {
		this.qualValue = qualValue;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public boolean isNeeded() {
		return needed;
	}

	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.exclusive = true;
		this.max = max;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.exclusive = true;
		this.min = min;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getCloudtype() {
		return Cloudtype;
	}

	public void setCloudtype(String cloudtype) {
		Cloudtype = cloudtype;
	}
	@Override
	public String toString() {
		return "Requirement [id=" + id + ", priority=" + priority
				+ ", description=" + description + ", type=" + type + ", exclusivityMax="
				+ exclusivityMax + ", min=" + min + ", max=" + max
				+ ", needed=" + positive + ", qualValue=" + qualValue
			    + ", criterion=" + criterion
				+ ", exclusive=" + exclusive + "]";
	}
}
