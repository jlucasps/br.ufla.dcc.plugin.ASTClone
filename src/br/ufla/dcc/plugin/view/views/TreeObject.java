package br.ufla.dcc.plugin.view.views;

import org.eposoft.jccd.data.ASourceUnit;

public class TreeObject {

	private ASourceUnit node;
	private ParentObject parent;
	
	public TreeObject(ASourceUnit node, ParentObject parent) {
		this.node = node;
		this.parent = parent;
	}
	public ASourceUnit getNode() {
		return node;
	}
	public void setNode(ASourceUnit node) {
		this.node = node;
	}
	public ParentObject getParent() {
		return parent;
	}
	public void setParent(ParentObject parent) {
		this.parent = parent;
	}
	
}
