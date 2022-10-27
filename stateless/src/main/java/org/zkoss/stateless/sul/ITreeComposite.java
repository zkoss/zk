/* ITreeComposite.java

	Purpose:
		
	Description:
		
	History:
		2:54 PM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

/**
 * Represents a composition of {@link IComponent} onto {@link ITree}.
 * @author jumperchen
 */
public interface ITreeComposite<I extends ITreeComposite> extends IMeshComposite<I> {
}
