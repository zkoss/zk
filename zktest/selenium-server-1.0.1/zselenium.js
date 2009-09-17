/* zselenium.js

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 21:05:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var zSelenium = {
	findAttribute: BrowserBot.prototype.findAttribute,
	findElement: BrowserBot.prototype.findElement
};
BrowserBot.prototype.findAttribute = function(locator) {
	var result = zSelenium.findAttribute.apply(this, arguments);
	if (result != null)
		return result;
	var attributePos = locator.lastIndexOf("@");
    var elementLocator = locator.slice(0, attributePos);
    var attributeName = locator.slice(attributePos + 1);

    // Find the element.
    var element = this.findElement(elementLocator);
    
    try {
    	result = element[attributeName];
	} catch (e) {}
    return result ? result.toString() : result;
};

BrowserBot.prototype.findElement = function(locator, win) {
	if (locator.charAt(0) == '$' || locator.charAt(0) == '#') {
		win = win || this.getCurrentWindow();
		if (win.zk) {
			var element = win.zk(locator).jq[0];
			if (element != null) {
	        	return this.browserbot.highlight(element);
	    	}
	    }
	    LOG.debug('ZK is not found!');
	}
    var element = this.findElementOrNull(locator, win);
    if (element == null) throw new SeleniumError("Element " + locator + " not found");
    return element;
}

Selenium.prototype.getEval = function(script) {
    try {
        var window = this.browserbot.getCurrentWindow(),
        	result = (window.jq ? window.jq.evalJSON : eval)(script);
        	
        // Selenium RC doesn't allow returning null
        if (null == result) return "null";
        return result;
    } catch (e) {
        throw new SeleniumError("Threw an exception: " + extractExceptionMessage(e));
    }
};

MozillaBrowserBot.prototype.findAttribute = IEBrowserBot.prototype.findAttribute = 
KonquerorBrowserBot.prototype.findAttribute = OperaBrowserBot.prototype.findAttribute
= SafariBrowserBot.prototype.findAttribute = BrowserBot.prototype.findAttribute;

MozillaBrowserBot.prototype.findElement = IEBrowserBot.prototype.findElement = 
KonquerorBrowserBot.prototype.findElement = OperaBrowserBot.prototype.findElement
= SafariBrowserBot.prototype.findElement = BrowserBot.prototype.findElement;
