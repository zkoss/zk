/* Displayable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 5:48:16 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Page;

/**
 * Root components that can be directly kids of a Page.
 * @author henrichen
 */
abstract public class Displayable extends MilComponent {
	private static final long serialVersionUID = 200705221758L;
	
	private String _title;
	private String _ticker;
	
	protected Displayable() {
		setVisible(false); //default to false
	}

	/**
	 * Get the title of this Displayable.
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Set the new title of this Displayable.
	 * @param title the new title.
	 */
	public void setTitle(String title) {
		if (title != null && title.length() == 0) {
			title = null;
		}
		if (!Objects.equals(_title, title)) {
			_title = title;
			smartUpdate("tt", title);
		}
	}

	/**
	 * Get the assoicated ticker id. 
	 * @return ticker's id.
	 */
	public String getTicker() {
		return _ticker;
	}

	/**
	 * Set the associated ticker id (Ticker must be defined before calling this method).
	 * @param ticker the ticker id of the associated Ticker.
	 */
	public void setTicker(String ticker) {
		if (ticker != null && ticker.length() == 0) {
			ticker = null;
		}
		
		if (!Objects.equals(_ticker, ticker)) {
			_ticker = ticker;
			smartUpdate("tc", ticker);
		}
	}
	
	/**
	 * Override to guarantee that always only one Displayable is visible in ZK Mobile.
	 */
	public boolean setVisible(boolean b) {
		final boolean old = super.setVisible(b); 
		if (old != b) {
			changeCurrentVisible(b);
		}
		return old;
	}
	
	private void changeCurrentVisible(boolean b) {
		final Page page = getPage();
		if (page != null) {
			Displayable current = (Displayable) page.getAttribute("CURRENT_DISPLAYABLE");
			if (b) {
				if (current != this) {
					page.setAttribute("CURRENT_DISPLAYABLE", this);
					if (current != null) current.setVisible(false);
				}
			} else if (current == this) {
				page.setAttribute("CURRENT_DISPLAYABLE", null);
			}
		}
	}
	
	public void setPage(Page page) {
		final Page old = getPage();
		super.setPage(page);
		if (old != page && page != null && isVisible()) {
			changeCurrentVisible(true);
		}
	}
	
	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64);

		if (_title != null) {
			HTMLs.appendAttribute(sb, "tt",  _title); //title
		}
		if (_ticker != null) {
			HTMLs.appendAttribute(sb, "tc",  _ticker); //ticker
		}
		if (isVisible()) {
			HTMLs.appendAttribute(sb, "cr", "t");
		}
		return sb.toString();
	}
}
