/* SimpleForm

	Purpose:
		
	Description:
		
	History:
		2011/10/25 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import org.zkoss.bind.impl.FormProxy;
/**
 * A simple implementation of {@link Form}
 * @author dennis
 * @since 6.0.0
 */
public class SimpleForm<T> extends FormProxy<T> {
	public SimpleForm() {
		super(null);
	}
	public SimpleForm(Binder binder) {
		super(binder);
	}

	private static final long serialVersionUID = 1463169907348730644L;
}
