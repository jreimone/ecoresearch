package org.hackathon.ecoresearch.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.hackathon.ecoresearch.commands.IOpenEditorCommand;

public class OpenEditorHandler extends AbstractHandler {

	private Map<EObject, View> localindex = new HashMap<EObject, View>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object resourceObject = event.getObjectParameterForExecution(IOpenEditorCommand.PARAM_RESOURCE);
		Object elementObject = event.getObjectParameterForExecution(IOpenEditorCommand.PARAM_ELEMENT);
		if(resourceObject instanceof Resource && elementObject instanceof EObject){
			Resource resource = (Resource) resourceObject;
			EObject element = (EObject) elementObject;
			URI uri = resource.getURI();
			if(uri.isPlatformResource()){
				String platformString = uri.toPlatformString(true);
				String path = uri.path();
				String fileString = uri.toFileString();
				IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(path);
				System.out.println(defaultEditor);
				
//				IEditorPart editor = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
//				Object adapter = editor.getAdapter(EcoreDiagramEditor.class);
//				if(adapter != null){
//					EcoreDiagramEditor diagramEditor = (EcoreDiagramEditor) adapter;
//					Diagram diagram = diagramEditor.getDiagram();
//					EObject element = diagram.getElement();
//					EObject model = EcoreUtil.getRootContainer(element, true);
//					createLocalIndex(diagram);
//				}
				
			}
			
		}
		return null;
	}


	/**
	 * @param diagram
	 */
	@SuppressWarnings("unchecked")
	private void createLocalIndex(Diagram diagram) {
		List<View> visibleChildren = (List<View>) diagram.getVisibleChildren();
		for (View view : visibleChildren) {
			localindex.put(view.getElement(), view);
		}
	}

}
