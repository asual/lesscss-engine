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
import org.mozilla.javascript.tools.shell.Global;

/**
 * @author Rostislav Hristov
 * @author Uriah Carpenter
 */
public class LessEngine {

	private final Log logger = LogFactory.getLog(getClass());

	private Scriptable scope;
	private Function cs;
	private Function cf;
	
	public LessEngine() {
		try {
			logger.debug("Initializing LESS Engine.");
			URL browser = getClass().getClassLoader().getResource("META-INF/browser.js");
			URL less = getClass().getClassLoader().getResource("META-INF/less.js");
			URL engine = getClass().getClassLoader().getResource("META-INF/engine.js");
			Context cx = Context.enter();
			logger.warn("Using implementation version: " + cx.getImplementationVersion());
			cx.setOptimizationLevel(9);
			Global global = new Global();
			global.init(cx);		  
			scope = cx.initStandardObjects(global);
			cx.evaluateReader(scope, new InputStreamReader(browser.openConnection().getInputStream()), browser.getFile(), 1, null);
			cx.evaluateReader(scope, new InputStreamReader(less.openConnection().getInputStream()), less.getFile(), 1, null);
			cx.evaluateReader(scope, new InputStreamReader(engine.openConnection().getInputStream()), engine.getFile(), 1, null);
			cs = (Function) scope.get("compileString", scope);
			cf = (Function) scope.get("compileFile", scope);
			Context.exit();
		} catch (Exception e) {
			logger.error("LESS Engine intialization failed.", e);
		}
	}
	
	public String compile(String input) throws LessException {
		try {
			long time = System.currentTimeMillis();
			String result = call(cs, new Object[] {input});
			logger.debug("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}
	
	public String compile(URL input) throws LessException {
		try {
			long time = System.currentTimeMillis();
			logger.debug("Compiling URL: " + input.getProtocol() + ":" + input.getFile());
			String result = call(cf, new Object[] {input.getProtocol() + ":" + input.getFile(), getClass().getClassLoader()});
			logger.debug("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}
	
	public String compile(File input) throws LessException {
		try {
			long time = System.currentTimeMillis();
			logger.debug("Compiling File: " + "file:" + input.getAbsolutePath());
			String result = call(cf, new Object[] {"file:" + input.getAbsolutePath(), getClass().getClassLoader()});
			logger.debug("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
			return result;
		} catch (Exception e) {
			throw parseLessException(e);
		}
	}
	
	public void compile(File input, File output) throws LessException, IOException {
		try {
			String content = compile(input);
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

	private synchronized String call(Function fn, Object[] args) {
		return (String) Context.call(null, fn, scope, scope, args);
	}
	
	private LessException parseLessException(Exception root) throws LessException {
		
		logger.debug("Parsing LESS Exception", root);
		
		if (root instanceof JavaScriptException) {
			
			Scriptable value = (Scriptable) ((JavaScriptException) root).getValue();
			
			boolean hasName = ScriptableObject.hasProperty(value, "name");
			boolean hasType = ScriptableObject.hasProperty(value, "type");
			
			if (hasName || hasType) {
				String errorType = "Error";
				
				if (hasName) {
					String type = (String) ScriptableObject.getProperty(value, "name");
					if ("ParseError".equals(type)) {
						errorType = "Parse Error";
					} else {
						errorType = type + " Error";
					}
				} else if (hasType) {
					Object prop = ScriptableObject.getProperty(value, "type");
					if (prop instanceof String) {
						errorType = (String) prop + " Error"; 
					}
				}
				
				String message = (String) ScriptableObject.getProperty(value, "message");
				
				String filename = "";
				if (ScriptableObject.hasProperty(value, "filename")) {
					filename = (String) ScriptableObject.getProperty(value, "filename"); 
				}
				
				int line = -1;
				if (ScriptableObject.hasProperty(value, "line")) {
					line = ((Double) ScriptableObject.getProperty(value, "line")).intValue(); 
				}
				
				int column = -1;
				if (ScriptableObject.hasProperty(value, "column")) {
					column = ((Double) ScriptableObject.getProperty(value, "column")).intValue();
				}
				
				
				List<String> extractList = new ArrayList<String>();
				if (ScriptableObject.hasProperty(value, "extract")) {
					NativeArray extract = (NativeArray) ScriptableObject.getProperty(value, "extract");
					for (int i = 0; i < extract.getLength(); i++) {
						if (extract.get(i, extract) instanceof String) {
							extractList.add(((String) extract.get(i, extract)).replace("\t", " "));
						}
					}
				}
				
				throw new LessException(message, errorType, filename, line, column, extractList);
			}
		}
		
		throw new LessException(root);
	}
	
}