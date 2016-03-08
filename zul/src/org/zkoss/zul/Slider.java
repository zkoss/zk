/* Slider.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 20:16:03     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ScrollEvent;
import org.zkoss.zul.impl.XulElement;

/**
 * A slider.
 * <p>Default {@link #getZclass}: z-slider.
 * 
 * @author tomyeh
 */
public class Slider extends XulElement {
	private String _orient = "horizontal", _mode = INTEGER;
	private Double _curpos = 0.0, _minpos = 0.0, _maxpos = 100.0, _pginc = -1.0, _step = -1.0;
	/** The name. */
	private String _name;
	private String _slidingtext = "{0}";
	private boolean _resetWidth = true; //B80-ZK-2895

	/** Represent integer slider.
	 * @since 7.0.1
	 */
	public static final String INTEGER = "integer";

	/** Represent decimal slider.
	 * @since 7.0.1
	 */
	public static final String DECIMAL = "decimal";

	static {
		addClientEvent(Slider.class, Events.ON_SCROLL, CE_IMPORTANT | CE_DUPLICATE_IGNORE);
		addClientEvent(Slider.class, Events.ON_SCROLLING, CE_DUPLICATE_IGNORE);
	}

	public Slider() {
		super.setWidth("200px");
	}

	/**
	 * @param curpos the current position (default: 0)
	 */
	public Slider(int curpos) {
		this();
		setCurpos(curpos);
	}

	/*package*/ boolean inScaleMold() {
		return "scale".equals(getMold());
	}

	/*package*/ boolean inSphereMold() {
		return "sphere".equals(getMold());
	}

	/** Overrides the method in HtmlBasedComponent, to avoid misuse hflex and width at the same time.
	 * @since 8.0.1
	 */
	@Override
	public void setWidth(String width) { //B80-ZK-2895
		_resetWidth = false;
		super.setWidth(width);
	}

