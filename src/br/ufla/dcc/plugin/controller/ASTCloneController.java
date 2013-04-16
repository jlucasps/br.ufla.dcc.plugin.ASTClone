package br.ufla.dcc.plugin.controller;

import java.util.HashMap;

import org.eclipse.contribution.visualiser.VisualiserPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;

import br.ufla.dcc.plugin.model.analysis.astcloneanalysis.ASTClone;
import br.ufla.dcc.plugin.model.analysis.astcloneanalysis.ASTCloneAnalysis;

public class ASTCloneController {

	public static String ID_HIGH_ASTCLONE_MARKER = "br.ufla.dcc.plugin.view.highASTCloneMarker";
	public static String ID_SELECTED_ASTCLONE_MARKER = "br.ufla.dcc.plugin.view.selectedASTCloneMarker";
	
	private ASTCloneAnalysis astCloneAnalysis;
	private HashMap<IMarker, IFile> markers;
	
	public ASTCloneController(){
		this.markers = new HashMap<IMarker, IFile>();
	}
	
	
	public void runAstClone(IJavaProject project){
		
		this.astCloneAnalysis = new ASTCloneAnalysis(project);
		
		this.astCloneAnalysis.start();
		
		try {
			this.astCloneAnalysis.join();
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		this.createMarkers();
		
		VisualiserPlugin.refresh();
		
	}
	
	
	
	public void createMarkers(){

		for(ASTClone clone : this.getAstCloneAnalysis().getASTClones()){
			
			for(ASourceUnit node : clone.getGroup().getNodes()){
				ANode fileNode = (ANode) node;
				while (fileNode.getType() != NodeTypes.FILE.getType()) {
					fileNode = fileNode.getParent();
				}
				try {
					
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					
					IPath location = Path.fromOSString(fileNode.getText());
					
					IFile iFile = workspace.getRoot().getFileForLocation(location);
				
					IMarker marker = iFile.createMarker(ASTCloneController.ID_HIGH_ASTCLONE_MARKER);
					
					
					marker.setAttribute(IMarker.MESSAGE, "AST Clone: " + clone.metric());
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					
					SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) node);

					marker.setAttribute(IMarker.LINE_NUMBER, minPos.getLine());

					this.markers.put(marker, iFile);
					
				} catch (CoreException e) {
					System.out.println(e);
					e.printStackTrace();
				}
				
			}
			
			
		}
	}


	public ASTCloneAnalysis getAstCloneAnalysis() {
		return astCloneAnalysis;
	}


	public void setAstCloneAnalysis(ASTCloneAnalysis astCloneAnalysis) {
		this.astCloneAnalysis = astCloneAnalysis;
	}
	
	
}
