var	fs = require('fs'),
	Str = require('string');

const FAKE_PROPERTY = '__faker___:';
const importRe = new RegExp('@import[\\s]{1,}["\']{1}~./[\\w/.]{1,}["\']{1}[;]{1}', 'g');
const escapeRe = new RegExp('@\\{([^\\}]+)\\}', 'g');
const elurlRe = new RegExp('url\\(\\$\\{([^\\}]+)\\}\\)', 'g');
const elfunRe = new RegExp('\\$\\{([^\\}]+)\\}', 'g');
const taglibRe = new RegExp('<(.*)>', 'g');
const escOpenRe = new RegExp('@\\{');
const escCloseRe = new RegExp('\\}');
const openRe = new RegExp('\\$\\{');
const spRe = new RegExp(':');
const endRe = new RegExp('\\}');

module.exports = {

	log: function(msg) {
		console.log(msg);
	},

	fixSyntaxIssue: function(data) {
		data = this.removeComments(data);
		if (data) {
			var dataArray = data.split('\n'),
				hasProp = false,
				result = '';

			for (var i = 0; i < dataArray.length; i++) {
				var line = dataArray[i].trim(),
					start = line.indexOf(':'),
					end = line.indexOf('${'),
					lastIndex = line.length -1;

				if (start > 0 && end >= 0 && start < end && line.charAt(lastIndex) == ',')
					hasProp = true;

				// syntax issue with less but works in zk EL function
				if (end >= 0 && start >= -1 && start >= end) {
					if (hasProp && (line.charAt(lastIndex) == ',' || line.charAt(lastIndex) == ';' )) {
						result = result + line + '\n';
						continue;
					}
					if (!line.charAt(lastIndex) == ';') {
						if (!line.charAt(lastIndex) == ',') 
							this.log(line + "\n\tmissing ',' or ';' signature at the end of line");
						continue;
						
					}
					result += FAKE_PROPERTY;
				}
				if (line.charAt(lastIndex) == ';')
					hasProp = false; //reset
				result = result + line + '\n';
			}
				return result;
		} else {
			return null;
		}
	},

	encodeDsp: function(data, theme, importpath) {
		data = this.fixSyntaxIssue(data);
		if (data) {
			var dataArray = data.split('\n'),
				sub = '',
				matched,
				start,
				end,
				current;

			for (var i = 0; i < dataArray.length; i++) {
				current = dataArray[i];
				//1. resolve imports start with ~./ */
				if (matched = current.match(importRe)) {    
					sub = matched[0];
					start = current.indexOf(sub);
					end = start + sub.length;
					var quoteIndex = sub.indexOf('~'),
						quote = sub.substring(quoteIndex - 1, quoteIndex),
						re = new RegExp('["\']{1}~./', 'g'),
						newStr = quote + './' + importpath + '/web/';
					sub = sub.replace(re, newStr);
					dataArray[i] = current.substring(0, start) + sub + current.substring(end + 1);
				}

				if (current.indexOf('@import "classpath:web') >= 0) {
					dataArray[i] = current.replace('classpath:web/', importpath + '/web/');
				}

				//2. escape like @{variable}
				if (current.match(escapeRe)) {      

					var newline = '',
						matchArray = current.match(escapeRe);

					for (var j = 0; j < matchArray.length; j++) {
						sub = matchArray[j];
						start = current.indexOf(sub);
						end = start + sub.length;
						sub = sub.replace(escOpenRe, '__LESSOPEN__');
						sub = sub.replace(escCloseRe, '__LESSEND__');
						newline = current.substring(0, start) + sub + current.substring(end);
						current = newline;
						sub = '';
					}

					dataArray[i] = current = newline;
				}

				//3. resolve EL function in url() like url(${c:endcodeThemeURL})
				if (current.match(elurlRe)) {    

					var newline = '',
						matchArray = current.match(elurlRe);
					
					for (var j = 0; j < matchArray.length; j++) {
						sub = matchArray[j];
						start = current.indexOf(sub);
						end = start + sub.length;
						sub = sub.replace(openRe, '__EL__');
						sub = sub.replace(spRe, '__ELSP__');
						sub = sub.replace(endRe, '__ELEND__');
						sub = '~"' + sub + '"';
						newline = current.substring(0, start) + sub + current.substring(end);
						current = newline;
						sub = '';
					}
					dataArray[i] = current = newline;
				}

				//4. resolve EL function like ${t:applyCSS3}
				if (current.match(elfunRe)) {

					var newline = '',
						matchArray = current.match(elfunRe);

					for (var j = 0; j < matchArray.length; j++) {
						sub = matchArray[j];
						start = current.indexOf(sub);
						end = start + sub.length;
						sub = sub.replace(openRe, '__EL__');
						sub = sub.replace(spRe, '__ELSP__');
						sub = sub.replace(endRe, '__ELEND__');
						newline = current.substring(0, start) + sub + current.substring(end);
						current = newline;
					}
					dataArray[i] = current = newline;
				}

				//5. resolve DSP declaration like <@taglib @>
				if (current.match(taglibRe)) {

					var newline = '',
					matchArray = current.match(taglibRe);

					for (var j = 0; j < matchArray.length; j++) {
						sub = matchArray[j];
						start = current.indexOf(sub);
						end = start + sub.length;
						sub = '/*__TAGLIB ' + sub + ' TAGLIB__*/';
						newline = current.substring(0, start) +  sub + current.substring(end);
						current = newline;
					}
					dataArray[i] = current = newline;	
				}

				dataArray[i] = Str(dataArray[i]).replaceAll('__LESSOPEN__', '@\{').replaceAll('__LESSEND__', '\}').s;

				matched = null;
				current = '';
			}

			if (theme) {
				var s;
				if (theme === 'sapphire' || theme === 'silvertail') {
					s = '@import ' + '"../../zkthemes/' + theme + '/src/archive/web/' + theme + '/zul/less/_zkvariables.less";';
				} else if (theme === 'atlantic') {
					s = '@import ' + '"../../' + theme + '/src/main/resources/web/' + theme + '/zul/less/_zkvariables.less";';
				}

				dataArray.unshift(s);
			}
		}

		return dataArray.join('\n');
	},

	decodeDsp: function(data) {
				      //1. restore DSP declaration like <@taglib @>	
		return Str(data).replaceAll('/*__TAGLIB ', '')
				   	  .replaceAll(' TAGLIB__*/', '')
				      //2. restore EL function like ${c:encodeThemeURL}
				      .replaceAll('__EL__', '${').replaceAll('__ELSP__', ':')
				      .replaceAll('__ELEND__', '}').replaceAll(FAKE_PROPERTY, '').s;
	},

	removeComments: function(str) {
	    
	    var NQ = ' ',
	    	quote = NQ,
	    	len = str.length;

	    for (var j = 0, lineno = 1; j < len; j++) {
	    	var cj = str.charAt(j);
	    	if (cj == '\n')
	    		++lineno;

	    	if (quote != NQ) {
	    		if (cj == quote) {
	    			quote = NQ;
	    		} else if (cj == '\\') {
	    			j++;
	    			//fix for "123\\\r\n123"
	    			if (cj == '\r')
	    				j++;
	    		} else if (cj == '\n') {
	    			this.log('ERROR: Unterminated string at line' + lineno);
	    		}
	    	} else if (cj == '/' && j + 1 < len && (str.charAt(j + 1) == '*' || str.charAt(j + 1) == '/')) {
	    		
	    		var l = j,
	    			eol = str.charAt(++j) == '/';

	    		while (++j < len) {
	    			if (cj == '\n')
	    				++lineno;

	    			if (eol) {
	    				if (cj == '\n') {
	    					str = str.replace(str.substring(l, str.charAt(j - 1) == '\r' ? j - 1 : j), '');
	    					len = str.length;
	    					j = l;
	    					break;
	    				}
	    			} else if (cj == '*' && j + 1 < len && str.charAt(j + 1) == '/') {
	    				str = str.replace(str.substring(l, j + 2), '');
	    				len = str.length;
	    				j = l;
	    				break;
	    			}
	    		}
	    	} else if (cj == '\'' || cj == '"') {
	    		quote = cj;
	    	} else if (cj == '/') {    //regex
	    		var regex = false;
	    		for (var k = j;;) {
	    			if (--k < 0) {
	    				regex = true;
	    				break;
	    			}

	    			var ck = str.charAt(k);
	    			var map = {
	    				'(': true, ',': true, '=': true, ':': true, '?': true, '{': true,
	    				'[': true, ';': true, '!': true, '&': true, '|': true, '^': true
	    			};

	    			if (!ck == ' ') {    // check if ck is a white space
	    				regex = map[ck] || (ck == 'n' && k > 4 && str.substring(k - 5, k + 1) == 'return')
	    						|| (ck == 'e' && k > 2 && str.substring(k - 3, k + 1) == 'case');
	    				break;
	    			}
	    		}
	    		if (regex) {
	    			while (++j < len && cj != '/') {
	    				if (cj == '\\')
	    					j++;
	    				else if (cj == '\n') {
	    					this.log('ERROR: Unterminated regex at line ' + lineno);
	    				}
	    			}
	    		}
	    	}
	    }
	    return str;
	}
}
