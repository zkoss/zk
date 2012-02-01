/* AbstractIterationStatus.java

	Purpose:
		
	Description:
		
	History:
		2012/1/5 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.IterationStatus;
import org.zkoss.bind.annotation.Immutable;

/**
 * The Class AbstractIterationStatus.
 *
 * @author dennis
 * @since 6.0.0
 */
@Immutable
/*package*/ abstract class AbstractIterationStatus implements IterationStatus, Serializable{
	private static final long serialVersionUID = 1L;
}
