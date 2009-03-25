/* Slider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 20:16:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.XulElement;

/**
 * A slider.
 *  <p>Default {@link #getZclass} as follows: (since 3.5.0)
 *  <ol>
 *  	<li>Case 1: If {@link #getOrient()} is vertical, "z-slider-ver" is assumed</li>
 *  	<li>Case 2: If {@link #getOrient()} is horizontal, "z-slider-hor" is assumed</li>
 *  </ol>
 * 
 * @author tomyeh
 */
public class Slider extends XulElement implements org.zkoss.zul.api.Slider {
	private String _orient = "horizontal";
	private int _curpos, _maxpos = 100, _pginc = 10;
	/** The name. */
	private String _name;
	private String _slidingtext = "{0}";

	public Slider() {
		setWidth("207px");
	}
	/**
	 * @param curpos the current position (default: 0)
	 */
	public Slider(int curpos) {
		this();
		setCurpos(curpos);
	}
	/*package*/ final boolean inScaleMold() {
		return "scale".equals(getMold());
	}
	/*package*/ final boolean inSphereMold() {
		return "sphere".equals(getMold());
	}
	
	// super
	public String getZclass() {
		final String name = "z-slider";
		if (_zclass == null) {
			if (inScaleMold())
				return name + "-scale";
			else if (inSphereMold())			  
				return name + ("horizontal".equals(getOrient()) ? "-sphere-hor" : "-sphere-ver");
			else
				return name + ("horizontal".equals(getOrient()) ? "-hor" : "-ver");
		}
		return _zclass;
	}
	
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * <p>Default : "horizontal" 
	 * @param orient either "horizontal" or "vertical".
	 * @since 3.5.0
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			if ("vertical".equals(_orient)) {
				setWidth(null);
				setHeight("207px");
			} else {
				setWidth("207px");
				setHeight(null);
			}
			invalidate();
		}
	}
	/**
	 * Returns the sliding text.
	 * <p>Default : "{0}"
	 * @since 3.0.1
	 */
	public String getSlidingtext() {
		return _slidingtext;
	}
	
	/**
	 * Sets the sliding text.
	 * The syntax "{0}" will be replaced with the position at client side.
	 * @since 3.0.1
	 */
	public void setSlidingtext(String slidingtext) {		
		if (slidingtext == null || slidingtext.length() == 0)
			slidingtext = "{0}";
		if (!_slidingtext.equals(slidingtext)) {
			_slidingtext = slidingtext;
			smartUpdate("z.slidingtext", _slidingtext);
		}
	}
	/** Returns the current position of the slider.
	 *
	 * <p>Default: 0.
	 */
	public final int getCurpos() {
		return _curpos;
	}
	/** Sets the current position of the slider.
	 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
	 * {@link #getMaxpos} is assumed.
	 */
	public final void setCurpos(int curpos)
	throws WrongValueException {
		if (curpos < 0) curpos = 0;
		else if (curpos > _maxpos) curpos = _maxpos;

		if (_curpos != curpos) {
			_curpos = curpos;
			smartUpdate("z.curpos", _curpos);
		}
	}

	/** Returns the maximum position of the slider.
	 *
	 * <p>Default: 100.
	 */
	public final int getMaxpos() {
		return _maxpos;
	}
	/** Sets the maximum position of the slider.
	 *
	 * @exception WrongValueException if non-positive maxpos is passed
	 */
	public final void setMaxpos(int maxpos)
	throws WrongValueException {
		if (maxpos <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+maxpos);

		if (_maxpos != maxpos) {
			if (_curpos > maxpos)
				setCurpos(maxpos);
			_maxpos = maxpos;
			smartUpdate("z.maxpos", _maxpos);
		}
	}
	
	/**
	 * Returns whether it is a vertical slider.
	 * 
	 * @since 3.5.0
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked. 
	 *
	 * <p>Default: 10.
	 */
	public final int getPageIncrement() {
		return _pginc;
	}
	/** Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 */
	public final void setPageIncrement(int pginc)
	throws WrongValueException {
		if (pginc <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+pginc);
		if (_pginc != pginc) {
			_pginc = pginc;
			smartUpdate("z.pginc", _pginc);
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @since 3.0.0
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name the name of this component.
	 * @since 3.0.0
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("z.name", _name);
		}
	}
	
	/**
	 * Sets the mold.
	 * 
	 * @param mold default , scale 
	 *            
	 */
	public void setMold(String mold){
		if (isVertical()){
			if (mold.startsWith("scale")){
				throw new WrongValueException("Unsupported vertical orient in mold : "+mold);
			}else{
				super.setMold(mold);				
			}
		}else{
			super.setMold(mold);			
		}
	}

	//-- super --//

	//-- Component --//
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#process},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getName();
		if (name.equals(Events.ON_SCROLL)) {
			ScrollEvent evt = ScrollEvent.getScrollEvent(request);
			int curpos = evt.getPos();
			_curpos = curpos >= 0 ? curpos: 0;
			Events.postEvent(evt);
		} else if (name.equals(Events.ON_SCROLLING)) {
			ScrollEvent evt = ScrollEvent.getScrollEvent(request);
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
