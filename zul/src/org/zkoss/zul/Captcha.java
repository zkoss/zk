/* Captcha.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 15 10:03:48     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.impl.CaptchaEngine;
import org.zkoss.image.AImage;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;

import java.awt.Font;
import java.awt.Image;
import java.awt.Color;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * The generic captcha component. 
 * @author henrichen
 */
public class Captcha extends org.zkoss.zul.Image {
	//control variable
	private boolean _smartDrawCaptcha; //whether post the smartDraw event already?
	private transient EventListener _smartDrawCaptchaListener; //the smartDrawListner
	
	private static Random _random = new Random();//random used for various operation
	private static final String EXCLUDE = "0123456789IOilo"; //default exclude list
	private static final int CHAR_START = '0'; //character start
	private static final int CHAR_END = 'z'; //character end
	private static final int CHAR_COUNT = CHAR_END - CHAR_START + 1; //charcater count
	private static final Font[] DEFAULT_FONTS = new Font[] {
		new Font("Arial", Font.BOLD, 35),
		new Font("Courier", Font.BOLD, 35)			
	};
	
	private int _intWidth = 200; //integer width in px
	private int _intHeight = 50; //integer height in px
	
	private String _fontColor = "#404040"; //font color that used to draw text
	private int _fontRGB = 0x404040; //font color in 0xRRGGBB
	
	private String _bgColor = "#74979B"; //background color in #RRGGBB form
	private int _bgRGB = 0x74979B; //background color in 0xRRGGBB
	
	private List _fonts = new ArrayList(9); //fonts that can be used to draw text
	private int _len = 5; //text length, default 5
	private String _exclude = null;
	private String _value; //captcha text value 
	private boolean _noise = true; //whether generate noise
	private CaptchaEngine _engine; //the captcha engine that generate the distortion image.

	public Captcha() {
		setWidth("200px");
		setHeight("50px");
		randomValue();
		smartDrawCaptcha();
	}
	
	/**
	 * Gets fonts list, default provide two fonts.
	 */
	public List getFonts() {
		return _fonts;
	}
	
	/**
	 * Gets the default font list.
	 */
	public Font[] getDefaultFonts() {
		return DEFAULT_FONTS;
	}
	
	/**
	 * Get nth Font.
	 */
	public Font getFont(int j) {
		if (_fonts.isEmpty()) {
			return DEFAULT_FONTS[j];
		}

		return (Font) _fonts.get(j);
	}
	
	/**
	 * Add fonts into fonts list. If you did not add fonts, the default implementation
	 * would use the default fonts; i.e. bold Arial 35, and bold courier 35.
	 */
	public void addFont(Font font) {
		_fonts.add(font);
	}

	/**
	 * Set font color.
	 */
	public void setFontColor(String color) {
		if (Objects.equals(color, _fontColor)) {
			return;
		}
		_fontColor = color;
		if (_fontColor == null) {
			_fontRGB = 0;
		} else {
			_fontRGB = decode(_fontColor);
		}
		smartDrawCaptcha();
	}
	
	/**
	 * Gets font color.
	 */
	public String getFontColor() {
		return _fontColor;
	}
	
	/**
	 * Get the font color in int array (0: red, 1: green, 2:blue).
	 */
	public int getFontRGB() {
		return _fontRGB;
	}

	/**
	 * Set the background color of the chart.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setBgColor(String color) {
		if (Objects.equals(color, _bgColor)) {
			return;
		}
		_bgColor = color;
		if (_bgColor == null) {
			_bgRGB = 0;
		} else {
			_bgRGB = decode(_bgColor);
		}
		smartDrawCaptcha();
	}
	
	/**
	 * Get the background color of the captcha box (in string as #RRGGBB).
	 * null means default.
	 */
	public String getBgColor() {
		return _bgColor;
	}
	
	/**
	 * Get the background color in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int getBgRGB() {
		return _bgRGB;
	}

	/**
	 * Override super class to prepare the int width.
	 */
	public void setWidth(String w) {
		if (Objects.equals(w, getWidth())) {
			return;
		}
		_intWidth = Chart.stringToInt(w);
		super.setWidth(w);

		smartDrawCaptcha();
	}
	
	/**
	 * Get the captcha int width in pixel; to be used by the derived subclass.
	 */
	public int getIntWidth() {
		return _intWidth;
	}
	
	/**
	 * Override super class to prepare the int height.
	 */
	public void setHeight(String h) {
		if (Objects.equals(h, getHeight())) {
			return;
		}
		_intHeight = Chart.stringToInt(h);
		super.setHeight(h);

		smartDrawCaptcha();
	}
	
	/**
	 * Get the captcha int height in pixel; to be used by the derived subclass.
	 */
	public int getIntHeight() {
		return _intHeight;
	}
	
	/**
	 * Get the text value of this captcha.
	 */
	public String getValue() {
		return _value;
	}
	
	/**
	 * Set the text value to be shown as the distortion captcha.
	 * @param text the captcha text value
	 */
	public void setValue(String text) throws WrongValueException {
		if (!Objects.equals(text, _value)) {
			if (Strings.isBlank(text))
				throw new WrongValueException("empty not allowed");
			_value = text;
			smartDrawCaptcha();
		}
	}
	
