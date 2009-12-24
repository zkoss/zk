/* xml.js

	Purpose:
		
	Description:
		
	History:
		Fri Nov 27 12:29:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
/** XML utilities.
 */
//zk.$package('zk.xml');

/** @class zk.xml.Utl
 * Utilities for parsing XML and others.
 * <p>Refer to {@link zUtl} for basic utilities.
 */
zk.xml.Utl = {
	loadXML: function (url, callback) {
		var doc = document.implementation;
		if (doc && doc.createDocument) {
			doc = doc.createDocument('', '', null); //FF, Safari, Opera
			if (callback)
				doc.onload = function () {callback(doc);};
		} else {
			doc = new ActiveXObject("Microsoft.XMLDOM");
			if (callback)
				doc.onreadystatechange = function() {
					if (doc.readyState == 4) callback(doc);
				};
		}
		if (!callback) doc.async = false;
		doc.load(url);
		return doc;
	},
	parseXML: function (text) {
		if (typeof DOMParser != "undefined")
			return (new DOMParser()).parseFromString(text, "text/xml");
			//FF, Safar, Opera
	
		doc = new ActiveXObject("Microsoft.XMLDOM"); //IE
		doc.async = false;
		doc.loadXML(text);
		return doc;
	},
	/** Renames the type embedded in an URL
	 * For example,
<pre><code>
zk.xml.Utl.renType("/zkdemo/img/whatever-off.gif", "on");
	//return "/zkdemo/img/whatever-on.gif"
</code></pre>
	 * as shown above, it assumes the type is embedded after dash (-).
	 * @param String url the URL to modify
	 * @param String type the type to replace with
	 * @return String the new URL after replacing the type
	 */
	renType: function (url, type) {
		var j = url.lastIndexOf(';');
		var suffix;
		if (j >= 0) {
			suffix = url.substring(j);
			url = url.substring(0, j);
		} else
			suffix = "";

		j = url.lastIndexOf('.');
		if (j < 0) j = url.length; //no extension at all
		var	k = url.lastIndexOf('-'),
			m = url.lastIndexOf('/'),
			ext = j <= m ? "": url.substring(j),
			pref = k <= m ? j <= m ? url: url.substring(0, j): url.substring(0, k);
		if (type) type = "-" + type;
		else type = "";
		return pref + type + ext + suffix;
	},

	/** Returns the concatenation of the text nodes under the specified
	 * DOM element.
	 * @param DOMElement el the DOM element.
	 * @return String the text
	 */
	getElementValue: function (el) {
		var txt = "";
		for (el = el.firstChild; el; el = el.nextSibling)
			if (el.data) txt += el.data;
		return txt;
	}
};