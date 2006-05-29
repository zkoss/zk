/* Listfooter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 13 12:42:38     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Iterator;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zul.html.impl.LabelImageElement;

/**
 * A column of the footer of a listbox.
 * Its parent must be {@link Listfoot}.
 *
 * <p>Unlike {@link Listheader}, you could place any child in a list footer.
 * <p>Note: {@link Listcell} also accepts children.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/29 04:28:23 $
 */
public class Listfooter extends LabelImageElement {
	public Listfooter() {
	}
	public Listfooter(String label) {
		setLabel(label);
	}
	public Listfooter(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the listbox that this belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox)comp.getParent(): null;
	}
	/** Returns the listhead that this belongs to.
	 */
	public Listfoot getListfoot() {
		return (Listfoot)getParent();
	}
	/** Returns the column index, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}
	/** Returns the list header that is in the same column as
	 * this footer, or null if not available.
	 */
	public Listheader getListheader() {
		final Listbox listbox = getListbox();
		if (listbox != null) {
			final Listhead lcs = listbox.getListhead();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Listheader)lcschs.get(j);
			}
		}
		return null;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final Listheader header = getListheader();
		return header != null ? attrs+header.getColAttrs(): attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listfoot))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	private void initAtClient() {
		final Listbox box = getListbox();
		if (box != null) box.initAtClient();
	}
}
