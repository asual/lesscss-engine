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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rostislav Hristov
 * @author Uriah Carpenter
 * @author Eliot Sykes
 */
public class LessEngineTest {

	private static LessEngine engine;

	@BeforeClass
	public static void before() {
		LessOptions options = new LessOptions();
		options.setCss(true);
		engine = new LessEngine(options);
	}

	@Test
	public void testString() throws LessException {
		assertEquals("div {\n  width: 2;\n}\n",
				engine.compile("div { width: 1 + 1 }"));
	}

	@Test
	public void testStringWithImport() throws LessException {
		String path = getResource("less/import.less").getPath();
		assertEquals(
				"body {\n  color: #f0f0f0;\n}\n",
				engine.compile("@import url('" + path
						+ "'); body { color: @color; }"));
	}

	@Test
	public void testStringWithLocation() throws LessException {
		/*
		 * A template engine would extract the <style type="text/less"> block
		 * from META-INF/template.html and compile it. Here, we're skipping the
		 * extraction and passing the stylesheet body and template location to
		 * the engine directly.
		 */
		String in = "@import \"less/subdir/import-from-root.less\";\n"
				+ "@import \"classpath:META-INF/less/import.less\";\n"
				+ "body { color: @color; }";
		String out = "a {\n  color: #dddddd;\n"
				+ "  background-image: url(img/logo.png);\n}\n"
				+ "body {\n  color: #f0f0f0;\n}\n";
		assertEquals(out, engine.compile(in, getResource("template.html")
				.toString(), false));
	}

	@Test
	public void testToString() throws LessException, IOException {
		assertEquals("body {\n  color: #f0f0f0;\n}\n",
				engine.compile(getResource("less/classpath.less")));
	}

	@Test
	public void testToFile() throws LessException, IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempFile = File.createTempFile("classpath.less", null, tempDir);
		engine.compile(new File(getResource("less/classpath.less").getPath()),
				new File(tempFile.getAbsolutePath()));
		FileInputStream fstream = new FileInputStream(
				tempFile.getAbsolutePath());
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		StringBuilder sb = new StringBuilder();
		while ((strLine = br.readLine()) != null) {
			sb.append(strLine);
		}
		in.close();
		assertEquals("body {  color: #f0f0f0;}", sb.toString());
		tempFile.delete();
	}

	@Test
	public void testToStringForMultipleImports() throws LessException,
			IOException {
		String expected = "body {\n"
				+ "  font-family: 'Helvetica Neue', Arial, sans-serif;\n}\n"
				+ "body {\n  width: 960px;\n  margin: 0;\n}\n"
				+ "#header {\n  border-radius: 5px;\n"
				+ "  -webkit-border-radius: 5px;\n"
				+ "  -moz-border-radius: 5px;\n}\n#footer {\n"
				+ "  border-radius: 10px;\n"
				+ "  -webkit-border-radius: 10px;\n"
				+ "  -moz-border-radius: 10px;\n}\n";
		assertEquals(expected,
				engine.compile(getResource("css/multiple-imports.css")));
	}

	@Test
	public void testToCompressedStringForMultipleImports()
			throws LessException, IOException {
		String expected = "body{font-family:'Helvetica Neue',Arial,sans-serif}body{width:960px;margin:0}"
				+ "#header{border-radius:5px;-webkit-border-radius:5px;-moz-border-radius:5px}"
				+ "#footer{border-radius:10px;-webkit-border-radius:10px;-moz-border-radius:10px}";
		assertEquals(expected,
				engine.compile(getResource("css/multiple-imports.css"), true));
	}

	@Test
	public void testImages() throws LessException {
		String expected = ".logo {\n  background-image: url(../img/logo.png);\n}\n";
		assertEquals(expected, engine.compile(getResource("less/img.less")));
	}

	@Test
	public void testSubdirImports() throws LessException, IOException {
		engine.compile(getResource("less/root.less"));
		engine.compile(getResource("less/subdir/import-from-root.less"));
		engine.compile(getResource("less/import-from-subdir.less"));
	}

	@Test(expected = LessException.class)
	public void testNameErrorInput() throws IOException, LessException {
		try {
			engine.compile(getResource("less/name-error.less"));
		} catch (LessException e) {
			assertTrue(
					"Name Error",
					e.getMessage()
							.contains(
									"Name Error: .bgColor is undefined (line 2, column 4)"));
			throw e;
		}
	}

	@Test(expected = LessException.class)
	public void testParseErrorInput() throws IOException, LessException {
		try {
			engine.compile(getResource("less/parse-error.less"));
		} catch (LessException e) {
			assertTrue("Parse Error",
					e.getMessage().contains("Parse Error: Unrecognised input"));
			throw e;
		}
	}

	@Test(expected = LessException.class)
	public void testParseUnbalancedInputUnder() throws IOException,
			LessException {
		try {
			engine.compile(getResource("less/unbalanced-under-error.less"));
		} catch (LessException e) {
			assertTrue("Parse Error",
					e.getMessage().contains("Parse Error: missing closing `}`"));
			throw e;
		}
	}

	@Test
	public void testImportWithUrl() throws LessException {
		String expected = "a {\n  color: #dddddd;\n  background-image: url(img/logo.png);\n}\n";
		String result = engine
				.compile(getResource("less/import-from-subdir.less"));
		assertEquals(expected, result);
	}

	@Test(expected = LessException.class)
	public void testImportWithMissingUrl() throws Exception {
		try {
			engine.compile(getResource("less/import-missing.less"));
		} catch (Exception e) {
			assertTrue("No such file", e.getMessage().contains("No such file"));
			throw e;
		}
	}

	@Test
	public void testSample() throws LessException {
		String expected = ".box {\n  color: #fe33ac;\n"
				+ "  border-color: #fdcdea;\n}\n.box div {\n"
				+ "  box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);\n"
				+ "  -webkit-box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);\n"
				+ "  -moz-box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);\n}\n";
		String result = engine.compile(getResource("less/sample.less"));
		assertEquals(expected, result);
	}

	@Test
	public void testDebugInfo() throws LessException, IOException {
		LessOptions options = new LessOptions();
		options.setLineNumbers(LessOptions.LINE_NUMBERS_COMMENTS);
		options.setCss(true);
		LessEngine debugEngine = new LessEngine(options);
		assertEquals("/* line 3",
				debugEngine.compile(getResource("css/multiple-imports.css"))
						.substring(0, 9));
	}

	private URL getResource(String path) {
		return getClass().getClassLoader().getResource("META-INF/" + path);
	}

}