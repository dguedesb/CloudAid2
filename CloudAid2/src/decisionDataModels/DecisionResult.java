package decisionDataModels;

import java.util.ArrayList;

public class DecisionResult {
	
	public DecisionResult(ArrayList<GNode> adjList,int[][] adjMatrix){
		this.adjacencyList = adjList;
		this.adjacencyMatrix = adjMatrix;
	}
	
	private ArrayList<GNode> adjacencyList;
	private int[][] adjacencyMatrix;
	public ArrayList<GNode> getAdjacencyList() {
		return adjacencyList;
	}
	public void setAdjancyList(ArrayList<GNode> adjacencyList) {
		this.adjacencyList = adjacencyList;
	}
	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}
	public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}
}
