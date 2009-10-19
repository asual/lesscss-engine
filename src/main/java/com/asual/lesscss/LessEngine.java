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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.RubyString;
import org.jruby.javasupport.JavaEmbedUtils;

/**
 * @author Rostislav Hristov
 */
public class LessEngine {

    private final Log logger = LogFactory.getLog(getClass());
    
    private Ruby runtime;
    private RubyRuntimeAdapter adapter;
    
    public LessEngine() {
        List<String> loadPaths = new ArrayList<String>();
        loadPaths.add("META-INF/jruby.home/lib/ruby/site_ruby/1.8");
        loadPaths.add("META-INF/less/lib");
        loadPaths.add("META-INF/mutter/lib");
        loadPaths.add("META-INF/polyglot/lib");
        loadPaths.add("META-INF/treetop/lib");
        runtime = JavaEmbedUtils.initialize(loadPaths);
        adapter = JavaEmbedUtils.newRuntimeAdapter();
        adapter.eval(runtime, "require 'less'");
    }
    
    public String compile(String input) throws LessException {
        try {
        	long time = System.currentTimeMillis();
            String result = ((RubyString) adapter.eval(runtime, 
                    "require 'less' \nLess::Engine.new('" + input + "').to_css")).toString().trim();
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
        } catch (Exception e) {
            logger.error("Parsing error.");
            throw new LessException(e);
        }
    }
    
    public String compile(URL input) throws LessException, IOException {
        if (input != null) {
            long time = System.currentTimeMillis();
            String result = ((RubyString) adapter.eval(runtime, 
                    "require 'less' \nLess::Engine.new(File.new('" + input.getFile() + "')).to_css")).toString().trim();
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
        } else {
            logger.error("The requested resource doesn't exist.");
            throw new IOException("The requested resource doesn't exist.");
        }
    }
    
    public String compile(File input) throws LessException, IOException {
        if (input.exists()) {
            long time = System.currentTimeMillis();
            String result = ((RubyString) adapter.eval(runtime, 
                    "require 'less' \nLess::Engine.new(File.new('" + input.getAbsolutePath() + "')).to_css")).toString().trim();
            logger.info("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
            return result;
        } else {
            logger.error("The requested resource doesn't exist.");
            throw new IOException("The requested resource doesn't exist.");
        }
    }
    
    public void compile(File input, File output) throws LessException, IOException {
        String content = compile(input);
        if (!output.exists()) {
            output.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(content);
        bw.close();
    }
    
    public void destroy() {
        JavaEmbedUtils.terminate(runtime);
    }    
    
}