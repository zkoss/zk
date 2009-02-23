/*  Prototype JavaScript framework, version 1.5.0
 *  (c) 2005-2007 Sam Stephenson
 *
 *  Prototype is freely distributable under the terms of an MIT-style license.
 *  For details, see the Prototype web site: http://prototype.conio.net/
 *
/*--------------------------------------------------------------------------*/
//Tom M. Yeh, Potix :prevent it from load twice
if (!window.z_prot_js) {
z_prot_js = true;

var zPrototype = {
  Version: '1.5.0',
/* Tom M. Yeh, Potix: remove unused codes
  BrowserFeatures: {
    XPath: !!document.evaluate
  },
*/
  ScriptFragment: '(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)',
  emptyFunction: function() {},
  K: function(x) { return x }
}

var zClass = {
  create: function() {
    return function() {
      this.initialize.apply(this, arguments);
    }
  }
}

/* Tom M. Yeh, Potix: remove unused codes
var Abstract = new Object();
*/
Object.extend = function(destination, source) {
  for (var property in source) {
    destination[property] = source[property];
  }
  return destination;
}

Object.extend(Object, {
  inspect: function(object) {
    try {
      if (object === undefined) return 'undefined';
      if (object === null) return 'null';
      return object.inspect ? object.inspect() : object.toString();
    } catch (e) {
      if (e instanceof RangeError) return '...';
      throw e;
    }
  },

  keys: function(object) {
    var keys = [];
    for (var property in object)
      keys.push(property);
    return keys;
  },

  values: function(object) {
    var values = [];
    for (var property in object)
      values.push(object[property]);
    return values;
  },

  clone: function(object) {
    return Object.extend({}, object);
  }
});

Function.prototype.bind = function() {
  var __method = this, args = z$A(arguments), object = args.shift();
  return function() {
    return __method.apply(object, args.concat(z$A(arguments)));
  }
}

Function.prototype.bindAsEventListener = function(object) {
  var __method = this, args = z$A(arguments), object = args.shift();
  return function(event) {
    return __method.apply(object, [( event || window.event)].concat(args).concat(z$A(arguments)));
  }
}

Object.extend(Number.prototype, {
  toColorPart: function() {
    var digits = this.toString(16);
    if (this < 16) return '0' + digits;
    return digits;
  },

  succ: function() {
    return this + 1;
  },

  times: function(iterator) {
    z$R(0, this, true).each(iterator);
    return this;
  }
});

/* Tom M. Yeh, Potix: remove unused codes
var Try = {
  these: function() {
    var returnValue;

    for (var i = 0, length = arguments.length; i < length; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) {}
    }

    return returnValue;
  }
}
*/
/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
var PeriodicalExecuter = zClass.create();
PeriodicalExecuter.prototype = {
  initialize: function(callback, frequency) {
    this.callback = callback;
    this.frequency = frequency;
    this.currentlyExecuting = false;

    this.registerCallback();
  },

  registerCallback: function() {
    this.timer = setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  stop: function() {
    if (!this.timer) return;
    clearInterval(this.timer);
    this.timer = null;
  },

  onTimerEvent: function() {
    if (!this.currentlyExecuting) {
      try {
        this.currentlyExecuting = true;
        this.callback(this);
      } finally {
        this.currentlyExecuting = false;
      }
    }
  }
}
*/
String.interpret = function(value){
  return value == null ? '' : String(value);
}

Object.extend(String.prototype, {
  gsub: function(pattern, replacement) {
    var result = '', source = this, match;
    replacement = arguments.callee.prepareReplacement(replacement);

    while (source.length > 0) {
      if (match = source.match(pattern)) {
        result += source.slice(0, match.index);
        result += String.interpret(replacement(match));
        source  = source.slice(match.index + match[0].length);
      } else {
        result += source, source = '';
      }
    }
    return result;
  },

  sub: function(pattern, replacement, count) {
    replacement = this.gsub.prepareReplacement(replacement);
    count = count === undefined ? 1 : count;

    return this.gsub(pattern, function(match) {
      if (--count < 0) return match[0];
      return replacement(match);
    });
  },

/* Tom M. Yeh, Potix: remove unused codes
  scan: function(pattern, iterator) {
    this.gsub(pattern, iterator);
    return this;
  },

  truncate: function(length, truncation) {
    length = length || 30;
    truncation = truncation === undefined ? '...' : truncation;
    return this.length > length ?
      this.slice(0, length - truncation.length) + truncation : this;
  },
*/
  strip: function() {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
  },

/* Tom M. Yeh, Potix: remove unused codes
  stripTags: function() {
    return this.replace(/<\/?[^>]+>/gi, '');
  },

  stripScripts: function() {
    return this.replace(new RegExp(zPrototype.ScriptFragment, 'img'), '');
  },

  extractScripts: function() {
    var matchAll = new RegExp(zPrototype.ScriptFragment, 'img');
    var matchOne = new RegExp(zPrototype.ScriptFragment, 'im');
    return (this.match(matchAll) || []).map(function(scriptTag) {
      return (scriptTag.match(matchOne) || ['', ''])[1];
    });
  },

  evalScripts: function() {
    return this.extractScripts().map(function(script) { return eval(script) });
  },

  escapeHTML: function() {
    var div = document.createElement('div');
    var text = document.createTextNode(this);
    div.appendChild(text);
    return div.innerHTML;
  },

  unescapeHTML: function() {
    var div = document.createElement('div');
    div.innerHTML = this.stripTags();
    return div.childNodes[0] ? (div.childNodes.length > 1 ?
      z$A(div.childNodes).inject('',function(memo,node){ return memo+node.nodeValue }) :
      div.childNodes[0].nodeValue) : '';
  },

  toQueryParams: function(separator) {
    var match = this.strip().match(/([^?#]*)(#.*)?$/);
    if (!match) return {};

    return match[1].split(separator || '&').inject({}, function(hash, pair) {
      if ((pair = pair.split('='))[0]) {
        var name = decodeURIComponent(pair[0]);
        var value = pair[1] ? decodeURIComponent(pair[1]) : undefined;

        if (hash[name] !== undefined) {
          if (hash[name].constructor != Array)
            hash[name] = [hash[name]];
          if (value) hash[name].push(value);
        }
        else hash[name] = value;
      }
      return hash;
    });
  },
*/
  toArray: function() {
    return this.split('');
  },

  succ: function() {
    return this.slice(0, this.length - 1) +
      String.fromCharCode(this.charCodeAt(this.length - 1) + 1);
  },

  camelize: function() {
    var parts = this.split('-'), len = parts.length;
    if (len == 1) return parts[0];

    var camelized = this.charAt(0) == '-'
      ? parts[0].charAt(0).toUpperCase() + parts[0].substring(1)
      : parts[0];

    for (var i = 1; i < len; i++)
      camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);

    return camelized;
  },

  capitalize: function(){
    return this.charAt(0).toUpperCase() + this.substring(1).toLowerCase();
  },

  underscore: function() {
    return this.gsub(/::/, '/').gsub(/([A-Z]+)([A-Z][a-z])/,'#{1}_#{2}').gsub(/([a-z\d])([A-Z])/,'#{1}_#{2}').gsub(/-/,'_').toLowerCase();
  },

  dasherize: function() {
    return this.gsub(/_/,'-');
  },

  inspect: function(useDoubleQuotes) {
    var escapedString = this.replace(/\\/g, '\\\\');
    if (useDoubleQuotes)
      return '"' + escapedString.replace(/"/g, '\\"') + '"';
    else
      return "'" + escapedString.replace(/'/g, '\\\'') + "'";
  }
});

String.prototype.gsub.prepareReplacement = function(replacement) {
  if (typeof replacement == 'function') return replacement;
  var template = new zTemplate(replacement);
  return function(match) { return template.evaluate(match) };
}

/* Tom M. Yeh, Potix: remove unused codes
String.prototype.parseQuery = String.prototype.toQueryParams;
*/
var zTemplate = zClass.create();
zTemplate.Pattern = /(^|.|\r|\n)(#\{(.*?)\})/;
zTemplate.prototype = {
  initialize: function(template, pattern) {
    this.template = template.toString();
    this.pattern  = pattern || zTemplate.Pattern;
  },

  evaluate: function(object) {
    return this.template.gsub(this.pattern, function(match) {
      var before = match[1];
      if (before == '\\') return match[2];
      return before + String.interpret(object[match[3]]);
    });
  }
}

var z$break    = new Object();
var z$continue = new Object();

var zEnum = {
  each: function(iterator) {
    var index = 0;
    try {
      this._each(function(value) {
        try {
          iterator(value, index++);
        } catch (e) {
          if (e != z$continue) throw e;
        }
      });
    } catch (e) {
      if (e != z$break) throw e;
    }
    return this;
  },

/* Tom M. Yeh, Potix: remove unused codes
  eachSlice: function(number, iterator) {
    var index = -number, slices = [], array = this.toArray();
    while ((index += number) < array.length)
      slices.push(array.slice(index, index+number));
    return slices.map(iterator);
  },

  all: function(iterator) {
    var result = true;
    this.each(function(value, index) {
      result = result && !!(iterator || zPrototype.K)(value, index);
      if (!result) throw z$break;
    });
    return result;
  },

  any: function(iterator) {
    var result = false;
    this.each(function(value, index) {
      if (result = !!(iterator || zPrototype.K)(value, index))
        throw z$break;
    });
    return result;
  },
*/
  collect: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      results.push((iterator || zPrototype.K)(value, index));
    });
    return results;
  },

  detect: function(iterator) {
    var result;
    this.each(function(value, index) {
      if (iterator(value, index)) {
        result = value;
        throw z$break;
      }
    });
    return result;
  },

  findAll: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (iterator(value, index))
        results.push(value);
    });
    return results;
  },

