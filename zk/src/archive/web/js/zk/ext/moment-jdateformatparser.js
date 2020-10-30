// this loading routine is shamelessly copied from the "moment-duration-format" plugin
// @see https://github.com/jsmreese/moment-duration-format
(function loadMomentJS (root, factory) {
  if (typeof define === 'function' && define.amd) {
    // Detected AMD;
    // will register as an anonymous module
    define(['moment'], factory);
  } else if (typeof exports === 'object') {
    // Detected node.js;
    // this does not work with strict CommonJS, but only CommonJS-like environments
    // whicht support `module.exports`, like node.js does
    try {
      module.exports = factory(require('moment'));
    } catch (ignore) {
      // if `moment.js` is not available, leave the setup to the user;
      // this is necessary if the user works with other plugins which will
      // come with it's very own version of `moment.js` as a peer dependency
      // @see https://github.com/icambron/twix.js/issues/102
      module.exports = factory;
    }
  } else {
    factory(zk.mm); /* Leon, Potix, 20201022 */
  }

  if (root) {
    //working with globals;
    root.momentJDateFormatParserSetup = root.moment ? factory(root.moment) : factory;
  }
})(this, function loadPlugin (moment) {
  /**
   * The internal **Java** date formats cache.
   *
   * @property javaDateFormats
   * @type {Object}
   */
  var javaDateFormats = {};

  /**
   * The internal **moment.js** date formats cache.
   *
   * @property momentDateFormats
   * @type {Object}
   */
  var momentDateFormats = {};

  /**
   * The format pattern mapping from Java format to momentjs.
   *
   * @property javaFormatMapping
   * @type {Object}
   */
  var javaFormatMapping = {
    d: 'D',
    dd: 'DD',
    y: 'YYYY',
    yy: 'YY',
    yyy: 'YYYY',
    yyyy: 'YYYY',
    a: 'a',
    A: 'A',
    M: 'M',
    MM: 'MM',
    MMM: 'MMM',
    MMMM: 'MMMM',
    h: 'h',
    hh: 'hh',
    H: 'H',
    HH: 'HH',
    m: 'm',
    mm: 'mm',
    s: 's',
    ss: 'ss',
    S: 'SSS',
    SS: 'SSS',
    SSS: 'SSS',
    E: 'ddd',
    EE: 'ddd',
    EEE: 'ddd',
    EEEE: 'dddd',
    EEEEE: 'dddd',
    EEEEEE: 'dddd',
    D: 'DDD',
    w: 'W',
    ww: 'WW',
    z: 'ZZ',
    zzzz: 'Z',
    Z: 'ZZ',
    X: 'ZZ',
    XX: 'ZZ',
    XXX: 'Z',
    u: 'E'
  };

  /**
   * The format pattern mapping from Java format to moment.js.
   *
   * @property momentFormatMapping
   * @type {Object}
   */
  var momentFormatMapping = {
    D: 'd',
    DD: 'dd',
    YY: 'yy',
    YYY: 'yyyy',
    YYYY: 'yyyy',
    a: 'a',
    A: 'a',
    M: 'M',
    MM: 'MM',
    MMM: 'MMM',
    MMMM: 'MMMM',
    h: 'h',
    hh: 'hh',
    H: 'H',
    HH: 'HH',
    m: 'm',
    mm: 'mm',
    s: 's',
    ss: 'ss',
    S: 'S',
    SS: 'S',
    SSS: 'S',
    ddd: 'E',
    dddd: 'EEEE',
    DDD: 'D',
    W: 'w',
    WW: 'ww',
    ZZ: 'z',
    Z: 'XXX',
    E: 'u'
  };


  /**
   * Translates the java date format String to a momentjs format String.
   *
   * @function translateFormat
   * @param {String}  formatString    The unmodified format string
   * @param {Object}  mapping         The date format mapping object
   * @returns {String}
   */
  var translateFormat = function (formatString, mapping) {
    var len = formatString.length;
    var i = 0;
    var startIndex = -1;
    var lastChar = null;
    var currentChar = "";
    var resultString = "";

    for (; i < len; i++) {
      currentChar = formatString.charAt(i);

      if (lastChar === null || lastChar !== currentChar) {
        // change detected
        resultString = _appendMappedString(formatString, mapping, startIndex, i, resultString);

        startIndex = i;
      }

      lastChar = currentChar;
    }

    return _appendMappedString(formatString, mapping, startIndex, i, resultString);
  };

  /**
   * Checks if the substring is a mapped date format pattern and adds it to the result format String.
   *
   * @function _appendMappedString
   * @param {String}  formatString    The unmodified format String.
   * @param {Object}  mapping         The date format mapping Object.
   * @param {Number}  startIndex      The begin index of the continuous format characters.
   * @param {Number}  currentIndex    The last index of the continuous format characters.
   * @param {String}  resultString    The result format String.
   * @returns {String}
   * @private
   */
  var _appendMappedString = function (formatString, mapping, startIndex, currentIndex, resultString) {
    if (startIndex !== -1) {
      var tempString = formatString.substring(startIndex, currentIndex);

      // check if the temporary string has a known mapping
      if (mapping[tempString]) {
        tempString = mapping[tempString];
      }

      resultString += tempString;
    }

    return resultString;
  };

// init
  function init (momentJS) {
    if (!momentJS) {
      throw new Error("Moment JDateFormatParser Plugin - Cannot find moment.js instance.");
    }

    // register as private function (good for testing purposes)
    momentJS.fn.__translateJavaFormat = translateFormat;

    /**
     * Translates the momentjs format String to a java date format String.
     *
     * @function toJDFString
     * @param {String}  formatString    The format String to be translated.
     * @returns {String}
     */
    momentJS.fn.toMomentFormatString = function (formatString) {
      if (!javaDateFormats[formatString]) {
        var mapped = "";
        var regexp = /[^']+|('[^']*')/g;
        var part = '';

        while ((part = regexp.exec(formatString))) {
          part = part[0];

          if (part.match(/'(.*?)'/)) {
            mapped += "[" + part.substring(1, part.length - 1) + "]";
          } else {
            mapped += translateFormat(part, javaFormatMapping);
          }
        }

        javaDateFormats[formatString] = mapped;
      }

      return javaDateFormats[formatString];
    };

    /**
     * Format the moment with the given java date format String.
     *
     * @function formatWithJDF
     * @param {String}  formatString    The format String to be translated.
     * @returns {String}
     */
    momentJS.fn.formatWithJDF = function (formatString) {
      return this.format(this.toMomentFormatString(formatString));
    };

    /**
     * Translates the momentjs format string to a java date format string
     *
     * @function toJDFString
     * @param {String}  formatString    The format String to be translated.
     * @returns {String}
     */
    momentJS.fn.toJDFString = function (formatString) {
      if (!momentDateFormats[formatString]) {
        momentDateFormats[formatString] = translateFormat(formatString, momentFormatMapping);
      }

      return momentDateFormats[formatString];
    };
  }

  // Initialize JDateFormatParser Plugin on the global moment instance.
  init(moment);

  // Return the init function so that the JDateFormatParser Plugin can be
  // initialized on other moment instances.
  return init;
});