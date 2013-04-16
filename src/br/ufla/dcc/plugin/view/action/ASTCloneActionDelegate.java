package br.ufla.dcc.plugin.view.action;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import br.ufla.dcc.plugin.controller.ASTCloneController;
import br.ufla.dcc.plugin.view.views.ASTCloneView;


public class ASTCloneActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	public static final String ID_ASTCLONE_VIEW = "br.ufla.dcc.plugin.ASTClone.clonesView";
	
	private ASTCloneController astCloneController;
	
	ListSelectionDialog dialog;
	
	@Override
	public void run(IAction action) {

		if(window.getActivePage().findViewReference(ID_ASTCLONE_VIEW) != null){
			window.getActivePage().hideView(window.getActivePage().findViewReference(ID_ASTCLONE_VIEW));
		}
		try {
			window.getActivePage().showView(ID_ASTCLONE_VIEW);
		} catch (PartInitException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		ASTCloneView astCloneView = (ASTCloneView) window.getActivePage().findView(ASTCloneActionDelegate.ID_ASTCLONE_VIEW);
		
		
		this.astCloneController = new ASTCloneController();
		astCloneView.setAstCloneController(astCloneController);
		
		
		IStructuredSelection selection = (IStructuredSelection)this.getWindow().getActivePage().getSelection("org.eclipse.jdt.ui.PackageExplorer");

		Object firstElement = selection.getFirstElement();
		
		if (firstElement instanceof IJavaProject) {
			IJavaProject project = (IJavaProject) firstElement;

			this.getAstCloneController().runAstClone(project);

			astCloneView.update();
			
		}else{
			String title = "AST Clone Analysis";
			String message = "Please, select one project on Package Explorer!";
			
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
			
		}
	
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
		BasicConfigurator.configure();
		
	}

	public ASTCloneController getAstCloneController() {
		return astCloneController;
	}

	public void setAstCloneController(ASTCloneController astCloneController) {
		this.astCloneController = astCloneController;
	}


}


