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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A base class for loader implementations.
 * 
 * Each loader implementation is associated with a URL schema name. When
 * requested resource path starts with the loader's associated schema, schema
 * name is stripped and lookup is retried with plain name instead. Paths
 * starting with other schema names are rejected, plain names are checked using
 * {@link #openStream(String)} - a non {@code null} return value indicates valid
 * resource.
 * 
 * @author Rafał Krzewski
 */
public abstract class StreamResourceLoader implements ResourceLoader {

	private static final Pattern PATTERN = Pattern.compile("^([\\w]{2,}):(.*)");

	/**
	 * Returns the schema name associated with the loader.
	 * 
	 * @return schema name.
	 */
	protected abstract String getSchema();

	/**
	 * Returns an {@link InputStream} for reading the resource.
	 * 
	 * @param path
	 *            the path of the resource.
	 * @return an {@link InputStream} or {@code null} if the resource does not
	 *         exist.
	 * @throws IOException
	 *             when i/o operation fails.
	 */
	protected abstract InputStream openStream(String path) throws IOException;

	@Override
	public boolean exists(String resource, String[] paths) throws IOException {
		// check if the resource imported absolutely under this schema
		String pathToResource = getSchemalessPath(resource, true, false);
		if(pathToResource != null && exists(pathToResource)) {
			return true;
		}
		// if this is a relative resource path check if resource imported relative to any of the configured import paths
		if(!getSchemaMatcher(resource).matches()) {
			for(String path : paths) {
				pathToResource = getSchemalessPath(appendPathToResource(path, resource), false, false);
				if(pathToResource != null && exists(pathToResource)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String load(String resource, String[] paths, String charset) throws IOException {
		// check if the resource imported absolutely under this schema
		String content = load(getSchemalessPath(resource, true, true), charset);
		if(content != null) {
			return content;
		}
		// if this is a relative resource path check if resource imported relative to any of the configured import paths
		if(!getSchemaMatcher(resource).matches()) {
			// check if resource imported relative to any of the configured import paths
			for(String path : paths) {
				content = load(getSchemalessPath(appendPathToResource(path, resource), false, true), charset);
				if(content != null) {
					return content;
				}
			}
		}
		throw new IOException("No such file " + resource);
	}
	
	private String load(String resourcePath, String charset) throws IOException {
		if(resourcePath != null) {
			InputStream is = openStream(resourcePath);
			if (is != null) {
				String readStream = readStream(is, charset);
				return readStream;
			}
		}
		return null;
	}
	
	private String getSchemalessPath(String resourcePath, boolean matchOnSchemaOnly, boolean failOnInvalidSchema) throws IOException {
		Matcher m = getSchemaMatcher(resourcePath);
		if (m.matches()) {
			if (m.group(1).equals(getSchema())) {
				return m.group(2);
			} else {
				if(failOnInvalidSchema) {
					throw new IOException("Invalid stream type for provided path " + resourcePath);
				}
				return null;
			}
		}
		if(matchOnSchemaOnly) {
			return null;
		}
		return resourcePath;
	}
	
	private Matcher getSchemaMatcher(String resourcePath) {
		return PATTERN.matcher(resourcePath);
	}
	
	private boolean exists(String resourcePath) throws IOException {
		InputStream stream = openStream(resourcePath);
		if (stream != null) {
			stream.close();
			return true;
		}
		return false;
	}
	
	private String appendPathToResource(String path, String resource) {
		if(path.length() > 0 && path.charAt(path.length() - 1) != '/') {
			return path + "/" + resource;
		} else {
			return path + resource;
		}
	}

	protected String readStream(InputStream is, String charset)
			throws IOException {
		Reader r = new InputStreamReader(is, charset);
		StringWriter w = new StringWriter();
		try {
			char[] b = new char[4096];
			int c = 0;
			while (c >= 0) {
				c = r.read(b);
				if (c > 0) {
					w.write(b, 0, c);
				}
			}
			return w.toString();
		} finally {
			w.close();
			r.close();
		}
	}
}
