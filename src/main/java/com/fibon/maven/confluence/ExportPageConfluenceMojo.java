package com.fibon.maven.confluence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import com.fibon.maven.confluence.model.PageDescriptor;

/**
 * 
 * @goal exportpage
 * @requiresProject false
 */
public class ExportPageConfluenceMojo extends AbstractConfluenceMojo {

	private final static String PDF = "/spaces/flyingpdf/pdfpageexport.action";

	/**
	 * Page description.
	 * 
	 * @parameter
	 * @required
	 */
	private PageDescriptor page;

	/**
	 * File to save pdf verion of newly created page.
	 * 
	 * @parameter
	 * @required
	 */
	private File outputFile;

	public ExportPageConfluenceMojo() {
	}

	public ExportPageConfluenceMojo(AbstractConfluenceMojo mojo, long pageId, File outputFile) {
		super(mojo);
		this.page = new PageDescriptor(pageId);
		this.outputFile = outputFile;
	}

	public void execute() throws MojoFailureException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Long pageId = getClient().getPageId(page);
		HttpGet request = prepareExportPageRequest(pageId);
		try {
			HttpResponse response = httpClient.execute(request);
			saveResponse(response);
		} catch (IOException e) {
			throw fail("Unable to retrieve PDF", e);
		}
	}

	private HttpGet prepareExportPageRequest(Long pageId) throws MojoFailureException {
		HttpGet get = new HttpGet(url + PDF + "?pageId=" + pageId);
		AuthenticationInfo info = wagonManager.getAuthenticationInfo(serverId);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(info.getUserName(), info.getPassword());
		BasicScheme scheme = new BasicScheme();
		try {
			Header authorizationHeader = scheme.authenticate(credentials, get);
			get.addHeader(authorizationHeader);
			return get;
		} catch (AuthenticationException e) {
			throw fail("Unable to set authentication data", e);
		}
	}

	private void saveResponse(HttpResponse response) throws MojoFailureException {
		if (response == null || response.getEntity() == null) {
			getLog().warn("Nothing to save - empty response");
		} else {
			InputStream in = null;
			FileOutputStream out = null;
			try {
				in = response.getEntity().getContent();
				out = new FileOutputStream(outputFile);
				IOUtils.copy(in, out);
			} catch (Exception e) {
				throw fail("Unable to save file", e);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
	}

}
