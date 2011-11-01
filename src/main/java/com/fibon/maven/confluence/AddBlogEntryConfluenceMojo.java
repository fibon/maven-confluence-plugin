package com.fibon.maven.confluence;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.maven.plugin.MojoFailureException;

import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;

/**
 * @goal addblogentry
 * @requiresProject false
 */
public class AddBlogEntryConfluenceMojo extends AbstractConfluenceMojo {

	/**
	 * Space id
	 * 
	 * @parameter
	 * @required
	 */
	private String space;

	/**
	 * Entry title
	 * 
	 * @parameter
	 * @required
	 */
	private String entryTitle;

	/**
	 * Text file with page content
	 * 
	 * @parameter
	 * @required
	 */
	private File entryFile;

	@Override
	public void execute() throws MojoFailureException {
		RemoteBlogEntry entry = new RemoteBlogEntry();
		entry.setSpace(space);
		entry.setTitle(entryTitle);
		entry.setContent(evaluateFile(entryFile));
		try {
			getClient().getService().storeBlogEntry(getClient().getToken(), entry);
		} catch (RemoteException e) {
			throw fail("Unable to upload blog entry", e);
		}
	}

}
