/* App.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Jan  1 20:58:50     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.prefs;

/**
 * The application.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.28 $ $Date: 2006/05/29 04:27:24 $
 */
public interface App {
	/** Returns the application name; never null. It retrieves the name in
	 * metainfo/i3app.xml as the application.
	 *
	 * <p>Default: [unknown]
	 *
	 * @see #getCodeName
	 * @see #getDescription
	 */
	public String getName();
	/** Sets the application name.
	 */
	public void setName(String name);
	/** Returns the code name of the application.
	 * The code name is used to access the preferences, while {@link #getName}
	 * returns the more self-describled name.
	 *
	 * <p>For example, {@link Apps} loads custom-app.properties from
	 * the i3/custom directory, if any, where the code name is assumed
	 * to be i3.
	 *
	 * <p>Default: i3
	 */
	public String getCodeName();
	/** Sets the code name of the application.
	 */
	public void setCodeName(String name);
	/** Returns the description about the application (never null).
	 */
	public String getDescription();
	/** Sets the description about the application.
	 */
	public void setDescription(String description);

	/** Returns the security realm of the application.
	 *
	 * <p>Default: i3
	 */
	public String getSecurityRealm();
	/** Sets the security realm of the application.
	 */
	public void setSecurityRealm(String realm);

	/** Returns the application version (never null).
	 *
	 * <p>Default: 1.0
	 */
	public String getVersion();
	/** Sets the application version.
	 */
	public void setVersion(String version);
}
