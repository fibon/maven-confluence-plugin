package com.fibon.maven.confluence;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.maven.plugin.MojoFailureException;

import com.atlassian.confluence.rpc.soap.beans.RemoteComment;
import com.fibon.maven.confluence.model.PageDescriptor;

/**
 * @goal addcomment
 * @requiresProject false
 */
public class AddCommentConfluenceMojo extends AbstractConfluenceMojo {

	/**
	 * Space id
	 * 
	 * @parameter
	 * @required
	 */
	private PageDescriptor page;

	/**
	 * Comment
	 * 
	 * @parameter
	 * @required
	 */
	private File commentBody;

	@Override
	public void execute() throws MojoFailureException {
		String token = getClient().getToken();
		Long pageId = getClient().getPageId(page);

		RemoteComment comment = new RemoteComment();
		comment.setPageId(pageId);
		comment.setContent(evaluateFile(commentBody));
		try {
			getClient().getService().addComment(token, comment);
		} catch (RemoteException e) {
			throw fail("Unable to upload comment", e);
		}
	}
}
