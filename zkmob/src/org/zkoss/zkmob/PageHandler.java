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
import java.util.Vector;

import javax.microedition.lcdui.Displayable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.zkoss.zkmob.ui.ZkDesktop;

/** The Page handler that deserialize Java Object from RMIL(Raw Mobile Interactive Language)
 *  XML Pages.
 * 
 * @author henrichen
 *
 */
public class PageHandler extends DefaultHandler {
	private Stack _stack = new Stack();
	private String _hostURL;
	private String _pathURL;
	private Vector _roots = new Vector(8);
	private ZkComponent _parent;
	private ZkDesktop _zk;
	
	/*package*/PageHandler(String hostURL, String pathURL) {
		_hostURL = hostURL;
		_pathURL = pathURL;
	}

	/*package*/PageHandler(ZkComponent parent, String hostURL, String pathURL) {
		_hostURL = hostURL;
		_pathURL = pathURL;
		_parent = parent;
		_zk = _parent == null ? null : _parent.getZkDesktop();
		_stack.push(parent);
	}
	
	public Vector getRoots() {
		return _roots;
	}
	
	public ZkDesktop getZk() {
		return _zk;
	}
	
	//super//
	public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
    throws SAXException {
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
		
		ZkComponent parent = (ZkComponent) (_stack.empty() ? null : _stack.peek());
		ZkComponent comp = UiManager.create(parent, eName, attrs, _hostURL, _pathURL);
		_stack.push(comp);

		if (parent instanceof ZkDesktop) {
			_roots.addElement(comp);
		}
		
		if (_zk == null) {
			if (comp instanceof ZkDesktop) {
				_zk = (ZkDesktop) comp;
			} else {
				throw new IllegalArgumentException("RMIL page must be in <zk> root:" + eName);
			}
		}
		
		if (comp instanceof Displayable) {
			final String cr = attrs.getValue("cr");
			if ("t".equalsIgnoreCase(cr)) {
				_zk.setCurrent((Displayable)comp);
			}
		}
		_zk.registerUi(comp.getId(), comp);
		
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
    	_stack.pop();
    	
    	String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
    	
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
}
