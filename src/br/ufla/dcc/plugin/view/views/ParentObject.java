package br.ufla.dcc.plugin.view.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufla.dcc.plugin.model.analysis.astcloneanalysis.ASTClone;

public class ParentObject {

	private ASTClone clones;
	private ArrayList<TreeObject> children;
	
	public ParentObject(ASTClone clones){
		this.clones = clones;
		this.children = new ArrayList<TreeObject>();
	}
	
	public ParentObject(ASTClone clones, TreeObject[] children){
		this.clones = clones;
		List<TreeObject> list = Arrays.asList(children);
		this.children = new ArrayList<TreeObject>(list);
		for (int i = 0; i < this.children.size(); i++) {
		         this.children.get(i).setParent(this);
      }
	}
	
	public void addChild(TreeObject treeObject){
		treeObject.setParent(this);
		this.getChildren().add(treeObject);
	}
	
	
	public ASTClone getClones() {
		return clones;
	}

	public void setClones(ASTClone clones) {
		this.clones = clones;
	}

	public ArrayList<TreeObject> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeObject> children) {
		this.children = children;
	}
	
}
