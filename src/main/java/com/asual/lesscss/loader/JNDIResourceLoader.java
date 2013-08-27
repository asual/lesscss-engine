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
 * A {@link ResourceLoader} that loads JNDI resources.
 * 
 * @author Tim Kingman
 */
public class JNDIResourceLoader extends StreamResourceLoader {

	private static final String SCHEMA = "jndi";

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
