package br.ufla.dcc.plugin.model.analysis.astcloneanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.CompleteToBlock;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodDeclarationNames;


public class ASTCloneAnalysis extends Thread {

	private static Logger log = Logger.getLogger(ASTCloneAnalysis.class);

	private ArrayList<ASTClone> astClones;
//	private HashMap<String, Integer> weightOfFile;
	
	private IJavaProject project;
	private ArrayList<JCCDFile> files;

	public ASTCloneAnalysis(IJavaProject project) {
		this.project = project;
		this.files = new ArrayList<JCCDFile>();
		
		this.astClones = new ArrayList<ASTClone>();
//		this.weightOfFile = new HashMap<String, Integer>();
		
	}

	public void run() {
		try {

			long begin = System.currentTimeMillis();
			this.startASTClone();
			long end = System.currentTimeMillis();
			
			System.out.println("Execution time: " + (end - begin) / 1000);
		} catch (JavaModelException modelException) {
			System.out.println(modelException.getMessage());
		} catch (CoreException coreException) {
			System.out.println(coreException.getMessage());
		}

	}

	public void startASTClone() throws CoreException, JavaModelException {

		log.info("Análise Iniciada: " + Calendar.getInstance().getTime());

//		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
//			IJavaProject javaProject = JavaCore.create(this.project);
			this.readProjectInfo(this.project);
//		}
		
		APipeline detector = new ASTDetector();

		detector.addOperator(new GeneralizeMethodDeclarationNames());
//		detector.addOperator(new GeneralizeVariableNames());
		detector.addOperator(new CompleteToBlock());
//		detector.addOperator(new GeneralizeMethodArgumentTypes());
//		detector.addOperator(new GeneralizeMethodReturnTypes());
//		detector.addOperator(new GeneralizeVariableDeclarationTypes());
//		detector.addOperator(new GeneralizeClassDeclarationNames());
		

		detector.setSourceFiles(this.getFiles().toArray(new JCCDFile[this.getFiles().size()]));  

		this.readSimilarityGroups(detector.process().getSimilarityGroups());
		
		
		for(ASTClone c : this.getASTClones()){
			
			c.calculateAverageSize();
			c.calculateNumberOfFiles();
			
//
//			System.out.println("Metric (NumFiles * AvgSize) = " +"("+c.getNumberOfFiles()+"  * "+c.getAverageCloneSize()+" ) " + c.getNumberOfFiles()*c.getAverageCloneSize() + " ");
//						
//			for(ASourceUnit node : c.getGroup().getNodes()){
//				final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) node);
//				final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) node);
//				
//				ANode fileNode = (ANode) node;
//				while (fileNode.getType() != NodeTypes.FILE.getType()) {
//					fileNode = fileNode.getParent();
//				}
//				System.out.print(node.getText() + " ");
//				System.out.print(fileNode.getText() + " ");
//
//				System.out.println(": "+minPos.getLine() + "."+minPos.getCharacter() +" -> "+maxPos.getLine() + "." + maxPos.getCharacter());
//				
//			}
//			
		}
	
		
		Collections.sort(this.getASTClones());
		Collections.reverse(this.getASTClones());
		
		for(ASTClone c : this.getASTClones()){
			System.out.println(c);
		}
		
		log.info("Análise Concluida: " + Calendar.getInstance().getTime());
	}

	private void readProjectInfo(IJavaProject javaProject)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();

		for (IPackageFragment mypackage : packages) {

			/* Analisa pacotes apenas do tipo source, descosidera os binários */
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {

				/* Analisa as CompilationUnits do pacote em questão. */
				this.readIPackageInfo(mypackage);

			}

		}
	}

	private void readIPackageInfo(IPackageFragment mypackage)
			throws JavaModelException {

		/* Para todas as CompilationsUnit do pacote */
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resourceInRuntimeWorkspace = root.findMember(unit.getPath());
			File file = new File(resourceInRuntimeWorkspace.getLocationURI());
			
			this.getFiles().add( new JCCDFile(file));
			
		}
	}
	
	public void readSimilarityGroups(SimilarityGroup[] simGroups){

		if(simGroups == null){
			simGroups = new SimilarityGroup[0];
		}
		if(simGroups != null && simGroups.length > 0){
			
			for(int i=0; i<simGroups.length; i++){
				
				ASTClone c = new ASTClone(simGroups[i]);
				
				this.astClones.add(c);
				
			}	
		}
	}
	
//	public void calculateWeightOfFile(){
//		
//		for(ASTClone c : this.getClones()){
//			
//			for(ASourceUnit node : c.getGroup().getNodes()){
//				
//				ANode fileNode = (ANode) node;
//				while (fileNode.getType() != NodeTypes.FILE.getType()) {
//					fileNode = fileNode.getParent();
//				}
//				String file = fileNode.getText();
//				if(this.getWeightOfFile().containsKey(file)){
//					int current = this.getWeightOfFile().get(file);
//					this.getWeightOfFile().put(file, current + 1);
//				}else{
//					this.getWeightOfFile().put(file, 1);
//				}
//				
//			}
//			
//			
//		}
//		
//	}
	
	
	public ArrayList<ASTClone> getASTClones() {
		return astClones;
	}

	public void setASTClones(ArrayList<ASTClone> clones) {
		this.astClones = clones;
	}

	public ArrayList<JCCDFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<JCCDFile> files) {
		this.files = files;
	}

//	public HashMap<String, Integer> getWeightOfFile() {
//		return weightOfFile;
//	}
//
//	public void setWeightOfFile(HashMap<String, Integer> weightOfFile) {
//		this.weightOfFile = weightOfFile;
//	}
	
	
	
	
}