/* Tom M. Yeh, Potix: remove unused codes
  grep: function(pattern, iterator) {
    var results = [];
    this.each(function(value, index) {
      var stringValue = value.toString();
      if (stringValue.match(pattern))
        results.push((iterator || zPrototype.K)(value, index));
    })
    return results;
  },
*/
  include: function(object) {
    var found = false;
    this.each(function(value) {
      if (value == object) {
        found = true;
        throw z$break;
      }
    });
    return found;
  },

/* Tom M. Yeh, Potix: remove unused codes
  inGroupsOf: function(number, fillWith) {
    fillWith = fillWith === undefined ? null : fillWith;
    return this.eachSlice(number, function(slice) {
      while(slice.length < number) slice.push(fillWith);
      return slice;
    });
  },
*/
  inject: function(memo, iterator) {
    this.each(function(value, index) {
      memo = iterator(memo, value, index);
    });
    return memo;
  },

  invoke: function(method) {
    var args = z$A(arguments).slice(1);
    return this.map(function(value) {
      return value[method].apply(value, args);
    });
  },

  max: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || zPrototype.K)(value, index);
      if (result == undefined || value >= result)
        result = value;
    });
    return result;
  },

/* Tom M. Yeh, Potix: remove unused codes
  min: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || zPrototype.K)(value, index);
      if (result == undefined || value < result)
        result = value;
    });
    return result;
  },

  partition: function(iterator) {
    var trues = [], falses = [];
    this.each(function(value, index) {
      ((iterator || zPrototype.K)(value, index) ?
        trues : falses).push(value);
    });
    return [trues, falses];
  },
*/
  pluck: function(property) {
    var results = [];
    this.each(function(value, index) {
      results.push(value[property]);
    });
    return results;
  },

  reject: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (!iterator(value, index))
        results.push(value);
    });
    return results;
  },

/* Tom M. Yeh, Potix: remove unused codes
  sortBy: function(iterator) {
    return this.map(function(value, index) {
      return {value: value, criteria: iterator(value, index)};
    }).sort(function(left, right) {
      var a = left.criteria, b = right.criteria;
      return a < b ? -1 : a > b ? 1 : 0;
    }).pluck('value');
  },
*/
  toArray: function() {
    return this.map();
/* Tom M. Yeh, Potix: remove unused codes
  },

  zip: function() {
    var iterator = zPrototype.K, args = z$A(arguments);
    if (typeof args.last() == 'function')
      iterator = args.pop();

    var collections = [this].concat(args).map($A);
    return this.map(function(value, index) {
      return iterator(collections.pluck(index));
    });
  },

  size: function() {
    return this.toArray().length;
  },

  inspect: function() {
    return '#<zEnum:' + this.toArray().inspect() + '>';
*/
  }
}

Object.extend(zEnum, {
  map:     zEnum.collect,
  find:    zEnum.detect,
  select:  zEnum.findAll,
  member:  zEnum.include,
  entries: zEnum.toArray
});
var z$A = Array.from = function(iterable) {
  if (!iterable) return [];
  if (iterable.toArray) {
    return iterable.toArray();
  } else {
    var results = [];
    for (var i = 0, length = iterable.length; i < length; i++)
      results.push(iterable[i]);
    return results;
  }
}

Object.extend(Array.prototype, zEnum);

if (!Array.prototype._reverse)
  Array.prototype._reverse = Array.prototype.reverse;

Object.extend(Array.prototype, {
  _each: function(iterator) {
    for (var i = 0, length = this.length; i < length; i++)
      iterator(this[i]);
  },

/* Tom M. Yeh, Potix: remove unused codes
  clear: function() {
    this.length = 0;
    return this;
  },
*/
  first: function() {
    return this[0];
  },

/* Tom M. Yeh, Potix: remove unused codes
  last: function() {
    return this[this.length - 1];
  },

  compact: function() {
    return this.select(function(value) {
      return value != null;
    });
  },
*/
  flatten: function() {
    return this.inject([], function(array, value) {
      return array.concat(value && value.constructor == Array ?
        value.flatten() : [value]);
    });
  },

  without: function() {
    var values = z$A(arguments);
    return this.select(function(value) {
      return !values.include(value);
    });
  },

/* Tom M. Yeh, Potix: remove unused codes
  indexOf: function(object) {
    for (var i = 0, length = this.length; i < length; i++)
      if (this[i] == object) return i;
    return -1;
  },
*/
  reverse: function(inline) {
    return (inline !== false ? this : this.toArray())._reverse();
  },

/* Tom M. Yeh, Potix: remove unused codes
  reduce: function() {
    return this.length > 1 ? this : this[0];
  },

  uniq: function() {
    return this.inject([], function(array, value) {
      return array.include(value) ? array : array.concat([value]);
    });
  },
*/
  clone: function() {
    return [].concat(this);
  },

/* Tom M. Yeh, Potix: remove unused codes
  size: function() {
    return this.length;
  },
*/
  inspect: function() {
    return '[' + this.map(Object.inspect).join(', ') + ']';
  }
});

Array.prototype.toArray = Array.prototype.clone;

function z$w(string){
  string = string.strip();
  return string ? string.split(/\s+/) : [];
}

if(window.opera){
  Array.prototype.concat = function(){
    var array = [];
    for(var i = 0, length = this.length; i < length; i++) array.push(this[i]);
    for(var i = 0, length = arguments.length; i < length; i++) {
      if(arguments[i].constructor == Array) {
        for(var j = 0, arrayLength = arguments[i].length; j < arrayLength; j++)
          array.push(arguments[i][j]);
      } else {
        array.push(arguments[i]);
      }
    }
    return array;
  }
}
var zHash = function(obj) {
  Object.extend(this, obj || {});
};