	/** Overrides the method in HtmlBasedComponent, to avoid misuse hflex and width at the same time.
	 * @since 8.0.1
	 */
	@Override
	public void setHflex(String flex) { //B80-ZK-2895
		if (_resetWidth)
			super.setWidth("");
		super.setHflex(flex);
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-slider" : _zclass;
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
			throw new WrongValueException("orient cannot be " + orient);
		if (!Objects.equals(orient, _orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
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
		if (!slidingtext.equals(_slidingtext)) {
			_slidingtext = slidingtext;
			smartUpdate("slidingtext", _slidingtext);
		}
	}

	/** Returns the current position of the slider.
	 *
	 * <p>Default: 0.
	 */
	public int getCurpos() {
		return _curpos.intValue();
	}

	/** Returns the double value of slider's current position.
	 *
	 * <p>Default: 0.
	 * @since 7.0.1
	 */
	public double getCurposInDouble() {
		return _curpos;
	}

	/** Sets the current position of the slider.
	 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
	 * {@link #getMaxpos} is assumed.
	 */
	public void setCurpos(int curpos) {
		setCurpos((double) curpos);
	}

	/** Sets the current position of the slider.
	 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
	 * {@link #getMaxpos} is assumed.
	 * @since 7.0.1
	 */
	public void setCurpos(double curpos) throws WrongValueException {
		if (curpos < _minpos)
			curpos = _minpos;
		else if (curpos > _maxpos)
			curpos = _maxpos;

		if (Double.compare(_curpos, curpos) != 0) {
			_curpos = curpos;
			smartUpdate("curpos", _curpos);
		}
	}

	/** Returns the minimum position of the slider.
	 *
	 * <p>Default: 0.
	 */
	public int getMinpos() {
		return _minpos.intValue();
	}

	/** Returns the double value of slider's minimum position.
	 *
	 * <p>Default: 0.
	 * @since 7.0.1
	 */
	public double getMinposInDouble() {
		return _minpos;
	}

	/** Sets the minimum position of the slider.
	 *
	 * @exception WrongValueException if non-positive minimum is passed
	 */
	public void setMinpos(int minpos) {
		setMinpos((double) minpos);
	}

	/** Sets the minimum position of the slider.
	 * @exception WrongValueException if non-positive minimum is passed
	 * @since 7.0.1
	 */
	public void setMinpos(double minpos) throws WrongValueException {
		if (minpos < 0)
			throw new WrongValueException("Nonpositive is not allowed: " + minpos);

		if (Double.compare(_minpos, minpos) != 0) {
			if (_curpos < minpos)
				_curpos = minpos;
			_minpos = minpos;
			smartUpdate("minpos", _minpos);
		}
	}

	/** Returns the maximum position of the slider.
	 *
	 * <p>Default: 100.
	 */
	public int getMaxpos() {
		return _maxpos.intValue();
	}

	/** Returns the double value of slider's maximum position.
	 *
	 * <p>Default: 100.
	 * @since 7.0.1
	 */
	public double getMaxposInDouble() {
		return _maxpos;
	}

	/** Sets the maximum position of the slider.
	 *
	 * @exception WrongValueException if non-positive maxpos is passed
	 */
	public void setMaxpos(int maxpos) {
		setMaxpos((double) maxpos);
	}

	/** Sets the maximum position of the slider.
	 *
	 * @exception WrongValueException if non-positive maxpos is passed
	 * @since 7.0.1
	 */
	public void setMaxpos(double maxpos) throws WrongValueException {
		if (maxpos <= 0)
			throw new WrongValueException("Nonpositive is not allowed: " + maxpos);

		if (Double.compare(_maxpos, maxpos) != 0) {
			if (_curpos > maxpos)
				_curpos = maxpos;
			_maxpos = maxpos;
			smartUpdate("maxpos", _maxpos);
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
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 */
	public int getPageIncrement() {
		return _pginc.intValue();
	}

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked. 
	 *
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * @since 7.0.1
	 */
	public double getPageIncrementInDouble() {
		return _pginc;
	}

	/** Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * @param pginc the page increment. If negative, slider will scroll
	 * to the position that user clicks.
	 */
	public void setPageIncrement(int pginc) {
		setPageIncrement((double) pginc);
	}

	/** Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * @param pginc the page increment. If negative, slider will scroll
	 * to the position that user clicks.
	 * @since 7.0.1
	 */
	public void setPageIncrement(double pginc) throws WrongValueException {
		if (Double.compare(_pginc, pginc) != 0) {
			_pginc = pginc;
			smartUpdate("pageIncrement", _pginc);
		}
	}

	/**
	 * Returns the step of slider
	 * 
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
	 * @since 7.0.1
	 */
	public int getStep() {
		return _step.intValue();
	}

	/**
	 * Returns the step of slider
	 * 
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
	 * @since 7.0.1
	 */
	public double getStepInDouble() {
		return _step;
	}

	/**
	 * Sets the step of slider
	 * @param step the step of slider. If negative, slider will not step.
	 * @since 7.0.1
	 */
	public void setStep(int step) {
		setStep((double) step);
	}

	/**
	 * Sets the step of slider
	 * @param step the step of slider. If negative, slider will not step.
	 * @since 7.0.1
	 */
	public void setStep(double step) throws WrongValueException {
		if (step <= 0)
			step = -1;
		if (Double.compare(_step, step) != 0) {
			_step = step;
			smartUpdate("step", _step);
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
		if (name != null && name.length() == 0)
			name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	/**
	 * Sets the mold.
	 * 
	 * @param mold default , scale 
	 *            
	 */
	public void setMold(String mold) {
		if (isVertical()) {
			if (mold.startsWith("scale")) {
				throw new WrongValueException("Unsupported vertical orient in mold : " + mold);
			} else {
				super.setMold(mold);
			}
		} else {
			super.setMold(mold);
		}
	}

	/** Sets the mode.
	 * <p>Default : "integer" 
	 * @param mode either "integer" or "decimal".
	 * @since 7.0.1
	 */
	public void setMode(String mode) {
		if (!INTEGER.equals(mode) && !DECIMAL.equals(mode))
			throw new WrongValueException("mode cannot be " + mode);
		if (!Objects.equals(_mode, mode)) {
			_mode = mode;
			smartUpdate("mode", _mode);
		}
	}

	/**
	 * Returns whether it is a decimal slider.
	 * 
	 * @since 7.0.1
	 */
	public boolean isDecimal() {
		return DECIMAL.equals(_mode);
	}

	/** Sets the range of slider.
	 * @param minpos the minimum position of the slider.
	 * @param maxpos the maximum position of the slider.
	 * @since 7.0.1
	 */
	public void setRange(int minpos, int maxpos) {
		setRange((double) minpos, (double) maxpos);
	}

	/** Sets the range of slider.
	 * 
	 * @param minpos the minimum position of the slider.
	 * @param maxpos the maximum position of the slider.
	 * @since 7.0.1
	 */
	public void setRange(double minpos, double maxpos) {
		setMinpos(minpos);
		setMaxpos(maxpos);
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
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (Events.ON_SCROLL.equals(cmd)) {
			ScrollEvent evt = ScrollEvent.getScrollEvent(request);
			double curpos = evt.getPosInDouble();
			_curpos = curpos > _minpos ? curpos : _minpos;
			Events.postEvent(evt);
		} else if (Events.ON_SCROLLING.equals(cmd)) {
			ScrollEvent evt = ScrollEvent.getScrollEvent(request);
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		if (!"horizontal".equals(_orient))
			renderer.render("orient", _orient);
		if (!"{0}".equals(_slidingtext))
			renderer.render("slidingtext", _slidingtext);
		if (_curpos != 0)
			renderer.render("curpos", _curpos);
		if (_maxpos != 100)
			renderer.render("maxpos", _maxpos);
		if (_minpos != 0)
			renderer.render("minpos", _minpos);
		if (_pginc >= 0)
			renderer.render("pageIncrement", _pginc);
		if (_step > 0)
			renderer.render("step", _step);
		if (_name != null)
			renderer.render("name", _name);
		if (!INTEGER.equals(_mode))
			renderer.render("mode", _mode);
	}
}