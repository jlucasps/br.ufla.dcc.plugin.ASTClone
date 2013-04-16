package br.ufla.dcc.plugin.analysis.faninanalysis;

import java.util.HashMap;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class OverriddenThread extends Thread {

	private IMethod method; 
	private IMethodBinding methodBinding;
	private HashMap<IMethod, IMethodBinding> chunk;
	HashMap<IMethod, IMethodBinding> overriddens;
	
	
	public OverriddenThread(HashMap<IMethod, IMethodBinding> chunck, IMethod method, IMethodBinding methodBinding){
		this.chunk = chunck;
		this.method = method;
		this.methodBinding = methodBinding;
		
		this.overriddens = new HashMap<IMethod, IMethodBinding>();
		
	}
	
	public void run(){
		/*Analisa todos os m�todos do software*/
		for(IMethod m : this.getChunk().keySet()){
			/*Para cada m�todo m do software, o seu binding 
			 * � armazenado em mb*/
			IMethodBinding mb = this.getChunk().get(m);
			
			/*Veririca se um m�todo � uma sub assinatura do outro.
			 * Consultar se��o 8.4.2 of The Java Language Specification, 
			 * Third Edition (JLS3) para mais informa��es */
			boolean isSubsignature = mb.isSubsignature(this.methodBinding) || this.methodBinding.isSubsignature(mb);
			
			/*Recupera as classes onde cada m�todo est� declarado*/
			IType methodType = (IType) this.method.getParent();
			IType mType = (IType) m.getParent();
			
			try{
				/*Recupera a hierarquia de cada uma das classes.*/
				ITypeHierarchy methodHierarchy = methodType.newSupertypeHierarchy(new NullProgressMonitor());
				ITypeHierarchy mHierarchy = mType.newSupertypeHierarchy(new NullProgressMonitor());
			
				/*Verifica se um m�todo � uma sub assinatura do outro;
				 * se a classe de um dos m�todos est� na hierarquia de classes
				 *    do outro m�todo e;
				 * se n�o est� analisando para o mesmo m�todo*/
				if( ( isSubsignature ) && 
					( methodHierarchy.contains(mType) || mHierarchy.contains(methodType) ) && 
					( !methodType.equals(mType) ) ){
					
					/*Caso estas condi��es sejam satisfeitas, o m�todo m 
					 * e seu respectivo binding � armazenado no conjunto de 
					 * m�todo que sobrescrevem ou s�o sobrescritos pelo
					 * par�metro method*/
					this.overriddens.put(m, mb);
				}
				
			}catch(JavaModelException modelException){
				System.out.println(modelException);
			}
		}
	}

	public HashMap<IMethod, IMethodBinding> getChunk() {
		return chunk;
	}

	public void setChunk(HashMap<IMethod, IMethodBinding> chunck) {
		this.chunk = chunck;
	}

	public IMethod getMethod() {
		return method;
	}

	public void setMethod(IMethod method) {
		this.method = method;
	}

	public IMethodBinding getMethodBinding() {
		return methodBinding;
	}

	public void setMethodBinding(IMethodBinding methodBinding) {
		this.methodBinding = methodBinding;
	}

	public HashMap<IMethod, IMethodBinding> getOverriddens() {
		return overriddens;
	}

	public void setOverriddens(HashMap<IMethod, IMethodBinding> overriddens) {
		this.overriddens = overriddens;
	}
	
	
	
}
