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

package com.fibon.maven.confluence.helpers;

import java.net.URL;
import java.rmi.RemoteException;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap_axis.confluenceservice_v2.ConfluenceSoapService;
import com.atlassian.confluence.rpc.soap_axis.confluenceservice_v2.ConfluenceSoapServiceServiceLocator;
import com.fibon.maven.confluence.model.PageDescriptor;

public class ConfluenceClient {

	private final static String V1_API = "/rpc/soap-axis/confluenceservice-v1";

	private final static String V2_API = "/rpc/soap-axis/confluenceservice-v2";

	private final ConfluenceSoapService service;

	private final String token;

	public ConfluenceClient(String username, String password, URL url, boolean v2api) throws Exception {
		ConfluenceSoapServiceServiceLocator locator = new ConfluenceSoapServiceServiceLocator();
		url = new URL(url.toExternalForm() + (v2api ? V2_API : V1_API));
		service = locator.getConfluenceserviceV2(url);
		token = service.login(username, password);
	}

	public Long getPageId(PageDescriptor descriptor) {
		if (descriptor.isAbsolute()) {
			return descriptor.getId();
		} else if (descriptor.isRelative()) {
			try {
				RemotePage page = service.getPage(token, descriptor.getSpace(), descriptor.getTitle());
				return page != null ? page.getId() : null;
			} catch (RemoteException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public ConfluenceSoapService getService() {
		return service;
	}

	public String getToken() {
		return token;
	}

}
