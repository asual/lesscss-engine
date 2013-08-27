var lessenv = {
	print : print,
	quit : quit,
	readFile : readFile,
	readUrl : readUrl
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