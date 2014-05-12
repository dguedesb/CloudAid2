package frontend;

import java.util.ArrayList;
import java.util.Scanner;
import csadata.CLOUDEnumMiddleMan;
import csadata.CSAData;
import csadata.Criterion;
import csadata.Requirement;
import csadata.ServiceTemplate;


public class CSABuilder {
	
	private int method;
	private CSAData data ;
	private ArrayList<ServiceTemplate> serviceTemplates ;
	private ArrayList<Requirement> reqs ;
	private ArrayList<Criterion> criteria;
	private ComInterface ci;
	private CLOUDEnumMiddleMan mm;
	// decision Method codes
	public static final int SAW = 0;
	public static final int ELECTRE = 1;
	public static final int PROMETHEE = 2;
	public static final int SMAA2 = 3;
	
	private ServiceTemplate currentST=null;
	
	public CSABuilder()
	{
		data = new CSAData();
		serviceTemplates = new ArrayList<ServiceTemplate>();
		reqs = new ArrayList<Requirement>() ;
		criteria = new ArrayList<Criterion>();
		mm = new CLOUDEnumMiddleMan();
		ci = new ComInterface();
	}
	
	@SuppressWarnings("resource")
	private void chooseDecisionMethod()
	{
		while(true){
			System.out.println("Choose one of the following decision methods:" +
					"\n1-Simple Additive Weighting " +
					"\n2-ELECTRE III" +
					"\n3-PROMETHEE 1" +
					"\n4-SMAA 2");
			Scanner in = new Scanner(System.in);
			String dm = in.nextLine();
			try {
				int method = Integer.parseInt(dm);
				if(method < 1 && method > 4)
				{
					System.out.println("Please, choose between 1,2,3 or 4.");
				}
				data.setMethod(method-1);
				this.setMethod(method-1);
				break;
			} catch (NumberFormatException e) {
				System.out.println("Please insert only numerical values!");
			}
		}
	}
	