	/** Set length of the autogenerated text value; default to 5.
	 */
	public void setLength(int len) {
		if (len == _len) {
			return;
		}
		_len = len;

		randomValue();
		smartDrawCaptcha();
	}
	
	/** Get length of the autogenerated text value; default to 5.
	 */
	public int getLength() {
		return _len;
	}
	
	/** Set exclude characters that will not be generated. Note that only digit and character is used
	 * in generating text value. If you leave exclude null, the default exclude list will be applied; 
	 * i.e.,  0123456789IilOo (only character (no digits) are used except I, i, l, O(big O), o(small o))
	 */
	public void setExclude(String exclude) {
		if (Objects.equals(_exclude, exclude))
			return;
			
		_exclude = exclude;

		randomValue();
		smartDrawCaptcha();
	}
	
	/** Get exclude characters.
	 */
	public String getExclude() {
		return _exclude;
	}
	
	/** Wheather generate noise; default to true.
	 */
	public void setNoise(boolean b) {
		_noise = b;
	}
	
	/** Whether generate noise; default to true.
	 */
	public boolean isNoise() {
		return _noise;
	}
	
	/**
	 * Regenerates new captcha text value and redraw.
	 */
	public String randomValue() {
		String exclude = _exclude == null ? EXCLUDE : _exclude;
		int len = _len;
				
		final StringBuffer sb = new StringBuffer(len);
		while (len > 0) {
			final char c = (char) ('0' + _random.nextInt(CHAR_COUNT)); // ASCII '0' to 'z'
			if (Character.isLetterOrDigit(c) && exclude.indexOf((int)c) < 0) {
				sb.append(c); 
				--len;
			}
		}
		setValue(sb.toString());
		return getValue();
	}
	
	/** Sets the captcha engine by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setEngine(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setEngine((CaptchaEngine)Classes.newInstanceByThread(clsnm));
		}
	}

	/**
	 * Set the captcha engine.
	 */
	public void setEngine(CaptchaEngine engine) {
		if (_engine != engine) {
			_engine = engine;
		}
		
		smartDrawCaptcha();
	}
	
	/**
	 * Get the captcha engine.
	 *
	 * @exception UiException if failed to load the engine.
	 */
	public CaptchaEngine getCaptchaEngine()
	throws UiException {
		if (_engine == null)
			_engine = newCaptchaEngine();
		return _engine;
	}
	/** Instantiates the default captcha engine.
	 * It is called, if {@link #setEngine} is not called with non-null
	 * engine.
	 *
	 * <p>By default, it looks up the component attribute called
	 * captcha-engine. If found, the value is assumed to be the class
	 * or the class name of the default engine (it must implement
	 * {@link CaptchaEngine}.
	 * If not found, {@link UiException} is thrown.
	 *
	 * <p>Derived class might override this method to provide your
	 * own default class.
	 *
	 * @exception UiException if failed to instantiate the engine
	 * @since 3.0.0
	 */
	protected CaptchaEngine newCaptchaEngine() throws UiException {
		Object v = getAttribute("captcha-engine");
		if (v == null)
			v = "org.zkoss.zkex.zul.impl.JHLabsCaptchaEngine";

		try {
			final Class cls;
			if (v instanceof String) {
				cls = Classes.forNameByThread((String)v);
			} else if (v instanceof Class) {
				cls = (Class)v;
			} else {
				throw new UiException(v != null ? "Unknown captcha-engine, "+v:
					"The captcha-engine attribute is not defined");
			}
	
			v = cls.newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		if (!(v instanceof CaptchaEngine))
			throw new UiException(CaptchaEngine.class + " must be implemented by "+v);
		return (CaptchaEngine)v;
	}

	/**
	 * mark a draw flag to inform that this Chart needs update.
	 */
	protected void smartDrawCaptcha() {
		if (_smartDrawCaptcha) { //already mark smart draw
			return;
		}
		_smartDrawCaptcha = true;
		if (_smartDrawCaptchaListener == null) {
			_smartDrawCaptchaListener = new EventListener() {
				public void onEvent(Event event) {
					if (Strings.isBlank(getWidth()))
						throw new UiException("captcha must specify width");
									
					if (Strings.isBlank(getHeight()))
						throw new UiException("captcha must specify height");
								
					try {
						//generate the distorted image based on the given text value
						byte[] bytes = getCaptchaEngine().generateCaptcha(Captcha.this);
						final AImage image = new AImage("captcha"+new Date().getTime(), bytes);
						setContent(image);
					} catch(java.io.IOException ex) {
						throw UiException.Aide.wrap(ex);
					} finally {
						_smartDrawCaptcha = false;
					}
				}
			};
			addEventListener("onSmartDrawCaptcha", _smartDrawCaptchaListener);
		}
		Events.postEvent("onSmartDrawCaptcha", this, null);
	}
	
	/*package*/ static int decode(String color) {
		if (color == null) {
			return 0;
		}
		if (color.length() != 7 || !color.startsWith("#")) {
			throw new UiException("Incorrect color format (#RRGGBB) : "+color);
		}
		return Integer.parseInt(color.substring(1), 16);
	}
}		
