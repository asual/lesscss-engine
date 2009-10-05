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

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rostislav Hristov
 */
class LessCache implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> content;
	private Map<String, Long> lastModified;
	private Map<String, LessCache> imports;
	
	public LessCache() {
		content = new HashMap<String, Object>();
		lastModified = new HashMap<String, Long>();
		imports = new HashMap<String, LessCache>();
	}
	
	public Object getContent(String key) {
		return content.get(key);
	}
	
	public LessCache setContent(String key, Object value) {
		content.put(key, value);
		return this;
	}
	
	public long getLastModified(String key) {
		return lastModified.get(key);
	}
	
	public LessCache setLastModified(String key, long value) {
		lastModified.put(key, value);
		return this;
	}
	
	public LessCache getImports(String key) {
		if (!imports.containsKey(key)) {
			imports.put(key, new LessCache());
		}
		return imports.get(key);
	}
	
	public boolean contains(String key) {
		return (content.containsKey(key) && lastModified.containsKey(key));
	}
	
	public Set<String> keySet() {
		return content.keySet();
	}	
}