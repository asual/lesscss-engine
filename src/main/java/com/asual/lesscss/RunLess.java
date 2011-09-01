package com.asual.lesscss;

import java.io.File;
import java.io.IOException;

public class RunLess {

	public static void main(String[] args) throws LessException, IOException {
		LessEngine engine = new LessEngine();
		
		if(args.length  == 1) {
			System.out.println(engine.compile(args[0]));
		} else if(args.length  == 2) {
			engine.compile(new File(args[0]),new File(args[1]));
		} else {
			System.err.println("Usage: java -jar lesscss-engine.jar <input_file> [<output_file>]");
		}
	}

}
