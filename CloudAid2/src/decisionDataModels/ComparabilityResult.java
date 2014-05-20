package decisionDataModels;


import java.util.ArrayList;

import searchDataModels.FiltRes;

public class ComparabilityResult{
	
	private ArrayList<FiltRes> services;
	private int[][] compareMatrix;



	public ArrayList<FiltRes> getServices() {
		return services;
	}


	public void setServices(ArrayList<FiltRes> services) {
		this.services = services;
	}


	public int[][] getCompareMatrix() {
		return compareMatrix;
	}


	public void setCompareMatrix(int[][] compareMatrix) {
		this.compareMatrix = compareMatrix;
	}


	public ComparabilityResult(ArrayList<FiltRes> serv, int[][] compare){
		this.services = serv;
		this.compareMatrix = compare;
	}



}
