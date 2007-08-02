/* DelegatingExpressionEvaluator.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
    Jul 25, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

import org.apache.commons.el.ExpressionEvaluatorImpl;

/**package**/ class DelegatingEexpressionEvaluator extends ExpressionEvaluator {

    ExpressionEvaluator impl;
    public DelegatingEexpressionEvaluator(){
        impl = new ExpressionEvaluatorImpl();
    }
    
    @Override
    public Object evaluate(String expression, Class expectedType, VariableResolver vResolver, FunctionMapper fMapper) throws ELException {
        //System.out.println("evaluate=====>["+expression+"]");
        return impl.evaluate(expression,expectedType,vResolver,fMapper);
    }

    @Override
    public Expression parseExpression(String expression, Class expectedType, FunctionMapper fMapper) throws ELException {
        //System.out.println("parseExpression=====>["+expression+"]");
        return impl.parseExpression(expression,expectedType,fMapper);
    }

}
