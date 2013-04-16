package br.ufla.dcc.plugin.model.analysis.astcloneanalysis;

import java.util.HashSet;
import java.util.Set;

import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;

public class ASTClone implements Comparable<ASTClone>{

	private SimilarityGroup group;
	private int averageCloneSize;
	private int numberOfFiles;
	
	public ASTClone(SimilarityGroup group){
		this.group = group;
	}

	public void calculateAverageSize(){
		
		int amountLines = 0;
		
		for(ASourceUnit node : this.getGroup().getNodes()){
			
			final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) node);
			final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) node);
			
			amountLines += (maxPos.getLine() - minPos.getLine());
			
		}
		
		int averageLines = (amountLines / this.getGroup().getNodes().length) +1;
		
		System.out.println("averageLines: " + averageLines);
		
		this.setAverageCloneSize( averageLines );
		
	}

	public void calculateNumberOfFiles(){
		Set<String> files = new HashSet<String>();
		
		for(ASourceUnit node : this.getGroup().getNodes()){
			ANode fileNode = (ANode) node;
			while (fileNode.getType() != NodeTypes.FILE.getType()) {
				fileNode = fileNode.getParent();
			}
			
			files.add(fileNode.getText());
		}
		System.out.println("files.size(): " + files.size());
		this.setNumberOfFiles(files.size());
	}
	
	public int metric(){
		
		return (this.getAverageCloneSize() * 4  +  this.getNumberOfFiles() * 6 ) / 10;
	}
	
	public SimilarityGroup getGroup() {
		return group;
	}

	public void setGroup(SimilarityGroup group) {
		this.group = group;
	}

	public int getAverageCloneSize() {
		return averageCloneSize;
	}
	
	public void setAverageCloneSize(int averageCloneSize) {
		this.averageCloneSize = averageCloneSize;
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	
	public String toString(){
		String s = "";
		
		s += "size:  = "+ this.getAverageCloneSize()+ ", " +this.getNumberOfFiles()+ " =" + this.metric() + " \n";
		
		for(ASourceUnit node : this.getGroup().getNodes()){
			final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) node);
			final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) node);
			
			ANode fileNode = (ANode) node;
			while (fileNode.getType() != NodeTypes.FILE.getType()) {
				fileNode = fileNode.getParent();
			}
			s += "\t" + (node.getText() + " ");
			s += (fileNode.getText() + " ");

			s += (": "+minPos.getLine() + "."+minPos.getCharacter() +" -> "+maxPos.getLine() + "." + maxPos.getCharacter()) + "\n";
			
		}
		return s;
	}

	
	public int compareTo(ASTClone other) {
			
		return (this.metric() - other.metric());
		
	}
}

