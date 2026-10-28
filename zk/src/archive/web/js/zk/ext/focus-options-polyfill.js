/* focus-options-polyfill.js

		Purpose:
		
		Description:
				this is a MIT License polyfill for focusOptions - preventScroll from https://github.com/calvellido/focus-options-polyfill
		History:
				Tue May 25 12:10:41 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.

focus-options-polyfill 1.6.0, Copyright (c) 2018 Juan Valencia

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
// focus - focusOptions - preventScroll polyfill
(function() {
  if (
    typeof window === "undefined" ||
    typeof document === "undefined" ||
    typeof HTMLElement === "undefined"
  ) {
    return;
  }

  var supportsPreventScrollOption = false;
  try {
    var focusElem = document.createElement("div");
    focusElem.addEventListener(
      "focus",
      function(event) {
        event.preventDefault();
        event.stopPropagation();
      },
      true
    );
    focusElem.focus(
      Object.defineProperty({}, "preventScroll", {
        get: function() {
          // Edge v18 gives a false positive for supporting inputs
          if (
            navigator &&
            typeof navigator.userAgent !== 'undefined' &&
            navigator.userAgent &&
            navigator.userAgent.match(/Edge\/1[7-8]/)) {
              return supportsPreventScrollOption = false
          }

          supportsPreventScrollOption = true;
        }
      })
    );
  } catch (e) {}

  if (
    HTMLElement.prototype.nativeFocus === undefined &&
    !supportsPreventScrollOption
  ) {
    HTMLElement.prototype.nativeFocus = HTMLElement.prototype.focus;

    var calcScrollableElements = function(element) {
      var parent = element.parentNode;
      var scrollableElements = [];
      var rootScrollingElement =
        document.scrollingElement || document.documentElement;

      while (parent && parent !== rootScrollingElement) {
        if (
          parent.offsetHeight < parent.scrollHeight ||
          parent.offsetWidth < parent.scrollWidth
        ) {
          scrollableElements.push([
            parent,
            parent.scrollTop,
            parent.scrollLeft
          ]);
        }
        parent = parent.parentNode;
      }
      parent = rootScrollingElement;
      scrollableElements.push([parent, parent.scrollTop, parent.scrollLeft]);

      return scrollableElements;
    };

    var restoreScrollPosition = function(scrollableElements) {
      for (var i = 0; i < scrollableElements.length; i++) {
        scrollableElements[i][0].scrollTop = scrollableElements[i][1];
        scrollableElements[i][0].scrollLeft = scrollableElements[i][2];
      }
      scrollableElements = [];
    };

    var patchedFocus = function(args) {
      if (args && args.preventScroll) {
        var evScrollableElements = calcScrollableElements(this);
        if (typeof setTimeout === 'function') {
          var thisElem = this;
          setTimeout(function () {
            thisElem.nativeFocus();
            restoreScrollPosition(evScrollableElements);
          }, 0);
        } else {
          this.nativeFocus();
          restoreScrollPosition(evScrollableElements);
        }
      }
      else {
        this.nativeFocus();
      }
    };

    HTMLElement.prototype.focus = patchedFocus;
  }
})();