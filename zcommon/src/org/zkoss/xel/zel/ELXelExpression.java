/* ELXelExpression.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 10:42:35     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.xel.zel;

import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ValueExpression;

/**
 * An XEL Expression that is based on ZEL ValueExpression.
 *
 * @author henrichen
 * @since 6.0.0
 */
public class ELXelExpression implements ExpressionX, java.io.Serializable {
	private static final long serialVersionUID = 5843639871525015820L;
	protected final ValueExpression _expr;

	public ELXelExpression(ValueExpression expr) {
		_expr = expr;
	}

	public Object evaluate(XelContext xelc)
	throws XelException {
		return _expr.getValue(newELContext(xelc));
	}

	public boolean isReadOnly(XelContext xelc)
	throws XelException {
		return _expr.isReadOnly(newELContext(xelc));
	}

	public void setValue(XelContext xelc, Object value) 
	throws XelException {
		_expr.setValue(newELContext(xelc), value);
	}

	public String getExpressionString() {
		return _expr.getExpressionString();
	}
	
	public Class getType(XelContext xelc) {
		return _expr.getType(newELContext(xelc));
	}
	
	public ValueReference getValueReference(XelContext xelc) {
		final org.zkoss.zel.ValueReference vr = _expr.getValueReference(newELContext(xelc));
		return vr==null?null:new ValueReferenceImpl(vr.getBase(), vr.getProperty());
	}
	
	protected ELContext newELContext(XelContext xelc) {
		return new XelELContext(xelc);
	}
	
	public static class ValueReferenceImpl implements ValueReference, java.io.Serializable {
	    private static final long serialVersionUID = 201109141039L;
	    
	    private final Object _base;
	    private final Object _property;
	    
	    public ValueReferenceImpl(Object base, Object property) {
	        this._base = base;
	        this._property = property;
	    }

	    public Object getBase() {
	        return _base;
	    }
	    
	    public Object getProperty() {
	        return _property;
	    }
	}
}
