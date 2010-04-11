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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rostislav Hristov
 */
public class LessEngineTest {
    
    private static LessEngine engine;
    
    @BeforeClass
    public static void before() {
        engine = new LessEngine();
    }
    
    @Test
    public void parse() throws LessException {
        assertEquals("div {\n  width: 2;\n}\n", engine.compile("div { width: 1 + 1 }"));
    }
    
    @Test
    public void compileToString() throws LessException, IOException {
        assertEquals("body {\n  color: #f0f0f0;\n}\n", 
                engine.compile(getClass().getClassLoader().getResource("META-INF/test.css")));
    }
    
    @Test
    public void compileToFile() throws LessException, IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = File.createTempFile("less.css", null, tempDir);
        engine.compile(
        		new File(getClass().getClassLoader().getResource("META-INF/test.css").getPath()), 
        		new File(tempFile.getAbsolutePath()));
        FileInputStream fstream = new FileInputStream(tempFile.getAbsolutePath());
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
    
}