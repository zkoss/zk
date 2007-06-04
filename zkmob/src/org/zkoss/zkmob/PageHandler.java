/* PageCreator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 5 21:15:13     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** The Page creator that deserialize Java Object from RMIL(Raw Mobile Interactive Language)
 *  XML Pages.
 * 
 * @author henrichen
 *
 */
public class PageHandler extends DefaultHandler {
	private Stack _stack;
	private UiManager _uiManager;
	private Context _ctx;
	
	/*package*/PageHandler(UiManager uiManager, Context ctx) {
		_uiManager = uiManager;
		_ctx = ctx;
	}
	
	//super//
	public void startDocument() throws SAXException {
		_stack = new Stack();
        emit("<?xml version='1.0' encoding='UTF-8'?>");
        nl();
    }

    public void endDocument() throws SAXException {
    	nl();
    	_stack = null;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
    throws SAXException {
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
		
		Object parent = _stack.empty() ? null : _stack.peek();
		Object comp = _uiManager.create(parent, eName, attrs, _ctx);
		_stack.push(comp);

		emit("<"+eName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName)) aName = attrs.getQName(i);
				emit(" ");
				emit(aName+"=\""+attrs.getValue(i)+"\"");
			}
		}
		emit(">");
	}

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false

		Object comp = _stack.peek();
    	_stack.pop();
		Object parent = _stack.empty() ? null : _stack.peek();
		_uiManager.afterCreate(parent, eName, comp, _ctx);
    	
    	emit("</"+eName+">");
    }

    public void characters(char buf[], int offset, int len) throws SAXException {
    	//do nothing, should never called here
		String s = new String(buf, offset, len);
        emit(s);
    }
    
	private void emit(String s) {
		System.out.print(s);
	}
	
	private void nl() {
		System.out.println("");
	}
}
