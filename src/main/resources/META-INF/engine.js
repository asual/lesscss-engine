var compileString = function(css) {
	var result;
    new(window.less.Parser)({ optimization: 3 }).parse(css, function (e, root) {
		result = root.toCSS();
    });
	return result;	
};

var compileFile = function(file) {
    var result, charset = 'UTF-8', dirname = file.replace(/\\/g, '/').replace(/[^\/]+$/, '');
    window.less.Parser.importer = function(path, paths, fn) {
        new(window.less.Parser)({ optimization: 3 }).parse(readUrl(dirname + path, charset), function (e, root) {
            fn(root);
        });
    };
    new(window.less.Parser)({ optimization: 3 }).parse(readUrl(file, charset), function (e, root) {
		result = root.toCSS();
    });
	return result;	
};

var treeImport = window.less.tree.Import;

window.less.tree.Import = function (path, imports) {
    var that = this;

    this._path = path;

    // The '.less' extension is optional
    if (path instanceof window.less.tree.Quoted) {
        this.path = /\.(le?|c)ss$/.test(path.value) ? path.value : path.value + '.less';
    } else {
        this.path = path.value.value || path.value;
    }

    imports.push(this.path, function (root) {
        if (! root) {
            throw new(Error)('Error parsing ' + that.path + '.');
        }
        that.root = root;
    });
};

for (var p in treeImport) {
    window.less.tree.Import[p] = treeImport[p]
};