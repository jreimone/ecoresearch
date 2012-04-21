package org.hackathon.ecoresearch.commands.converter;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class ResourceConverter extends AbstractParameterValueConverter {

	public ResourceConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
		URI uri = URI.createPlatformResourceURI(parameterValue, true);
		ResourceSet rs = new ResourceSetImpl();
		return rs.getResource(uri, true);
	}

	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException {
		Resource resource = (Resource) parameterValue;
		URI uri = resource.getURI();
		return uri.toPlatformString(true);
	}

}
