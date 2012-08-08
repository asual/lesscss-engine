package com.asual.lesscss.loader;

import java.io.InputStream;

/**
 * A {@link ResourceLoader} that loads resources from a {@link ClassLoader}.
 * 
 * @author Rafał Krzewski
 */
public class ClasspathResourceLoader extends StreamResourceLoader {

	private static final String SCHEMA = "classpath";

	private final ClassLoader classLoader;

	/**
	 * Creates a new {@link ClasspathResourceLoader}.
	 * 
	 * @param classLoader
	 *            a {@link ClassLoader} to load resources from.
	 */
	public ClasspathResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	protected String getSchema() {
		return SCHEMA;
	}

	/**
	 * Please note that path should NOT have a leading slash.
	 */
	@Override
	protected InputStream openStream(String path) {
		return classLoader.getResourceAsStream(path);
	}
}
