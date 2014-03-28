package com.asual.lesscss.compiler;

import com.asual.lesscss.LessException;

public interface LessCompiler {
	String compile(String input, String location, boolean compress) throws LessException;
}
