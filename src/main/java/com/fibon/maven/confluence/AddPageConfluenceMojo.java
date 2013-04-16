/*
 * Copyright 2011 Tomasz Maciejewski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fibon.maven.confluence;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.fibon.maven.confluence.model.PageDescriptor;

/**
 * @goal addpage
 * @requiresProject false
 */
public class AddPageConfluenceMojo extends AbstractConfluenceMojo {

	/**
	 * Page's parent descriptor
	 * 
	 * @parameter
	 * @required
	 */
	private PageDescriptor parent;

	/**
	 * Page title
	 * 
	 * @parameter
	 * @required
	 */
	private String pageTitle;

	/**
	 * Text file with page content
	 * 
	 * @parameter
	 * @required
	 */
	private File inputFile;

	/**
	 * File to save exported verion of newly created page.
	 * 
	 * @parameter
	 */
	private File outputFile;

	/**
	 * Attachments to add
	 * 
	 * @parameter
	 */
	private File[] attachments;

	@Override
	public void execute() throws MojoFailureException {
		Long parentId = getClient().getPageId(parent);
		String content = preparePageContent();
		RemotePage page = createPageObject(parentId, content);
		uploadPage(page);
	}

	private String preparePageContent() throws MojoFailureException {
		try {
			return getEvaluator().evalutate(inputFile);
		} catch (FileNotFoundException e) {
			throw fail("Unable to evaluate page content", e);
		}
	}

	private RemotePage createPageObject(Long parentId, String content) throws MojoFailureException {
		RemotePage page = getClient().getPage(parent.getSpace(), pageTitle);
		if( page == null )
			page = new RemotePage();
		page.setTitle(pageTitle);
		if (parentId != null) {
			page.setParentId(parentId);
		}
		page.setContent(content);
		page.setSpace(parent.getSpace());
		return page;
	}

	private void uploadPage(RemotePage page) throws MojoFailureException {
		try {
			RemotePage created = getClient().getService().storePage(getClient().getToken(), page);
			if (!ArrayUtils.isEmpty(attachments)) {
				new AddAttachmentConfluenceMojo(this, created.getId(), attachments).execute();
			}
			if (outputFile != null) {
				new ExportPageConfluenceMojo(this, created.getId(), outputFile).execute();
			}
		} catch (RemoteException e) {
			throw fail("Unable to upload page", e);
		}
	}
}
