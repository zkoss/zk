/* Gpolyline.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 4:38:11 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.gmaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.util.CollectionsX;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Google Maps support Gpolyline.
 * 
 * @author henrichen
 * @since 2.5.0
 */
public class Gpolyline extends HtmlBasedComponent {
	private List _points = new ArrayList(32);
	private String _strpoints;
	private String _color = "#808080"; //default to dark gray
	private int _weight = 5;  //default to 5
	private int _numLevels = 4; //default to 4
	private int _zoomFactor = 32; //zoom factor between polyline levels (2^5 if _numLevels == 4).
	private String _encodedPolyline; //cached value
	private String _encodedLevels; //cached value

	public static void main(String[] arg) {
		Gpolyline pline = new Gpolyline();
		pline.addPoint(38.5, -120.2, 3);
		pline.addPoint(40.7, -120.95, 3);
		pline.addPoint(43.252, -126.453, 3);

		System.out.println(pline.getEncodedPolyline());
	}

	public Gpolyline() {
	}
	
	/**
	 * Add polyline axix point.
	 * <p>Note the polyline "level" does not correspond directly to a zoom level but they are related. 
	 * More accurately, the numLevels parameter divides up the existing zoom levels (currently 18) 
	 * into groups of zoom levels and polyline level is confined by the numLevels. For example, the
	 * default numLevels value is 4, so the polyline level can be 0 to 3 only. The Google maps zoom 
	 * levels are grouped together into the following 4 levels (determined by numLevels):</p>
	 * <table>
	 * <th><td>Polyline Level</td><td>Zoom Levels</td></th>
	 * <tr><td>Level 0</td><td>Zoom levels 0-5</td></tr>
	 * <tr><td>Level 1</td><td>Zoom levels 6-10</td></tr>
	 * <tr><td>Level 2</td><td>Zoom levels 11-14</td></tr>
	 * <tr><td>Level 3</td><td>Zoom levels 15-18</td></tr>
	 * </table>
	 * <p>An polyline level value of 3 in this case would ensure that a point appears in all zoom level, 
	 * while a level of 0 will ignore points specified beyond zoom level 5. The endpoints of a polyline 
	 * should often be set to the largest encoded level, to ensure that the endpoints will appear in 
	 * all zoom levels and the line is drawn correctly.</p>
	 * <p>A general rule is that whenever the level of the end point is greater or equal to the current Gmaps level,
	 * that end point is appeared; otherwise, it should be skipped.</p> 
	 * 
	 * @param lat the latitude
	 * @param lng the longtitude
	 * @param level the polyline level(default to 0 - 3)
	 */
	public void addPoint(double lat, double lng, int level) {
		if (lat > 90.0 || lat < -90.0) {
			throw new IllegalArgumentException("latitude must be from -90 ~ +90: "+lat);
		}
		if (lng > 180.0 || lng < -180.0) {
			throw new IllegalArgumentException("longtitude must be from -180 ~ +180: "+lng);
		}
		if (level < 0 || level >= _numLevels) {
			throw new IllegalArgumentException("level must be from 0 ~ " + (_numLevels - 1) + ": " + level);
		}
		_points.add(new Tuple(lat, lng, level));

		clearCacheAndInvalidate();
	}
	
	private void clearCacheAndInvalidate() {
		_encodedPolyline = null; //cached value
		_encodedLevels = null; //cached value
		invalidate();
	}
	/**
	 * Set polyline in lat,lng,level tuple(double, double, int); 
	 * e.g. "lat1,lng1,level1,lat2,lng2,level2,lat3,lng3,level3, ...".
	 * @param points
	 */
	public void setPoints(String points) {
		if (!Objects.equals(points, _strpoints)) {
			_strpoints = points;
			if (points != null) {
				final Collection list = CollectionsX.parse(null, points, ',');
				for(final Iterator it = list.iterator(); it.hasNext();) {
					double lat = Double.parseDouble((String)it.next());
					double lng = Double.parseDouble((String)it.next());
					int level = Integer.parseInt((String)it.next());
					addPoint(lat, lng, level);
				}
			}

			clearCacheAndInvalidate();
		}
	}