/* Tom M. Yeh, Potix: remove unused codes
Object.extend(zHash, {
  toQueryString: function(obj) {
    var parts = [];

	  this.prototype._each.call(obj, function(pair) {
      if (!pair.key) return;

      if (pair.value && pair.value.constructor == Array) {
        var values = pair.value.compact();
        if (values.length < 2) pair.value = values.reduce();
        else {
        	key = encodeURIComponent(pair.key);
          values.each(function(value) {
            value = value != undefined ? encodeURIComponent(value) : '';
            parts.push(key + '=' + encodeURIComponent(value));
          });
          return;
        }
      }
      if (pair.value == undefined) pair[1] = '';
      parts.push(pair.map(encodeURIComponent).join('='));
	  });

    return parts.join('&');
  }
});
*/
Object.extend(zHash.prototype, zEnum);
Object.extend(zHash.prototype, {
  _each: function(iterator) {
    for (var key in this) {
      var value = this[key];
      if (value && value == zHash.prototype[key]) continue;

      var pair = [key, value];
      pair.key = key;
      pair.value = value;
      iterator(pair);
    }
  },

  keys: function() {
    return this.pluck('key');
  },

  values: function() {
    return this.pluck('value');
  },

/* Tom M. Yeh, Potix: remove unused codes
  merge: function(hash) {
    return z$H(hash).inject(this, function(mergedHash, pair) {
      mergedHash[pair.key] = pair.value;
      return mergedHash;
    });
  },
*/
  remove: function() {
    var result;
    for(var i = 0, length = arguments.length; i < length; i++) {
      var value = this[arguments[i]];
      if (value !== undefined){
        if (result === undefined) result = value;
        else {
          if (result.constructor != Array) result = [result];
          result.push(value)
        }
      }
      delete this[arguments[i]];
    }
    return result;
/* Tom M. Yeh, Potix: remove unused codes
  },

  toQueryString: function() {
    return zHash.toQueryString(this);
  },

  inspect: function() {
    return '#<zHash:{' + this.map(function(pair) {
      return pair.map(Object.inspect).join(': ');
    }).join(', ') + '}>';
*/
  }
});

function z$H(object) {
  if (object && object.constructor == zHash) return object;
  return new zHash(object);
};
zObjectRange = zClass.create();
Object.extend(zObjectRange.prototype, zEnum);
Object.extend(zObjectRange.prototype, {
  initialize: function(start, end, exclusive) {
    this.start = start;
    this.end = end;
    this.exclusive = exclusive;
  },

  _each: function(iterator) {
    var value = this.start;
    while (this.include(value)) {
      iterator(value);
      value = value.succ();
    }
  },

  include: function(value) {
    if (value < this.start)
      return false;
    if (this.exclusive)
      return value < this.end;
    return value <= this.end;
  }
});

var z$R = function(start, end, exclusive) {
  return new zObjectRange(start, end, exclusive);
}

/* Tom M. Yeh, Potix: remove Ajax
var Ajax = {
...
}
*/
function z$(element) {
  if (arguments.length > 1) {
    for (var i = 0, elements = [], length = arguments.length; i < length; i++)
      elements.push(z$(arguments[i]));
    return elements;
  }
  if (typeof element == 'string')
    element = document.getElementById(element);
  return Element.extend(element);
}

/* Tom M. Yeh, Potix: remove unused codes
if (zPrototype.BrowserFeatures.XPath) {
  document._getElementsByXPath = function(expression, parentElement) {
    var results = [];
    var query = document.evaluate(expression, z$(parentElement) || document,
      null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
    for (var i = 0, length = query.snapshotLength; i < length; i++)
      results.push(query.snapshotItem(i));
    return results;
  };
}

document.getElementsByClassName = function(className, parentElement) {
  if (zPrototype.BrowserFeatures.XPath) {
    var q = ".//*[contains(concat(' ', @class, ' '), ' " + className + " ')]";
    return document._getElementsByXPath(q, parentElement);
  } else {
    var children = (z$(parentElement) || document.body).getElementsByTagName('*');
    var elements = [], child;
    for (var i = 0, length = children.length; i < length; i++) {
      child = children[i];
      if (Element.hasClassName(child, className))
        elements.push(Element.extend(child));
    }
    return elements;
  }
};
*/
/*--------------------------------------------------------------------------*/

if (!window.Element)
  var Element = new Object();

Element.extend = function(element) {
  if (!element || _nativeExtensions || element.nodeType == 3) return element;

  if (!element._extended && element.tagName && element != window) {
    var methods = Object.clone(Element.Methods), cache = Element.extend.cache;

/* Tom M. Yeh, Potix: remove unused codes
    if (element.tagName == 'FORM')
      Object.extend(methods, Form.Methods);
    if (['INPUT', 'TEXTAREA', 'SELECT'].include(element.tagName))
      Object.extend(methods, Form.Element.Methods);

    Object.extend(methods, Element.Methods.Simulated);
*/
    for (var property in methods) {
      var value = methods[property];
      if (typeof value == 'function' && !(property in element))
        element[property] = cache.findOrStore(value);
    }
  }

  element._extended = true;
  return element;
};

Element.extend.cache = {
  findOrStore: function(value) {
    return this[value] = this[value] || function() {
      return value.apply(null, [this].concat(z$A(arguments)));
    }
  }
};

