package br.ufla.dcc.plugin.view.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;

import br.ufla.dcc.plugin.controller.ASTCloneController;

public class ASTCloneView extends ViewPart {

	private TreeViewer treeViewer;
	private ASTCloneController astCloneController;

	private List<IMarker> markers;

	public ASTCloneView() {
		this.markers = new ArrayList<IMarker>();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.treeViewer = new TreeViewer(parent, SWT.SINGLE);
		this.treeViewer.setContentProvider(new ASTCloneTreeContentProvider());
		this.treeViewer.setLabelProvider(new ASTCloneLabelProvider());

//		this.getSite().setSelectionProvider(this.treeViewer);
		
	}

	@Override
	public void setFocus() {
		this.treeViewer.getControl().setFocus();

	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public ASTCloneController getAstCloneController() {
		return astCloneController;
	}

	public void setAstCloneController(ASTCloneController astCloneController) {
		this.astCloneController = astCloneController;
		this.getTreeViewer().addSelectionChangedListener(new ClickListener(this.astCloneController, this));
	}

	public void update() {
		ParentObject[] parents = new ParentObject[this.getAstCloneController().getAstCloneAnalysis().getASTClones().size()];

		// for(int i=parents.length-1; i>=0; i--){
		for (int i = 0; i < parents.length; i++) {
			ParentObject parent = new ParentObject(this.getAstCloneController().getAstCloneAnalysis().getASTClones().get(i));

			for (ASourceUnit child : this.getAstCloneController().getAstCloneAnalysis().getASTClones().get(i).getGroup().getNodes()) {
				TreeObject treeObject = new TreeObject(child, parent);
				parent.addChild(treeObject);
			}

			parents[i] = parent;
		}

		this.treeViewer.setInput(parents);
		this.treeViewer.refresh();
	}

	class ClickListener implements ISelectionChangedListener {

//		private ASTCloneController astCloneController;
		private ASTCloneView astCloneView;
		
		public ClickListener(ASTCloneController astCloneController, ASTCloneView astCloneView) {
//			this.astCloneController = astCloneController;
			this.astCloneView = astCloneView;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			
			for(IMarker m : this.getAstCloneView().getMarkers()){
				try {
					m.delete();
				} catch (CoreException e) {
					System.out.println();
					e.printStackTrace();
				}
			}
			
			if (obj instanceof ParentObject) {

				ParentObject parentObject = (ParentObject) obj;

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				try {
					for (ASourceUnit unit : parentObject.getClones().getGroup().getNodes()) {

						ANode fileNode = (ANode) unit;
						while (fileNode.getType() != NodeTypes.FILE.getType()) {
							fileNode = fileNode.getParent();
						}

						IPath location = Path.fromOSString(fileNode.getText());

						IFile iFile = workspace.getRoot().getFileForLocation(location);

						IMarker marker = iFile.createMarker(ASTCloneController.ID_SELECTED_ASTCLONE_MARKER);
						SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) unit);

						marker.setAttribute(IMarker.LINE_NUMBER, minPos.getLine());
						marker.setAttribute(IMarker.MESSAGE, "Selected Clone");
						
						this.getAstCloneView().getMarkers().add(marker);
						
						
					}

					
					
				} catch (CoreException core) {
					System.out.println(core);
					core.printStackTrace();
				}

				/*
				 * Acho q a alteração no outro view deve ser feita aqui.
				 */
				//ESTA BAGAÇA AQUI NAO FUNCIONA
//				int i = 0;
//				i++;
//				if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(VisualiserPlugin.PLUGIN_ID)!= null){
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(VisualiserPlugin.PLUGIN_ID));
//				}
//				try {
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VisualiserPlugin.PLUGIN_ID);
//				} catch (PartInitException e) {
//					System.out.println(e);
//					e.printStackTrace();
//				}
				/* Fim da manipulação do outro view */

			} else if (obj instanceof TreeObject) {
				TreeObject treeObject = (TreeObject) obj;
				try {
					
					// System.out.println(treeObject.getMethod().getDeclaringType().getElementName()
					// + "."+treeObject.getMethod().getElementName());
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					
					IWorkspace workspace = ResourcesPlugin.getWorkspace();

					ANode fileNode = (ANode) treeObject.getNode();
					while (fileNode.getType() != NodeTypes.FILE.getType()) {
						fileNode = fileNode.getParent();
					}

					IPath location = Path.fromOSString(fileNode.getText());

					IFile iFile = workspace.getRoot().getFileForLocation(location);

					/*Criei este marker apenas para facilitar o jump para o trecho do código*/
					IMarker marker = iFile.createMarker(ASTCloneController.ID_HIGH_ASTCLONE_MARKER);
					SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) treeObject.getNode());

					marker.setAttribute(IMarker.LINE_NUMBER, minPos.getLine());

					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), iFile,true);

					IDE.gotoMarker(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), marker);

					marker.delete();
					
				} catch (PartInitException e) {
					System.out.println(e);
					e.printStackTrace();
				} catch (CoreException core) {
					System.out.println(core);
					core.printStackTrace();
				}

			}

		}

		public ASTCloneView getAstCloneView() {
			return astCloneView;
		}

		public void setAstCloneView(ASTCloneView astCloneView) {
			this.astCloneView = astCloneView;
		}

		
		
		
	}

	public List<IMarker> getMarkers() {
		return markers;
	}

	public void setMarkers(List<IMarker> markers) {
		this.markers = markers;
	}

}
