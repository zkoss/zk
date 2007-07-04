/* Item.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 6:31:32 PM, Created by henrichen
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Generic Item component under the {@link Frame} component.
 * @author henrichen
 */
abstract public class Item extends MilComponent {
	//JavaME Item's appearance mode
	protected final static int BUTTON = 2;
    protected final static int HYPERLINK = 1;
    protected final static int PLAIN = 0;
    
	//JavaME Item's layout constants
    protected final static int LAYOUT_DEFAULT = 0; //follow the default layout policy of its container
    protected final static int LAYOUT_2 = 0x4000; //indicating that new MIDP 2.0 layout rules are in effect.
    
    //align
    protected final static int LAYOUT_LEFT = 1;
    protected final static int LAYOUT_RIGHT = 2;
    protected final static int LAYOUT_CENTER = 3;
    
    //valign
    protected final static int LAYOUT_TOP = 0x10;
    protected final static int LAYOUT_BOTTOM = 0x20;
    protected final static int LAYOUT_VCENTER = 0x30;

    //flow
    protected final static int LAYOUT_NEWLINE_BEFORE = 0x100; //the first item for a new line or row.
    protected final static int LAYOUT_NEWLINE_AFTER = 0x200; //next Item on a new line or row.

    //widthHint
    protected final static int LAYOUT_SHRINK = 0x400; //width may be reduced to its minimum width
    protected final static int LAYOUT_EXPAND = 0x800; //width may be increased to fill available space

    //heightHint
    protected final static int LAYOUT_VSHRINK = 0x1000; //height may be reduced to its minimum height
    protected final static int LAYOUT_VEXPAND = 0x2000; //height may be increase to fill available space
    
	protected String _label;
	protected String _align; //left, center, right
	protected String _valign; //top, middle, bottom
	protected String _flow; //(change row) first, last, only  
	protected String _heightHint; //shrink, expand
	protected String _widthHint; //shrink, expand
	
	protected int _preferredHeight = -1; //-1 means default
	protected int _preferredWidth = -1; //-1 means default
	
	/** 
	 * Get the label of this Item.
	 * 
	 * @return the label of this Item.
	 */
	public String getLabel() {
		return _label;
	}
	
