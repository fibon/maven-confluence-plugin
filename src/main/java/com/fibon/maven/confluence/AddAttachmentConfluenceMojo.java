package com.fibon.maven.confluence;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.fibon.maven.confluence.model.PageDescriptor;

import eu.medsea.mimeutil.MimeUtil;

/**
 * @goal addattachment
 * @requiresProject false
 */
public class AddAttachmentConfluenceMojo extends AbstractConfluenceMojo {

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
	 * @parameter default-value=""
	 */
	private String comment;

	/**
	 * Files to attach
	 * 
	 * @parameter
	 * @required
	 */
	private File[] attachments;

	public AddAttachmentConfluenceMojo() {
	}

	public AddAttachmentConfluenceMojo(AbstractConfluenceMojo mojo, long pageId, File[] attachments) {
		super(mojo);
		this.page = new PageDescriptor(pageId);
		this.attachments = attachments;
	}

	@Override
	public void execute() throws MojoFailureException {
		String token = getClient().getToken();
		Long pageId = getClient().getPageId(page);
		for (File file : attachments) {
			addAttachment(token, pageId, file);
		}
	}

	private void addAttachment(String token, Long pageId, File file) throws MojoFailureException {
		RemoteAttachment attachment = new RemoteAttachment();
		attachment.setPageId(pageId);
		attachment.setComment(comment);
		attachment.setFileName(file.getName());
		try {
			byte[] content = FileUtils.readFileToByteArray(file);
			String type = MimeUtil.getMimeTypes(file).iterator().next().toString();
			attachment.setContentType(type);
			attachment = getClient().getService().addAttachment(token, attachment, content);
		} catch (Exception e) {
			throw fail("Unable to upload attachment", e);
		}
	}

}
