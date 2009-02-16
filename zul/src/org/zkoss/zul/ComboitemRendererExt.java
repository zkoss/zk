/* ComboitemRendererExt.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 27, 2007 11:27:33 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Provides additional control to {@link ComboitemRenderer}.
 * @author jumperchen
 *
 */
public interface ComboitemRendererExt {
	/** Creates an instance of {@link Comboitem} for rendering.
	 * The created component will be passed to {@link ComboitemRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Comboitem#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default comboitem is created as follow.
<pre><code>
final Comboitem item = new Comboitem();
item.applyProperties();
return item;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Comboitem#setParent}.
	 * 
	 * @return the comboitem if you'd like to create it differently, or null
	 * if you want {@link Comboitem} to create it for you
	 */
	public Comboitem newComboitem(Combobox combobox);
}
