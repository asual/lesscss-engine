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

package com.asual.lesscss.compiler;

import com.asual.lesscss.LessException;
import com.asual.lesscss.LessOptions;
import com.asual.lesscss.loader.ResourceLoader;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RhinoCompiler implements LessCompiler {

	private final Log logger = LogFactory.getLog(getClass());
	private final Scriptable scope;
	private final Function compile;

	public RhinoCompiler(LessOptions options, ResourceLoader loader, URL less, URL env, URL engine, URL cssmin, URL sourceMap) throws IOException {
		Context cx = Context.enter();
		logger.debug("Using implementation version: "
				+ cx.getImplementationVersion());
		cx.setOptimizationLevel(-1);
		Global global = new Global();
		global.init(cx);
		scope = cx.initStandardObjects(global);
		cx.evaluateReader(scope, new InputStreamReader(sourceMap
				.openConnection().getInputStream()), sourceMap.toExternalForm(), 1,
				null);
		cx.evaluateReader(scope, new InputStreamReader(env.openConnection()
				.getInputStream()), env.toExternalForm(), 1, null);
		Scriptable lessEnv = (Scriptable) scope.get("lessenv", scope);
		lessEnv.put("charset", lessEnv, options.getCharset());
		lessEnv.put("css", lessEnv, options.isCss());
		lessEnv.put("lineNumbers", lessEnv, options.getLineNumbers());
		lessEnv.put("optimization", lessEnv, options.getOptimization());
		lessEnv.put("sourceMap", lessEnv, options.isSourceMap());
		lessEnv.put("sourceMapRootpath", lessEnv, options.getSourceMapRootpath());
		lessEnv.put("sourceMapBasepath", lessEnv, options.getSourceMapBasepath());
		lessEnv.put("sourceMapURL", lessEnv, options.getSourceMapUrl());
		lessEnv.put("loader", lessEnv, Context.javaToJS(loader, scope));
		if(options.getPaths() != null) {
			NativeArray nativeArray = new NativeArray(options.getPaths());
			lessEnv.put("paths", lessEnv, nativeArray);
		}
		cx.evaluateReader(scope, new InputStreamReader(less
				.openConnection().getInputStream()), less.toExternalForm(), 1,
				null);
		cx.evaluateReader(scope, new InputStreamReader(cssmin
				.openConnection().getInputStream()), cssmin.toExternalForm(), 1,
				null);
		cx.evaluateReader(scope, new InputStreamReader(engine
				.openConnection().getInputStream()), engine.toExternalForm(), 1,
				null);
		compile = (Function) scope.get("compile", scope);
		Context.exit();
	}

	@Override
	public String compile(String input, String location, boolean compress) throws LessException {
		try {
			return (String) Context.call(null, compile, scope, scope,
					new Object[]{input, location, compress});
		} catch (Exception e) {
			throw new LessException(parseLessException(e));
		}
	}

	private boolean hasProperty(Scriptable value, String name) {
		Object property = ScriptableObject.getProperty(value, name);
		return property != null && !property.equals(UniqueTag.NOT_FOUND);
	}

	private Exception parseLessException(Exception root) {
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
			return new LessException(message, type, filename, line, column,
					extractList);
		}
		return root;
	}

}
