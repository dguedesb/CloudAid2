package decisionEngine;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeOnCriteriaPerformances;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeOnCriteriaPerformances.Performance;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativesComparisons.Pairs.Pair;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteria;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteriaValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterion;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterionValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XFunction;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMethodParameters;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XNumericValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XParameter;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPreferenceDirection;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XQuantitative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XScale;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XThresholds;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValue;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAReadUtils;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import searchDataModels.FiltRes;

import com.google.common.io.Files;

import csadata.Criterion;
import csadata.ServiceTemplate;
import decisionDataModels.ComparabilityResult;
import decisionDataModels.SAWResult;
import decisionDataModels.SAWResults;
import decisionDataModels.SMAAResult;
import decisionDataModels.SMAAResults;


public class XMCDAConverter {
	
	public XMCDAConverter(){
	}
	
	public XMCDA getFromFile(String source){
		File f = new File(source);
		XMCDAReadUtils reader = new XMCDAReadUtils();
		XMCDADoc doc = null;
		try {
			doc = reader.getXMCDADoc(Files.newInputStreamSupplier(f));
		} catch (IOException | XmlException e) {
			System.err.println("Unable to retrieve XMCDA data from file!!!");
			e.printStackTrace();
		}
		return doc.getXMCDA();
	}
	
	public XMCDA createAlternatives(ArrayList<FiltRes> alternatives){
		//variable for prefixing alternatives
		//int prefix = 1;
		//create the XMCDA file
		XMCDA XMCDAalternatives = XMCDADoc.XMCDA.Factory.newInstance();
		
		XAlternatives top = XMCDAalternatives.addNewAlternatives();
		for(FiltRes s : alternatives){
			XAlternative alt = top.addNewAlternative();
			alt.setId(s.getMyID());
			alt.setName(s.getMyOff().getName());
		}
		//print data
//		System.out.println(XMCDAalternatives.toString());
		
		return XMCDAalternatives;
	}
	
	public static XMCDA createMethodParameters(String val,int type)
	{
		XMCDA methodParam = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMethodParameters mp = methodParam.addNewMethodParameters();
		
		XParameter p = mp.addNewParameter();
		XValue v = p.addNewValue();
		if(type == 0)
			v.setLabel(val);
		else if(type == 1)
			v.setReal(Float.parseFloat(val));
		
		return methodParam;
	}
	
	public XMCDA createAlternativeValues(ArrayList<FiltRes> alternatives,HashMap<String,String> criterionIDs, int method){
		XMCDA XMCDAalternativesValues = XMCDADoc.XMCDA.Factory.newInstance();
		
		XPerformanceTable ptable = XMCDAalternativesValues.addNewPerformanceTable();
		
		
		for(FiltRes s : alternatives){
			XAlternativeOnCriteriaPerformances top = ptable.addNewAlternativePerformances();
			
			top.setAlternativeID(s.getMyID());
			
			Iterator<Entry<String,Double>> it;
			it = s.getNormalizedAttributes().entrySet().iterator();

		    while (it.hasNext()) {
		    	Performance p = top.addNewPerformance();
		    	
		    	Map.Entry<String,Double> atribute = (Map.Entry<String,Double>)it.next();
		    	
				p.setCriterionID(criterionIDs.get(atribute.getKey()));

				XValue val = p.addNewValue();
				val.setName((String) atribute.getKey());
				try {
					val.setReal((float) Double.parseDouble(atribute.getValue().toString()));
				} catch (ClassCastException e) {
					System.out.println("Couldn't convert "+atribute.getValue().toString() +" to float!");
				}
			}
		}
		//print data
//		System.out.println(XMCDAalternativesValues.toString());
		return XMCDAalternativesValues;
	}
	
