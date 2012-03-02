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

import java.net.URL;

/**
 * @author Rostislav Hristov
 */
public class LessOptions {
	
	public static final String CHARSET_OPTION = "charset";
	public static final String COMPRESS_OPTION = "compress";
	public static final String CSS_OPTION = "css";
	public static final String LESS_OPTION = "less";

	private String charset;
	private boolean compress;
	private boolean css;
	private URL less;
	
	public String getCharset() {
		if (charset == null) {
			return "UTF-8";
		}
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public URL getLess() {
		if (less == null) {
			return getClass().getClassLoader().getResource("META-INF/less.js");
		}
		return less;
	}
	
	public void setLess(URL less) {
		this.less = less;
	}
	
	public boolean isCss() {
		return css;
	}
	
	public void setCss(boolean css) {
		this.css = css;
	}
	
	public boolean isCompress() {
		return compress;
	}
	
	public void setCompress(boolean compress) {
		this.compress = compress;
	}
}