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
