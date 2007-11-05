/* Ginfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 19 16:36:52     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.gmaps;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.Command;

/**
 * The popup info window of the Gooogle Maps. You can specify the content
 * in pure text or HTML.
 *
 * @author henrichen
 */
public class Ginfo extends HtmlBasedComponent {
	protected double _lat = 37.4419;
	protected double _lng = -122.1419;
	private String _content = "";
	private Boolean _open = Boolean.FALSE; //used only when init the Ginfo

	public Ginfo() {
	}
	public Ginfo(String content) {
		setContent(content);
	}
	public Ginfo(String content, double lat, double lng) {
		setContent(content);
		setAnchor(lat, lng);
	}

	/** Returns the content of the info window.
	 * <p>Default: empty.
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the contents (can be pure text or HTML).
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			smartUpdate("z.content", _content);
		}
	}

	/** set the anchor point of the info window.
	 * @param lat latitude of the anchor point in Google Maps.
	 * @param lng longitude of the anchor point in Google Maps.
	 */
	public void setAnchor(double lat, double lng) {
		boolean update = false;
		if (lat != _lat) {
			_lat = lat;
			update = true;
		}
		if (lng != _lng) {
			_lng = lng;
			update = true;
		}
		if (update) {
			smartUpdate("z.anch", getAnchor());
		}
	}

	/** set the latitude of the anchor point.
	 */
	public void setLat(double lat) {
		if (lat != _lat) {
			_lat = lat;
			smartUpdate("z.anch", getAnchor());
		}
	}
	
	/** get the latitude of the anchor point.
	 */
	public double getLat() {
		return _lat;
	}
	
	/** set the longitude of the anchor point.
	 */
	public void setLng(double lng) {
		if (lng != _lng) {
			_lng = lng;
			smartUpdate("z.anch", getAnchor());
		}
	}
	
	/** get the longitude of the anchor point.
	 */
	public double getLng() {
		return _lng;
	}
	
	/** Open this Info */
	public void setOpen(boolean b) {
		Gmaps gmaps = (Gmaps) getParent();
		if (gmaps != null) {
			if (b) {
				gmaps.openInfo(this);
			} else if (gmaps.getInfo() == this) {
				gmaps.closeInfo();
			}
		}
		if (_open != null) _open = Boolean.valueOf(b);
	}
	
    /** whether this Ginfo is the currently opened info window of its parent {@link Gmaps}.
     */
    public boolean isOpen() {
        Gmaps gmaps = (Gmaps) getParent();
        return (gmaps != null && gmaps.getInfo() == this) || (_open != null && _open.booleanValue());
    }
   
	/** get the Maps center in String form lat,lng; used by component developers
	 * only.
	 */
	private String getAnchor() {
		return ""+_lat+","+_lng;
	}

	/** Returns the HTML attributes for this tag.
	 * <p>Used only for component development, not for application developers.
	 */
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final StringBuffer sb = new StringBuffer(64);
		if (attrs != null) {
			sb.append(attrs);
		}
		HTMLs.appendAttribute(sb, "z.anch", getAnchor());
		HTMLs.appendAttribute(sb, "z.pid", getParent().getUuid());
		if (isOpen()) {
			HTMLs.appendAttribute(sb, "z.open", "true");
		}
		_open = null;
		return sb.toString();
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Gmaps))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
