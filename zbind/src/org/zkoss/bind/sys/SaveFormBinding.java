/* SaveFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 2:53:18 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;


/**
 * Binding for saving a form.
 * @author henrichen
 *
 */
public interface SaveFormBinding extends FormBinding, SaveBinding {
	/**
	 * Returns {@link Property}s to be validated, includes the member field of the form
	 * @param ctx the binding runtime context
	 * @return {@link Property} to be validated.
	 */
	public Set<Property> getValidates(BindContext ctx);
}
