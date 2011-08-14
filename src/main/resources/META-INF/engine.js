var less = window.less,
    tree = less.tree;

var treeImport = tree.Import;

tree.Import = function (path, imports) {
    var that = this;

    this._path = path;

    // The '.less' extension is optional
    if (path instanceof tree.Quoted) {
        this.path = /\.(le?|c)ss$/.test(path.value) ? path.value : path.value + '.less';
    } else {
        this.path = path.value.value || path.value;
    }
    
    // Pre-compile all files
    imports.push(this.path, function (root) {
        if (! root) {
            throw new(Error)("Error parsing " + that.path);
        }
        that.root = root;
    });
};

for (var p in treeImport) {
    tree.Import[p] = treeImport[p]
};

var treeUrlProtype = tree.URL.prototype;

tree.URL = function (val, paths) {
    if (val.data) {
        this.attrs = val;
    } else {
        this.value = val;
        this.paths = paths;
    }
};

tree.URL.prototype = treeUrlProtype;


var compileString = function(css) {
    var result;
    new (less.Parser) ({ optimization: 3 }).parse(css, function (e, root) {
            result = root.toCSS();
            if (e instanceof Object)
                throw e;
    });
    return result;
};

var compileFile = function(file, classLoader) {
    var result, charset = 'UTF-8', cp = 'classpath:';
    less.Parser.importer = function(path, paths, fn) {
        if (path.indexOf(cp) != -1) {
            path = classLoader.getResource(path.replace(new RegExp('^.*' + cp), ''));
        } else if (!/^\//.test(path)) {
            path = paths[0] + path;
        }
        new(less.Parser)({ optimization: 3, paths: [String(path).replace(/[\w\.-]+$/, '')] }).parse(readUrl(path, charset).replace(/\r/g, ''), function (e, root) {
            fn(root);
            if (e instanceof Object)
                throw e;
        });
    };
    new(window.less.Parser)({ optimization: 3, paths: [file.replace(/[\w\.-]+$/, '')] }).parse(readUrl(file, charset).replace(/\r/g, ''), function (e, root) {
        result = root.toCSS();
        if (e instanceof Object)
            throw e;
    });
    return result;
};