Element.Methods = {
  visible: function(element) {
    return z$(element).style.display != 'none';
  },

  toggle: function(element) {
    element = z$(element);
    Element[Element.visible(element) ? 'hide' : 'show'](element);
    return element;
  },

  hide: function(element) {
    z$(element).style.display = 'none';
    return element;
  },

  show: function(element) {
    z$(element).style.display = '';
    return element;
  },

  remove: function(element) {
    element = z$(element);
    element.parentNode.removeChild(element);
    return element;
  },

/* Tom M. Yeh, Potix: remove unused codes
  update: function(element, html) {
    html = typeof html == 'undefined' ? '' : html.toString();
    z$(element).innerHTML = html.stripScripts();
    setTimeout(function() {html.evalScripts()}, 10);
    return element;
  },

  replace: function(element, html) {
    element = z$(element);
    html = typeof html == 'undefined' ? '' : html.toString();
    if (element.outerHTML) {
      element.outerHTML = html.stripScripts();
    } else {
      var range = element.ownerDocument.createRange();
      range.selectNodeContents(element);
      element.parentNode.replaceChild(
        range.createContextualFragment(html.stripScripts()), element);
    }
    setTimeout(function() {html.evalScripts()}, 10);
    return element;
  },

  inspect: function(element) {
    element = z$(element);
    var result = '<' + element.tagName.toLowerCase();
    z$H({'id': 'id', 'className': 'class'}).each(function(pair) {
      var property = pair.first(), attribute = pair.last();
      var value = (element[property] || '').toString();
      if (value) result += ' ' + attribute + '=' + value.inspect(true);
    });
    return result + '>';
  },

  recursivelyCollect: function(element, property) {
    element = z$(element);
    var elements = [];
    while (element = element[property])
      if (element.nodeType == 1)
        elements.push(Element.extend(element));
    return elements;
  },

  ancestors: function(element) {
    return z$(element).recursivelyCollect('parentNode');
  },
*/
  descendants: function(element) {
    return z$A(z$(element).getElementsByTagName('*'));
  },

/* Tom M. Yeh, Potix: remove unused codes
  immediateDescendants: function(element) {
    if (!(element = z$(element).firstChild)) return [];
    while (element && element.nodeType != 1) element = element.nextSibling;
    if (element) return [element].concat(z$(element).nextSiblings());
    return [];
  },

  previousSiblings: function(element) {
    return z$(element).recursivelyCollect('previousSibling');
  },

  nextSiblings: function(element) {
    return z$(element).recursivelyCollect('nextSibling');
  },

  siblings: function(element) {
    element = z$(element);
    return element.previousSiblings().reverse().concat(element.nextSiblings());
  },
*/
  match: function(element, selector) {
    if (typeof selector == 'string')
      selector = new zSelector(selector);
    return selector.match(z$(element));
  },

/* Tom M. Yeh, Potix: remove unused codes
  up: function(element, expression, index) {
    return zSelector.findElement(z$(element).ancestors(), expression, index);
  },
*/
  down: function(element, expression, index) {
    return zSelector.findElement(z$(element).descendants(), expression, index);
  },

/* Tom M. Yeh, Potix: remove unused codes
  previous: function(element, expression, index) {
    return zSelector.findElement(z$(element).previousSiblings(), expression, index);
  },

  next: function(element, expression, index) {
    return zSelector.findElement(z$(element).nextSiblings(), expression, index);
  },

  getElementsBySelector: function() {
    var args = z$A(arguments), element = z$(args.shift());
    return zSelector.findChildElements(element, args);
  },

  getElementsByClassName: function(element, className) {
    return document.getElementsByClassName(className, element);
  },
*/
  readAttribute: function(element, name) {
    element = z$(element);
    if (document.all && !window.opera) {
      var t = Element._attributeTranslations;
      if (t.values[name]) return t.values[name](element, name);
      if (t.names[name])  name = t.names[name];
      var attribute = element.attributes[name];
      if(attribute) return attribute.nodeValue;
    }
    return element.getAttribute(name);
  },

/* Tom M. Yeh, Potix: remove unused codes
  getHeight: function(element) {
    return z$(element).getDimensions().height;
  },

  getWidth: function(element) {
    return z$(element).getDimensions().width;
  },
*/
  classNames: function(element) {
    return new Element.ClassNames(element);
  },

  hasClassName: function(element, className) {
    if (!(element = z$(element))) return;
    var elementClassName = element.className;
    if (elementClassName.length == 0) return false;
    if (elementClassName == className ||
        elementClassName.match(new RegExp("(^|\\s)" + className + "(\\s|$)")))
      return true;
    return false;
  },

  addClassName: function(element, className) {
    if (!(element = z$(element))) return;
    Element.classNames(element).add(className);
    return element;
  },

  removeClassName: function(element, className) {
    if (!(element = z$(element))) return;
    Element.classNames(element).remove(className);
    return element;
  },

/* Tom M. Yeh, Potix: remove unused codes
  toggleClassName: function(element, className) {
    if (!(element = z$(element))) return;
    Element.classNames(element)[element.hasClassName(className) ? 'remove' : 'add'](className);
    return element;
  },
*/
  observe: function() {
    Event.observe.apply(Event, arguments);
    return z$A(arguments).first();
  },

  stopObserving: function() {
    Event.stopObserving.apply(Event, arguments);
    return z$A(arguments).first();
  },

  // removes whitespace-only text node children
  cleanWhitespace: function(element) {
    element = z$(element);
    var node = element.firstChild;
    while (node) {
      var nextNode = node.nextSibling;
      if (node.nodeType == 3 && !/\S/.test(node.nodeValue))
        element.removeChild(node);
      node = nextNode;
    }
    return element;
  },

/* Tom M. Yeh, Potix: remove unused codes
  empty: function(element) {
    return z$(element).innerHTML.match(/^\s*$/);
  },
*/
  descendantOf: function(element, ancestor) {
    element = z$(element), ancestor = z$(ancestor);
    while (element = element.parentNode)
      if (element == ancestor) return true;
    return false;
  },

  scrollTo: function(element) {
    element = z$(element);
    var pos = zPos.cumulativeOffset(element);
    window.scrollTo(pos[0], pos[1]);
    return element;
  },

  getStyle: function(element, style) {
    element = z$(element);
    if (['float','cssFloat'].include(style))
      style = (typeof element.style.styleFloat != 'undefined' ? 'styleFloat' : 'cssFloat');
    style = style.camelize();
    var value = element.style[style];
    if (!value) {
      if (document.defaultView && document.defaultView.getComputedStyle) {
        var css = document.defaultView.getComputedStyle(element, null);
        value = css ? css[style] : null;
      } else if (element.currentStyle) {
        value = element.currentStyle[style];
      }
    }

    if((value == 'auto') && ['width','height'].include(style) && (element.getStyle('display') != 'none'))
      value = element['offset'+style.capitalize()] + 'px';

    if (window.opera && ['left', 'top', 'right', 'bottom'].include(style))
      if (Element.getStyle(element, 'position') == 'static') value = 'auto';
    if(style == 'opacity') {
      if(value) return parseFloat(value);
      if(value = (element.getStyle('filter') || '').match(/alpha\(opacity=(.*)\)/))
        if(value[1]) return parseFloat(value[1]) / 100;
      return 1.0;
    }
    return value == 'auto' ? null : value;
  },

  setStyle: function(element, style) {
    element = z$(element);
    for (var name in style) {
      var value = style[name];
      if(name == 'opacity') {
        if (value == 1) {
          value = (/Gecko/.test(navigator.userAgent) &&
            !/Konqueror|Safari|KHTML/.test(navigator.userAgent)) ? 0.999999 : 1.0;
          if(/MSIE/.test(navigator.userAgent) && !window.opera)
            element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
        } else if(value === '') {
          if(/MSIE/.test(navigator.userAgent) && !window.opera)
            element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
        } else {
          if(value < 0.00001) value = 0;
          if(/MSIE/.test(navigator.userAgent) && !window.opera)
            element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'') +
              'alpha(opacity='+value*100+')';
        }
      } else if(['float','cssFloat'].include(name)) name = (typeof element.style.styleFloat != 'undefined') ? 'styleFloat' : 'cssFloat';
      element.style[name.camelize()] = value;
    }
    return element;
  },

  getDimensions: function(element) {
    element = z$(element);
    var display = z$(element).getStyle('display');
    if (display != 'none' && display != null) // Safari bug
      return {
	  		width: zk.offsetWidth(element), height: zk.offsetHeight(element),
			top: zk.offsetTop(element), left: zk.offsetLeft(element)
			}; //Tom M. Yeh, Potix: safari bug

    // All *Width and *Height properties give 0 on elements with display none,
    // so enable the element temporarily
    var els = element.style;
    var originalVisibility = els.visibility;
    var originalPosition = els.position;
    var originalDisplay = els.display;
    els.visibility = 'hidden';
    els.position = 'absolute';
    els.display = 'block';
    var originalWidth = element.clientWidth,
		originalHeight = element.clientHeight,
		originalTop = element.offsetTop,
		originalLeft = element.offsetLeft;
    els.display = originalDisplay;
    els.position = originalPosition;
    els.visibility = originalVisibility;
    return {
			width: originalWidth, height: originalHeight,
			top: originalTop, left: originalLeft
		};
  },

  makePositioned: function(element) {
    element = z$(element);
    var pos = Element.getStyle(element, 'position');
    if (pos == 'static' || !pos) {
      element._madePositioned = true;
      element.style.position = 'relative';
      // Opera returns the offset relative to the positioning context, when an
      // element is position relative but top and left have not been defined
      if (window.opera) {
        element.style.top = 0;
        element.style.left = 0;
      }
    }
    return element;
  },

  undoPositioned: function(element) {
    element = z$(element);
    if (element._madePositioned) {
      element._madePositioned = undefined;
      element.style.position =
        element.style.top =
        element.style.left =
        element.style.bottom =
        element.style.right = '';
    }
    return element;
  },

  makeClipping: function(element) {
    element = z$(element);
    if (element._clipping) return element;
	element._clipping = true;
    element._overflow = element.style.overflow;
    element._overflowX = element.style.overflowX;
	element._overflowY = element.style.overflowY;
//Tom M. Yeh, Potix: optimize it
//    if ((Element.getStyle(element, 'overflow') || 'visible') != 'hidden')
    if (Element.getStyle(element, 'overflow') != 'hidden')
      element.style.overflow = 'hidden';
    return element;
  },

  undoClipping: function(element) {
    element = z$(element);
    if (!element._clipping) return element;
//Tom M. Yeh, Potix
//Bug 1822717 and 1882277
    element.style.overflow = element._overflow;
	element.style.overflowX = element._overflowX;
	element.style.overflowY = element._overflowY;
    element._clipping = element._overflow = element._overflowX = element._overflowY = undefined;
    return element;
  }
};

Object.extend(Element.Methods, {childOf: Element.Methods.descendantOf});

Element._attributeTranslations = {};

Element._attributeTranslations.names = {
  colspan:   "colSpan",
  rowspan:   "rowSpan",
  valign:    "vAlign",
  datetime:  "dateTime",
  accesskey: "accessKey",
  tabindex:  "tabIndex",
  enctype:   "encType",
  maxlength: "maxLength",
  readonly:  "readOnly",
  longdesc:  "longDesc"
};

Element._attributeTranslations.values = {
  _getAttr: function(element, attribute) {
    return element.getAttribute(attribute, 2);
  },

  _flag: function(element, attribute) {
    return z$(element).hasAttribute(attribute) ? attribute : null;
  },

  style: function(element) {
    return element.style.cssText.toLowerCase();
  },

  title: function(element) {
    var node = element.getAttributeNode('title');
    return node.specified ? node.nodeValue : null;
  }
};