	public XMCDA createCriteria(ServiceTemplate c){
		//variable for prefixing criteria
//		int prefix = 1;
		//list of criteria
		ArrayList<Criterion> criteria = c.getCriteria();
		//create the XMCDA file
		XMCDA XMCDAcriteria = XMCDADoc.XMCDA.Factory.newInstance();
		
		
		XCriteria top = XMCDAcriteria.addNewCriteria();
		for(Criterion crit : criteria){
			XCriterion criterion = top.addNewCriterion();
			criterion.setId(crit.getId());
			criterion.setName(crit.getName());
			XScale scale = criterion.addNewScale();
			scale.setMcdaConcept("PreferenceDirection");
			XQuantitative quant = scale.addNewQuantitative();
			if(crit.getPreferenceDirection().equalsIgnoreCase("max"))
				quant.setPreferenceDirection(XPreferenceDirection.MAX);
			else if(crit.getPreferenceDirection().equalsIgnoreCase("min"))
				quant.setPreferenceDirection(XPreferenceDirection.MIN);
			
			if(crit.getIndifference_threshold() >=0)
			{
				XThresholds thresh = criterion.addNewThresholds();
				XFunction pref = thresh.addNewThreshold();
				pref.setMcdaConcept("ind");
				XNumericValue val = pref.addNewConstant();
				val.setReal((float)crit.getIndifference_threshold());
			}
			
			if(crit.getPreference_threshold() >=0)
			{
				XThresholds thresh = criterion.addNewThresholds();
				XFunction pref = thresh.addNewThreshold();
				pref.setMcdaConcept("pref");
				XNumericValue val = pref.addNewConstant();
				val.setReal((float)crit.getPreference_threshold());
			}
			
			if(crit.getVeto_threshold() >=0)
			{
				XThresholds thresh = criterion.addNewThresholds();
				XFunction pref = thresh.addNewThreshold();
				pref.setMcdaConcept("veto");
				XNumericValue val = pref.addNewConstant();
				val.setReal((float)crit.getVeto_threshold());
			}
		}
		//print data
//		System.out.println(XMCDAcriteria.toString());	
		return XMCDAcriteria;
	}
	
	public XMCDA createWeights(ServiceTemplate c){
		//list of criteria
		ArrayList<Criterion> criteria = c.getCriteria();
		//create the XMCDA file
		XMCDA XMCDAweights = XMCDADoc.XMCDA.Factory.newInstance();
		

		XCriteriaValues top = XMCDAweights.addNewCriteriaValues();
		top.setMcdaConcept("Importance");
		top.setName("significance");
		for(Criterion crit : criteria){
			XCriterionValue criVal = top.addNewCriterionValue();
			criVal.setCriterionID(crit.getId());
			XValue val = criVal.addNewValue();
			Double weight = crit.getWeight();
			val.setReal(weight.floatValue());
		}
		//print data
//		System.out.println(XMCDAweights.toString());
		return XMCDAweights;
	}
	
	public XMCDA attachCompTimestamp(long time, ServiceTemplate c){
		XMCDA XMCDATime = XMCDADoc.XMCDA.Factory.newInstance();
		XMethodParameters parameters = XMCDATime.addNewMethodParameters();
		XParameter p1 = parameters.addNewParameter();
		p1.setName("FileTimestamp");
		XValue val1 = p1.addNewValue();
		val1.setLabel(Long.toString(time));
		XParameter p2 = parameters.addNewParameter();
		p2.setName("ComponentID");
		XValue val2 = p2.addNewValue();
		val2.setLabel(c.getId());
		
		return XMCDATime;
	}
	
	public HashMap<String, String> getMethodParameters(XMCDA xmcda){
		HashMap<String, String> param = new HashMap<String, String>(); 
		List<XMethodParameters> methodParameters = xmcda.getMethodParametersList();
		List<XParameter> parametersList = methodParameters.get(0).getParameterList();
		for(XParameter parameter : parametersList){
			param.put(parameter.getName(), parameter.getValue().getLabel());
		}
		return param;
	}
	
	public ArrayList<SAWResult> getPerformance(XMCDA xmcda, ArrayList<FiltRes> alternatives){
		ArrayList<SAWResult> resultList = new ArrayList<SAWResult>();
		for(XPerformanceTable performanceTable : xmcda.getPerformanceTableList()){
			for(XAlternativeOnCriteriaPerformances altPerformances : performanceTable.getAlternativePerformancesList()){
				//get Correct service alternative information
				for(FiltRes alt : alternatives){
					if(alt.getMyID().equalsIgnoreCase(altPerformances.getAlternativeID())){
						Double perf = new Double(altPerformances.getPerformanceList().get(0).getValue().getReal());
						SAWResult res = new SAWResult(alt, perf);
						resultList.add(res);
					}
				}
			}
		}
		return resultList;
	}
	
