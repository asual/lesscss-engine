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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Rostislav Hristov
 */
public class LessEngineCli {

	public static void main(String[] args) throws LessException, URISyntaxException {
		Options cmdOptions = new Options();
		cmdOptions.addOption(LessOptions.CHARSET_OPTION, true, "Input file charset encoding. Defaults to UTF-8.");
		cmdOptions.addOption(LessOptions.COMPRESS_OPTION, false, "Flag that enables compressed CSS output.");
		cmdOptions.addOption(LessOptions.CSS_OPTION, false, "Flag that enables compilation of .css files.");
		cmdOptions.addOption(LessOptions.LESS_OPTION, true, "Path to a custom less.js for Rhino version.");
		try {
			CommandLineParser cmdParser = new GnuParser();
			CommandLine cmdLine = cmdParser.parse(cmdOptions, args);
			LessOptions options = new LessOptions();
			if (cmdLine.hasOption(LessOptions.CHARSET_OPTION)) {
				options.setCharset(cmdLine.getOptionValue(LessOptions.CHARSET_OPTION));
			}
			if (cmdLine.hasOption(LessOptions.COMPRESS_OPTION)) {
				options.setCompress(true);
			}
			if (cmdLine.hasOption(LessOptions.CSS_OPTION)) {
				options.setCss(true);
			}
			if (cmdLine.hasOption(LessOptions.LESS_OPTION)) {
				options.setLess(new File(cmdLine.getOptionValue(LessOptions.LESS_OPTION)).toURI().toURL());
			}
			LessEngine engine = new LessEngine(options);
			if (System.in.available() != 0) {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				StringWriter sw = new StringWriter();
				char[] buffer = new char[1024];
				int n = 0;
				while (-1 != (n = in.read(buffer))) {
					sw.write(buffer, 0, n);
				}
				String src = sw.toString();
				if (!src.isEmpty()) {
					System.out.println(engine.compile(src, options.isCompress()));
					System.exit(0);
				}
			}
			String[] files = cmdLine.getArgs();
			if (files.length == 1) {
				System.out.println(engine.compile(new File(files[0]), options.isCompress()));
				System.exit(0);
			}
			if (files.length == 2) {
				engine.compile(new File(files[0]), new File(files[1]), options.isCompress());
				System.exit(0);
			}
			
		} catch (IOException ioe) {
			System.err.println("Error opening input file.");
		} catch (ParseException pe) {
			System.err.println("Error parsing arguments.");
		}
		String[] paths = LessEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().split(File.separator);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar " + paths[paths.length - 1] + " input [output] [options]", cmdOptions);
		System.exit(1);
	}

}
