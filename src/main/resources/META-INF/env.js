var lessenv = {
	print : print,
	quit : quit,
	readFile : readFile,
	readUrl : readUrl,
	sourceMapGenerator : require("source-map/source-map-generator").SourceMapGenerator,
	writeSourceMap : function(sourceMap) {
		var url = lessenv.sourceMapURL;
		if (url !== null) {
			var writer = new java.io.FileWriter(url);
			try {
				writer.write(sourceMap);
			} finally {
				writer.close();
			}
		}
	}
}, arguments = [ '' ], exports = {}, location = {
	port : 0
}, document = {
	getElementsByTagName : function(name) {
		return [];
	}
}, window = {};

print = function() {};
quit = function() {
};
readFile = function() {
	return '';
};
