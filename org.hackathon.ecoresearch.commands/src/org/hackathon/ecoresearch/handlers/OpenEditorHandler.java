package org.hackathon.ecoresearch.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
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
			if(uri.isFile()){
				String path = uri.path();
				IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(path);
//				String id = defaultEditor.getId();
				if(element instanceof View){
					// GMF Diagram Editor
					View view = (View) element;
					Diagram diagram = (Diagram) EcoreUtil.getRootContainer(view, true);
//					DiagramEditorInput input = new DiagramEditorInput(diagram);
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IPath workspacePath = new Path(path);
						IFile editorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(workspacePath);
						IEditorPart openEditor = IDE.openEditor(page, editorFile);
//						IEditorPart editor = IDE.openEditor(page, input, id, true);
//						IEditorPart editor = page.openEditor(input, id, true);
						System.out.println(openEditor);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				} else if(element instanceof EObject){
					// Ecore Tree Editor
					
				}
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId, activate)
//				PlatformUI.getWorkbench().getEditorRegistry().
				
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
