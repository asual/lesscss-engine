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
	public static final String LINE_NUMBERS_OPTION = "less";
	public static final String LINE_NUMBERS_COMMENTS = "comments";
	public static final String LINE_NUMBERS_MEDIA_QUERY = "mediaquery";
	public static final String LINE_NUMBERS_ALL = "all";
	public static final String OPTIMIZATION_OPTION = "optimization";

	private String charset = "UTF-8";
	private Boolean compress = false;
	private Boolean css = false;
	private URL less = getClass().getClassLoader().getResource("META-INF/less.js");
	private String lineNumbers;
	private Integer optimization = 3;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
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

	public URL getLess() {
		return less;
	}

	public void setLess(URL less) {
		this.less = less;
	}

	public String getLineNumbers() {
		return lineNumbers;
	}

	public void setLineNumbers(String lineNumbers) {
		this.lineNumbers = lineNumbers;
	}

	public Integer getOptimization() {
		return optimization;
	}

	public void setOptimization(Integer optimization) {
		this.optimization = optimization;
	}
}