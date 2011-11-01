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

	private RemotePage createPageObject(Long parentId, String content) {
		RemotePage page = new RemotePage();
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
