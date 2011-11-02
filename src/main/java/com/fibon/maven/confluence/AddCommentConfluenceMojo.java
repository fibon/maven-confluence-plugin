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
