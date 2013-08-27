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
 * A {@link ResourceLoader} delegates to a sequence of other loaders, asking
 * each one for the requested resource in turn.
 * 
 * @author Rafał Krzewski
 */
public class ChainedResourceLoader implements ResourceLoader {

	private final ResourceLoader[] loaders;

	/**
	 * Creates a new ChainedResourceLoader.
	 * 
	 * @param loaders
	 *            the loaders to delegate to. Order of the loaders is important.
	 */
	public ChainedResourceLoader(ResourceLoader... loaders) {
		this.loaders = loaders;
	}

	/**
	 * Returns {@code true}, if any of the delegate loaders returns {@true} from
	 * {@link #exists(String)} for the given path.
	 */
	@Override
	public boolean exists(String path) throws IOException {
		for (ResourceLoader loader : loaders) {
			if (loader.exists(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The first delegate that returns {@true} from {@link #exists(String)} for
	 * the given path will have {@link #load(String, String)} invoked, and the
	 * result will be retured.
	 */
	@Override
	public String load(String path, String charset) throws IOException {
		for (ResourceLoader loader : loaders) {
			if (loader.exists(path)) {
				return loader.load(path, charset);
			}
		}
		throw new IOException("No such file " + path);
	}

}
