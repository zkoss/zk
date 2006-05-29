/* AppImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Jan  1 21:37:38     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.prefs.impl;

import java.util.Map;
import java.net.URL;

import com.potix.lang.SystemException;
import com.potix.util.resource.Locators;
import com.potix.util.prefs.App;
import com.potix.comp.Initial;
import com.potix.idom.Element;
import com.potix.idom.util.IDOMs;
import com.potix.idom.input.SAXBuilder;

/**
 * A simple implementation of {@link App}.
 * It loads i3app.xml for application information.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AppImpl implements App, Initial {
	private String _name = "ZK Application", _codenm = "i3",
		_desc = "", _realm = "i3", _vern = "1.0";

	public AppImpl() {
	}

	public String getName() {
		return _name;
	}
	public void setName(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty name");
		_name = name;
	}
	public String getCodeName() {
		return _codenm;
	}
	public void setCodeName(String codeName) {
		if (codeName == null || codeName.length() == 0)
			throw new IllegalArgumentException("empty codeName");
		_codenm = codeName;
	}
	public String getDescription() {
		return _desc;
	}
	public void setDescription(String description) {
		if (description == null)
			throw new IllegalArgumentException("null");
		_desc = description;
	}

	public String getSecurityRealm() {
		return _realm;
	}
	public void setSecurityRealm(String realm) {
		if (realm == null || realm.length() == 0)
			throw new IllegalArgumentException("empty realm");
		_realm = realm;
	}

	public String getVersion() {
		return _vern;
	}
	public void setVersion(String version) {
		if (version == null || version.length() == 0)
			throw new IllegalArgumentException("empty version");
		_vern = version;
	}

	//-- Initial --//
	public void init(Map params) {
		try {
			final URL xmlUrl = Locators.getDefault().getResource("/metainfo/i3-app.xml");
			if (xmlUrl == null) return;

			final Element root =
				new SAXBuilder(false, false,true).build(xmlUrl).getRootElement();
			_name = IDOMs.getRequiredElementValue(root, "name");
			_codenm = IDOMs.getRequiredElementValue(root, "code-name");
			String s = getElementValue(root, "security-realm");
			if (s != null) _realm = s;
			s = getElementValue(root, "description");
			if (s != null) _desc = s;
			s = getElementValue(root, "version");
			if (s != null) _vern = s;
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
	private String getElementValue(Element root, String elnm) {
		final String s = root.getElementValue(elnm, true);
		return s == null || s.length() == 0 ? null: s;
	}

	//-- Object --//
	public final String toString() {
		return getName();
	}
}
