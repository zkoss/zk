/* Path.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 19 14:07:44     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
		_path = normalize(path);
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
			if (ComponentsCtrl.isAutoId(compId))
				throw new UiException(MZk.AUTO_ID_NOT_ALLOWED_IN_PATH, comp);
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
		return getComponent0(null, normalize(path));
	}
	/** Returns the component of the specified path which is related
	 * to the specified ID space, or null if no such component.
	 *
	 * @param is the current ID space. It is required only if path is related
	 * (in other words, not starting with / or //).
	 */
	public static final Component getComponent(IdSpace is, String path) {
		return getComponent0(is, normalize(path));
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
					final Collection pages = desktop.getPages();
					if (pages.isEmpty())
						return null;

					page = (Page)pages.iterator().next();
						//the first page assumed
				}

				if (path.length() == 1) // "/" only
					return getFirstRoot(page); //the first root assumed

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

			final Component c = is.getFellow(nm);
			if (k < 0) return c;

			if (!(c instanceof IdSpace))
				return null;
			is = (IdSpace)c;
		}
	}
	private static Component getFirstRoot(Page page) {
		final Collection roots = page.getRoots();
		return roots.isEmpty() ? null: (Component)roots.iterator().next();
	}

	/**
	 * Normalizes the giving path.
	 * It removes consecutive slahses, ending slahes,
	 * redudant . and ..
	 */
	private static final String normalize(String path) {
		if (path == null)
			return "";

		//remove consecutive slashes
		final StringBuffer sb = new StringBuffer(path);
		boolean slash = false;
		for (int j = 0, len = sb.length(); j < len; ++j) {
			final boolean curslash = sb.charAt(j) == '/';
			if (curslash && slash && j != 1) {
				sb.deleteCharAt(j);
				--j; --len;
			}
			slash = curslash;
		}

		if (sb.length() > 1 && slash) //remove ending slash except "/"
			sb.setLength(sb.length() - 1);

		//remove ./
		while (sb.length() >= 2 && sb.charAt(0) == '.' && sb.charAt(1) == '/')
			sb.delete(0, 2); // "./" -> ""

		//remove /./
		for (int j = 0; (j = sb.indexOf( "/./", j)) >= 0;)
			sb.delete(j + 1, j + 3); // "/./" -> "/"

		//ends with "/."
		int len = sb.length();
		if (len >= 2 && sb.charAt(len - 1) == '.' && sb.charAt(len - 2) == '/')
			if (len == 2) return "/";
			else sb.delete(len - 2, len);

		//remove /../
		for (int j = 0; (j = sb.indexOf("/../", j)) >= 0;)
			j = removeDotDot(sb, j);

		// ends with "/.."
		len = sb.length();
		if (len >= 3 && sb.charAt(len - 1) == '.' && sb.charAt(len - 2) == '.'
		&& sb.charAt(len - 3) == '/') 
			if (len == 3) return "/";
			else removeDotDot(sb, len - 3);

		return sb.length() == path.length() ? path: sb.toString();
	}
	/** Removes "/..".
	 * @param j points '/' in "/.."
	 * @return the next index to search from
	 */
	static private int removeDotDot(StringBuffer sb, int j) {
		int k = j;
		while (--k >= 0 && sb.charAt(k) != '/') 
			;

		if (k + 3 == j && sb.charAt(k + 1) == '.' && sb.charAt(k + 2) == '.')
			return j + 4; // don't touch: "../.."

		sb.delete(j, j + 3); // "/.." -> ""

		if (j == 0) // "/.."
			return 0;

		if (k < 0) { // "a/+" => kill "a/", "a" => kill a
			sb.delete(0, j < sb.length() ? j + 1: j);
			return 0;
		}

		// "/a/+" => kill "/a", "/a" => kill "a"
		if (j >= sb.length()) ++k;
		sb.delete(k, j);
		return k;
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
