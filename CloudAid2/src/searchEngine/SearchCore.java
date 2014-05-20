package searchEngine;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.topbraid.spin.util.JenaUtil;

import searchDataModels.FiltRes;
import searchDataModels.PriceVariable;
import searchDataModels.PricingVariables;
import usdl.constants.enums.CLOUDEnum;
import usdl.constants.enums.GREnum;
import usdl.constants.enums.RDFEnum;
import usdl.constants.enums.RDFSEnum;
import usdl.constants.enums.USDLCoreEnum;
import usdl.queries.QueryUtils;
import usdl.servicemodel.Offering;
import usdl.servicemodel.PriceComponent;
import usdl.servicemodel.PriceFunction;
import usdl.servicemodel.PricePlan;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Usage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import controller.Controller;
import csadata.Criterion;
import csadata.Requirement;
import csadata.ServiceTemplate;
import exceptions.InvalidLinkedUSDLModelException;
import exceptions.ReadModelException;

public class SearchCore {
	static TripleStore ts;
	static {
		try {
			ts = new TripleStore();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void searchy()
//	{
//		System.out.println("testing");
//		String variableName = "offering";
//		String query = " PREFIX core: <http://www.linked-usdl.org/ns/usdl-core#>  PREFIX price: <http://www.linked-usdl.org/ns/usdl-price#>  PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  PREFIX gr: <http://purl.org/goodrelations/v1#>  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  PREFIX cloudtaxonomy: <http://rdfs.genssiz.org/CloudTaxonomy#> PREFIX spin: <http://spinrdf.org/spin#> SELECT REDUCED ?offering   WHERE {  {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering.  ?offering core:includes ?serv .  {  ?serv gr:qualitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:Location .  ?f rdfs:label ?value  FILTER regex(?value, 'tokyo', 'i')  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:qualitativeProductOrServiceProperty ?f.  ?f rdf:type cloudtaxonomy:Location .  ?f rdfs:label ?value  FILTER regex(?value, 'tokyo', 'i')  }  }  } . {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering . ?offering core:includes ?serv .  {  ?serv gr:quantitativeProductOrServiceProperty cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 50.0)  }UNION{  ?serv gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 50.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 50.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 50.0)  } UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasMinValue ?value  FILTER(?value >= 50.0)  }  }  } . {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering . ?offering core:includes ?serv .  {  ?serv gr:quantitativeProductOrServiceProperty cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 2.0)  }UNION{  ?serv gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 2.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 2.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 2.0)  } UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasMinValue ?value  FILTER(?value >= 2.0)  }  }  }  }";
////		String query = SearchCore.readOfferingsFromTokyo(variableName);
//		System.out.println(query);
//		QueryExecution exec = QueryExecutionFactory.create(query, ts.getMyTripleStore());
//        
//		ResultSet results = exec.execSelect();
//		int c =0;
//		while(results.hasNext()){
//			System.out.println("Got something!!!!   ->" + ++c);
//			QuerySolution row = results.next();
//			Offering offering = Offering.readFromModel(row.getResource(variableName), ts.getMyTripleStore());
//			
//			
////			System.out.println(offering.toString());
//		}
//	}
	
	private ArrayList<Object> queryBuilder(ArrayList<Requirement> reqs){
		ArrayList<Object> res = new ArrayList<Object>();
		Requirement priceReq = null;
		//add query prefixes
		String query = QueryUtils.startQueryWithBasicPrefixes();
		
		//adds the query header
		query = query + 
				" SELECT REDUCED ?offering  " + 
				" WHERE { ";
		int count = 0;
		for(Requirement req : reqs){
			//build the requirement query
			if(count > 0 && req.getType() != 2)
				query = query + ".";
		
			if(req.getType()  == 1){
				query = query + " { " +this.qualitativeReqQueryBuilder(req) +" } ";
				count++;
			}else if(req.getType() == 0){
				query = query + " { " +this.quantitativeReqQueryBuilder(req) +" } ";
				count++;
			}else{
				priceReq=req;
			}
			
		}
		//close the query
		query = query +
				" }";
		res.add(query);
		if(priceReq != null)
			res.add(priceReq);
		else
			res.add(null);
		
		return res;
	}
	
	private String qualitativeReqQueryBuilder(Requirement req){
		String reqSearch = 
				" SELECT REDUCED ?offering " +
				" WHERE { " +
					" ?offering "+RDFEnum.RDF_TYPE.getPropertyString()+ " " +USDLCoreEnum.OFFERING.getPropertyString()+". " +
					" ?offering "+USDLCoreEnum.INCLUDES.getPropertyString()+" ?serv . " ;		
		
		if(!req.isPositive())
			reqSearch = reqSearch +" MINUS{ ";

		String value = req.getQualValue();
		if(value != null){
			//there is a value to this requirement that must be included in the search
			String filter = " FILTER regex(?value, '"+ value +"', 'i') ";
			
			reqSearch = 
				reqSearch +
				" { " +
					" ?serv "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" ?f . " +
					" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" " + req.getCloudtype() +" . " +
					" ?f " +RDFSEnum.LABEL.getPropertyString()+" ?value " +
					filter +
				" }UNION{ " +
					" ?serv " + USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString() + " ?model . " +
					" ?model "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" ?f. " +
					" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" " + req.getCloudtype() +" . " +
					" ?f " +RDFSEnum.LABEL.getPropertyString()+" ?value " +
					filter +
				" } ";
		}else{
			reqSearch = 
				reqSearch +
					" { " +
						" ?serv "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" "+req.getCloudtype()+" . " +
					" }UNION{ " +
						" ?serv "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" ?f . " +
						" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" " +
					" }UNION{ " +
						" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
						" ?model "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" "+ req.getCloudtype() +" . " +
					" }UNION{ " +
						" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
						" ?model "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" ?f. " +
						" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" " +
					" } ";
		}
		//close the MINUS statement
		if(!req.isPositive())
			reqSearch = reqSearch +" } ";

		return reqSearch + " } ";
	}
	
	private String quantitativeReqQueryBuilder(Requirement req){
		//this is a quantitative feature requirement
		String reqSearch;
		String filter = "";
		
		if(req.isExclusivityMax()){
			//we want values smaller than the max field
			filter = " FILTER(?value <= "+ req.getMax()+") ";
		}else{
			//we want values bigger than the min field
			if(req.getMin() >= 0)
				filter = " FILTER(?value >= "+ req.getMin()+") ";
		}
		
		reqSearch = 
				" SELECT REDUCED ?offering " +
				" WHERE { " +
					" ?offering "+RDFEnum.RDF_TYPE.getPropertyString()+" " + USDLCoreEnum.OFFERING.getPropertyString() + " ." +
					" ?offering "+USDLCoreEnum.INCLUDES.getPropertyString()+" ?serv . " +
					" { " + 
						" ?serv "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+ " " + req.getCloudtype() +" . " +
						" ?f "+GREnum.HAS_VALUE.getPropertyString()+" ?value " +
						filter +
					" }UNION{ " +
						" ?serv "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+" ?f . " +
						" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" . " +
						" ?f "+GREnum.HAS_VALUE.getPropertyString()+" ?value " + 
						filter +
					" }UNION{ " +
						" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
						" ?model "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+" "+ req.getCloudtype() +" . " +
						" ?f "+GREnum.HAS_VALUE.getPropertyString()+" ?value " +
						filter +
					" }UNION{ " +
						" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
						" ?model "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+" ?f . " +
						" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" . " +
						" ?f "+GREnum.HAS_VALUE.getPropertyString()+" ?value " +
						filter +
					" } ";
					
				if(req.isExclusivityMax())
				{
					reqSearch+= "UNION{ " +
						" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
						" ?model "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+" ?f . " +
						" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" . " +
						" ?f "+GREnum.HAS_MAX_VALUE.getPropertyString()+" ?value " +
						filter +
					" } ";
				}
				else
				{
					reqSearch+= "UNION{ " +
							" ?serv "+USDLCoreEnum.HAS_SERVICE_MODEL.getPropertyString()+" ?model . " +
							" ?model "+GREnum.QUANT_PROD_OR_SERV.getPropertyString()+" ?f . " +
							" ?f "+RDFEnum.RDF_TYPE.getPropertyString()+" "+ req.getCloudtype() +" . " +
							" ?f "+GREnum.HAS_MIN_VALUE.getPropertyString()+" ?value " +
							filter +
						" } ";
				}
			
				reqSearch+=" } ";
			
	return reqSearch;
}
	
	public static String readOfferingsFromTokyo(String variableName)
	{
		String query = QueryUtils.startQueryWithBasicPrefixes();
		query = query +
				" SELECT REDUCED ?"+variableName +
				" WHERE { " +
					" ?"+variableName+" "+RDFEnum.RDF_TYPE.getPropertyString()+" "+USDLCoreEnum.OFFERING.getPropertyString()+" . " + 
					" ?"+variableName+" "+USDLCoreEnum.INCLUDES.getPropertyString()+" ?serv . " + 
					" ?serv"+" "+GREnum.QUAL_PROD_OR_SERV.getPropertyString()+" ?loc  . " + 
					"?loc " + RDFEnum.RDF_TYPE.getPropertyString() + " " + CLOUDEnum.LOCATION.getConceptString() + "." +
					"?loc " + RDFSEnum.LABEL.getPropertyString() + " ?loc_name ." +
					"FILTER (str(?loc_name) = \"apac-tokyo\" ) ." +
				" }";
		return query;
	}
	
	public ArrayList<FiltRes> searchOfferings(ServiceTemplate st,List<Requirement> globalReqs) throws IOException, InvalidLinkedUSDLModelException
	{
		ArrayList<Requirement> mergedReqs = new ArrayList<Requirement>();
		mergedReqs.addAll(st.getRequirements());
		mergedReqs.addAll(globalReqs);
		ArrayList<Requirement> exclusiveRequirements = this.getExclusiveReqs( mergedReqs );
		
		ArrayList<Object> obj = this.queryBuilder(exclusiveRequirements);
		String builtQuery = (String)obj.get(0);
		Requirement priceReq = null;//filter according to the price limit defined by the user
		if(obj.get(1) != null)
			priceReq=(Requirement)obj.get(1);

//		System.out.println(st.getRequirements().size());
		
		ArrayList<Offering> offeringsList = new ArrayList<>();
		String variableName = "offering";
		
		//build query based on exclusive requirements (exclusiveRequirements)
		//String queryString =readOfferingsFromTokyo(variableName);
		System.out.println(builtQuery);
		Query query = QueryFactory.create(builtQuery);
        QueryExecution exec = QueryExecutionFactory.create(query, ts.getMyTripleStore());
        
		ResultSet results = exec.execSelect();
		
		while(results.hasNext()){
			QuerySolution row = results.next();
			Offering offering = Offering.readFromModel(row.getResource(variableName), ts.getMyTripleStore());
//			System.out.println(offering.toString());
			offeringsList.add(offering);
		}
		
		exec.close();
		
		System.out.println("Prior to price filtering:   "+offeringsList.size());
		ArrayList<FiltRes> finalres = OfferingsPrice(priceReq,offeringsList,ts.getMyTripleStore());//add price restriction. after we've found the alternatives that fit every other requirement, apply a secondary filter to filter according to their price.
		setCritAttr(finalres,st.getCriteria());
		
		FiltRes.resetIDCounter();
		return finalres;
	}
	
	private void setCritAttr(ArrayList<FiltRes> finalres, ArrayList<Criterion> crits) {
		
		for(Criterion crit : crits)
		{
			for(FiltRes of : finalres)
			{
				if(crit.getType() == 0 || crit.getType()==2)
				{
					if(crit.getName().toLowerCase().contains("price"))
					{
						of.getCritAttr().put(crit.getName().toLowerCase(), Double.toString(of.getMyPrice()));
					}
					else
					{
						boolean found = false;
						for(QuantitativeValue qv : of.getMyOff().getIncludes().get(0).getQuantfeatures())
						{
							for(String type : qv.getTypes())
							{
								if(type.contains(crit.getName().replaceAll("cloudtaxonomy:", "")))
								{
									found=true;
									if(qv.getValue() >= 0)
										of.getCritAttr().put(crit.getName(), Double.toString(qv.getValue()));
									else if(qv.getMaxValue() >= 0 )
										of.getCritAttr().put(crit.getName(), Double.toString(qv.getMaxValue()));
								}
							}
						}
						if(found == false)
						{
							if(crit.getPreferenceDirection().equalsIgnoreCase("max"))
								of.getCritAttr().put(crit.getName(),Double.toString(0));
							else
								of.getCritAttr().put(crit.getName(),Double.toString(Double.MAX_VALUE));
						}
							
					}
				}
				else if(crit.getType() == 1)
				{
					boolean found = false;
					for(QualitativeValue qv : of.getMyOff().getIncludes().get(0).getQualfeatures())
					{
						for(String type : qv.getTypes())
						{
							if(type.contains(crit.getName().replaceAll("cloudtaxonomy:", ""))){
								found=true;
								of.getCritAttr().put(crit.getName(),qv.getHasValue());
							}
						}
					}
					if(found==false)
						of.getCritAttr().put(crit.getName(),"N/A");
				}
				else
				{
					System.out.println("Criterion type is not recognized! Type: "+crit.getType());
				}
			}
		}
	}

	private ArrayList<Requirement> getExclusiveReqs(ArrayList<Requirement> reqs){
		ArrayList<Requirement> exReqs = new ArrayList<Requirement>();
		
		for(Requirement r : reqs){
			if(!r.isCriterion() || r.isExclusive() || r.isNeeded() ){
				exReqs.add(r);
			}
		}
		
		return exReqs;
	}
	
	private static Model setPrefixes(Model model, Map<String, String> prefixes){
		
		Iterator<Entry<String,String>> it = prefixes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
	        String key = (String)pairs.getKey(); //URI
	        String value = (String)pairs.getValue(); //preffix name
	        model.setNsPrefix(value, key);
	    }
		
		return model;
	}
	
	public  ArrayList<FiltRes> OfferingsPrice(Requirement priceReq,ArrayList<Offering> offs,Model model) throws IOException, InvalidLinkedUSDLModelException
	{
		HashMap<String,Usage> definedVariables = new HashMap<String,Usage>();
		
		List<PriceVariable> vars = new ArrayList<PriceVariable>();
		for(Offering off : offs)//for each offering
		{
			if(off.getPricePlan() != null)//if it has a price plan
			{
				PricePlan pp = off.getPricePlan();
				for(PriceComponent pc : pp.getPriceComponents())//for every component of the priceplan
				{
					if(pc.getPriceFunction() != null)//if it has a price function
					{
						PriceFunction pf = pc.getPriceFunction();
						List<Usage> usageVars = pf.getUsageVariables();//fetch its usage variables
						for(Usage var : usageVars)//for each usage var, set a value.
						{
							if(!definedVariables.containsKey(var.getName().replaceAll("TIME\\d+.*", "")))
							{
								PriceVariable vart = new PriceVariable();
								vart.setName(var.getName().replaceAll("TIME\\d+.*", ""));
								vart.setDetails(var.getComment());
								vars.add(vart);
								
								definedVariables.put(var.getName().replaceAll("TIME\\d+.*", ""),var);
							}
						}
					}
				}
			}
			else
				System.out.println("Offering with a null price plan");
		}
		PricingVariables variables = new PricingVariables();
		variables.setVars(vars);
		
		definedVariables.clear();
		variables = Controller.requestVariablesInfo(variables);
		
		System.out.println(variables.getVars().size());
		
		for(PriceVariable newVar : variables.getVars())
		{
			for(Offering off : offs)//for each offering
			{
				if(off.getPricePlan() != null)//if it has a price plan
				{
					PricePlan pp = off.getPricePlan();
					for(PriceComponent pc : pp.getPriceComponents())//for every component of the priceplan
					{
						if(pc.getPriceFunction() != null)//if it has a price function
						{
							PriceFunction pf = pc.getPriceFunction();
							
							List<Usage> usageVars = pf.getUsageVariables();//fetch its usage variables
							
							for(Usage var : usageVars)//for each usage var, set a value.
							{
								if(var.getName().contains(newVar.getName()))
								{
									QuantitativeValue val = new QuantitativeValue();

									val.setValue(newVar.getVal());
									var.setValue(val);
								}
							}
						}
					}
				}
			}
		}
		
		// Create main model
		Model instance = JenaUtil.createDefaultModel();
		instance = setPrefixes(instance, ts.getMyPrefixes());
		String baseURI = "http://LinkedUSDLPricingAPImodelInstance.com#";
		for(Offering of : offs)
			of.writeToModel(instance,baseURI);
		
		
		ArrayList<FiltRes> final_res = new ArrayList<FiltRes>();
		
		for(Offering of : offs)
		{
			double ofprice = of.getPricePlan().calculatePrice(instance);
			if(priceReq!=null)//if there's a price requirement defined
			{
				if(ofprice >= priceReq.getMin() && priceReq.getMin() >= 0)
					final_res.add(new FiltRes(of,ofprice));
				else if(ofprice <= priceReq.getMax() && priceReq.getMax() >= 0)
					final_res.add(new FiltRes(of,ofprice));
			}
			else
				final_res.add(new FiltRes(of,ofprice));
		}
		
		return final_res;
	}
}
