package br.ufla.dcc.plugin.view.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ASTCloneTreeContentProvider extends ArrayContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if(parentElement instanceof ParentObject){
			ParentObject treeObject = (ParentObject) parentElement;
			
			return treeObject.getChildren().toArray();
		}
		
		return null;
		
	}

	@Override
	public Object getParent(Object element) {
		
		if(element instanceof TreeObject){
			TreeObject treeObject = (TreeObject) element;
			return treeObject.getParent();
		}
		
		return null;
		
		
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if(element instanceof ParentObject){
			ParentObject treeObject = (ParentObject) element;
			return treeObject.getChildren().size() > 0;
		}
		
		return false;

	}

}
