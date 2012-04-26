package org.hackathon.ecoresearch.commands.converter;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class EObjectConverter extends AbstractParameterValueConverter {

	public EObjectConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
		URI uri = URI.createURI(parameterValue);
		ResourceSet rs = new ResourceSetImpl();
		EObject element = rs.getEObject(uri, true);
		return element;
	}

	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException {
		URI uri = EcoreUtil.getURI((EObject) parameterValue);
		return uri.toPlatformString(true);
	}

}
