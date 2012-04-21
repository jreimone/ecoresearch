package org.hackathon.ecoresearch.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class SearchDialogHandler extends AbstractHandler {

	private Map<EObject, View> localindex = new HashMap<EObject, View>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
		Object adapter = editor.getAdapter(EcoreDiagramEditor.class);
		if(adapter != null){
			EcoreDiagramEditor diagramEditor = (EcoreDiagramEditor) adapter;
			Diagram diagram = diagramEditor.getDiagram();
			EObject element = diagram.getElement();
			EObject model = EcoreUtil.getRootContainer(element, true);
			createLocalIndex(diagram);
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
