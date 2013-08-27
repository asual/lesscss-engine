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
	public boolean exists(String path) throws IOException {
		Matcher m = PATTERN.matcher(path);
		if (m.matches()) {
			if (m.group(1).equals(getSchema())) {
				return exists(m.group(2));
			}
			return false;
		}
		InputStream stream = openStream(path);
		if (stream != null) {
			stream.close();
			return true;
		}
		return false;
	}

	@Override
	public String load(String path, String charset) throws IOException {
		Matcher m = PATTERN.matcher(path);
		if (m.matches()) {
			if (m.group(1).equals(getSchema())) {
				return load(m.group(2), charset);
			}
			throw new IOException("No such file " + path);
		}
		InputStream is = openStream(path);
		if (is != null) {
			return readStream(is, charset);
		}
		throw new IOException("No such file " + path);
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
