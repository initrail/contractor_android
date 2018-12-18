package com.integrail.networkers.data_representations.project_representations;

public class ProjectFlags {
	private int exteriorSkills;
	private int interiorSkills;
	public ProjectFlags(int e, int i){
		setInteriorSkills(i);
		setExteriorSkills(e);
	}
	public int getInteriorSkills() {
		return interiorSkills;
	}
	public void setInteriorSkills(int interiorSkills) {
		this.interiorSkills = interiorSkills;
	}
	public int getExteriorSkills() {
		return exteriorSkills;
	}
	public void setExteriorSkills(int exteriorSkills) {
		this.exteriorSkills = exteriorSkills;
	}
}

