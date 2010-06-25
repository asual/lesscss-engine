/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

/**
 * @author Rostislav Hristov
 */
public class LessEngine {

    private final Log logger = LogFactory.getLog(getClass());

    private Scriptable scope;
    private Function cs;
    private Function cf;
    
    public LessEngine() {
    	try {
        	logger.info("Initializing LESS Engine");
        	URL browser = getClass().getClassLoader().getResource("META-INF/browser.js");
        	URL less = getClass().getClassLoader().getResource("META-INF/less.js");
        	URL engine = getClass().getClassLoader().getResource("META-INF/engine.js");
        	Context cx = Context.enter();
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
			logger.error("LESS Engine intialization failed", e);
		}
    }
    
    public String compile(String input) throws LessException {
        try {
        	long time = System.currentTimeMillis();
            String result = call(cs, new Object[] {input});
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
            throw new LessException(e);
		}
    }
    
    public String compile(URL input) throws LessException {
    	try {
            long time = System.currentTimeMillis();
            String result = call(cf, new Object[] {input.getProtocol() + ":" + input.getFile()});
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
            throw new LessException(e);
		}
    }
    
    public String compile(File input) throws LessException {
    	try {
            long time = System.currentTimeMillis();
            String result = call(cf, new Object[] {"file:" + input.getAbsolutePath()});
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
            throw new LessException(e);
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
			logger.error(e.getMessage());
            throw new LessException(e);
		}
    }

    private synchronized String call(Function fn, Object[] args) {
		return (String) Context.call(null, fn, scope, scope, args);
    }
    
}