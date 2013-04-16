package br.ufla.dcc.plugin.view.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;

public class ASTCloneLabelProvider extends LabelProvider {

	public String getText(Object obj){
		
		String label = "";
		
		if(obj instanceof ParentObject){
			
			ParentObject parent = (ParentObject) obj;
			
			label += "Tipo de bloco: " + parent.getClones().getGroup().getNodes()[0].getText() + ", valor da métrica: " + parent.getClones().metric();

			return label;
		}else if(obj instanceof TreeObject){
			TreeObject treeObject = (TreeObject) obj;
			
			SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) treeObject.getNode());
			SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) treeObject.getNode());
			
			ANode fileNode = (ANode) treeObject.getNode();
			while (fileNode.getType() != NodeTypes.FILE.getType()) {
				fileNode = fileNode.getParent();
			}
			
			label += "Arquivo: " + fileNode.getText() + " - ";
//			label += minPos.getLine() + "." + minPos.getCharacter() + " -> ";
//			label += maxPos.getLine() + "." + maxPos.getCharacter();
			
			label += "Linha inicial: " + minPos.getLine() + ", ";
			label += "linha final: " + maxPos.getLine();
			
			return label;
			
		}
		
		return label;
	}
	
}
