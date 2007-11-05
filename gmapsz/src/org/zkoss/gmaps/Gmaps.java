/* Gmaps.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 12 16:35:20     2006, Created by henrichen
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

import java.util.Iterator;


/**
 * The component used to represent
 * &lt;a href="http://www.google.com/apis/maps/"&gt;Google Maps&lt;/a&gt;
 *
 * @author henrichen
 * @version $Revision: 1.6 $ $Date: 2006/03/31 08:38:55 $
 */
public class Gmaps extends HtmlBasedComponent {
	private transient Ginfo _oneinfo; //the only one Ginfo child of this Gmaps.
	private transient Ginfo _info; //current opened info window, null means none is open.

	private double _lat = 37.4419;
	private double _lng = -122.1419;
	private int _zoom = 13;
	private boolean _large;
	private boolean _small;
	private boolean _type;
	private String _mapType;

	/** set the center of the Google Maps.
	 * @param lat latitude of the Google Maps center
	 * @param lng longitude of the Google Maps center
	 */
	public void setCenter(double lat, double lng) {
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
			smartUpdate("z.center", getCenter());
		}
	}

	/** set the current latitude of the Maps center.
	 */
	public void setLat(double lat) {
		if (lat != _lat) {
			_lat = lat;
			smartUpdate("z.center", getCenter());
		}
	}
	
	/** get the current latitude of the Maps center.
	 */
	public double getLat() {
		return _lat;
	}
	
	/** set the current longitude of the Maps center.
	 */
	public void setLng(double lng) {
		if (lng != _lng) {
			_lng = lng;
			smartUpdate("z.center", getCenter());
		}
	}
	
	/** get the currrent longitude of the Maps center.
	 */
	public double getLng() {
		return _lng;
	}
	
	/** get the Maps center in String form lat,lng; used by component developers
	 * only.
	 */
	private String getCenter() {
		return ""+_lat+","+_lng;
	}

	/** panTo the new center of the Google Maps.
	 */
	public void panTo(double lat, double lng) {
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
			smartUpdate("z.panTo", getCenter());
		}
	}
	
	/** set zoom level.
	 */
	public void setZoom(int zoom) {
		if (zoom != _zoom) {
			_zoom = zoom;
			smartUpdate("z.zoom", ""+_zoom);
		}
	}
	
	/** get zoom level.
	 */
	public int getZoom() {
		return _zoom;
	}
	
	/** Whether show the large Google Maps Control.
	 */
	public void setShowLargeCtrl(boolean b) {
		if (_large == b) {
			return;
		}
		_large = b;
		smartUpdate("z.lctrl", ""+b);
	}
	
	/** Whether show the large Google Maps Control.
	 */
	public boolean isShowLargeCtrl() {
		return _large;
	}
	
	/** Whether show the small Google Maps Control.
	 */
	public void setShowSmallCtrl(boolean b) {
		if (_small == b) {
			return;
		}
		_small = b;
		smartUpdate("z.sctrl", ""+b);		
	}
	
	/** Whether show the large Google Maps Control.
	 */
	public boolean isShowSmallCtrl() {
		return _small;
	}
	
	/** Whether show the Google Maps type Control.
	 */
	public void setShowTypeCtrl(boolean b) {
		if (_type == b) {
			return;
		}
		_type = b;
		smartUpdate("z.tctrl", ""+b);
	}

	/** Whether show the Google Maps type Control.
	 */
	public boolean isShowTypeCtrl() {
		return _type;
	}
	
	/**
	 * Get the current Map Type.
	 * @return the current Map Type.
	 */
	public String getMapType() {
		return _mapType;
	}
	
	/** 
	 * Set the map type (normal, satellite, hybrid), default is normal.
	 * @param mapType (normal, satellite, hybrid), default is normal.
	 */
	public void setMapType(String mapType) {
		_mapType = mapType;
		smartUpdate("z.mt", mapType);
	}
	
    /** Open the specified Ginfo. The specified Ginfo must be child of this Gmaps.
            */
    public void openInfo(Ginfo info) {
        if (info == null) {
            closeInfo();
            return;
        }
        if (info.getParent() != this) {
            throw new UiException("The to be opened Ginfo or Gmarker must be child of this Gmaps!");
        }
        if (info != getInfo()) {
            smartUpdate("z.open", info.getUuid());
        }
    }
    
    /** Close the currently opened info window.
     */
    public void closeInfo() {
        if (getInfo() != null) {
            smartUpdate("z.close", "");
        }
    }
    
	/** Returns the currently opened info window of this Google Maps (might be Gmarker or Ginfo).
	 */
	public Ginfo getInfo() {
		return _info;
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
		if (Events.isListened(this, "onMapMove", true)) {
			HTMLs.appendAttribute(sb, "z.onMapMove", "true");
		}
		if (Events.isListened(this, "onMapZoom", true)) {
			HTMLs.appendAttribute(sb, "z.onMapZoom", "true");
		}
		if (Events.isListened(this, "onInfoChange", true)) {
			HTMLs.appendAttribute(sb, "z.onInfoChange", "true");
		}
		if (Events.isListened(this, "onMapClick", true)) {
			HTMLs.appendAttribute(sb, "z.onMapClick", "true");
		}
		if (Events.isListened(this, "onMapDoubleClick", true)) {
			HTMLs.appendAttribute(sb, "z.onMapDoubleClick", "true");
		}
		final StringBuffer ctrls = new StringBuffer(3);
		if (isShowLargeCtrl()) {
			ctrls.append("l");
		}
		if (isShowSmallCtrl()) {
			ctrls.append("s");
		}
		if (isShowTypeCtrl()) {
			ctrls.append("t");
		}
		HTMLs.appendAttribute(sb, "z.init", getCenter()+","+_zoom+(ctrls.length() == 0 ? "" : (","+ctrls)));
		HTMLs.appendAttribute(sb, "z.mt", getMapType());
		return sb.toString();
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
        if (!(child instanceof Ginfo) && !(child instanceof Gpolyline)) { //not Ginfo or Gpolyline
            throw new UiException("Only Ginfo, Gmarker, or Gpolyline is allowed to be child of Gmaps: "+this+", "+child);
        }
		if (!(child instanceof Gmarker) && !(child instanceof Gpolyline)) { //so it is Ginfo
			if (_oneinfo != null && _oneinfo != child)
				throw new UiException("Only one Ginfo is allowed: "+this);
			_oneinfo = (Ginfo)child;
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (!(child instanceof Gmarker)) { //so it is Ginfo
            _oneinfo = null;
        }
		if (child == _info) { //the detached Ginfo is the currently opened Ginfo.
            closeInfo();
		}
		super.onChildRemoved(child);
	}

	//Cloneable//
	public Object clone() {
		final Gmaps clone = (Gmaps)super.clone();
		if (clone._oneinfo != null || clone._info != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
            if (!(child instanceof Gmarker)) { //so it is Ginfo
                _oneinfo = (Ginfo)child;
            }
			if (_info != null && ((Component)child).getId() == _info.getId()) {
				_info = (Ginfo)child;
				break;
			}
		}
	}
	
	/** used by the MapMoveEvent */
	/* package */ void setLatByClient(double lat) {
		_lat = lat;
	}
	/* package */ void setLngByClient(double lng) {
		_lng = lng;
	}
	/* package */ void setZoomByClient(int zoom) {
		_zoom = zoom;
	}
    /** used by the InfoChangeEvent */
    /* package */ void setInfoByClient(Ginfo info) {
        _info = info;
    }

	//register the Gmaps related event
	static {	
		new MapMoveCommand("onMapMove", Command.IGNORE_OLD_EQUIV);
		new MapZoomCommand("onMapZoom", Command.IGNORE_OLD_EQUIV);
		new InfoChangeCommand("onInfoChange", Command.IGNORE_OLD_EQUIV);
		new MapClickCommand("onMapClick", Command.IGNORE_OLD_EQUIV);
		new MapDoubleClickCommand("onMapDoubleClick", Command.IGNORE_OLD_EQUIV);
		new MarkerDropCommand("onMarkerDrop", Command.IGNORE_OLD_EQUIV);
	}
}
