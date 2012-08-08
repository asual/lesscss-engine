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
	 * Note that path should be absolute, otherwise the results are dependent on the VM's {@code user.dir}.
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
