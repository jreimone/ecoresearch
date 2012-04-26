package org.hackathon.ecoresearch.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditor;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.hackathon.ecoresearch.ui.IOpenEditorHandler;

public class OpenEditorHandler implements IOpenEditorHandler {

	private Map<EObject, View> localindex = new HashMap<EObject, View>();

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


	@Override
	public IEditorPart openEditorBySearchResultSelection(EObject selectedResult) {
		if(selectedResult == null){
			return null;
		}
		Resource resource = selectedResult.eResource();
		URI uri = resource.getURI();
		if(uri.isPlatformResource()){
			try {
				String platformUriString = uri.toString();
				//					IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(path);
				//				String id = defaultEditor.getId();
				uri = resource.getResourceSet().getURIConverter().normalize(uri);
				if (uri.segmentCount() > 1 && "resource".equals(uri.segment(0))) {
					StringBuffer platformResourcePath = new StringBuffer();
					for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
						platformResourcePath.append('/');
						platformResourcePath.append(uri.segment(j));
					}
					platformUriString = platformResourcePath.toString();
				} 
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IPath workspacePath = new Path(platformUriString);
				IFile editorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(workspacePath);
				IEditorPart openEditor = IDE.openEditor(page, editorFile);
				if(openEditor instanceof EcoreDiagramEditor){
					// GMF Diagram Editor
					EcoreDiagramEditor diagramEditor = (EcoreDiagramEditor) openEditor;
					//					diagramEditor.getDiagramGraphicalViewer().
					View view = (View) selectedResult;	
					Diagram diagram = (Diagram) EcoreUtil.getRootContainer(view, true);

				} else if (openEditor instanceof EcoreEditor){
					// Ecore Tree Editor
					EcoreEditor treeEditor = (EcoreEditor) openEditor;
					List<EObject> selectedElement = new ArrayList<EObject>();
					EditingDomain editingDomain = treeEditor.getEditingDomain();
					ResourceSet resourceSet = editingDomain.getResourceSet();
					URI resolveUri = EcoreUtil.getURI(selectedResult);
					selectedResult = resourceSet.getEObject(resolveUri, true);
					selectedElement.add(selectedResult);
					IStructuredSelection selection = new StructuredSelection(selectedElement);
					treeEditor.getViewer().setSelection(selection);
				}
				return openEditor;
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
