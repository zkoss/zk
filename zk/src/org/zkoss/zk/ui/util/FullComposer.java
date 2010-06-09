/* FullComposer.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 23 12:27:17     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

/**
 * A decorative interface used with {@link Composer} (and/or {@link ComposerExt})
 * to indicate that it requires the full control.
 * By full control we mean, in addition to the component it is associated,
 * the method of {@link Composer}/{@link ComposerExt} will be called
 * when composing every its child component.
 *
 * <p>For example,
 * <pre></code>
 *&lt;window apply="FooComposer"&gt;
 *  &lt;div&gt;
 *    &lt;textbox/&gt;
 *  &lt;/div&gt;
 *&lt;/window&gt;
 * </code></pre>
 *
 * <p>If FooComposer implements both {@link Composer} and {@link FullComposer},
 * then its {@link Composer#doAfterCompose} is called after textbox, div
 * and window are composed.
 *
 * <p>If FooComposer implements only {@link Composer},
 * its {@link Composer#doAfterCompose} is called only after window is composed.
 *
 * @author tomyeh
 * @since 3.6.1
 */
public interface FullComposer {
}
