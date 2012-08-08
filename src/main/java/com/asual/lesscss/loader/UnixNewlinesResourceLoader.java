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
