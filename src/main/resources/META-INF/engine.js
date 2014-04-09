print = lessenv.print;
quit = lessenv.quit;
readFile = lessenv.readFile;
delete arguments;

var basePath = function(path) {
	if (path != null) {
		return path.replace(/^(.*[\/\\])[^\/\\]*$/, '$1');
	}
	return '';
}, compile = function(source, path, compress) {
	var error = null, result = null, parser = new (window.less.Parser)({
		optimization : lessenv.optimization,
		paths : [ basePath(path) ],
		filename : path,
		dumpLineNumbers : lessenv.lineNumbers,
		sourceMap: lessenv.sourceMap,
		sourceMapBasepath : lessenv.sourceMapBasepath,
		sourceMapRootpath : lessenv.sourceMapRootpath,
		sourceMapURL : lessenv.sourceMapURL,
		sourceMapGenerator : lessenv.sourceMapGenerator,
		writeSourceMap: lessenv.sourceMapURL ? lessenv.writeSourceMap : null
	});
	window.less.Parser.importer = function(path, currentFileInfo, callback, env) {
		if (!/^\//.test(path) && !/^\w+:/.test(path)
				&& currentFileInfo.currentDirectory) {
			path = currentFileInfo.currentDirectory + path;
		}
		if (path != null) {
			try {
				new (window.less.Parser)({
					optimization : lessenv.optimization,
					paths : [ basePath(path) ],
					filename : path,
					dumpLineNumbers : lessenv.lineNumbers,
					sourceMap: lessenv.sourceMap,
					sourceMapBasepath : lessenv.sourceMapBasepath,
					sourceMapRootpath : lessenv.sourceMapRootpath,
					sourceMapURL : lessenv.sourceMapURL,
					sourceMapGenerator : lessenv.sourceMapGenerator,
					writeSourceMap: lessenv.sourceMapURL ? lessenv.writeSourceMap : null
				}).parse(String(lessenv.loader.load(path, lessenv.charset)),
						function(e, root) {
							if (e != null)
								throw e;
							callback(e, root, path);
						});
			} catch (e) {
				error = e;
				throw e;
			}
		}
	};
	parser.parse(source, function(e, root) {
		if (e != null)
			throw e;
		result = root.toCSS({
			sourceMap: lessenv.sourceMap,
			sourceMapBasepath : lessenv.sourceMapBasepath,
			sourceMapRootpath : lessenv.sourceMapRootpath,
			sourceMapURL : lessenv.sourceMapURL,
			sourceMapGenerator : lessenv.sourceMapGenerator,
			writeSourceMap: lessenv.sourceMapURL ? lessenv.writeSourceMap : null
		});
		if (compress)
			result = exports.compressor.cssmin(result);
	});
	if (error != null)
		throw error;
	if (result != null)
		return result;
	else
		return '';
};
