package org.hackathon.ecoresearch.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.hackathon.ecoresearch.commands.IOpenEditorCommand;

public class OpenCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if(selection instanceof IStructuredSelection){
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if(firstElement instanceof EditPart){
				Object model = ((EditPart) firstElement).getModel();
				if(model instanceof View){
					try {
						View view = (View) model;
						Resource resource = view.eResource();
						//					EObject element = view.getElement();
						ICommandService commandService = (ICommandService) HandlerUtil.getActiveWorkbenchWindow(event).getService(ICommandService.class);
						IHandlerService handlerService = (IHandlerService) HandlerUtil.getActiveWorkbenchWindow(event).getService(IHandlerService.class);
						Command command = commandService.getCommand(IOpenEditorCommand.COMMAND_ID);
						IParameter resourceParameter = command.getParameter(IOpenEditorCommand.PARAM_RESOURCE);
						IParameter elementParameter = command.getParameter(IOpenEditorCommand.PARAM_ELEMENT);
						URI uri = resource.getURI();
						Parameterization resourceParam = new Parameterization(resourceParameter, uri.toPlatformString(true));
						URI elementUri = EcoreUtil.getURI(view);
						Parameterization elementParam = new Parameterization(elementParameter, elementUri.toString());
						ParameterizedCommand parameterCommand = new ParameterizedCommand(command, new Parameterization[]{resourceParam, elementParam});
						handlerService.executeCommand(parameterCommand, null);
					} catch (NotDefinedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotEnabledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotHandledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return null;
	}

}
