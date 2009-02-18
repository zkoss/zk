/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:00:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.mesg.Messages;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.impl.HeadersElement;
import org.zkoss.zul.mesg.MZul;

/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 * <p>Default {@link #getZclass}: z-columns.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Columns extends HeadersElement implements org.zkoss.zul.api.Columns {
	private String _mpop = "none";
	private transient Menupopup _menupopup;
	private Object _value;
	private boolean _columnshide = true;
	private boolean _columnsgroup = true;
	
	/** Returns the grid that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}
	/** Returns the grid that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Grid getGridApi() {
		return getGrid();
	}

	/**
	 * Sets whether to enable hiding of columns with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto. 
	 * @since 3.5.0
	 */
	public void setColumnshide(boolean columnshide) {
		if (_columnshide != columnshide) {
			_columnshide = columnshide;
			postOnInitLater();
			smartUpdate("z.columnshide", _columnshide);
		}
	}
	/**
	 * Returns whether to enable hiding of columns with the header context menu.
	 * <p>Default: true.
	 * @since 3.5.0
	 */
	public boolean isColumnshide() {
		return _columnshide;
	}
	/**
	 * Sets whether to enable grouping of columns with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto. 
	 * @since 3.5.0
	 */
	public void setColumnsgroup(boolean columnsgroup) {
		if (_columnsgroup != columnsgroup) {
			_columnsgroup = columnsgroup;
			postOnInitLater();
			smartUpdate("z.columnsgroup", _columnsgroup);
		}
	}
	/**
	 * Returns whether to enable grouping of columns with the header context menu.
	 * <p>Default: true.
	 * @since 3.5.0
	 */
	public boolean isColumnsgroup() {
		return _columnsgroup;
	}
	/** Returns the ID of the Menupopup ({@link Menupopup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: none (a default menupoppup).
	 * @since 3.5.0
	 */
	public String getMenupopup() {
		return _mpop;
	}
	/** Sets the ID of the menupopup ({@link Menupopup}) that should appear
	 * when the user clicks on the element of each column.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in 
	 * the same ID space or not.
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * @param mpop an ID of the menupopup component, "none", or "auto".
	 * 	"none" is assumed by default, "auto" means the menupopup component is 
	 *  created automatically.
	 * @since 3.5.0
	 * @see #setMenupopup(String)
	 */
	public void setMenupopup(String mpop) {
		if (!Objects.equals(_mpop, mpop)) {
			_mpop = mpop;
			invalidate();
			postOnInitLater();
		}
	}
	public void invalidate() {
		final Grid grid = getGrid();
		if (grid != null) grid.invalidate();
		else super.invalidate();
	}
	/** Sets the UUID of the popup menu that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Note: it actually invokes
	 * <code>setMenupopup("uuid(" + menupop.getUuid() + ")")</code>
	 * @since 3.5.0
	 * @see #setMenupopup(String)
	 */
	public void setPopup(Menupopup mpop) {
		setPopup(mpop != null ? "uuid(" + mpop.getUuid() + ")": null);
	}
	/** Sets the UUID of the popup menu that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Note: it actually invokes
	 * <code>setMenupopup("uuid(" + menupop.getUuid() + ")")</code>
	 * @since 3.5.2
	 * @see #setMenupopup(String)
	 */
	public void setPopupApi(org.zkoss.zul.api.Menupopup mpopApi) {
		setPopup((Menupopup) mpopApi);		
	}
	/**
	 * @since 3.5.0
	 */
	private String getMpopId() {
		final String mpop = getMenupopup();
		if ("none".equals(mpop)) return "zk_n_a";
		if ("auto".equals(mpop)) return _menupopup.getId();
		return mpop;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public void setValue(Object value) {
		_value = value;
	}
	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-columns" : _zclass;
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: "+parent);
		super.setParent(parent);
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		final Grid grid = getGrid();
		if (grid != null)
			HTMLs.appendAttribute(sb, "z.rid", grid.getUuid());

		if (_columnsgroup)
			HTMLs.appendAttribute(sb, "z.columnsgroup", _columnsgroup);
		if (_columnshide)
			HTMLs.appendAttribute(sb, "z.columnshide", _columnshide);
		if (_mpop != null) HTMLs.appendAttribute(sb, "z.mpop", getMpopId());
		return sb.toString();
	}
	public boolean setVisible(boolean visible) {
		final boolean vis = super.setVisible(visible);
		final Grid grid = getGrid();
		if (grid != null)
			grid.invalidate();
		return vis;
	}
	
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: "+child);
		return super.insertBefore(child, insertBefore);
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		postOnInitLater();
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		postOnInitLater();
	}
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		postOnInitLater();
	}

	/** Handles a private event, onInitLater. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 * @since 3.5.0
	 */
	public void onInitLater(Event event) {
		if (getPage() != null) {
			final Execution exe = Executions.getCurrent();
			final String uuid = getUuid();
			if (exe.getAttribute("_zk_columns_menupopup_" + uuid) == null) {
				if (_menupopup != null) _menupopup.detach();
				_menupopup = new Columnmenupopup(this);
				smartUpdate("z.mpop", _menupopup.getId());
				exe.setAttribute("_zk_columns_menupopup_" + uuid, Boolean.TRUE);
			}
		}
	}
	//Cloneable//
	public Object clone() {
		final Columns clone = (Columns) super.clone();
		clone._menupopup = null;
		return clone;
	}

	/**
	 * @since 3.5.0
	 */
	/*package*/ void postOnInitLater() {
		if (getPage() != null && "auto".equals(getMenupopup()))
			Events.postEvent(-20010, "onInitLater", this, null);
	}
	/**
	 * @since 3.5.0
	 */
	private final class Columnmenupopup extends Menupopup {
		private transient Component _ref;
		private Columnmenupopup(Columns cols) {
			init(cols);
		}

		private void init(Columns cols) {
			setPage(cols.getPage());
			addEventListener(Events.ON_OPEN, new EventListener() {
				public void onEvent(Event event) throws Exception {
					_ref = ((OpenEvent)event).getReference();
				}
			});
			if(WebApps.getFeature("professional") && cols._columnsgroup) {
				Menuitem group = new Menuitem(Messages.get(MZul.GRID_GROUP));
				group.setSclass(cols.getZclass() + "-menu-grouping");
				group.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event event) throws Exception {
						if (_ref instanceof Column) {
							Column col = ((Column) _ref);
							final String dir = col.getSortDirection();
							if ("ascending".equals(dir)) col.group(false);
							else if ("descending".equals(dir)) col.group(true);
							else if (!col.group(true)) col.group(false);
						}
					}
				});
				group.setParent(this);
			}
			final Menuitem asc = new Menuitem(Messages.get(MZul.GRID_ASC));
			asc.setSclass(cols.getZclass() + "-menu-asc");
			asc.addEventListener(Events.ON_CLICK, new EventListener(){
				public void onEvent(Event event) throws Exception {
					if (_ref instanceof Column) ((Column) _ref).sort(true);
				}
			});
			asc.setParent(this);
			final Menuitem des = new Menuitem(Messages.get(MZul.GRID_DESC));
			des.setSclass(cols.getZclass() + "-menu-desc");
			des.addEventListener(Events.ON_CLICK, new EventListener(){
				public void onEvent(Event event) throws Exception {
					if (_ref instanceof Column) ((Column) _ref).sort(false);
				}
			});
			des.setParent(this);
			if (cols._columnshide) {
				Menuseparator sep = new Menuseparator();
				sep.setParent(this);
				for (Iterator it = cols.getChildren().iterator(); it.hasNext();) {
					this.appendChild(createMenuitem((Column)it.next()));
				}
			}
		}
		private Menuitem createMenuitem(final Column col) {
			final Menuitem item = new Menuitem(col.getLabel());
			item.setAutocheck(true);
			item.setCheckmark(true);
			item.setChecked(col.isVisible());
			item.addEventListener(Events.ON_CLICK, new EventListener() {
				public void onEvent(Event event) throws Exception {
					Menupopup pop = (Menupopup)item.getParent();
					int checked = 0;
					for (Iterator it = pop.getChildren().iterator(); it.hasNext();){
						Object obj = it.next();
						if (obj instanceof Menuitem && ((Menuitem)obj).isChecked()){
							checked++;
						}
					}
					if (checked == 0) {//at least show on column
						item.setChecked(true);
					}
					col.setVisible(item.isChecked());
				}
			});
			return item;
		}
		public String getOuterAttrs() {
			return super.getOuterAttrs() + " z.autocreate=\"true\"";
		}
		public Object clone() {
			final Columnmenupopup clone = (Columnmenupopup) super.clone();
			clone._ref = null;
			return clone;
		}
	}
}