Object.extend(Element._attributeTranslations.values, {
  href: Element._attributeTranslations.values._getAttr,
  src:  Element._attributeTranslations.values._getAttr,
  disabled: Element._attributeTranslations.values._flag,
  checked:  Element._attributeTranslations.values._flag,
  readonly: Element._attributeTranslations.values._flag,
  multiple: Element._attributeTranslations.values._flag
});

/* Tom M. Yeh, Potix: remove unused codes
Element.Methods.Simulated = {
  hasAttribute: function(element, attribute) {
    var t = Element._attributeTranslations;
    attribute = t.names[attribute] || attribute;
    return z$(element).getAttributeNode(attribute).specified;
  }
};

// IE is missing .innerHTML support for TABLE-related elements
if (document.all && !window.opera){
  Element.Methods.update = function(element, html) {
    element = z$(element);
    html = typeof html == 'undefined' ? '' : html.toString();
    var tagName = element.tagName.toUpperCase();
    if (['THEAD','TBODY','TR','TD'].include(tagName)) {
      var div = document.createElement('div');
      switch (tagName) {
        case 'THEAD':
        case 'TBODY':
          div.innerHTML = '<table><tbody>' +  html.stripScripts() + '</tbody></table>';
          depth = 2;
          break;
        case 'TR':
          div.innerHTML = '<table><tbody><tr>' +  html.stripScripts() + '</tr></tbody></table>';
          depth = 3;
          break;
        case 'TD':
          div.innerHTML = '<table><tbody><tr><td>' +  html.stripScripts() + '</td></tr></tbody></table>';
          depth = 4;
      }
      z$A(element.childNodes).each(function(node){
        element.removeChild(node)
      });
      depth.times(function(){ div = div.firstChild });

      z$A(div.childNodes).each(
        function(node){ element.appendChild(node) });
    } else {
      element.innerHTML = html.stripScripts();
    }
    setTimeout(function() {html.evalScripts()}, 10);
    return element;
  }
};
*/
Object.extend(Element, Element.Methods);

var _nativeExtensions = false;

if(/Konqueror|Safari|KHTML/.test(navigator.userAgent))
  ['', 'Form', 'Input', 'TextArea', 'Select'].each(function(tag) {
    var className = 'HTML' + tag + 'Element';
    if(window[className]) return;
    var klass = window[className] = {};
    klass.prototype = document.createElement(tag ? tag.toLowerCase() : 'div').__proto__;
  });

Element.addMethods = function(methods) {
  Object.extend(Element.Methods, methods || {});

  function copy(methods, destination, onlyIfAbsent) {
    onlyIfAbsent = onlyIfAbsent || false;
    var cache = Element.extend.cache;
    for (var property in methods) {
      var value = methods[property];
      if (!onlyIfAbsent || !(property in destination))
        destination[property] = cache.findOrStore(value);
    }
  }

  if (typeof HTMLElement != 'undefined') {
    copy(Element.Methods, HTMLElement.prototype);
/* Tom M. Yeh, Potix: remove unused codes
    copy(Element.Methods.Simulated, HTMLElement.prototype, true);
    copy(Form.Methods, HTMLFormElement.prototype);
    [HTMLInputElement, HTMLTextAreaElement, HTMLSelectElement].each(function(klass) {
      copy(Form.Element.Methods, klass.prototype);
    });
*/
    _nativeExtensions = true;
  }
}

/* Tom M. Yeh, Potix: remove unused codes
var Toggle = new Object();
Toggle.display = Element.toggle;
*/
/*--------------------------------------------------------------------------*/

/* Tom M. Yeh, Potix: remove unused codes
Abstract.Insertion = function(adjacency) {
  this.adjacency = adjacency;
}

Abstract.Insertion.prototype = {
  initialize: function(element, content) {
    this.element = z$(element);
    this.content = content.stripScripts();

    if (this.adjacency && this.element.insertAdjacentHTML) {
      try {
        this.element.insertAdjacentHTML(this.adjacency, this.content);
      } catch (e) {
        var tagName = this.element.tagName.toUpperCase();
        if (['TBODY', 'TR'].include(tagName)) {
          this.insertContent(this.contentFromAnonymousTable());
        } else {
          throw e;
        }
      }
    } else {
      this.range = this.element.ownerDocument.createRange();
      if (this.initializeRange) this.initializeRange();
      this.insertContent([this.range.createContextualFragment(this.content)]);
    }

    setTimeout(function() {content.evalScripts()}, 10);
  },

  contentFromAnonymousTable: function() {
    var div = document.createElement('div');
    div.innerHTML = '<table><tbody>' + this.content + '</tbody></table>';
    return z$A(div.childNodes[0].childNodes[0].childNodes);
  }
}

var Insertion = new Object();

Insertion.Before = zClass.create();
Insertion.Before.prototype = Object.extend(new Abstract.Insertion('beforeBegin'), {
  initializeRange: function() {
    this.range.setStartBefore(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment, this.element);
    }).bind(this));
  }
});

Insertion.Top = zClass.create();
Insertion.Top.prototype = Object.extend(new Abstract.Insertion('afterBegin'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(true);
  },

  insertContent: function(fragments) {
    fragments.reverse(false).each((function(fragment) {
      this.element.insertBefore(fragment, this.element.firstChild);
    }).bind(this));
  }
});

Insertion.Bottom = zClass.create();
Insertion.Bottom.prototype = Object.extend(new Abstract.Insertion('beforeEnd'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.appendChild(fragment);
    }).bind(this));
  }
});

Insertion.After = zClass.create();
Insertion.After.prototype = Object.extend(new Abstract.Insertion('afterEnd'), {
  initializeRange: function() {
    this.range.setStartAfter(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment,
        this.element.nextSibling);
    }).bind(this));
  }
});
*/
/*--------------------------------------------------------------------------*/

Element.ClassNames = zClass.create();
Element.ClassNames.prototype = {
  initialize: function(element) {
    this.element = z$(element);
  },

  _each: function(iterator) {
    this.element.className.split(/\s+/).select(function(name) {
      return name.length > 0;
    })._each(iterator);
  },

  set: function(className) {
    this.element.className = className;
  },

  add: function(classNameToAdd) {
    if (this.include(classNameToAdd)) return;
    this.set(z$A(this).concat(classNameToAdd).join(' '));
  },

  remove: function(classNameToRemove) {
    if (!this.include(classNameToRemove)) return;
    this.set(z$A(this).without(classNameToRemove).join(' '));
  },

  toString: function() {
    return z$A(this).join(' ');
  }
};

