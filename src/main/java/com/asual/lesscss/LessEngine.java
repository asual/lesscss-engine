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

package com.asual.lesscss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.tools.shell.Global;

import com.asual.lesscss.loader.ChainedResourceLoader;
import com.asual.lesscss.loader.ClasspathResourceLoader;
import com.asual.lesscss.loader.CssProcessingResourceLoader;
import com.asual.lesscss.loader.FilesystemResourceLoader;
import com.asual.lesscss.loader.HTTPResourceLoader;
import com.asual.lesscss.loader.JNDIResourceLoader;
import com.asual.lesscss.loader.ResourceLoader;
import com.asual.lesscss.loader.UnixNewlinesResourceLoader;

/**
 * @author Rostislav Hristov
 * @author Uriah Carpenter
 * @author Noah Sloan
 */
public class LessEngine {

	private final Log logger = LogFactory.getLog(getClass());

	private final LessOptions options;
	private final ResourceLoader loader;

	private Scriptable scope;
	private Function compile;

	public LessEngine() {
		this(new LessOptions());
	}

	public LessEngine(LessOptions options) {
		this(options, defaultResourceLoader(options));
	}

	private static ResourceLoader defaultResourceLoader(LessOptions options) {
		ResourceLoader resourceLoader = new ChainedResourceLoader(
				new FilesystemResourceLoader(), new ClasspathResourceLoader(
						LessEngine.class.getClassLoader()),
				new JNDIResourceLoader(), new HTTPResourceLoader());
		if (options.isCss()) {
			return new CssProcessingResourceLoader(resourceLoader);
		}
		resourceLoader = new UnixNewlinesResourceLoader(resourceLoader);
		return resourceLoader;
	}

	public LessEngine(LessOptions options, ResourceLoader loader) {
		this.options = options;
		this.loader = loader;
		try {
			logger.debug("Initializing LESS Engine.");
			ClassLoader classLoader = getClass().getClassLoader();
			URL less = options.getLess();
			URL env = classLoader.getResource("META-INF/env.js");
			URL engine = classLoader.getResource("META-INF/engine.js");
			URL cssmin = classLoader.getResource("META-INF/cssmin.js");
			Context cx = Context.enter();
			logger.debug("Using implementation version: "
					+ cx.getImplementationVersion());
			cx.setOptimizationLevel(-1);
			Global global = new Global();
			global.init(cx);
			scope = cx.initStandardObjects(global);
			cx.evaluateReader(scope, new InputStreamReader(env.openConnection()
					.getInputStream()), env.getFile(), 1, null);
			Scriptable lessEnv = (Scriptable) scope.get("lessenv", scope);
			lessEnv.put("charset", lessEnv, options.getCharset());
			lessEnv.put("css", lessEnv, options.isCss());
			lessEnv.put("lineNumbers", lessEnv, options.getLineNumbers());
			lessEnv.put("optimization", lessEnv, options.getOptimization());
			lessEnv.put("loader", lessEnv, Context.javaToJS(loader, scope));
			cx.evaluateReader(scope, new InputStreamReader(less
					.openConnection().getInputStream()), less.getFile(), 1,
					null);
			cx.evaluateReader(scope, new InputStreamReader(cssmin
					.openConnection().getInputStream()), cssmin.getFile(), 1,
					null);
			cx.evaluateReader(scope, new InputStreamReader(engine
					.openConnection().getInputStream()), engine.getFile(), 1,
					null);
			compile = (Function) scope.get("compile", scope);
			Context.exit();
		} catch (Exception e) {
			logger.error("LESS Engine intialization failed.", e);
		}
	}

	public String compile(String input) throws LessException {
		return compile(input, null, false);
	}

	public String compile(String input, String location) throws LessException {
		return compile(input, location, false);
	}

	public String compile(String input, String location, boolean compress)
			throws LessException {
		try {
			long time = System.currentTimeMillis();
			String result = call(compile, new Object[] { input,
					location == null ? "" : location, compress });
			logger.debug("The compilation of '" + input + "' took "
					+ (System.currentTimeMillis() - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}

	public String compile(URL input) throws LessException {
		return compile(input, false);
	}

	public String compile(URL input, boolean compress) throws LessException {
		try {
			long time = System.currentTimeMillis();
			String location = input.toString();
			logger.debug("Compiling URL: " + location);
			String source = loader.load(location, options.getCharset());
			String result = call(compile, new Object[] { source, location,
					compress });
			logger.debug("The compilation of '" + input + "' took "
					+ (System.currentTimeMillis() - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}

	public String compile(File input) throws LessException {
		return compile(input, false);
	}

	public String compile(File input, boolean compress) throws LessException {
		try {
			long time = System.currentTimeMillis();
			String location = input.getAbsolutePath();
			logger.debug("Compiling File: " + "file:" + location);
			String source = loader.load(location, options.getCharset());
			String result = call(compile, new Object[] { source, location,
					compress });
			logger.debug("The compilation of '" + input + "' took "
					+ (System.currentTimeMillis() - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}

	public void compile(File input, File output) throws LessException,
			IOException {
		compile(input, output, false);
	}

	public void compile(File input, File output, boolean compress)
			throws LessException, IOException {
		try {
			String content = compile(input, compress);
			if (!output.exists()) {
				output.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			bw.write(content);
			bw.close();
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}

	private String call(Function fn, Object[] args) {
		return (String) Context.call(null, fn, scope, scope, args);
	}

	private boolean hasProperty(Scriptable value, String name) {
		Object property = ScriptableObject.getProperty(value, name);
		return property != null && !property.equals(UniqueTag.NOT_FOUND);
	}

	private LessException parseLessException(Exception root)
			throws LessException {
		logger.debug("Parsing LESS Exception", root);
		if (root instanceof JavaScriptException) {
			Scriptable value = (Scriptable) ((JavaScriptException) root)
					.getValue();
			String type = ScriptableObject.getProperty(value, "type")
					.toString() + " Error";
			String message = ScriptableObject.getProperty(value, "message")
					.toString();
			String filename = "";
			if (hasProperty(value, "filename")) {
				filename = ScriptableObject.getProperty(value, "filename")
						.toString();
			}
			int line = -1;
			if (hasProperty(value, "line")) {
				line = ((Double) ScriptableObject.getProperty(value, "line"))
						.intValue();
			}
			int column = -1;
			if (hasProperty(value, "column")) {
				column = ((Double) ScriptableObject
						.getProperty(value, "column")).intValue();
			}
			List<String> extractList = new ArrayList<String>();
			if (hasProperty(value, "extract")) {
				NativeArray extract = (NativeArray) ScriptableObject
						.getProperty(value, "extract");
				for (int i = 0; i < extract.getLength(); i++) {
					if (extract.get(i, extract) instanceof String) {
						extractList.add(((String) extract.get(i, extract))
								.replace("\t", " "));
					}
				}
			}
			throw new LessException(message, type, filename, line, column,
					extractList);
		}
		throw new LessException(root);
	}

}