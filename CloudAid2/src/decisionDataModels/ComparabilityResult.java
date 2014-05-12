package decisionDataModels;


import java.util.ArrayList;

import searchDataModels.FiltRes;

public class ComparabilityResult{
	
	private ArrayList<FiltRes> services;
	private int[][] compareMatrix;
	
	private ArrayList<ArrayList<FiltRes>> rankedList;

	public ArrayList<ArrayList<FiltRes>> getRankedList() {
		return rankedList;
	}


	public void setRankedList(ArrayList<ArrayList<FiltRes>> rankedList) {
		this.rankedList = rankedList;
	}


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
