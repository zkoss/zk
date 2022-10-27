/* ITreeitemComposite.java

	Purpose:
		
	Description:
		
	History:
		3:40 PM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

/**
 * Represents a composition of {@link IComponent} onto {@link ITreeitem}.
 * @author jumperchen
 */
public interface ITreeitemComposite<I extends ITreeitemComposite> extends IComponent<I> {
}
