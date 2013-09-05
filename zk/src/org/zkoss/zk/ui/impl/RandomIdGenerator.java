/* RandomIdGenerator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 5 11:30:02 AM 2013, Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui.impl;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.IdGenerator;

/**
 * The Default Id Generator used in ZK.
 * <p>Use this generator will generate uuid randomly for each component. 
 * It is recommended in production environment.
 * <p>To use this Id Generator, add system-config in zk.xml.
 * <pre>
 * &lt;system-config&gt;
 *     &lt;id-generator-class&gt;org.zkoss.zk.ui.impl.RandomIdGenerator&lt;/id-generator-class&gt;
 * &lt;/system-config&gt;
 * </pre>
 * 
 * @since 7.0.0
 * @author Vincent
 */
public class RandomIdGenerator implements IdGenerator, Serializable {
	private static final long serialVersionUID = 20130905113002L;

	private String _uuid;
	private int _nextUuid = 0;

	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		if (_uuid == null)
			_uuid = updateUuidPrefix(desktop.getId());
		else
			_uuid = updateUuidPrefix(_uuid);

		_uuid = ComponentsCtrl.toAutoId(_uuid, _nextUuid++);
		return _uuid;
	}

	public String nextPageUuid(Page page) {
		final Desktop desktop = page.getDesktop();
		if (_uuid == null)
			_uuid = updateUuidPrefix(desktop.getId());
		else
			_uuid = updateUuidPrefix(_uuid);

		_uuid = ComponentsCtrl.toAutoId(_uuid, _nextUuid++);
		return _uuid;
	}

	public String nextDesktopId(Desktop desktop) {
		return null;
	}

	private String updateUuidPrefix(String prefix) {
		final StringBuffer sb = new StringBuffer();
		int val = prefix.hashCode();

		// Thus, the number will 0, 1... max, 0, 1..., max, 0, 1 (less conflict)
		if (val < 0 && (val += Integer.MIN_VALUE) < 0)
			val = -val; // impossible but just in case

		// 0: lower, 1: digit or upper, 2: letter or digit, 3: upper
		int v = (val % 26) + 36;
		val /= 26;
		sb.append(toLetter(v));
		v = val % 36;
		val /= 36;
		sb.append(toLetter(v));
		v = val % 62;
		val /= 62;
		sb.append(toLetter(v));
		return sb.append(toLetter((val % 26) + 10)).toString();
	}

	private char toLetter(int v) {
		if (v < 10) {
			return (char) ('0' + v);
		} else if (v < 36) {
			return (char) (v + ('A' - 10));
		} else {
			return (char) (v + ('a' - 36));
		}
	}

}
