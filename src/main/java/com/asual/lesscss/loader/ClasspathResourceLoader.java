/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.asual.lesscss.loader;

import java.io.InputStream;

/**
 * A {@link ResourceLoader} that loads resources from a {@link ClassLoader}.
 * 
 * @author Rafał Krzewski
 */
public class ClasspathResourceLoader extends StreamResourceLoader {

	private static final String SCHEMA = "classpath";

	private final ClassLoader classLoader;

	/**
	 * Creates a new {@link ClasspathResourceLoader}.
	 * 
	 * @param classLoader
	 *            a {@link ClassLoader} to load resources from.
	 */
	public ClasspathResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	protected String getSchema() {
		return SCHEMA;
	}

	/**
	 * Please note that path should NOT have a leading slash.
	 */
	@Override
	protected InputStream openStream(String path) {
		return classLoader.getResourceAsStream(path);
	}
}