	public XMCDA append(ArrayList<XMCDA> list, int method){
		
		XMCDA xmcda = XMCDADoc.XMCDA.Factory.newInstance();
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		writer.appendTo(list, xmcda);
		
	
		XMCDADoc file = XMCDADoc.Factory.newInstance();
		
		File result = new File("./XMCDA/"+"temp.xml");
		
		file.setXMCDA(xmcda);

		try {
			writer.write(file, Files.newOutputStreamSupplier(result));
		} catch (IOException e) {
			System.out.println("Unable to create file!!!");
			e.printStackTrace();
		}
		//print data
		//System.out.println(xmcda.toString());
		return xmcda;
	}
	
	public static void export (XMCDA xmcda, String dest){
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		XMCDADoc file = XMCDADoc.Factory.newInstance();
		
		File result = new File(dest);
		
		file.setXMCDA(xmcda);
		try {
			writer.write(file, Files.newOutputStreamSupplier(result));
		} catch (IOException e) {
			System.out.println("Unable to create file!!!");
			e.printStackTrace();
		}
	}
	
	public static SAWResults processSAWResults(XMCDA results,ServiceTemplate st)
	{
		
		ArrayList<SAWResult> fresults = new ArrayList<SAWResult>();
		
		List<XAlternativeValue> alternativeValues = results.getAlternativesValuesList().get(0).getAlternativeValueList();
		

		for(XAlternativeValue av : alternativeValues)
			fresults.add(new SAWResult(  st.getFoundAlternativesMap().get(av.getAlternativeID()),     av.getValueList().get(0).getReal()     ));

		
		return new SAWResults(fresults);
	}
	
	public static SMAAResults processSMAAResults(XMCDA results,ServiceTemplate st)
	{
		ArrayList<SMAAResult> fresults = new ArrayList<SMAAResult>();
		
		List<XAlternativeValue> alternativeValues = results.getAlternativesValuesList().get(0).getAlternativeValueList();
		
		int counter = 0;
		double[] probs = new double[st.getFoundAlternatives().size()];
		for(XAlternativeValue av : alternativeValues)
		{
			String id = av.getAlternativeID();
			probs[av.getValuesList().get(0).getValueList().get(0).getInteger()-1]=av.getValuesList().get(0).getValueList().get(1).getReal();
			
			counter++;
			if(counter == st.getFoundAlternatives().size())
			{
				fresults.add( new SMAAResult(st.getFoundAlternativesMap().get(id), probs.clone()));
				probs = new double[st.getFoundAlternatives().size()];
				counter=0;
			}
		}
		
		return new SMAAResults(fresults);
	}
	
	public static ComparabilityResult processComparabilityResults(XMCDA results,ServiceTemplate st)
	{
		ComparabilityResult fresults = null;
		
		
		List<Pair> pairs = results.getAlternativesComparisonsList().get(0).getPairs().getPairList();

		int[][] comparisonMatrix = new int[st.getFoundAlternatives().size()][st.getFoundAlternatives().size()];

		for(Pair p : pairs)
		{
			
			String best = p.getInitial().getAlternativeID().replace("Alt", "");
			String worst = p.getTerminal().getAlternativeID().replace("Alt", "");
			
			if(best.equalsIgnoreCase(worst))
			{
				comparisonMatrix[Integer.parseInt(best)-1][Integer.parseInt(worst)-1] = 0;
				comparisonMatrix[Integer.parseInt(worst)-1][Integer.parseInt(best)-1] = 0;
			}
			else
			{
				comparisonMatrix[Integer.parseInt(best)-1][Integer.parseInt(worst)-1] = 1;
				comparisonMatrix[Integer.parseInt(worst)-1][Integer.parseInt(best)-1] = -1;
			}
			
		}
		
		fresults = new ComparabilityResult(st.getFoundAlternatives(), comparisonMatrix);
		
		return fresults;
	}
}
