var compileString = function(css) {
	var result;
    new(less.Parser)({ optimization: 3 }).parse(css, function (e, root) {
		result = root.toCSS();
    });
	return result;	
};

var compileFile = function(file) {
    var result, charset = 'UTF-8', dirname = file.replace(/[^\/]+$/, '');
    less.Parser.importer = function(path, paths, fn) {
        new(less.Parser)({ optimization: 3 }).parse(readUrl(dirname + path, charset), function (e, root) {
            fn(root);
        });
    };
    new(less.Parser)({ optimization: 3 }).parse(readUrl(file, charset), function (e, root) {
		result = root.toCSS();
    });
	return result;	
};