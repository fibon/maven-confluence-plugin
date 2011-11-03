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
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
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
 * @goal exportpage
 * @requiresProject false
 */
public class ExportPageConfluenceMojo extends AbstractConfluenceMojo {

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
		String extension = FilenameUtils.getExtension(outputFile.getPath());
		Format format = selectFormat(extension);
		if (format == null) {
			throw new MojoFailureException("Format " + format + " is not suported");
		} else {
			Long pageId = getClient().getPageId(page);
			HttpGet request = prepareExportPageRequest(format, pageId);
			downloadFile(request);
		}
	}

	private Format selectFormat(String extension) {
		for (Format format : Format.values()) {
			if (format.name().equalsIgnoreCase(extension)) {
				return format;
			}
		}
		return null;

	}

	private HttpGet prepareExportPageRequest(Format format, Long pageId) throws MojoFailureException {
		HttpGet get = new HttpGet(url + format.url + "?pageId=" + pageId);
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

	private void downloadFile(HttpGet request) throws MojoFailureException {
		InputStream in = null;
		FileOutputStream out = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			if (response == null || response.getEntity() == null) {
				getLog().warn("Nothing to save - empty response");
			} else {
				in = response.getEntity().getContent();
				out = new FileOutputStream(outputFile);
				IOUtils.copy(in, out);
			}
		} catch (Exception e) {
			throw fail("Unable to download page", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	static enum Format {
		PDF("/spaces/flyingpdf/pdfpageexport.action"), DOC("/exportword");

		private String url;

		private Format(String url) {
			this.url = url;
		}

	}

}
