/* LayoutRegion.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:17:16 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * A layout region in a border layout.
 * <p>
 * Events:<br/> onOpen, onSize.<br/>
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public abstract class LayoutRegion extends XulElement implements org.zkoss.zul.api.LayoutRegion {

	private String _border = "normal";
	private int[] _margins = new int[] { 0, 0, 0, 0 };
	private boolean _flex;
	private boolean _autoscroll;

	private String _title = null;	
	private int _maxsize = 2000;
	private int _minsize = 0;
	private int[] _cmargins = new int[] { 5, 5, 5, 5 };
	private boolean _splittable;
	private boolean _collapsible;
	private boolean _open = true;

	static {
		addClientEvent(LayoutRegion.class, Events.ON_OPEN, CE_IMPORTANT);
		addClientEvent(LayoutRegion.class, Events.ON_SIZE, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
	}

	public LayoutRegion() {
	}

	/**
	 * Returns the border.
	 * <p>
	 * The border actually controls what CSS class to use: If border is null, it
	 * implies "none".
	 * 
	 * <p>
	 * If you also specify the CSS class ({@link #setClass}), it overwrites
	 * whatever border you specify here.
	 * 
	 * <p>
	 * Default: "normal".
	 */
	public String getBorder() {
		return _border;
	}

	/**
	 * Sets the border (either none or normal).
	 * 
	 * @param border
	 *            the border. If null or "0", "none" is assumed.
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border))
			border = "none";
		if (!_border.equals(border)) {
			_border = border;
			smartUpdate("border", _border);
		}
	}

	/**
	 * Returns whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * 
	 * <p>
	 * Default: false.
	 */
	public final boolean isFlex() {
		return _flex;
	}

	/**
	 * Sets whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * 
	 */
	public void setFlex(boolean flex) {
		if (_flex != flex) {
			_flex = flex;
			smartUpdate("flex", _flex);
		}
	}

	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * Default: "0,0,0,0".
	 */
	public String getMargins() {
		return Utils.intsToString(_margins);
	}

	/**
	 * Sets margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 */
	public void setMargins(String margins) {
		final int[] imargins = Utils.stringToInts(margins, 0);
		if (!Objects.equals(imargins, _margins)) {
			_margins = imargins;
			smartUpdate("imargins", getMargins());
		}
	}

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutoscroll() {
		return _autoscroll;
	}

	/**
	 * Sets whether enable overflow scrolling.
	 */
	public void setAutoscroll(boolean autoscroll) {
		if (_autoscroll != autoscroll) {
			_autoscroll = autoscroll;
			smartUpdate("autoscroll", _autoscroll);
		}
	}

	/**
	 * Returns this regions position (north/south/east/west/center).
	 * 
	 * @see Borderlayout#NORTH
	 * @see Borderlayout#SOUTH
	 * @see Borderlayout#EAST
	 * @see Borderlayout#WEST
	 * @see Borderlayout#CENTER
	 */
	abstract public String getPosition();

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setHeight(String)} and {@link #setWidth(String)}. If this region
	 * is {@link North} or {@link South}, this method will invoke
	 * {@link #setHeight(String)}. If this region is {@link West} or
	 * {@link East}, this method will invoke {@link #setWidth(String)}.
	 * Otherwise it will throw a {@link UnsupportedOperationException}.
	 */
	abstract public void setSize(String size);

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()} and {@link #getWidth()}. If this region is
	 * {@link North} or {@link South}, this method will invoke
	 * {@link #getHeight()}. If this region is {@link West} or {@link East},
	 * this method will invoke {@link #getWidth()}. Otherwise it will throw a
	 * {@link UnsupportedOperationException}.
	 */
	abstract public String getSize();
	

	/** 
	 * Returns the title.
	 * <p>Default: null.
	 */
	public String getTitle() {
		return _title;
	}
	
	/**
	 * Sets the title.
	 */
	public void setTitle(String title) {
		if (!Objects.equals(_title, title)) {
			_title = title;
			smartUpdate("title", _title);
		}
	}

	/**
	 * Returns whether enable the split functionality.
	 * <p>
	 * Default: false.
	 */
	public boolean isSplittable() {
		return _splittable;
	}

	/**
	 * Sets whether enable the split functionality.
	 */
	public void setSplittable(boolean splittable) {
		if (_splittable != splittable) {
			_splittable = splittable;
			smartUpdate("splittable", _splittable);
		}
	}

	/**
	 * Sets the maximum size of the resizing element.
	 */
	public void setMaxsize(int maxsize) {
		if (_maxsize != maxsize) {
			_maxsize = maxsize;
			smartUpdate("maxsize", _maxsize);
		}
	}

	/**
	 * Returns the maximum size of the resizing element.
	 * <p>
	 * Default: 2000.
	 */
	public int getMaxsize() {
		return _maxsize;
	}

	/**
	 * Sets the minimum size of the resizing element.
	 */
	public void setMinsize(int minsize) {
		if (_minsize != minsize) {
			_minsize = minsize;
			smartUpdate("minsize", minsize);
		}
	}

	/**
	 * Returns the minimum size of the resizing element.
	 * <p>
	 * Default: 0.
	 */
	public int getMinsize() {
		return _minsize;
	}

	/**
	 * Returns the collapsed margins, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * Default: "5,5,5,5".
	 */
	public String getCmargins() {
		return Utils.intsToString(_cmargins);
	}

	/**
	 * Sets the collapsed margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 */
	public void setCmargins(String cmargins) {
		final int[] imargins = Utils.stringToInts(cmargins, 0);
		if (!Objects.equals(imargins, _cmargins)) {
			_cmargins = imargins;
			smartUpdate("imargins", getCmargins());
		}
	}

	/**
	 * Returns whether set the initial display to collapse.
	 * <p>
	 * Default: false.
	 */
	public boolean isCollapsible() {
		return _collapsible;
	}

	/**
	 * Sets whether set the initial display to collapse.
	 * 
	 * <p>It only applied when {@link #getTitle()} is not null. (since 3.5.0)
	 */
	public void setCollapsible(boolean collapsible) {
		if (collapsible != _collapsible) {
			_collapsible = collapsible;
			smartUpdate("collapsible", _collapsible);
		}
	}

	/**
	 * Returns whether it is open (i.e., not collapsed. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 * <p>
	 * Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}

	/**
	 * Opens or collapses the splitter. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("open", open);
		}
	}
	
	public String getZclass() {
		return _zclass == null ? "z-" + getPosition() : _zclass;
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (getChildren().size() > 0)
			throw new UiException("Only one child is allowed: " + this);
		super.beforeChildAdded(child, refChild);
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Borderlayout))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (!"normal".equals(_border))
			render(renderer, "border", _border);
		render(renderer, "flex", _flex);
		render(renderer, "autoscroll", _autoscroll);
		if (_margins[0] != 0 || _margins[1] != 0 || _margins[2] != 0 || _margins[3] != 0)
			render(renderer, "margins", getMargins());

		render(renderer, "title", _title);

		if (_maxsize != 2000)
			renderer.render("maxsize", _maxsize);
		if (_minsize != 0)
			renderer.render("minsize", _minsize);

		render(renderer, "splittable", _splittable);
		render(renderer, "collapsible", _collapsible);

		render(renderer, "cmargins", getCmargins());
			//always generate since different region might have different default
		if (!_open)
			renderer.render("open", _open);
	}
	
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link org.zkoss.zul.LayoutRegion#service},
	 * it also handles onOpen.
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
