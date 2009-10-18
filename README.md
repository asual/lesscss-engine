LESS Engine
===========

LESS Engine is a Maven 2 artifact that wraps the original LESS sources 
and makes them available to Java developers thanks to JRuby. The project 
provides a straightforward access to the core LESS functionality.

Usage
-----

The following sample demonstrates how the API can be used to parse strings and
compile URL resources:

    // Instantiates a new LessEngine
    LessEngine engine = new LessEngine();
    
    // Compiles a CSS string
    String text = engine.compile("div { width: 1 + 1 }");

    // Compiles an URL resource
    String url = engine.compile(getClass().getClassLoader().getResource("META-INF/test.css"));

    // Creates a new file containing the compiled content
    engine.compile(new File("/Users/User/Projects/styles.less"), 
                   new File("/Users/User/Projects/styles.css"));
    
    // Destroys the engine
    engine.destroy();