Object.extend(Element.ClassNames.prototype, zEnum);
var zSelector = zClass.create();
zSelector.prototype = {
  initialize: function(expression) {
    this.params = {classNames: []};
    this.expression = expression.toString().strip();
    this.parseExpression();
    this.compileMatcher();
  },

  parseExpression: function() {
    function abort(message) { throw 'Parse error in selector: ' + message; }

    if (this.expression == '')  abort('empty expression');

    var params = this.params, expr = this.expression, match, modifier, clause, rest;
    while (match = expr.match(/^(.*)\[([a-z0-9_:-]+?)(?:([~\|!]?=)(?:"([^"]*)"|([^\]\s]*)))?\]$/i)) {
      params.attributes = params.attributes || [];
      params.attributes.push({name: match[2], operator: match[3], value: match[4] || match[5] || ''});
      expr = match[1];
    }

    if (expr == '*') return this.params.wildcard = true;

    while (match = expr.match(/^([^a-z0-9_-])?([a-z0-9_-]+)(.*)/i)) {
      modifier = match[1], clause = match[2], rest = match[3];
      switch (modifier) {
        case '#':       params.id = clause; break;
        case '.':       params.classNames.push(clause); break;
        case '':
        case undefined: params.tagName = clause.toUpperCase(); break;
        default:        abort(expr.inspect());
      }
      expr = rest;
    }

    if (expr.length > 0) abort(expr.inspect());
  },

  buildMatchExpression: function() {
    var params = this.params, conditions = [], clause;

    if (params.wildcard)
      conditions.push('true');
    if (clause = params.id)
      conditions.push('element.readAttribute("id") == ' + clause.inspect());
    if (clause = params.tagName)
      conditions.push('element.tagName.toUpperCase() == ' + clause.inspect());
    if ((clause = params.classNames).length > 0)
      for (var i = 0, length = clause.length; i < length; i++)
        conditions.push('element.hasClassName(' + clause[i].inspect() + ')');
    if (clause = params.attributes) {
      clause.each(function(attribute) {
        var value = 'element.readAttribute(' + attribute.name.inspect() + ')';
        var splitValueBy = function(delimiter) {
          return value + ' && ' + value + '.split(' + delimiter.inspect() + ')';
        }

        switch (attribute.operator) {
          case '=':       conditions.push(value + ' == ' + attribute.value.inspect()); break;
          case '~=':      conditions.push(splitValueBy(' ') + '.include(' + attribute.value.inspect() + ')'); break;
          case '|=':      conditions.push(
                            splitValueBy('-') + '.first().toUpperCase() == ' + attribute.value.toUpperCase().inspect()
                          ); break;
          case '!=':      conditions.push(value + ' != ' + attribute.value.inspect()); break;
          case '':
          case undefined: conditions.push('element.hasAttribute(' + attribute.name.inspect() + ')'); break;
          default:        throw 'Unknown operator ' + attribute.operator + ' in selector';
        }
      });
    }

    return conditions.join(' && ');
  },

// Tom M. Yeh, Potix: make it compressable with YUI compressor
  compileMatcher: function() {
    this.match = new Function('element', 'if (!element.tagName) return false; element = z$(element); return ' + this.buildMatchExpression());
  },

  findElements: function(scope) {
    var element;

    if (element = z$(this.params.id))
      if (this.match(element))
        if (!scope || Element.childOf(element, scope))
          return [element];

    scope = (scope || document).getElementsByTagName(this.params.tagName || '*');

    var results = [];
    for (var i = 0, length = scope.length; i < length; i++)
      if (this.match(element = scope[i]))
        results.push(Element.extend(element));

    return results;
  },

  toString: function() {
    return this.expression;
  }
}

Object.extend(zSelector, {
  matchElements: function(elements, expression) {
    var selector = new zSelector(expression);
    return elements.select(selector.match.bind(selector)).map(Element.extend);
  },

  findElement: function(elements, expression, index) {
    if (typeof expression == 'number') index = expression, expression = false;
    return zSelector.matchElements(elements, expression || '*')[index || 0];
/* Tom M. Yeh, Potix: remove unused codes
  },

  findChildElements: function(element, expressions) {
    return expressions.map(function(expression) {
      return expression.match(/[^\s"]+(?:"[^"]*"[^\s"]+)*\/g).inject([null], function(results, expr) {
        var selector = new zSelector(expr);
        return results.inject([], function(elements, result) {
          return elements.concat(selector.findElements(result || element));
        });
      });
    }).flatten();
*/
  }
});

/* Tom M. Yeh, Potix: remove unused codes
function $z$() {
  return zSelector.findChildElements(document, z$A(arguments));
}

var Form = {
  reset: function(form) {
    z$(form).reset();
    return form;
  },

  serializeElements: function(elements, getHash) {
    var data = elements.inject({}, function(result, element) {
      if (!element.disabled && element.name) {
        var key = element.name, value = z$(element).getValue();
        if (value != undefined) {
          if (result[key]) {
            if (result[key].constructor != Array) result[key] = [result[key]];
            result[key].push(value);
          }
          else result[key] = value;
        }
      }
      return result;
    });

    return getHash ? data : zHash.toQueryString(data);
  }
};

Form.Methods = {
  serialize: function(form, getHash) {
    return Form.serializeElements(Form.getElements(form), getHash);
  },

  getElements: function(form) {
    return z$A(z$(form).getElementsByTagName('*')).inject([],
      function(elements, child) {
        if (Form.Element.Serializers[child.tagName.toLowerCase()])
          elements.push(Element.extend(child));
        return elements;
      }
    );
  },

  getInputs: function(form, typeName, name) {
    form = z$(form);
    var inputs = form.getElementsByTagName('input');

    if (!typeName && !name) return z$A(inputs).map(Element.extend);

    for (var i = 0, matchingInputs = [], length = inputs.length; i < length; i++) {
      var input = inputs[i];
      if ((typeName && input.type != typeName) || (name && input.name != name))
        continue;
      matchingInputs.push(Element.extend(input));
    }

    return matchingInputs;
  },

  disable: function(form) {
    form = z$(form);
    form.getElements().each(function(element) {
      element.blur();
      element.disabled = 'true';
    });
    return form;
  },

  enable: function(form) {
    form = z$(form);
    form.getElements().each(function(element) {
      element.disabled = '';
    });
    return form;
  },

  findFirstElement: function(form) {
    return z$(form).getElements().find(function(element) {
      return element.type != 'hidden' && !element.disabled &&
        ['input', 'select', 'textarea'].include(element.tagName.toLowerCase());
    });
  },

  focusFirstElement: function(form) {
    form = z$(form);
    form.findFirstElement().activate();
    return form;
  }
}

Object.extend(Form, Form.Methods);
*/
/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
Form.Element = {
  focus: function(element) {
    z$(element).focus();
    return element;
  },

  select: function(element) {
    z$(element).select();
    return element;
  }
}

Form.Element.Methods = {
  serialize: function(element) {
    element = z$(element);
    if (!element.disabled && element.name) {
      var value = element.getValue();
      if (value != undefined) {
        var pair = {};
        pair[element.name] = value;
        return zHash.toQueryString(pair);
      }
    }
    return '';
  },

  getValue: function(element) {
    element = z$(element);
    var method = element.tagName.toLowerCase();
    return Form.Element.Serializers[method](element);
  },

  clear: function(element) {
    z$(element).value = '';
    return element;
  },

  present: function(element) {
    return z$(element).value != '';
  },

  activate: function(element) {
    element = z$(element);
    element.focus();
    if (element.select && ( element.tagName.toLowerCase() != 'input' ||
      !['button', 'reset', 'submit'].include(element.type) ) )
      element.select();
    return element;
  },

  disable: function(element) {
    element = z$(element);
    element.disabled = true;
    return element;
  },

  enable: function(element) {
    element = z$(element);
    element.blur();
    element.disabled = false;
    return element;
  }
}

Object.extend(Form.Element, Form.Element.Methods);
var Field = Form.Element;
var $F = Form.Element.getValue;
*/
/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
Form.Element.Serializers = {
  input: function(element) {
    switch (element.type.toLowerCase()) {
      case 'checkbox':
      case 'radio':
        return Form.Element.Serializers.inputSelector(element);
      default:
        return Form.Element.Serializers.textarea(element);
    }
  },

  inputSelector: function(element) {
    return element.checked ? element.value : null;
  },

  textarea: function(element) {
    return element.value;
  },

  select: function(element) {
    return this[element.type == 'select-one' ?
      'selectOne' : 'selectMany'](element);
  },

  selectOne: function(element) {
    var index = element.selectedIndex;
    return index >= 0 ? this.optionValue(element.options[index]) : null;
  },

  selectMany: function(element) {
    var values, length = element.length;
    if (!length) return null;

    for (var i = 0, values = []; i < length; i++) {
      var opt = element.options[i];
      if (opt.selected) values.push(this.optionValue(opt));
    }
    return values;
  },

  optionValue: function(opt) {
    // extend element because hasAttribute may not be native
    return Element.extend(opt).hasAttribute('value') ? opt.value : opt.text;
  }
}
*/
/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
Abstract.TimedObserver = function() {}
Abstract.TimedObserver.prototype = {
  initialize: function(element, frequency, callback) {
    this.frequency = frequency;
    this.element   = z$(element);
    this.callback  = callback;

    this.lastValue = this.getValue();
    this.registerCallback();
  },

  registerCallback: function() {
    setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  onTimerEvent: function() {
    var value = this.getValue();
    var changed = ('string' == typeof this.lastValue && 'string' == typeof value
      ? this.lastValue != value : String(this.lastValue) != String(value));
    if (changed) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  }
}

Form.Element.Observer = zClass.create();
Form.Element.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.Observer = zClass.create();
Form.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});
*/
/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
Abstract.EventObserver = function() {}
Abstract.EventObserver.prototype = {
  initialize: function(element, callback) {
    this.element  = z$(element);
    this.callback = callback;

    this.lastValue = this.getValue();
    if (this.element.tagName.toLowerCase() == 'form')
      this.registerFormCallbacks();
    else
      this.registerCallback(this.element);
  },

  onElementEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  },

  registerFormCallbacks: function() {
    Form.getElements(this.element).each(this.registerCallback.bind(this));
  },

  registerCallback: function(element) {
    if (element.type) {
      switch (element.type.toLowerCase()) {
        case 'checkbox':
        case 'radio':
          Event.observe(element, 'click', this.onElementEvent.bind(this));
          break;
        default:
          Event.observe(element, 'change', this.onElementEvent.bind(this));
          break;
      }
    }
  }
}

