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

/**
 * A delegating {@ResourceLoader} that removes \r characters
 * from resource content, thus converting Windows newlines to Unix ones.
 * 
 * @author Rafał Krzewski
 */
public class UnixNewlinesResourceLoader implements ResourceLoader {

	private final ResourceLoader delegate;

	public UnixNewlinesResourceLoader(ResourceLoader delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean exists(String path) throws IOException {
		return delegate.exists(path);
	}

	@Override
	public String load(String path, String charset) throws IOException {
		return delegate.load(path, charset).replaceAll("\r", "");
	}
}
