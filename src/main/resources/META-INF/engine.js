print = lessenv.print;
quit = lessenv.quit;
readFile = lessenv.readFile;
delete arguments;

if (lessenv.css) {
	readUrl = function(url, charset) {
		var content;
		if (!/^\w+:/.test(url)) {
			url = 'file:' + url;
		}
		try {
			content = lessenv.readUrl.apply(this, arguments);
		} catch (e) {
			content = lessenv.readUrl.apply(this, [url.replace(/\.less$/, '.css'), charset]);
		}
		return content.replace(/\.css/g, '.less');
	};
}

var compileString = function(css, compress) {
	var result;
	less.Parser.importer = function(path, paths, fn) {
		if (!/^\//.test(path)) {
			path = paths[0] + path;
		}
		if (path != null) {
			new(less.Parser)({ optimization: 3, paths: [String(path).replace(/[\w\.-]+$/, '')] }).parse(readUrl(path, lessenv.charset).replace(/\r/g, ''), function (e, root) {
				if (e instanceof Object)
					throw e;
				fn(e, root);
				if (e instanceof Object)
					throw e;
			});
		}
	};
	new (less.Parser) ({ optimization: 3 }).parse(css, function (e, root) {
		if (e instanceof Object)
			throw e;
		result = root.toCSS();
		if (compress)
			result = exports.compressor.cssmin(result);
		if (e instanceof Object)
			throw e;
	});
	return result;
};

var compileFile = function(file, classLoader, compress) {
	var result, cp = 'classpath:';
	less.Parser.importer = function(path, paths, fn) {
		if (path.indexOf(cp) != -1) {
			var resource = classLoader.getResource(path.replace(new RegExp('^.*' + cp), ''));
			if (lessenv.css && resource === null) {
				path = classLoader.getResource(path.replace(new RegExp('^.*' + cp), '').replace(/\.less$/, '.css'));
			} else {
				path = resource;
			}
		} else if (!/^\//.test(path)) {
			path = paths[0] + path;
		}
		if (path != null) {
			new(less.Parser)({ optimization: 3, paths: [String(path).replace(/[\w\.-]+$/, '')] }).parse(readUrl(path, lessenv.charset).replace(/\r/g, ''), function (e, root) {
				if (e instanceof Object)
					throw e;
				fn(e, root);
				if (e instanceof Object)
					throw e;
			});
		}
	};
	new(less.Parser)({ optimization: 3, paths: [file.replace(/[\w\.-]+$/, '')] }).parse(readUrl(file, lessenv.charset).replace(/\r/g, ''), function (e, root) {
		if (e instanceof Object)
			throw e;
		result = root.toCSS();
		if (compress)
			result = exports.compressor.cssmin(result);
		if (e instanceof Object)
			throw e;
	});
	return result;
};
