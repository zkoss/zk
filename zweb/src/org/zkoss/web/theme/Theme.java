/* Theme.java

	Purpose:
		
	Description:
		
	History:
		Mar 14, 2013 06:25:00 PM, Created by neillee

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.theme;

/**
 * A Theme encapsulates theme-specific attributes. Each theme should have
 * at least a name, which helps the web application to identify it.
 * 
 * A Theme should be subclassed to define other attributes associated with
 * concrete themes, such as file paths included in a theme, or variables that
 * could be used to parameterize a theme.
 *  
 * @author Neil
 * @since 6.5.2
 */
public abstract class Theme {
	
	// name of the theme 
	private String _name = "";
	
	/**
	 * Instantiate a theme
	 */
	public Theme() {
	}
	
	/**
	 * Instantiate a theme with the given name
	 * @param themeName name used to identify the theme
	 */
	public Theme(String themeName) {
		setName(themeName);
	}
	
	/**
	 * @return name used to identify the theme
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Rename the theme
	 * @param themeName new name used by the theme
	 */
	public void setName(String themeName) {
		if (themeName == null) themeName = "";
		_name = themeName;
	}

	/**
	 * @return name of the theme
	 */
	@Override
	public String toString() {
		return _name;
	}
	
}
