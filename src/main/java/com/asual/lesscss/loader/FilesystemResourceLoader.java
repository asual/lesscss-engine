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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link ResourceLoader} that loads resources from the file system.
 * 
 * @author Rafał Krzewski
 */
public class FilesystemResourceLoader extends StreamResourceLoader {

	private final static String SCHEMA = "file";

	@Override
	protected String getSchema() {
		return SCHEMA;
	}

	/**
	 * Note that path should be absolute, otherwise the results are dependent on
	 * the VM's {@code user.dir}.
	 */
	@Override
	protected InputStream openStream(String path) throws IOException {
		File file = new File(path);
		if (file.isFile() && file.canRead()) {
			return new FileInputStream(path);
		} else {
			return null;
		}
	}
}
