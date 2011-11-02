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
import java.net.URL;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import com.fibon.maven.confluence.helpers.ConfluenceClient;
import com.fibon.maven.confluence.helpers.TemplateEvaluator;

public abstract class AbstractConfluenceMojo extends AbstractMojo {

	/**
	 * Server id corresponding to entry within <i>settings.xml</i>
	 * 
	 * @parameter expression="${confluence.server}"
	 */
	protected String serverId;

	/**
	 * URL pointing to Confluence server, i.e:
	 * <ul>
	 * <li>https://developer.atlassian.com</li>
	 * <li>http://www.example.org/confluence/</li>
	 * </ul>
	 * 
	 * @parameter expression="${confluence.url}"
	 * @required
	 */
	protected URL url;

	/**
	 * Whether to use v2 API instead of v1 which is the default one
	 * 
	 * @parameter expression="${confluence.v2api}" default-value="false"
	 */
	protected boolean v2api;

	/**
	 * The Maven project
	 * 
	 * @parameter default-value="${project}"
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The Maven Wagon manager to use when obtaining server authentication details.
	 * 
	 * @component role="org.apache.maven.artifact.manager.WagonManager"
	 * @required
	 * @readonly
	 */
	protected WagonManager wagonManager;

	private TemplateEvaluator evaluator;

	private ConfluenceClient client;

	public AbstractConfluenceMojo() {
	}

	public AbstractConfluenceMojo(AbstractConfluenceMojo mojo) {
		this.serverId = mojo.serverId;
		this.url = mojo.url;
		this.v2api = mojo.v2api;
		this.project = mojo.project;
		this.wagonManager = mojo.wagonManager;
		this.evaluator = mojo.evaluator;
		this.client = mojo.client;
		this.setLog(mojo.getLog());
		this.setPluginContext(mojo.getPluginContext());
	}

	public TemplateEvaluator getEvaluator() {
		if (evaluator == null) {
			getLog().debug("Initializing Template Helper...");
			evaluator = new TemplateEvaluator(project);
			getLog().debug("Template Helper initialized");
		}
		return evaluator;
	}

	public ConfluenceClient getClient() throws MojoFailureException {
		if (client == null) {
			getLog().debug("Connecting to Confluence server");
			try {
				AuthenticationInfo info = wagonManager.getAuthenticationInfo(serverId);
				client = new ConfluenceClient(info.getUserName(), info.getPassword(), url, v2api);
				getLog().info("Successfuly connected to Confluence server");
			} catch (Exception e) {
				throw fail("Unable to connect to Confluence server", e);
			}
		}
		return client;
	}

	protected String evaluateFile(File file) throws MojoFailureException {
		try {
			getLog().info("Evaluating file: " + file.getName());
			return getEvaluator().evalutate(file);
		} catch (FileNotFoundException e) {
			throw fail("Unable to evaluate file", e);
		}
	}

	protected MojoFailureException fail(String message, Exception e) {
		getLog().error(message, e);
		return new MojoFailureException(e, message, e.getMessage());
	}

}