Form.Element.EventObserver = zClass.create();
Form.Element.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.EventObserver = zClass.create();
Form.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});
*/
if (!window.Event) {
  var Event = new Object();
}

Object.extend(Event, {
/* Tom M. Yeh, Potix: remove unused codes
  KEY_BACKSPACE: 8,
  KEY_TAB:       9,
  KEY_RETURN:   13,
  KEY_ESC:      27,
  KEY_LEFT:     37,
  KEY_UP:       38,
  KEY_RIGHT:    39,
  KEY_DOWN:     40,
  KEY_DELETE:   46,
  KEY_HOME:     36,
  KEY_END:      35,
  KEY_PAGEUP:   33,
  KEY_PAGEDOWN: 34,
*/
  element: function(event) {
    return event.target || event.srcElement;
  },

  isLeftClick: function(event) {
    return (((event.which) && (event.which == 1)) ||
            ((event.button) && (event.button == 1)));
  },
  pointer: function (event) {
  	return [Event.pointerX(event), Event.pointerY(event)];
  },
  pointerX: function(event) {
    return event.pageX || (event.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft));
  },

  pointerY: function(event) {
    return event.pageY || (event.clientY +
      (document.documentElement.scrollTop || document.body.scrollTop));
  },
/* Jumper Chen, Potix: add the code of event more controllable.*/
  safariKeymap: {
  	25: 9, 	   // SHIFT-TAB
    63276: 33, // page up
    63277: 34, // page down
    63275: 35, // end
    63273: 36, // home
    63234: 37, // left
    63232: 38, // up
    63235: 39, // right
    63233: 40, // down
    63272: 46 // delete
  },
  specialKeyChar: ' 0 9 13 16 17 18 19 20 27 33 34 35 36 37 38 39 40 44 45 ',
  charCode: function(evt) {
  	return evt.charCode || evt.keyCode;
  },

  keyCode: function(evt) {
    var key = evt.keyCode || evt.charCode;
    return zk.safari ? (this.safariKeymap[key] || key) : key;
  },
  isSpecialKey: function(evt) {
    return (evt.ctrlKey && evt.type == 'keypress') ||
		this.specialKeyChar.indexOf(' ' + (evt.shiftKey ? evt.keyCode : this.keyCode(evt)) + ' ') != -1;
  },
/* Jumper Chen, Potix*/  
  
  stop: function(event) {
    if (event.preventDefault) {
      event.preventDefault();
      event.stopPropagation();
    } else {
      event.returnValue = false;
      event.cancelBubble = true;
	  if (!event.shiftKey && !event.ctrlKey)
	  	event.keyCode = 0; //Jumper Chen, Potix: Bug #1834891
    }
  },
/* Tom M. Yeh, Potix: remove unused codes
  // find the first node with the given tagName, starting from the
  // node the event was triggered on; traverses the DOM upwards
  findElement: function(event, tagName) {
    var element = Event.element(event);
    while (element.parentNode && (!element.tagName ||
        (element.tagName.toUpperCase() != tagName.toUpperCase())))
      element = element.parentNode;
    return element;
  },
*/
  observers: false,

  _observeAndCache: function(element, name, observer, useCapture) {
    if (!this.observers) this.observers = [];
    if (element.addEventListener) {
      this.observers.push([element, name, observer, useCapture]);
      element.addEventListener(name, observer, useCapture);
    } else if (element.attachEvent) {
      this.observers.push([element, name, observer, useCapture]);
      element.attachEvent('on' + name, observer);
    }
  },

  unloadCache: function() {
    if (!Event.observers) return;
    for (var i = 0, length = Event.observers.length; i < length; i++) {
      Event.stopObserving.apply(this, Event.observers[i]);
      Event.observers[i][0] = null;
    }
    Event.observers = false;
  },

  observe: function(element, name, observer, useCapture) {
    element = z$(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.attachEvent))
      name = 'keydown';

    Event._observeAndCache(element, name, observer, useCapture);
  },

  stopObserving: function(element, name, observer, useCapture) {
    element = z$(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.detachEvent))
      name = 'keydown';

    if (element.removeEventListener) {
      element.removeEventListener(name, observer, useCapture);
    } else if (element.detachEvent) {
      try {
        element.detachEvent('on' + name, observer);
      } catch (e) {}
    }
  }
});

/* prevent memory leaks in IE */
if (navigator.appVersion.match(/\bMSIE\b/))
  Event.observe(window, 'unload', Event.unloadCache, false);
var zPos = {
  // set to true if needed, warning: firefox performance problems
  // NOT neeeded for page scrolling, only if draggable contained in
  // scrollable elements
  includeScrollOffsets: false,

  // must be called before calling withinIncludingScrolloffset, every time the
  // page is scrolled
  prepare: function() {
    this.deltaX =  window.pageXOffset
                || document.documentElement.scrollLeft
                || document.body.scrollLeft
                || 0;
    this.deltaY =  window.pageYOffset
                || document.documentElement.scrollTop
                || document.body.scrollTop
                || 0;
  },

  realOffset: function(element) {
    var valueT = 0, valueL = 0, tagName = element.tagName;
    do {
//Tom M. Yeh, Potix: fix opera bug (see the page function)
// If tagName is "IMG" or "TR", the "DIV" element's scrollTop should be ignored.
// Because the offsetTop of element "IMG" or "TR" is excluded its scrollTop.  
if (!window.opera || element.tagName == 'BODY' || (tagName != "TR" && tagName != "IMG"  && element.tagName == 'DIV') ) { 
      valueT += element.scrollTop  || 0;
      valueL += element.scrollLeft || 0;
}
      element = element.parentNode;
    } while (element);
    return [valueL, valueT];
  },

  cumulativeOffset: function(element) {
    var valueT = 0, valueL = 0, operaBug = false, el = element.parentNode;
//Jumper Chen, Poitx: fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute.	
	if (zk.gecko) {
		while (el && el != document.body) {
			var style = Element.getStyle(el, "position");
			if (style == "relative" || style == "absolute") {
				valueT += $int(Element.getStyle(el, "border-top-width"));
				valueL += $int(Element.getStyle(el, "border-left-width"));
			}
	        el = el.offsetParent;
	    }
	}
	
    do {
//Tom M. Yeh, Potix: Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
if (Element.getStyle(element, "position") == 'fixed') {
	valueT += zk.innerY() + element.offsetTop;
	valueL += zk.innerX() + element.offsetLeft;
	break;
} else {
//Jumper Chen, Poitx: fix opera bug. If the parent of "INPUT" or "SPAN" element is "DIV" 
// and the scrollTop of "DIV" element is more than 0, the offsetTop of "INPUT" or "SPAN" element always is wrong.
	if (window.opera) { 
		if (element.nodeName == "SPAN" || element.nodeName == "INPUT") operaBug = true;
		else if (element.nodeName == "DIV" && operaBug) {
			operaBug = false;
			if (element.scrollTop != 0) valueT += element.scrollTop  || 0;
		} else operaBug = false;			
	}
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
//Tom M. Yeh, Potix: Bug 1721158: In FF, element.offsetParent is null in this case
      element = zk.gecko && element != document.body ? zPos.offsetParent(element): element.offsetParent;
}
    } while (element);
    return [valueL, valueT];
  },

  positionedOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
//Tom M. Yeh, Potix: Bug 1721158: In FF, element.offsetParent is null in this case
      element = zk.gecko && element != document.body ? zPos.offsetParent(element): element.offsetParent;
      if (element) {
        if(element.tagName=='BODY') break;
        var p = Element.getStyle(element, 'position');
        if (p == 'relative' || p == 'absolute') break;
      }
    } while (element);
    return [valueL, valueT];
  },

  offsetParent: function(element) {
    if (element.offsetParent) return element.offsetParent;
    if (element == document.body) return element;

    while ((element = element.parentNode) && element != document.body)
//Tom M. Yeh, Potix: in IE, style might not be available
      if (element.style && Element.getStyle(element, 'position') != 'static')
        return element;

    return document.body;
  },

  // caches x/y coordinate pair to use with overlap
  within: function(element, x, y) {
    if (this.includeScrollOffsets)
//Tom Yeh, Potix: make the name shorter
//      return this.withinIncludingScrolloffsets(element, x, y);
      return this.withinScroll(element, x, y);
    this.xcomp = x;
    this.ycomp = y;
    this.offset = this.cumulativeOffset(element);

    return (y >= this.offset[1] &&
            y <  this.offset[1] + zk.offsetHeight(element) && //Tom M. Yeh, Potix: safari bug
            x >= this.offset[0] &&
            x <  this.offset[0] + zk.offsetWidth(element)); //Tom M. Yeh, Potix: safari bug
  },

