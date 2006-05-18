/* Toolbarbutton.java

{{IS_NOTE
	$Id: Toolbarbutton.java,v 1.14 2006/04/25 09:06:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:45     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.WrongValueException;
import com.potix.zul.html.impl.LabelImageElement;

/**
 * A tool button.
 *
 * <p>The default CSS class is "button".
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.14 $ $Date: 2006/04/25 09:06:33 $
 */
public class Toolbarbutton extends LabelImageElement {
	private String _orient = "horizontal", _dir = "normal";
	private String _href, _target;

	public Toolbarbutton() {
	}
	public Toolbarbutton(String label) {
		setLabel(label);
	}
	public Toolbarbutton(String label, String image) {
		setLabel(label);
		setImage(image);
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _dir;
	}
	/** Sets the direction.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_dir, dir)) {
			_dir = dir;
			invalidate(INNER);
		}
	}
	/** Returns the href.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick handler.
	 */
	public String getHref() {
		return _href;
	}
	/** Sets the href.
	 */
	public void setHref(String href) throws WrongValueException {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_href, href)) {
			_href = href;
			invalidate(OUTER);
		}
	}
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate(INNER);
		}
	}

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 */
	public String getTarget() {
		return _target;
	}
	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_target, target)) {
			_target = target;
			smartUpdate("target", _target);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		if (isAsapRequired("onFocus"))
			HTMLs.appendAttribute(sb, "zk_onFocus", true);
		if (isAsapRequired("onBlur"))
			HTMLs.appendAttribute(sb, "zk_onBlur", true);
		if (_href == null) {
			sb.append(" href=\"javascript:;\"");
		} else {
			sb.append(" href=\"")
				.append(getDesktop().getExecution().encodeURL(_href))
				.append('"');
				//When hyper to other page, we always show progress dlg
		}
		HTMLs.appendAttribute(sb, "target", _target);
		return sb.toString();
	}
}
