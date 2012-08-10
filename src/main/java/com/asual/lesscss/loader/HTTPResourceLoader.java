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
