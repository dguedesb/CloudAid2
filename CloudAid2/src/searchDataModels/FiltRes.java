package searchDataModels;

import java.util.ArrayList;
import java.util.HashMap;

import usdl.servicemodel.Offering;

public class FiltRes {
	private static int ID=0;
	private Offering myOff;
	private double myPrice;
	private HashMap<String,String> critAttr;
	private HashMap<String, Double> normalizedAttributes;
	private String myID;
	public Integer inferior=0;
	public ArrayList<FiltRes> incomparableWith;
	public int myRank=-1;
	public FiltRes(Offering of, double price)
	{
		FiltRes.ID++;
		this.setMyID("Alt"+ID);
		this.myOff = of;
		this.myPrice = price;
		this.critAttr =new HashMap<String,String>();
		this.normalizedAttributes = new HashMap<String,Double>();
		incomparableWith = new ArrayList<FiltRes>();
	}
	
	public FiltRes()
	{FiltRes.ID++;this.setMyID("Alt"+ID);}
	
	public HashMap<String, Double> getNormalizedAttributes() {
		return normalizedAttributes;
	}

	public void setNormalizedAttributes(HashMap<String, Double> normalizedAttributes) {
		this.normalizedAttributes = normalizedAttributes;
	}
	
	public HashMap<String, String> getCritAttr() {
		return critAttr;
	}

	public void setCritAttr(HashMap<String, String> critAttr) {
		this.critAttr = critAttr;
	}
	
	public Offering getMyOff() {
		return myOff;
	}

	public void setMyOff(Offering myOff) {
		this.myOff = myOff;
	}

	public double getMyPrice() {
		return myPrice;
	}

	public void setMyPrice(double myPrice) {
		this.myPrice = myPrice;
	}

	public String getMyID() {
		return myID;
	}

	public void setMyID(String myID) {
		this.myID = myID;
	}
	
	public static void resetIDCounter()
	{
		FiltRes.ID=0;
	}
	
}
