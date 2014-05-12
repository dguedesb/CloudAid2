package csaevaluator;
import controller.Controller;
import csadata.CSAData;
import csadata.Criterion;
import csadata.ServiceTemplate;


//class for evaluation and contructing the search queries
public class CSAEvaluator {
	
	private CSAData data;
	public static final int OK = 0;
	public static final int ERROR_1 = 1; // no components in the CSA
	
	private int methodID;
		
	public CSAEvaluator(int methodID){
		this.methodID = methodID;
	}

	public CSAData getData() {
		return data;
	}

	public void setData(CSAData data) {
		this.data = data;
	}
	
	public CSAData evaluator(CSAData csa){
		System.out.println("Evaluating CSA Data.");
		try{
			csa = this.checkData(csa);
			if(csa.getEvalResult() == 0){
				//evaluation ok
				this.data = csa;
				if(this.methodID == Controller.ELECTRE || this.methodID == Controller.PROMETHEE || this.methodID == Controller.SAW){
					this.normalizeServiceTemplateWeights();
					this.normalizeCriteriaWeights();
				}
				return this.data;
			}else{
				return csa;
			}
		}catch(NullPointerException ex){
			//System.out.println("No data do evaluate.");
			return null;
		}
	}
	
	//checks if the CSA has enough data for a decision
	private CSAData checkData(CSAData data){
		if(data !=null){
			if(data.getServiceTemplates().size() > 0){
				data.setEvalResult(OK);
				return data;
			}else{
				data.setEvalResult(ERROR_1);
				return data;
			}
		}else{
			return data;
		}
		
	}


	
	private void normalizeServiceTemplateWeights(){
		
		//normalize the general criteria
		System.out.println("CSAEval: Normalizing Service Template Weights");
		double total = 0;
		for(ServiceTemplate template : data.getServiceTemplates()){
			total = total + template.getWeight();
		}
		
		for(ServiceTemplate template : data.getServiceTemplates()){
			double res = template.getWeight() / total;
			System.out.println("CSAEval: Total: "+ total +" || value: "+template.getWeight()+"|| res: "+ res);
			template.setWeight((float) res);
		}
	}
	
	private void normalizeCriteriaWeights(){
		
		//normalize each of the Service Templates
		for(ServiceTemplate template : data.getServiceTemplates()){
			System.out.println("CSAEval: Normalizing criteria of the serviceTemplate: "+ template.getId());
			double templateTotal = 0;
			for(Criterion crit : template.getCriteria()){
//				normalizeCriteriaThresholds(crit);
				templateTotal = templateTotal + crit.getWeight();
			}
			
			for(Criterion crit : template.getCriteria()){
				double res = crit.getWeight() / templateTotal;
				System.out.println("CSAEval: Total: "+ templateTotal +" || value: "+crit.getWeight()+"|| res: "+ res);
				crit.setWeight(res);
			}
		}
	}
}

