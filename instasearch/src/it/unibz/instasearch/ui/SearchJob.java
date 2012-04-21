/*
 * Copyright (c) 2009 Andrejs Jermakovics.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrejs Jermakovics - initial implementation
 */
package it.unibz.instasearch.ui;

import it.unibz.instasearch.indexing.Field;
import it.unibz.instasearch.indexing.SearchQuery;
import it.unibz.instasearch.indexing.SearchResultDoc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
/**
 * A search job that runs search in UI thread.
 * Is used to start search after user has stopped typing
 */
public class SearchJob extends Job {
	
	/**
	 * 
	 */
	private TreeViewer resultViewer = null;
	private SearchQuery searchQuery = null;
	private boolean selectLast = false;
	private InstaSearchView searchView;
	private IStatusLineManager statusLineManager;
	
	public SearchJob(InstaSearchView searchView)
	{
		super("Search");
		
		this.searchView = searchView;
		this.resultViewer = searchView.getResultViewer();
		this.statusLineManager = searchView.getViewSite().getActionBars().getStatusLineManager();
		
		this.addJobChangeListener(new JobChangeAdapter(){
			public void done(IJobChangeEvent event) {
				if( event.getResult() == Status.CANCEL_STATUS )
					cancelSearch();
			}
		});
		
		setPriority(Job.INTERACTIVE);
	}
	
	public void schedule(SearchQuery searchQuery, boolean selectLast, long delay) {
		cancelSearch();
		
		this.searchQuery = searchQuery;
		this.selectLast = selectLast;
		
		if( this.searchQuery != null ) {
			this.searchQuery.setCanceled(false);
			this.searchQuery.setCurrentProject( InstaSearchUI.getActiveProject() );
			
			schedule(delay);
		}
	}
	
	/**
	 * Cancel current search
	 */
	private void cancelSearch() {
		if( searchQuery != null )
			searchQuery.setCanceled(true);
	}
	
	protected IStatus run(IProgressMonitor monitor)
	{
		
		//BEGIN InstaEMFSearch modifications
		Map<Field, Set<String>> hackedFilter = new HashMap<Field, Set<String>>();
		Set<String> filterExtensions = new HashSet<String>();
		filterExtensions.add("ecore");
		filterExtensions.add("ecorediag");
		hackedFilter.put(Field.EXT, filterExtensions);
		searchQuery.setFilter(hackedFilter);
		//END InstaEMFSearch modifications
		
		
		ResultContentProvider prov = (ResultContentProvider) resultViewer.getContentProvider();
		
		if(  prov == null || monitor.isCanceled() || searchQuery == null || searchQuery.isCanceled() ) return Status.CANCEL_STATUS;
		
		Object[] results = prov.getElements(searchQuery); // runs query and caches result. running in this job to avoid UI freeze
		
		if( monitor.isCanceled() || searchQuery.isCanceled() ) return Status.CANCEL_STATUS; // perhaps we cancelled while executing query
		
		Display display = resultViewer.getControl().getDisplay();
		
		display.syncExec(new Runnable() {	
			public void run() {
				search(); // run in UI
			}
		});
		
		//BEGIN InstaEMFSearch modifications
		//!!!This is a hack!!! searching for ecore files
		for (Object result : results) {
			if (result instanceof SearchResultDoc) {
				SearchResultDoc searchResultDocument = (SearchResultDoc) result;
							
				if (searchResultDocument.getFileExtension().equals("ecore")) {

					IFile file = ((SearchResultDoc)result).getFile();
					//searchInModel(file);
					//					System.out.println("OIOIOI " + file.getName());
//					
//					Object a = searchResultDocument.getDoc();
//					Object b = searchResultDocument.getDocId();
//					
					//SearchResultDoc a = new SearchResultDoc(dir, doc, docId, score)
				}
			}
		}
		
		return Status.OK_STATUS;
	}
	
	
	
	private void search() {
		int items = resultViewer.getTree().getItemCount();
		
		resultViewer.setInput(searchQuery); // run in UI, displays cached results immediately
		
		int newItems = resultViewer.getTree().getItemCount();
		
		if( selectLast && items != 0 && newItems >= items )
			resultViewer.getTree().setSelection(resultViewer.getTree().getItem(items-1)); // show previous selection
		
		int resultCount = countSearchResults(newItems);
		
		if( searchQuery.isLimited() ) // limited
		{
			if( resultCount >= searchQuery.getMaxResults() )
				statusLineManager.setMessage(searchView.getTitleImage(), searchQuery.getMaxResults() + "+ results (Ctrl+Enter to see all)");
			else 
				statusLineManager.setMessage(searchView.getTitleImage(), resultCount + " results");
		}
		else { // not limited
			statusLineManager.setMessage(searchView.getTitleImage(), resultCount + " results");
		}
	}

	/**
	 * @param newItems
	 * @return
	 */
	private int countSearchResults(int newItems) {
		
		int i, allItems = resultViewer.getTree().getItemCount();
		
		for(i = allItems-1; i >= 0; i--)
		{
			TreeItem treeItem = resultViewer.getTree().getItem(i);
			
			if( treeItem == null || treeItem.getData() == null )
				continue;
			
			if( treeItem.getData() instanceof SearchResultDoc ) // from now on we have results
				return i+1;
		}
		
		return 0;
	}

	
}