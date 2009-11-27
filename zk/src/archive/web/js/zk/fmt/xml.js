/* xml.js

	Purpose:
		
	Description:
		
	History:
		Fri Nov 27 12:29:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
zk.fmt.XML = {
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
	}
};