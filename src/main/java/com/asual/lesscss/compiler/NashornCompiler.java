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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.ECMAException;

@SuppressWarnings("restriction")
public class NashornCompiler implements LessCompiler {

	private final Log logger = LogFactory.getLog(getClass());
	private ScriptObjectMirror compile;

	public NashornCompiler(LessOptions options, ResourceLoader loader, URL less, URL env, URL engine, URL cssmin, URL sourceMap) throws IOException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine scriptEngine = factory.getEngineByName("nashorn");
        try {
            scriptEngine.eval(new InputStreamReader(sourceMap
    				.openConnection().getInputStream()));
            scriptEngine.eval(new InputStreamReader(env.openConnection()
    				.getInputStream()));
            ScriptObjectMirror lessenv = (ScriptObjectMirror) scriptEngine.get("lessenv");
            lessenv.put("charset", options.getCharset());
            lessenv.put("css", options.isCss());
            lessenv.put("lineNumbers", options.getLineNumbers());
            lessenv.put("optimization", options.getOptimization());
            lessenv.put("sourceMap", options.isSourceMap());
            lessenv.put("sourceMapRootpath", options.getSourceMapRootpath());
            lessenv.put("sourceMapBasepath", options.getSourceMapBasepath());
            lessenv.put("sourceMapURL", options.getSourceMapUrl());
            lessenv.put("loader", loader);
            lessenv.put("paths", options.getPaths());
            scriptEngine.eval(new InputStreamReader(less
    				.openConnection().getInputStream()));
            scriptEngine.eval(new InputStreamReader(cssmin.openConnection()
    				.getInputStream()));    		
            scriptEngine.eval(new InputStreamReader(engine.openConnection()
    				.getInputStream()));
    		compile = (ScriptObjectMirror) scriptEngine.get("compile");
        } catch (ScriptException e) { 
        	logger.error(e.getMessage(), e);
        }
	}

	@Override
	public String compile(String input, String location, boolean compress) throws LessException {
		try {
			return (String) compile.call(null, input, location, compress);
		} catch (Exception e) {
			throw new LessException(parseLessException(e));
		}
	}

	private Exception parseLessException(Exception root) {
		logger.debug("Parsing LESS Exception", root);
		if (root instanceof ECMAException) {
			ECMAException e = (ECMAException) root;
			Object thrown = e.getThrown();
			String type = null;
			String message = null;
			String filename = null;
			int line = -1;
			int column = -1;
			List<String> extractList = new ArrayList<String>();
			if (thrown instanceof ScriptObject) {
				ScriptObject so = (ScriptObject) e.getThrown();
				type = so.get("type").toString() + " Error";
				message = so.get("message").toString();
				filename = "";
				if (so.has("filename")) {
					filename = so.get("filename").toString();
				}
				if (so.has("line")) {
					line = ((Long) so.get("line")).intValue();
				}
				if (so.has("column")) {
					column = ((Double) so.get("column")).intValue();
				}
				if (so.has("extract")) {
					NativeArray extract = (NativeArray) so.get("extract");
					for (int i = 0; i < extract.size(); i++) {
						if (extract.get(i) instanceof String) {
							extractList.add(((String) extract.get(i))
									.replace("\t", " "));
						}
					}
				}
			} else {
				type = thrown.getClass().getSimpleName() + " Error";
				message = e.getMessage().replaceFirst("[^:]+: ", "");
			}
			return new LessException(message, type, filename, line, column,
					extractList);
		}
		return root;
	}

}
