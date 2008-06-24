/* Group.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 25, 2008 4:15:11 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zul.impl.XulElement;

/**
 * Adds the ability for single level grouping to the Grid.
 * 
 * <p>Event:
 * <ol>
 * 	<li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 * 
 * <p>Default {@link #getSclass}: group.
 * 
 * <p>Note: All the child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * @author jumperchen
 * @since 3.5.0
 */
public class Group extends Row {
	private String _src = null;
	private boolean _open = true;	
	private transient List _items;
	
	public Group() {
		setSclass("group");
		init();
	}
	public Group(String label) {
		this();
		setLabel(label);
	}
	public Group(String label, Object value) {
		this();
		setLabel(label);
		setValue(value);
	}

	private void init() {
		_items =  new AbstractList() {
			public int size() {
				return getItemCount();
			}
			public Iterator iterator() {
				return new IterItems();
			}
			public Object get(int index) {
				final Rows rows = (Rows)getParent();
				if (rows != null) {
					int i = 0;
					for (Iterator it = rows.getChildren().listIterator(getIndex() + 1); 
						it.hasNext() && i <= index; i++) {
						if (i == index) 
							return it.next();
						it.next();	
					}
				}
				throw new IndexOutOfBoundsException("Index: "+index);
			}
		};
	}
	/** 
	 * Returns a list of all {@link Row} are grouped by this group.
	 */
	public List getItems() {
		return _items;
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		final Rows rows = (Rows)getParent();
		if (rows != null) {
			int[] g = rows.getGroupsInfoAtIndex(getIndex(), true);
			if (g != null) {
				if (g[2] == -1)
					return g[1] - 1;
				else
					return g[1] - 2;
			}
			
		}
		return 0;
	}
	/**
	 * Returns the index of Groupfoot
	 * <p> -1: no Groupfoot
	 */
	public int getGroupfootIndex(){
		final Rows rows = (Rows)getParent();
		if (rows != null) {
			int[] g = rows.getGroupsInfoAtIndex(getIndex(), true);
			if (g != null) return g[2];
		}
		return -1;
	}
	/** Returns the image URI.
	 * <p>Default: null.
	 * <p>The same as {@link #getSrc}.
	 */
	public String getImage() {
		return _src;
	}
	/** Sets the image URI.
	 * <p>If src is changed, the component's inner is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 * <p>The same as {@link #setSrc}.
	 */
	public void setImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();

			//_src is meaningful only if _image is null
			//NOTE: Tom Yeh: 20051222
			//It is possible to use smartUpdate if we always generate
			//an image (with an ID) in getImgTag.
			//However, it is too costly by making HTML too big, so
			//we prefer to invalidate (it happens rarely)
		}
	}
	/** Returns the src (an image URI).
	 * <p>Default: null.
	 * <p>The same as {@link #getImage}.
	 */
	public String getSrc() {
		return getImage();
	}
	/** Sets the src (the image URI).
	 * <p>If src is changed, the component's inner is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 * <p>The same as {@link #setImage}.
	 */
	public void setSrc(String src) {
		setImage(src);
	}
	/** Returns whether this container is open.
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Sets whether this container is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("z.open", _open);
		}
	}
	/** Returns the HTML IMG tag for the image part, or null
	 * if no image is assigned.
	 *
	 * <p>Used only for component template, not for application developers.
	 *
	 * <p>Note: the component template shall use this method to
	 * generate the HTML tag, instead of using {@link #getImage}.
	 */
	public String getImgTag() {
		if (_src == null)
			return null;

		final StringBuffer sb = new StringBuffer(64)
			.append("<img src=\"")
			.append(getDesktop().getExecution().encodeURL(_src))
			.append("\" align=\"absmiddle\"/>");

		final String label = getLabel();
		if (label != null && label.length() > 0) sb.append(' ');

		return sb.toString(); //keep a space
	}
	private void applyImageIfAny() {
		if (getImage() == null) {
			_src = (isOpen() ? "~./zul/img/tree/open.png" : "~./zul/img/tree/close.png");
		}
	}
	/** Returns the value of the {@link Label} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Component cell = (Component)getFirstChild();
		return cell != null && cell instanceof Label ? ((Label)cell).getValue(): null;
	}
	/** Sets the value of the {@link Label} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setValue(label);
	}
	private Label autoFirstCell() {
		Component cell = (Component)getFirstChild();
		if (cell == null || cell instanceof Label) {
			if (cell == null) cell = new Label();
			cell.applyProperties();
			cell.setParent(this);
			return (Label)cell;
		}
		throw new UiException("Unsupported child for setLabel: "+cell);
	}	
	public void onChildAdded(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String clx = cmp.getSclass();
		cmp.setSclass(clx != null && clx.length() > 0 ? clx + " group-cell" : "group-cell");
	}
	public void onChildRemoved(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String cls = cmp.getSclass();
		cmp.setSclass(cls != null && cls.indexOf("group-cell") > -1 ? 
				cls.replaceAll("(?:^|\\s+)" + "group-cell" + "(?:\\s+|$)", " ").trim() : cls);
	}
	public String getOuterAttrs() {
		applyImageIfAny();
		final StringBuffer sb = new StringBuffer(64).append( super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.open", isOpen());
		appendAsapAttr(sb, Events.ON_OPEN);
		return sb.toString();
	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Openable {
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;
		}
	}
	/**
	 * An iterator used by _items.
	 */
	private class IterItems implements Iterator {
		private final Iterator _it = getParent().getChildren().listIterator(getIndex()+1);
		private int _j;

		public boolean hasNext() {
			return _j < getItemCount();
		}
		public Object next() {
			final Object o = _it.next();
			++_j;
			return o;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
