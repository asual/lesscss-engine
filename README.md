LESS Engine
===========

LESS Engine is a Maven 2 artifact that wraps the original LESS sources 
and makes them available to Java developers thanks to JRuby. The project 
provides a straightforward access to the core LESS functionality.

Usage
-----

The following sample demonstrates how the API can be used to parse strings and
compile URL resources:

    LessEngine engine = new LessEngine();
    engine.parse("div { width: 1 + 1 }");
    engine.compile(getClass().getClassLoader().getResource("META-INF/test.css"));
    engine.destroy();