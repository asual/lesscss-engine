package com.asual.lesscss.loader;

import java.io.IOException;

/**
 * A {@link ResourceLoader} that allows using lesscss @include in plain .css
 * files.
 * 
 * Multiple levels of nesting are achieved by combining these two actions:
 * <ol>
 * <li>When a resource is loaded all .css strings are replaced with .less</li>
 * <li>When a .less include file is requested and it does not exist, the
 * extension is changed to .css and lookup is retried.</li>
 * </ol>
 * 
 * @author Rafał Krzewski
 */
public class CssProcessingResourceLoader implements ResourceLoader {

	private final ResourceLoader delegate;

	public CssProcessingResourceLoader(ResourceLoader delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean exists(String path) throws IOException {
		return delegate.exists(path)
				|| delegate.exists(path.replaceFirst("\\.less$", ".css"));
	}

	@Override
	public String load(String path, String charset) throws IOException {
		String content;
		if (delegate.exists(path)) {
			content = delegate.load(path, charset);
		} else {
			content = delegate.load(path.replaceFirst("\\.less$", ".css"),
					charset);
		}
		return content.replaceAll("\\.css", ".less");
	}
}
