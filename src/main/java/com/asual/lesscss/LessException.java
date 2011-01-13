/*
 * Copyright 2009-2010 the original author or authors.
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

import java.util.List;


/**
 * @author Rostislav Hristov
 */
public class LessException extends Exception {

    private static final long serialVersionUID = 662552833197468936L;

	private String errorType;
	private String filename;
	private int line;
	private int column;
	private List<String> extract;
    
	public static LessException fromLessJsError(String errorType, String message, String filename, int line, int column, List<String> extract) {
		LessException le = new LessException(message);
		if (errorType == null) {
			le.errorType = "LESSJS Error";
		}
		else {
			le.errorType = errorType;
		}
		le.filename = filename;
		le.line = line;
		le.column = column;
		le.extract = extract;
		return le;
	}
	
    public LessException() {
        super();
    }
    
    public LessException(String message) {
        super(message);
    }
    
    public LessException(String message, Throwable e) {
        super(message, e);
    }

    public LessException(Throwable e) {
        super(e);
    }
    
    @Override
    public String getMessage()
    {
    	if (errorType != null) {
    		String msg = String.format("%s: %s (line %s, column %s)", errorType, super.getMessage(), line, column);
    		if (!(extract == null) && !extract.isEmpty())
    		{
    			msg += " near";
    			for (String l : extract) {
    				msg += "\n" + l;
    			}
    		}
    		return msg;
    	}
    	
    	return super.getMessage();
    }

    /**
     * Type of error as reported by less.js
     */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * Filename that error occured in as reported by less.js
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Line number where error occurred as reported by less.js or -1 if unknown.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Column number where error occurred as reported by less.js or -1 if unknown.
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Lines around error as reported by less.js
	 */
	public List<String> getExtract() {
		return extract;
	}
    
}