/* PropertyExpression.java

	Purpose:
		
	Description:
		
	History:
		12:25 PM 9/7/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.PropertyAccess;

/**
 * An implementation of component property expression to evaluate it with a reflection
 * method call instead of EL resolving.
 * <p>Note that this class is used for Data Binding internal only</p>
 * @author jumperchen
 * @since 8.0.0
 */
public class PropertyExpression implements ExpressionX, java.io.Serializable {
	private Component cmp;
	private String field;

	public PropertyExpression(Component cmp, String field) {
		this.cmp = cmp;
		this.field = field;
	}

	public Object evaluate(XelContext ctx) throws XelException {
		try {
			if (cmp instanceof ComponentCtrl) {
				PropertyAccess propertyAccess = ((ComponentCtrl) cmp)
						.getPropertyAccess(field);
				if (propertyAccess != null)
					return propertyAccess.getValue(cmp);
			}
			return Fields.get(cmp, field);
		} catch (NoSuchMethodException e) {
			if (cmp instanceof DynamicPropertied) {
				return ((DynamicPropertied) cmp).getDynamicProperty(field);
			}
			return null;
		}
	}

	public boolean isReadOnly(XelContext ctx) throws XelException {
		return false;
	}

	public void setValue(XelContext ctx, Object value) throws XelException {
		try {
			if (cmp instanceof ComponentCtrl) {
				PropertyAccess propertyAccess = ((ComponentCtrl) cmp)
						.getPropertyAccess(field);
				if (propertyAccess != null) {
					propertyAccess.setValue(cmp, Classes.coerce(propertyAccess.getType(), value));
					return;// done
				}
			}

			Fields.set(cmp, field, value, true);
		} catch (NoSuchMethodException e) {
			if (cmp instanceof DynamicPropertied) {
				((DynamicPropertied) cmp).setDynamicProperty(field, value);
			}
		}
	}

	public String getExpressionString() {
		return "self." + field;
	}

	public Class getType(XelContext xelc) {
		return Object.class;
	}

	public ValueReference getValueReference(XelContext xelc) {
		return null;
	}
}