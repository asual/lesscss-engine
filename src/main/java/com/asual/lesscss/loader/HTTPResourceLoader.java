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
import java.net.URL;
import java.net.URLConnection;

/**
 * A naive resource loader using {@link java.net.URLConnection}.
 * 
 * For any sort of serious usage, a proper loader needs to be implemented using
 * Apache httpclient or similar.
 * 
 * @author Rafał Krzewski
 */
public class HTTPResourceLoader extends StreamResourceLoader {

	private static final String SCHEMA = "http";

	@Override
	protected String getSchema() {
		return SCHEMA;
	}

	@Override
	protected InputStream openStream(String path) throws IOException {
		URL url = new URL(SCHEMA + ":" + path);
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}
}
