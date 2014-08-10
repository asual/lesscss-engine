print = lessenv.print;
quit = lessenv.quit;
readFile = lessenv.readFile;
readUrl = lessenv.readUrl;
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
		var fullpath = path;
		if (!/^\//.test(path) && !/^\w+:/.test(path)
				&& currentFileInfo.currentDirectory) {
			fullpath = currentFileInfo.currentDirectory + path;
		}
		var searchpaths = [];
		if(lessenv.paths) {
			for(var i = 0; i < lessenv.paths.length; i++) {
				searchpaths.push(lessenv.paths[i]);
			}
		}
		searchpaths.push(currentFileInfo.currentDirectory);
		if (fullpath != null) {
			try {
				new (window.less.Parser)({
					optimization : lessenv.optimization,
					paths : [ basePath(fullpath) ],
					filename : fullpath,
					dumpLineNumbers : lessenv.lineNumbers,
					sourceMap: lessenv.sourceMap,
					sourceMapBasepath : lessenv.sourceMapBasepath,
					sourceMapRootpath : lessenv.sourceMapRootpath,
					sourceMapURL : lessenv.sourceMapURL,
					sourceMapGenerator : lessenv.sourceMapGenerator,
					writeSourceMap: lessenv.sourceMapURL ? lessenv.writeSourceMap : null
				}).parse(String(lessenv.loader.load(path, searchpaths, lessenv.charset)),
						function(e, root) {
							if (e != null)
								throw e;
							callback(e, root, fullpath);
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