//Tom Yeh, Potix: make the name shorter
//  withinIncludingScrolloffsets: function(element, x, y) {
  withinScroll: function(element, x, y) {
  	/**
    var offsetcache = this.realOffset(element);

    this.xcomp = x + offsetcache[0] - this.deltaX;
    this.ycomp = y + offsetcache[1] - this.deltaY;
    this.offset = this.cumulativeOffset(element);*/ // Jumper Chen, Potix: bug #1692556
	this.xcomp = x;
	this.ycomp = y;
	this.offset = zk.revisedOffset(element);
    return (this.ycomp >= this.offset[1] &&
            this.ycomp <  this.offset[1] + zk.offsetHeight(element) && //Tom M. Yeh, Potix: safari bug
            this.xcomp >= this.offset[0] &&
            this.xcomp <  this.offset[0] + zk.offsetWidth(element)); //Tom M. Yeh, Potix: safari bug
  },

  // within must be called directly before
  overlap: function(mode, element) {
    if (!mode) return 0;
    if (mode == 'vertical')
      return ((this.offset[1] + zk.offsetHeight(element)) - this.ycomp) / //Tom M. Yeh, Potix: safari bug
        zk.offsetHeight(element); //Tom M. Yeh, Potix: safari bug
    if (mode == 'horizontal')
      return ((this.offset[0] + zk.offsetWidth(element)) - this.xcomp) / //Tom M. Yeh, Potix: safari bug
        zk.offsetWidth(element); //Tom M. Yeh, Potix: safari bug
  },

  page: function(forElement) {
    var valueT = 0, valueL = 0;

    var element = forElement;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;

      // Safari fix
      if (element.offsetParent==document.body)
        if (Element.getStyle(element,'position')=='absolute') break;

    } while (element = element.offsetParent);

    element = forElement;
    do {
      if (!window.opera || element.tagName=='BODY') {
        valueT -= element.scrollTop  || 0;
        valueL -= element.scrollLeft || 0;
      }
    } while (element = element.parentNode);

    return [valueL, valueT];
  },

/* Tom M. Yeh, Potix: remove unused codes
  clone: function(source, target) {
    var options = Object.extend({
      setLeft:    true,
      setTop:     true,
      setWidth:   true,
      setHeight:  true,
      offsetTop:  0,
      offsetLeft: 0
    }, arguments[2] || {})

    // find page position of source
    source = z$(source);
    var p = zPos.page(source);

    // find coordinate system to use
    target = z$(target);
    var delta = [0, 0];
    var parent = null;
    // delta [0,0] will do fine with position: fixed elements,
    // position:absolute needs offsetParent deltas
    if (Element.getStyle(target,'position') == 'absolute') {
      parent = zPos.offsetParent(target);
      delta = zPos.page(parent);
    }

    // correct by body offsets (fixes Safari)
    if (parent == document.body) {
      delta[0] -= document.body.offsetLeft;
      delta[1] -= document.body.offsetTop;
    }

    // set position
    if(options.setLeft)   target.style.left  = (p[0] - delta[0] + options.offsetLeft) + 'px';
    if(options.setTop)    target.style.top   = (p[1] - delta[1] + options.offsetTop) + 'px';
    if(options.setWidth)  target.style.width = zk.offsetWidth(source) + 'px'; //Tom M. Yeh, Potix: safari bug
    if(options.setHeight) target.style.height = zk.offsetHeight(source) + 'px'; //Tom M. Yeh, Potix: safari bug
  },
*/
  absolutize: function(element) {
    element = z$(element);
    if (element.style.position == 'absolute') return;
    zPos.prepare();

    var offsets = zPos.positionedOffset(element);
    var top     = offsets[1];
    var left    = offsets[0];
/* Tom M Yeh, Potix: Bug 1591389
    var width   = element.clientWidth;
    var height  = element.clientHeight;
*/

    element._originalLeft   = left - parseFloat(element.style.left  || 0);
    element._originalTop    = top  - parseFloat(element.style.top || 0);
/* Tom M Yeh, Potix: Bug 1591389
    element._originalWidth  = element.style.width;
    element._originalHeight = element.style.height;
*/
    element.style.position = 'absolute';
    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
/* Tom M Yeh, Potix: Bug 1591389
    element.style.width  = width + 'px';
    element.style.height = height + 'px';
*/
  },

  relativize: function(element) {
    element = z$(element);
    if (element.style.position == 'relative') return;
    zPos.prepare();

    element.style.position = 'relative';
    var top  = parseFloat(element.style.top  || 0) - (element._originalTop || 0);
    var left = parseFloat(element.style.left || 0) - (element._originalLeft || 0);

    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
/* Tom M Yeh, Potix: Bug 1591389
    element.style.height = element._originalHeight;
    element.style.width  = element._originalWidth;
*/
  }
}

// Safari returns margins on body which is incorrect if the child is absolutely
// positioned.  For performance reasons, redefine zPos.cumulativeOffset for
// KHTML/WebKit only.
if (/Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
  zPos.cumulativeOffset = function(element) {
    var valueT = 0, valueL = 0, el = element.parentNode;
//Jumper Chen, Poitx: fix safari difference, the offset of safari excludes its border-width when its CSS position is relative or absolute.
	if (zk.safari) {
		while (el && el != document.body) {
			var style = Element.getStyle(el, "position");
			if (style == "relative" || style == "absolute") {
				valueT += $int(Element.getStyle(el, "border-top-width"));
				valueL += $int(Element.getStyle(el, "border-left-width"));
			} 
	        el = el.parentNode;
	    }
	}
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      if (element.offsetParent == document.body)
        if (Element.getStyle(element, 'position') == 'absolute') break;

      element = element.offsetParent;
    } while (element);

    return [valueL, valueT];
  }
}

Element.addMethods();

//backward compatible (3.0 or earlier)
if (!window.$) $ = z$;
if (!window.$A) $A = z$A;
if (!window.Enumerable) Enumerable = zEnum;
if (!window.Prototype) Prototype = zPrototype;
if (!window.Class) Class = zClass;
if (!window.Template) Template = zTemplate;
if (!window.$break) $break = z$break;
if (!window.$continue) $continue = z$continue;
if (!window.$w) $w = z$w;
if (!window.Hash) Hash = zHash;
if (!window.$H) $H = z$H;
if (!window.ObjectRange) ObjectRange = zObjectRange;
if (!window.$R) $R = z$R;
if (!window.Selector) Selector = zSelector;
if (!window.Position) Position = zPos;

}//Tom M. Yeh, Potix :prevent it from load twice
