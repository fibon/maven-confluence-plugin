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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.Map.Entry;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class TemplateEvaluator {

	private final VelocityEngine engine = new VelocityEngine();

	private final VelocityContext context = new VelocityContext();

	public TemplateEvaluator(MavenProject project) {
		if (project != null) {
			context.put("project", project);
			for (Entry<Object, Object> p : project.getProperties().entrySet()) {
				context.put(p.getKey().toString(), p.getValue());
			}
		}
	}

	public String evalutate(File file) throws FileNotFoundException {
		FileReader reader = new FileReader(file);
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "[Confluence]", reader);
		return writer.toString();
	}

}
