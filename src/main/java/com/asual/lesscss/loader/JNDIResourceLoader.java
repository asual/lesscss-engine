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
