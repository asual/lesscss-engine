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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private Ruby runtime;
	private RubyRuntimeAdapter adapter;
	private LessCache cache;
    private String charset = "UTF-8";
    private final Log logger = LogFactory.getLog(getClass());
	
	public LessEngine() {
		
		List<String> loadPaths = new ArrayList<String>();
		loadPaths.add("META-INF/jruby.home/lib/ruby/site_ruby/1.8");
		loadPaths.add("META-INF/less/lib");
        loadPaths.add("META-INF/mutter/lib");
        loadPaths.add("META-INF/polyglot/lib");
        loadPaths.add("META-INF/treetop/lib");
		
		runtime = JavaEmbedUtils.initialize(loadPaths);
		adapter = JavaEmbedUtils.newRuntimeAdapter();
		cache = new LessCache();
	}
	
	public void destroy() {
		JavaEmbedUtils.terminate(runtime);
	}
    
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    
	public String parse(String source) throws LessException {
		try {
			return ((RubyString) adapter.eval(runtime, 
					"require 'less' \nLess::Engine.new('" + source.replaceAll("'", "\"") + "').to_css")).toString().trim();
		} catch (Exception e) {
			logger.error("Parsing error.");
			throw new LessException(e);
		}
	}
	
	public String compile(File source) throws LessException, IOException {
		return compile(source, cache);
	}
	
	public String compile(URL source) throws LessException, IOException {
		return compile(source, cache);
	}
	
	private String compile(File source, LessCache cache) throws IOException, LessException {
		
		String path = source.getAbsolutePath();
		String folder = path.replace(source.getName(), "");
		
		if (source.exists()) {
			
			Long lastModified = source.lastModified();
			if (!cache.contains(path) || (cache.contains(path) && cache.getLastModified(path) < lastModified) || (areImportsModified(cache.getImports(path)))) {
				
				String content = new String(LessUtils.readFile(source), charset);
				
				Pattern p = Pattern.compile("@import\\s+(\"[^\"]*\"|'[^']*')");
				Matcher m = p.matcher(content);
				
				while (m.find()) {
					compile(new File(folder + m.group(1).replaceAll("\"|'", "")), cache.getImports(path));
				}
				
				if (cache == this.cache) {
					try {
	                    logger.info("Compiling '" + path + "'.");
					    content = ((RubyString) adapter.eval(runtime, 
	                            "require 'less' \nLess::Engine.new(File.new('" + path + "')).to_css")).toString().trim();
					} catch (Exception e) {
			            logger.error("Compilation error.");
			            throw new LessException("Compilation error.", e);
					}
					cache.setContent(path, content).setLastModified(path, lastModified);
				} else {
					cache.setContent(path, content).setLastModified(path, lastModified);
				}
			}
			
			return (String) cache.getContent(path);
			
		} else {
			logger.error("The file '" + path + "' doesn't exist.");
			throw new IOException("The file '" + path + "' doesn't exist.");
		}
	}
	
	private String compile(URL source, LessCache cache) throws LessException, IOException {
		
		if (source != null) {

            String path = source.getFile();
			String folder = path.substring(0, path.lastIndexOf(System.getProperty("file.separator")) + 1);

			Long lastModified = source.openConnection().getLastModified();
			
			if (!cache.contains(path) || (cache.contains(path) && cache.getLastModified(path) < lastModified) || (areImportsModified(cache.getImports(path)))) {
				
				String content = new String(LessUtils.readURL(source), charset);
				
				Pattern p = Pattern.compile("@import\\s+(\"[^\"]*\"|'[^']*')");
				Matcher m = p.matcher(content);
				
				while (m.find()) {
                    String urlPath = folder + m.group(1).replaceAll("\"|'", "");
                    URL url = null;
                    try {
                        url = new URL(urlPath);
                    } catch (Exception e) {
                        url = new URL("file:" + urlPath);                       
                    }
                    compile(url, cache.getImports(path));
				}
				
				if (cache == this.cache) {
                    try {
                        logger.info("Compiling: " + path);
                        content = ((RubyString) adapter.eval(runtime, 
                                "require 'less' \nLess::Engine.new(File.new('" + path + "')).to_css")).toString().trim();
                    } catch (Exception e) {
                        logger.error("Compilation error.");
                        throw new LessException("Compilation error.", e);
                    }
                    cache.setContent(path, content).setLastModified(path, lastModified);
				} else {
					cache.setContent(path, content).setLastModified(path, lastModified);
				}
			}
			
			return (String) cache.getContent(path);
			
		} else {
			logger.error("The requested URL doesn't exist.");
			throw new IOException("The requested URL doesn't exist.");
		}
	}
	
	private boolean areImportsModified(LessCache cache) {
		for(String key : cache.keySet()) {
			if ((new File(key)).lastModified() > cache.getLastModified(key) || areImportsModified(cache.getImports(key))) {
				return true;
			}
		}
		return false;
	}	
}