	/**
	 * Set the new label of this Item.
	 * @param label the new label of this Item.
	 */
	public void setLabel(String label) {
		if (label != null && label.length() == 0) {
			label = null;
		}
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("lb", encodeString(label));
		}
	}

	/**
	 * Get preferred height. -1 means use defualt.
	 * @return the preferred height.
	 */
	public int getPreferredHeight() {
		return _preferredHeight;
	}
	
	/**
	 * Set preferred height. -1 means use default
	 * @param preferredHeight the preferred height.
	 */
	public void setPreferredHeight(int preferredHeight) {
		if (_preferredHeight != preferredHeight) {
			_preferredHeight = preferredHeight;
			smartUpdatePreferredSize();
		}
	}
	
	/**
	 * Get preferred width. -1 means use default.
	 * @return the preferred width;
	 */
	public int getPreferredWidth() {
		return _preferredWidth;
	}
	
	/**
	 * Set preferred width. -1 means use default. 
	 * @param preferredWidth the preferred width.
	 */
	public void setPreferredWidth(int preferredWidth) {
		if (_preferredWidth != preferredWidth) {
			_preferredWidth = preferredWidth;
			smartUpdatePreferredSize();
		}
	}
	
	private String getPreferredSize() {
		return ""+_preferredWidth+","+_preferredHeight;
	}
	
	private void smartUpdatePreferredSize() {
		smartUpdate("ps", getPreferredSize());
	}

	/**
	 * Get the horizontal alignment of this Item (left, center, right).
	 * <ul>
	 * <li>left: align to left.</li>
	 * <li>center: align to center.</li>
	 * <li>right: align to right</li>
	 * </ul> 
	 * null means use default alignment.
	 * @return current horizontal alignment setting
	 */
	public String getAlign() {
		return _align;
	}

	/**
	 * Set the horizontal alignment of this Item (left, center, right);
	 * <ul>
	 * <li>left: align to left.</li>
	 * <li>center: align to center.</li>
	 * <li>right: align to right</li>
	 * </ul> 
	 * null means use default alignment.
	 * @param align
	 */
	public void setAlign(String align) {
		if (align != null && align.length() == 0) {
			align = null;
		}
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdateLayout();
		}
	}

	private int getAlignLayout() {
		if ("left".equalsIgnoreCase(_align)) return LAYOUT_LEFT;
		else if ("center".equalsIgnoreCase(_align)) return LAYOUT_CENTER;
		else if ("right".equalsIgnoreCase(_align)) return LAYOUT_RIGHT;
		else return LAYOUT_LEFT;
	}

	/**
	 * Get the vertical alignment of this Item (top, middle, bottom).
	 * <ul>
	 * <li>top: align to top.</li>
	 * <li>middle: align to middle.</li>
	 * <li>bottom: align to bottom.</li>
	 * </ul>
	 * null means use default vertical alignment.
	 * @return the vertical alignment. 
	 */
	public String getValign() {
		return _valign;
	}

	/**
	 * Set the vertical alignment of this Item (top, middle, bottom).
	 * <ul>
	 * <li>top: align to top.</li>
	 * <li>middle: align to middle.</li>
	 * <li>bottom: align to bottom.</li>
	 * </ul>
	 * null means use default vertical alignment.
	 * @param valign (top, middle, bottom)
	 */
	public void setValign(String valign) {
		if (valign != null && valign.length() == 0) {
			valign = null;
		}
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdateLayout();
		}
	}

	private int getValignLayout() {
		if ("top".equalsIgnoreCase(_valign)) return LAYOUT_TOP;
		else if ("middle".equalsIgnoreCase(_valign)) return LAYOUT_VCENTER;
		else if ("bottom".equalsIgnoreCase(_valign)) return LAYOUT_BOTTOM;
		else return LAYOUT_TOP;
	}

	/**
	 * Set the width hint of this Item (shrink, expand).
	 * <ul>
	 * <li>shrink: means width can be reduced to its minimum.</li>
	 * <li>expand: means width can be expanded to available space.</li>
	 * </ul>
	 * null means use default.
	 * @return the current width hint.
	 */
	public String getWidthHint() {
		return _widthHint;
	}

	/**
	 * Set the width hint of this Item (shrink, expand).
	 * <ul>
	 * <li>shrink: means width can be reduced to its minimum.</li>
	 * <li>expand: means width can be expanded to available space.</li>
	 * </ul>
	 * null means use default.
	 * @param widthHint the current width hint.
	 */
	public void setWidthHint(String widthHint) {
		if (widthHint != null && widthHint.length() == 0) {
			widthHint = null;
		}
		if (!Objects.equals(_widthHint, widthHint)) {
			_widthHint = widthHint;
			smartUpdateLayout();
		}
	}

	private int getWidthHintLayout() {
		if ("shrink".equalsIgnoreCase(_widthHint)) return LAYOUT_SHRINK;
		else if ("expand".equalsIgnoreCase(_widthHint)) return LAYOUT_EXPAND;
		else return 0;
	}
	
	/**
	 * Set the hieght hint of this Item (shrink, expand).
	 * <ul>
	 * <li>shrink: means height can be reduced to its minimum.</li>
	 * <li>expand: means height can be expanded to available space.</li>
	 * </ul>
	 * null means use default.
	 * @return the current height hint.
	 */
	public String getHeightHint() {
		return _heightHint;
	}

	/**
	 * Set the hieght hint of this Item (shrink, expand).
	 * <ul>
	 * <li>shrink: means height can be reduced to its minimum.</li>
	 * <li>expand: means height can be expanded to available space.</li>
	 * </ul>
	 * null means use default.
	 * @param heightHint the current height hint.
	 */
	public void setHeightHint(String heightHint) {
		if (heightHint != null && heightHint.length() == 0) {
			heightHint = null;
		}
		if (!Objects.equals(_heightHint, heightHint)) {
			_heightHint = heightHint;
			smartUpdateLayout();
		}
	}
	
	private int getHeightHintLayout() {
		if ("shrink".equalsIgnoreCase(_heightHint)) return LAYOUT_VSHRINK;
		else if ("expand".equalsIgnoreCase(_heightHint)) return LAYOUT_VEXPAND;
		else return 0;
	}
	
	/**
	 * Get the layout flow of this Item (first, last, only).
	 * <ul>
	 * <li>first: means this Item must be the first in a row.</li>
	 * <li>last: means this Item must be the last in a row.</li>
	 * <li>only: means this Item is the only Item in a row.</li>
	 * </ul>
	 * null means the default flow rule.
	 * @return the layout flow of this Item.
	 */
	public String getFlow() {
		return _flow;
	}

	/**
	 * Set the layout flow of this Item (first, last, only).
	 * <ul>
	 * <li>first: means this Item must be the first in a row.</li>
	 * <li>last: means this Item must be the last in a row.</li>
	 * <li>only: means this Item is the only Item in a row.</li>
	 * </ul>
	 * null means use the default flow rule.
	 * @param flow the layout flow of this Item.
	 */
	public void setFlow(String flow) {
		if (flow != null && flow.length() == 0) {
			flow = null;
		}
		if (!Objects.equals(_flow, flow)) {
			_flow = flow;
			smartUpdateLayout();
		}
	}

	//--Component--//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Frame)) {
			throw new UiException("Unsupported parent for item: "+this+", parent: "+parent);
		}
		super.setParent(parent);
	}


	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "ps", getPreferredSize());
		HTMLs.appendAttribute(sb, "lb", encodeString(getLabel()));
		HTMLs.appendAttribute(sb, "lo", getLayout());
		return sb.toString();
	}
	
	private int getFlowLayout() {
		if ("first".equalsIgnoreCase(_flow)) return LAYOUT_NEWLINE_BEFORE;
		else if ("last".equalsIgnoreCase(_flow)) return LAYOUT_NEWLINE_AFTER;
		else if ("only".equalsIgnoreCase(_flow)) return (LAYOUT_NEWLINE_BEFORE | LAYOUT_NEWLINE_AFTER);
		else return 0;
	}

	private int getLayout() {
		return LAYOUT_2 | getAlignLayout() | getValignLayout() 
			| getWidthHintLayout() | getHeightHintLayout() | getFlowLayout();
	}
	
	private void smartUpdateLayout() {
		smartUpdate("lo", getLayout());
	}
}
