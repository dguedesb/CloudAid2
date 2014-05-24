package decisionDataModels;

import java.util.ArrayList;

import searchDataModels.FiltRes;

public class GNode {
	
	
	
	private Integer out=0;
	private Integer in=0;
	private ArrayList<GNode> incomparableWith;
	private ArrayList<GNode> preferableTo;
	private int level=-1;

	private FiltRes data=null;
	
	public GNode()
	{
		incomparableWith = new ArrayList<GNode>();
		preferableTo = new ArrayList<GNode>();

	}
	public Integer getOut() {
		return out;
	}
	public void setOut(Integer out) {
		this.out = out;
	}
	public ArrayList<GNode> getIncomparableWith() {
		return incomparableWith;
	}
	public void setIncomparableWith(ArrayList<GNode> incomparableWith) {
		this.incomparableWith = incomparableWith;
	}
	public ArrayList<GNode> getPreferableTo() {
		return preferableTo;
	}
	public void setPreferableTo(ArrayList<GNode> preferableTo) {
		this.preferableTo = preferableTo;
	}
	public FiltRes getData() {
		return data;
	}
	public void setData(FiltRes data) {
		this.data = data;
	}
	public Integer getIn() {
		return in;
	}
	public void setIn(Integer in) {
		this.in = in;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
