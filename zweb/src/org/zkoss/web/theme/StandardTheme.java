/* StandardTheme.java

	Purpose:
		
	Description:
		
	History:
		Mar 14, 2013 05:47:00 PM, Created by neillee

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.theme;

import java.util.Comparator;

/**
 * A standard implementation of Theme.
 * 
 * @author neillee
 * @since 6.5.2
 */
public final class StandardTheme extends Theme {

	/**
	 * Used to specify the origin of theme resources (e.g. CSS, Images)
	 * @since 6.5.2
	 */	
	public static enum ThemeOrigin {
		JAR, FOLDER
	}
	
	/**
	 * Name used to identify the default theme
	 */
	public final static String DEFAULT_NAME    = "breeze";
	/**
	 * Name used to display the default theme
	 */
	public final static String DEFAULT_DISPLAY = "Breeze";
	/**
	 * Priority of the default theme
	 */
	public final static int DEFAULT_PRIORITY   = 500;
	/**
	 * Origin of the default theme.
	 * For default desktop theme, it is inside zul.jar/zkex.jar/zkmax.jar.
	 * For default tablet theme, it is inside zkmax.jar.
	 */
	public final static ThemeOrigin DEFAULT_ORIGIN  = ThemeOrigin.JAR;
	
	// Name used to display the theme
	private String _displayName;
	// Used in theme selection process
	private int _priority;
	// Location to retrieve theme resources such as CSS files and images
	private ThemeOrigin _origin;
		
	/**
	 * Instantiate a default theme
	 */
	public StandardTheme() {
		super(DEFAULT_NAME);
	}
	
	/**
	 * Instantiate a standard theme
	 * 
	 * @param themeName name used to identify the theme
	 */
	public StandardTheme(String themeName) {
		this(themeName, themeName, DEFAULT_PRIORITY, DEFAULT_ORIGIN);
	}

	/**
	 * Instantiate a standard theme
	 * 
	 * @param themeName name used to identify the theme
	 * @param origin where the theme resource is located, jar or folder
	 */
	public StandardTheme(String themeName, ThemeOrigin origin) {
		this(themeName, themeName, DEFAULT_PRIORITY, origin);
	}
	
	/**
	 * Instantiate a standard theme
	 * 
	 * @param themeName name used to identify the theme
	 * @param displayName name used to display the theme
	 * @param priority used to choose which theme to use, if no theme is 
	 * specified, and no preferred theme is set. Higher priority themes are 
	 * chosen over the lower priority themes. Lower the priority value, higher
	 * the priority.
	 */
	public StandardTheme(String themeName, String displayName, int priority) {
		this(themeName, displayName, priority, DEFAULT_ORIGIN);
	}
	
	/**
	 * Instantiate a standard theme
	 * 
	 * @param themeName name used to identify the theme
	 * @param displayName name used to display the theme
	 * @param priority used to choose which theme to use, if no theme is 
	 * specified, and no preferred theme is set. Higher priority themes are 
	 * chosen over the lower priority themes. Lower the priority value, higher
	 * the priority. 
	 * @param origin where the theme resources (i.e. CSS and images) are located
	 */
	public StandardTheme(String themeName, String displayName, int priority, ThemeOrigin origin) {
		if (!"".equals(themeName)) {
			super.setName(themeName);
			_displayName = displayName;
			_priority = priority;
			_origin = origin;
		} else
			throw new IllegalArgumentException("Standard themes should not have blank names");
	}
	
	/**
	 * @return the name used to display the theme 
	 */
	public String getDisplayName() {
		return _displayName;
	}
	
	/**
	 * Rename the display name
	 * @param displayName the new name used to display the theme
	 */
	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	/**
	 * @return the priority of the theme
	 */
	public int getPriority() {
		return _priority; 
	}
	
	/**
	 * Adjust the priority of the theme
	 * 
	 * @param priority the new priority of the theme
	 */
	public void setPriority(int priority) {
		_priority = priority;
	}

	/**
	 * @return the origin of the theme resource
	 */
	public ThemeOrigin getOrigin() {
		return _origin;
	}
	
	private static final Comparator<StandardTheme> _COMPARATOR = new Comparator<StandardTheme>() {

		@Override
		public int compare(StandardTheme t1, StandardTheme t2) {
			if (t1 == null) {
				if (t2 == null)
					return 0;
				else
					return 1;
			} else if (t2 == null) {
				return -1;
			} else
				return t1._priority - t2._priority;		
		}

	};
	
	/**
	 * Defines the ordering of standard themes. Higher priority themes are
	 * ordered before the lower priority themes.
	 *  
	 * @return the comparator based on theme's priority values
	 */
	public static Comparator<StandardTheme> getComparator() {
		return _COMPARATOR;
	}
	
}
