/*global ActiveXObject*/
/* utl.ts

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
export let Utl = {
	/**
	 * Loads XML from the resource at the specified URL.
	 * If the callback method is specified, the URL is loaded asynchronously and
	 * the callback is called with the XML document after the document is loaded and parsed.
	 * @param String url
	 * @param Function callback
	 * @return DOMElement
	 */
	loadXML(url: string, callback: CallableFunction): DOMImplementation | XMLDocument {
		const doc: XMLDocument & Partial<{load(url: string); async: boolean}> = document.implementation.createDocument('', '', undefined);
		if (callback)
			doc.onload = function () {callback(doc);};
		if (!callback) doc.async = false;
		doc.load!(url);
		return doc;
	},
	/**
	 * Parses and returns the XML document from the specified text.
	 * @param String text
	 * @return DOMElement
	 */
	parseXML(text: string): XMLDocument {
		if (typeof DOMParser != 'undefined')
			return (new DOMParser()).parseFromString(text, 'text/xml');
			//FF, Safar, Opera
	
		interface Doc extends XMLDocument {
			async: boolean;
			loadXML(text: string): void;
		}
		var doc = new ActiveXObject('Microsoft.XMLDOM') as Doc; //IE
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
	renType(url: string, type: string): string {
		var j = url.lastIndexOf(';'),
			suffix;
		if (j >= 0) {
			suffix = url.substring(j);
			url = url.substring(0, j);
		} else
			suffix = '';

		j = url.lastIndexOf('.');
		if (j < 0) j = url.length; //no extension at all
		var	k = url.lastIndexOf('-'),
			m = url.lastIndexOf('/'),
			ext = j <= m ? '' : url.substring(j),
			pref = k <= m ? j <= m ? url : url.substring(0, j) : url.substring(0, k);
		if (type) type = '-' + type;
		else type = '';
		return pref + type + ext + suffix;
	},

	/** Returns the concatenation of the text nodes under the specified
	 * DOM element.
	 * @param DOMElement el the DOM element.
	 * @return String the text
	 */
	getElementValue(el: HTMLElement): string {
		var txt = '';
		// eslint-disable-next-line zk/noNull
		for (let target: Element | null = el.firstElementChild; target; target = target.nextElementSibling)
			if (target['data']) txt += target['data'];
		return txt;
	}
};
zk.xml.Utl = Utl;