	public String getEncodedPolyline() {
		if (_encodedPolyline == null) {
			int lat = 0;
			int lng = 0;
			final StringBuffer sb = new StringBuffer(_points.size()*4);
			for(final Iterator it = _points.iterator(); it.hasNext();) {
				Tuple tuple = (Tuple) it.next();
				final int tlat = e5(tuple.lat);
				final int tlng = e5(tuple.lng);
				sb.append(encodeLatLng(tlat - lat)).append(encodeLatLng(tlng - lng));
				lat = tlat;
				lng = tlng;
			}
			_encodedPolyline = sb.length() == 0 ? "??" : sb.toString();
		}
		return _encodedPolyline;
	}
	
	private int e5(double db) {
		return (int) Math.floor(db * 100000);
	}
	
	public String getEncodedLevels() {
		if (_encodedLevels == null) {
			final StringBuffer sb = new StringBuffer(_points.size()*2);
			for(final Iterator it = _points.iterator(); it.hasNext();) {
				Tuple tuple = (Tuple) it.next();
				sb.append(encodeInt(tuple.level));
			}
			_encodedLevels = sb.length() == 0 ? "?" : sb.toString();
		}
		return _encodedLevels;
	}
	
	private static String encodeLatLng(int e5) {
		boolean sign = e5 < 0;
		e5 <<=1; //shift left
		if (sign) e5 ^= 0xffffffff;
		return encodeInt(e5);
	}
	
	private static String encodeInt(int x) {
		final StringBuffer sb = new StringBuffer(6);
		do {
			int chunk = (x & 0x1f);
			x >>>= 5;
			if (x != 0) {
				chunk |= 0x20;
			}
			chunk += 63;
			sb.append((char)chunk);
		} while(x != 0);
		return sb.toString();
	}
	
	/** Returns the HTML attributes for this tag.
	 * <p>Used only for component development, not for application developers.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "z.pts", getEncodedPolyline());
		HTMLs.appendAttribute(sb, "z.lvs", getEncodedLevels());
		HTMLs.appendAttribute(sb, "z.zf", getZoomFactor());
		HTMLs.appendAttribute(sb, "z.nlvs", getNumLevels());
		HTMLs.appendAttribute(sb, "z.cr", getColor());
		HTMLs.appendAttribute(sb, "z.wg", getWeight());
		HTMLs.appendAttribute(sb, "z.pid", getParent().getUuid());
		return sb.toString();
	}

	/**
	 * Line color in form of #RRGGBB, default to #808080.
	 * @return the color
	 */
	public String getColor() {
		return _color;
	}

	/**
	 * Line color in form of #RRGGBB, default to #808080.
	 * @param color the color to set
	 */
	public void setColor(String color) {
		if (color == null) color = "#808080";
		if (!color.equals(_color)) {
			this._color = color;
			invalidate();
		}
	}

	/**
	 * line weight 1 - 10, default to 5.
	 * @return the weight
	 */
	public int getWeight() {
		return _weight;
	}

	/**
	 * line weight 1 - 10, default to 5.
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		if (weight != _weight) {
			this._weight = weight;
			invalidate();
		}
	}
	
	/**
	 * Returns the number of polyline levels.
	 * @return the number of polyline levels.
	 */
	public int getNumLevels() {
		return _numLevels;
	}
	
	/**
	 * Sets the number of polyline levels.
	 * @param numLevels the number of levels.
	 */
	public void setNumLevels(int numLevels) {
		if (numLevels < 1 || numLevels > 19) {
			throw new IllegalArgumentException("numLevels must be from 1 ~ 19: " + numLevels);
		}
		if (_numLevels != numLevels) {
			this._numLevels = numLevels;
			
			//calculate proper zoomFactor
			int factor = 19/numLevels;
			int mod = 19%numLevels;
			
			//if mod is larger than numLevels / 2
			_zoomFactor = 2 << ((mod > (numLevels >> 1)) ? (factor + 1) : factor);
			
			invalidate();
		}
	}
	
	/**
	 * Returns the zoomFactor (zoomFactor change per the numLevels).
	 */
	public int getZoomFactor() {
		return _zoomFactor;
	}

	private static class Tuple {
		public double lat;
		public double lng;
		public int level;
		public Tuple(double lat, double lng, int level) {
			this.lat = lat;
			this.lng = lng;
			this.level = level;
		}
	}
	
}
