/* ResponseHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 12:08:03 PM, Created by henrichen
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
import org.zkoss.zkmob.ui.CTag;
import org.zkoss.zkmob.ui.DTag;
import org.zkoss.zkmob.ui.RTag;
import org.zkoss.zkmob.ui.RsTag;
import org.zkoss.zkmob.ui.SidTag;
import org.zkoss.zkmob.ui.ZkDesktop;


/**
 * MIL update response handler.
 * @author henrichen
 *
 */
public class UpdateHandler extends DefaultHandler {
	private Stack _stack;
	private ZkDesktop _zk;
	
	public UpdateHandler(ZkDesktop zk) {
		_zk = zk;
	}
	
	//super//
	public void startDocument() throws SAXException {
		_stack = new Stack();
    }

    public void endDocument() throws SAXException {
    	_stack = null;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
    throws SAXException {
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
		
		ResponseTag tag = null; 
    	if ("d".equals(eName)){
    		tag = new DTag();
    	} else if ("c".equals(eName)) {
			tag = new CTag();
		} else if ("r".equals(eName)) {
			tag = new RTag();
		} else if ("rs".equals(eName)) {
			tag = new RsTag();
		} else if ("sid".equals(eName)) {
			tag = new SidTag();
		} else {
			throw new IllegalArgumentException("Unsupported response Tag: "+eName);
		}
		
		_stack.push(tag);

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
    	final ResponseTag tag = (ResponseTag) _stack.peek();
    	_stack.pop();
		final ResponseTag parent = (ResponseTag) (_stack.empty() ? null : _stack.peek());

		if (tag instanceof RTag){
    		_zk.executeResponse((RTag) tag);
    	} else if (parent instanceof RTag) {
			final RTag rtag = (RTag) parent;
			rtag.addKid(tag);
		}
    	
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
    	emit("</"+eName+">");
    }

    public void characters(char buf[], int offset, int len) throws SAXException {
    	ResponseTag tag = (ResponseTag) _stack.peek();
    	tag.setValue(new String(buf, offset, len));

    	//do nothing, should never called here
		String s = new String(buf, offset, len);
        emit(s);
    }
    
	private void emit(String s) {
		System.out.print(s);
	}
}