	@SuppressWarnings("resource")
	public CSAData buildCSA()
	{
		boolean stop = false;
		
		chooseDecisionMethod();
		
		while(!stop){
			System.out.println("CSA Data:");
			System.out.println("1 - New Service Template");
			System.out.println("2 - New Aggregation Requirement");
			System.out.println("0 - Finish");
			Scanner in = new Scanner(System.in);
			String s = in.next();
			try {
				int opt = Integer.parseInt(s);
				switch (opt) {
					case 0:					
						data.setServiceTemplates(serviceTemplates);
						data.setRequirements(reqs);
						stop=true;
						break;
		            case 1:
		            	serviceTemplates.add(this.newServiceTemplate());
		            	break;
		            case 2:
		            	System.out.println("You are adding an Aggregation Requirement. These requirements will affect only at the aggregation of the services.");
		            	Requirement req = this.newRequirement();
		            	if(req != null)
		            		reqs.add(req);
		            	break;
		            default: break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Choose one of the available options please.");
			}
		}
		return this.data;
	}
	
	@SuppressWarnings("resource")
	private ServiceTemplate newServiceTemplate(){
		ServiceTemplate st = new ServiceTemplate();
		currentST=st;
		
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>();
		
		Scanner inn = new Scanner(System.in);
		System.out.println("Name your Service Template:");
		st.setName(inn.nextLine());
		System.out.println("Give a description to your Service Template:");
		st.setDescription(inn.nextLine());
		
		if(this.getMethod() == ELECTRE || this.getMethod() == PROMETHEE || this.getMethod() == SAW)
		{
			while(true){
				System.out.println("Specify the Service Template decision weight:");
				String weight = inn.nextLine();
				try {
					Float weightValue = Float.parseFloat(weight);
					st.setWeight(weightValue);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Please insert only numerical values!");
				}
			}
		}
		while(true){
			System.out.println("----------------" + st.getName()+" Specification---------------");
			System.out.println("1 - New Requirement");
			System.out.println("2 - New Criterion");
			System.out.println("0 - Finish");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try {
				int opt = Integer.parseInt(s);
				switch (opt) {
					case 0:
						st.setRequirements(reqs);
						if(st.getCriteria().size() > 0)
						{
							if(criteria.size() > 0)
							{
								for(Criterion c : criteria)
									st.addCrit(c);
							}
						}
						else
							st.setCriteria(criteria);
						return st;
		            case 1:
		            	Requirement req = this.newRequirement();
		            	if(req != null)
		            		reqs.add(req);
		            	break;
		            case 2:
		            	criteria.add(this.newCriterion());
		            	break;
		            default: break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please, choose one of the options.");
			}
		}
	}
	
	@SuppressWarnings("resource")
	private Requirement newRequirement() {
		Requirement req = null;
		Scanner in;

		// ask for requirement data
		while (true) {
			System.out.println("Please specify the Requirement Type from the list of Cloud Concepts, or write 'Price' for a Price requirement: ");
			in = new Scanner(System.in);
			String type = in.nextLine();
			req=null;
			// QualitativeFeature
			if (mm.getQualMiddleMan().get(type) != null) {
				req = new Requirement();
				req.setCloudtype(mm.getQualMiddleMan().get(type));
				req.setType(1);
				// specify the qualitative value
				while (true) {
					System.out.println("Do you want to specify a particular value for this feature? (Y/N)");
					String needed = in.nextLine();
					if (needed.equalsIgnoreCase("y")) {
						System.out.println("Please specify the value:");
						String qualValue = in.nextLine();
						req.setQualValue(qualValue);
						break;
					} else if (needed.equalsIgnoreCase("n")) {
						break;
					} else {
						System.out.println("Answer with Y/N please.");
					}
				}

				// does it exclude the service?
				while (true) {
					System.out.println("Is this a feature that if not present excludes the Service? (Y/N)");
					String needed = in.nextLine();
					if (needed.equalsIgnoreCase("y")) {
						req.setNeeded(true);
						req.setExclusive(true);
						while (true) {
							System.out.println("Is this a feature that you want the service to have (Y) or you want the service to don't have(N)? (Y/N)");
							String positive = in.nextLine();
							if (positive.equalsIgnoreCase("y")) {
								req.setPositive(true);
								break;
							} else if (positive.equalsIgnoreCase("n")) {
								req.setPositive(false);
								break;
							} else {
								System.out.println("Answer with Y/N please.");
							}
						}
						break;
					} else if (needed.equalsIgnoreCase("n")) {
						req.setNeeded(false);
						req.setExclusive(false);
						break;
					} else {
						System.out.println("Answer with Y/N please.");
					}
				}

			} else if (mm.getQuantMiddleMan().get(type) != null || type.equalsIgnoreCase("price"))// QuantitativeFeature
			{
				req = new Requirement();
				
				if (type.equalsIgnoreCase("price"))
				{
					req.setType(2);
					req.setCloudtype("price");
				}
				else
				{
					req.setCloudtype(mm.getQuantMiddleMan().get(type));
					req.setType(0);
				}

				while (true) {
					System.out.println("Does it have a limit value? (Y/N)");
					String s = in.nextLine();
					if (s.equalsIgnoreCase("y")) {
						while (true) {
							System.out.println("Please specify the limit:");
							String limit = in.nextLine();
							try {
								Float limitValue = Float.parseFloat(limit);
								while (true) {
									System.out.println("Is it a minimum or maximum limit? (min/max)");
									String limitType = in.nextLine();
									if (limitType.equalsIgnoreCase("min")) {
										req.setMin(limitValue);
										req.setExclusivityMax(false);
										req.setExclusive(true);
										break;
									} else if (limitType.equalsIgnoreCase("max")) {
										req.setMax(limitValue);
										req.setExclusivityMax(true);
										req.setExclusive(true);
										break;
									} else {
										System.out.println("Answer with min/max please.");
									}
								}
								break;
							} catch (NumberFormatException e) {
								System.out.println("Insert a numerical value please. "+ limit+ " is not a valid value.");
							}
						}
						break;
					} else if (s.equalsIgnoreCase("n")) {
						while (true) {
							System.out.println("Is this a feature that if not present excludes the Service? (Y/N)");
							String needed = in.nextLine();
							if (needed.equalsIgnoreCase("y")) {
								req.setNeeded(true);
								req.setExclusive(true);
								break;
							} else if (needed.equalsIgnoreCase("n")) {
								req.setExclusive(false);
								req.setNeeded(false);
								break;
							} else {
								System.out.println("Answer with Y/N please.");
							}
						}
						break;
					} else {
						System.out.println("Answer with Y/N please.");
					}
				}
			}

			if (req != null) {
				while (true) {
					System.out.println("Will this requirement also be decision criterion? (Y/N)");
					String s = in.nextLine();
					if (s.equalsIgnoreCase("y")) {
						currentST.addCrit(newCriterion(req));
						req.setExclusive(true);
						req.setCriterion(true);
						break;
					} else if (s.equalsIgnoreCase("n")) {
						req.setCriterion(false);
						req.setExclusive(false);
						break;
					} else {
						System.out.println("Answer with Y/N please.");
					}
				}
				return req;
			} else {
				while (true) {
					System.out.println("The type you specified is not recognized! Try again? (Y/N)");
					String s = in.nextLine();
					if (s.equalsIgnoreCase("y")) {
						break;
					} else if (s.equalsIgnoreCase("n")) {
						return null;
					} else {
						System.out.println("Answer with Y/N please.");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	private Criterion newCriterion(){
		Criterion crit = new Criterion();
		Scanner in;
		while(true){
	    	System.out.println("Please specify the Criterion Type from the list of Cloud Concepts or 'Price' for a price Criterion: ");
	    	in = new Scanner(System.in);
			String type = in.nextLine();
			
			if(mm.getQuantMiddleMan().get(type) != null || mm.getQualMiddleMan().get(type) != null || type.equalsIgnoreCase("Price")){
				
				if(mm.getQuantMiddleMan().get(type) != null ){
					crit.setName(mm.getQuantMiddleMan().get(type));
					crit.setType(0);
				}
				else if(mm.getQualMiddleMan().get(type) != null){
					crit.setName(mm.getQualMiddleMan().get(type));
					crit.setType(1);
				}
				else if(type.equalsIgnoreCase("Price"))
				{
					crit.setName(type.toLowerCase());
					crit.setType(2);
				}
				if(this.getMethod() == SAW || this.getMethod() == ELECTRE || this.getMethod() == PROMETHEE){
					while(true){
						System.out.println("Please specify the criterion's weight:");
						String weight = in.nextLine();
						try {
							Float weightValue = Float.parseFloat(weight);
							crit.setWeight(weightValue);
							break;
						} catch (NumberFormatException e) {
							System.out.println("Insert a numerical value please. " + weight + " isn't a valid value.");
						}
					}
				}
				while(true){
					System.out.println("Do you want to maximize the Criterion value? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						crit.setPreferenceDirection("max");
						break;
					}else if (s.equalsIgnoreCase("n")){
						crit.setPreferenceDirection("min");
						break;
					}else{
						System.out.println("Answer with Y/N please.");
					}
				}
				
				if(crit.getType() == 0 || crit.getType() == 2)
				{
					System.out.println("Does criterion "+ crit.getName() +" have a preferable value? (Y/N)");
					Scanner in3 = new Scanner(System.in);
					String s = in3.nextLine();
					if(s.equalsIgnoreCase("y")){
						while(true){
							System.out.println("Please insert the preferable value for criterion: "+ crit.getName()+ ":");
							Scanner in4 = new Scanner(System.in);
							String s3 = in4.nextLine();
							crit.setPreference(s3);	
							try{
								double value = Double.parseDouble(s3);
								crit.setPreference(Double.toString(value));
								break;
							}catch(Exception ex){
								System.out.println("Please, insert only numerical values");
							}				
						}
					}else if(s.equalsIgnoreCase("n")){
						//no defined preference. Maximum value is set later (on the numerical normalization) as the preference.
						crit.setPreference(crit.getPreferenceDirection().toUpperCase());
					}
				}
				else if(crit.getType() == 1){
					System.out.println("Please insert the preferable value for criterion: "+ crit.getName()+ ":");
					Scanner in3 = new Scanner(System.in);
					String s3 = in3.nextLine();
					crit.setPreference(s3);	
				}
				
				while(true){
					System.out.println("Do you want to define any type of decision threshold? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						defineCriterionThresholds(crit);
						return crit;
					}else if (s.equalsIgnoreCase("n")){
						return crit;
					}else{
						System.out.println("Answer with Y/N please.");
					}
				}
				
			}else{
				while(true){
					System.out.println("The type you specified is not recognized! Try again? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						break;
					}else if (s.equalsIgnoreCase("n")){
						return null;
					}else{
						System.out.println("Please answer only with Y/N");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	private void defineCriterionThresholds(Criterion crit) {
		
		while(true){
			System.out.println("----------------" + crit.getName().replaceAll("cloudtaxonomy:", "") +"Thresholds Specification---------------");
			System.out.println("0 - Save and Exit");
			System.out.println("1 - Indifference Threshold");
			System.out.println("2 - Preference Threshold");
			System.out.println("3 - Veto Threshold");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try {
				int opt = Integer.parseInt(s);
				switch (opt) {
					case 1:
						while(true){
							System.out.println("Indifference Threshold:");
							String indthresh = in.nextLine();
							try {
								Float indValue = Float.parseFloat(indthresh);
								crit.setIndifference_threshold(indValue);
								break;
							} catch (NumberFormatException e) {
								System.out.println("Insert a numerical value please. " + indthresh + " isn't a valid value.");
							}
						}
						break;
		            case 2:
		            	while(true){
							System.out.println("Preference Threshold:");
							String prefthresh = in.nextLine();
							try {
								Float prefValue = Float.parseFloat(prefthresh);
								crit.setPreference_threshold(prefValue);
								break;
							} catch (NumberFormatException e) {
								System.out.println("Insert a numerical value please. " + prefthresh + " isn't a valid value.");
							}
						}
		            	break;
		            case 3:
		            	while(true){
							System.out.println("Veto Threshold:");
							String vetothresh = in.nextLine();
							try {
								Float vetoValue = Float.parseFloat(vetothresh);
								crit.setVeto_threshold(vetoValue);
								break;
							} catch (NumberFormatException e) {
								System.out.println("Insert a numerical value please. " + vetothresh + " isn't a valid value.");
							}
						}
		            	break;
		            case 0:
		            	return;
		            default: break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please, choose one of the options.");
			}
		}
		
	}

	@SuppressWarnings("resource")
	private Criterion newCriterion(Requirement req){
		Criterion crit = new Criterion();
		Scanner in;
		while(true){
			in = new Scanner(System.in);
			crit.setName(req.getCloudtype());
			crit.setType(req.getType());

				if(this.getMethod() == SAW || this.getMethod() == ELECTRE || this.getMethod() == PROMETHEE){
					while(true){
						System.out.println("Please specify the criterion's weight:");
						String weight = in.nextLine();
						try {
							Float weightValue = Float.parseFloat(weight);
							crit.setWeight(weightValue);
							break;
						} catch (NumberFormatException e) {
							System.out.println("Insert a numerical value please. " + weight + " isn't a valid value.");
						}
					}
				}
				while(true){
					System.out.println("Do you want to maximize the Criterion value? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						crit.setPreferenceDirection("max");
						break;
					}else if (s.equalsIgnoreCase("n")){
						crit.setPreferenceDirection("min");
						break;
					}else{
						System.out.println("Answer with Y/N please.");
					}
				}
				
				if(crit.getType() == 0 || crit.getType() == 2)
				{
					System.out.println("Does criterion "+ crit.getName() +" have a preferable value? (Y/N)");
					Scanner in3 = new Scanner(System.in);
					String s = in3.nextLine();
					if(s.equalsIgnoreCase("y")){
						while(true){
							System.out.println("Please insert the preferable value for criterion: "+ crit.getName()+ ":");
							Scanner in4 = new Scanner(System.in);
							String s3 = in4.nextLine();
							crit.setPreference(s3);	
							try{
								double value = Double.parseDouble(s3);
								crit.setPreference(Double.toString(value));
								break;
							}catch(Exception ex){
								System.out.println("Please, insert only numerical values");
							}				
						}
					}else if(s.equalsIgnoreCase("n")){
						//no defined preference. Maximum value is set later (on the numerical normalization) as the preference.
						crit.setPreference(crit.getPreferenceDirection().toUpperCase());
					}
				}
				else if(crit.getType() == 1){
					System.out.println("Please insert the preferable value for criterion: "+ crit.getName()+ ":");
					Scanner in3 = new Scanner(System.in);
					String s3 = in3.nextLine();
					crit.setPreference(s3);	
				}
				
				
				while(true){
					System.out.println("Do you want to define any type of decision threshold? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						defineCriterionThresholds(crit);
						return crit;
					}else if (s.equalsIgnoreCase("n")){
						return crit;
					}else{
						System.out.println("Answer with Y/N please.");
					}
				}
			}
		}
	
	public CSAData getData() {
		return data;
	}

	public void setData(CSAData data) {
		this.data = data;
	}

	public ArrayList<ServiceTemplate> getComponents() {
		return serviceTemplates;
	}

	public void setComponents(ArrayList<ServiceTemplate> components) {
		this.serviceTemplates = components;
	}

	public ArrayList<Requirement> getReqs() {
		return reqs;
	}

	public void setReqs(ArrayList<Requirement> reqs) {
		this.reqs = reqs;
	}

	public ArrayList<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(ArrayList<Criterion> criteria) {
		this.criteria = criteria;
	}
	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public ComInterface getCi() {
		return ci;
	}

	public void setCi(ComInterface ci) {
		this.ci = ci;
	}

	
	///////////////////////////////////////////////// COMMUNICATION INTERFACE FOR THE CONTROLLER/////////////////////////////////////
	
	
	
}
