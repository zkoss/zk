/* Path.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 19 14:07:44     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;

import org.zkoss.io.Files;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * A representation of a component path.
 *
 * <p>There are three formats:
 * //page-id/comp-id/comp-id<br/>
 * /comp-id/comp-id<br/>
 * comp-id/comp-id
 *
 * @author tomyeh
 */
public class Path {
	private final String _path;

	public Path() {
		this((String)null);
	}
	public Path(String path) {
		_path = Files.normalize(path);
	}
	public Path(String parent, String child) {
		this(parent == null || parent.length() == 0 ? child:
			child == null || child.length() == 0 ? parent:
				parent + '/' + child);
	}
	public Path(Path parent, String child) {
		this(parent != null ? parent.getPath(): null, child);
	}
	/** Returns the path of the specified component. */
	public Path(Component comp) {
		this(getPath(comp));
	}

	/** Returns the path (after normalized).
	 */
	public String getPath() {
		return _path;
	}

	/** Returns the component with this path, or null if no such component.
	 */
	public Component getComponent() {
		return getComponent0(null, _path);
	}

	/** Returns the path of the specified component.
	 */
	public static final String getPath(Component comp) {
		final StringBuffer sb = new StringBuffer(64);
		for (;;) {
			if (sb.length() > 0) sb.insert(0, '/');
			final String compId = comp.getId();
			if (compId.length() == 0)
				throw new UiException("ID required: " + comp);
			sb.insert(0, compId);
			IdSpace is = comp.getSpaceOwner();
			if (is instanceof Page) break; //done

			if (is == comp) {
				final Component p = ((Component)is).getParent();
				if (p == null) break; //topmost
				is = p.getSpaceOwner();
				if (is instanceof Page) break; //done
			}
			comp = (Component)is;
		}
		sb.insert(0, '/');
		return sb.toString();
	}
	/** Returns the component of the specified path, or null if no such component.
	 */
	public static final Component getComponent(String path) {
		return getComponent0(null, Files.normalize(path));
	}
	/** Returns the component of the specified path which is related
	 * to the specified ID space, or null if no such component.
	 *
	 * @param is the current ID space. It is required only if path is related
	 * (in other words, not starting with / or //).
	 */
	public static final Component getComponent(IdSpace is, String path) {
		return getComponent0(is, Files.normalize(path));
	}
	private static final Component getComponent0(IdSpace is, String path) {
		Component found = null;
		for (int j = 0, k;; j = k + 1) {
			k = path.indexOf('/', j);

			if (k == 0) { //starts with /
				final Execution exec = Executions.getCurrent();
				final Desktop desktop = exec.getDesktop();
				Page page = ((ExecutionCtrl)exec).getCurrentPage();
				if (page == null) {
					page = desktop.getFirstPage();
					if (page == null)
						return null;
				}

				if (path.length() == 1) // "/" only
					return page.getFirstRoot(); //the first root assumed

				if (path.charAt(1) == '/') { //starts with //
					k = path.indexOf('/', 2);
					if (k < 0)
						return null; //page is not component

					final String nm = path.substring(2, k);
					is = desktop.getPageIfAny(nm);
					if (is == null)
						return null; //no such page
				} else {
					is = page;
				}
				continue;
			}

			final String nm = k >= 0 ? path.substring(j, k): path.substring(j);
			if ("..".equals(nm)) {
				if (!(is instanceof Component))
					return null;

				final Component c = (Component)is;
				final Component p = c.getParent();
				is = p != null ? p.getSpaceOwner(): (IdSpace)c.getPage();
				if (k < 0) {
					return (is instanceof Component) ? (Component)is: null;
				}
				continue;
			}
			if (is == null)
				return null;

			final Component c = is.getFellowIfAny(nm);
			if (k < 0 || c == null) return c;

			if (!(c instanceof IdSpace))
				return null;
			is = (IdSpace)c;
		}
	}

	//--Object--//
	public boolean equals(Object o) {
		return o instanceof Path && ((Path)o)._path.equals(_path);
	}
	public int hashCode() {
		return _path.hashCode();
	}
	public String toString() {
		return _path;
	}
}
