/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zkoss.zel.impl;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.ExpressionFactory;
import org.zkoss.zel.MethodExpression;
import org.zkoss.zel.ValueExpression;
import org.zkoss.zel.impl.lang.ELSupport;
import org.zkoss.zel.impl.lang.ExpressionBuilder;
import org.zkoss.zel.impl.stream.StreamELResolverImpl;
import org.zkoss.zel.impl.util.MessageFactory;


/**
 * @see org.zkoss.zel.ExpressionFactory
 *
 * @author Jacob Hookom [jacob@hookom.net]
 */
public class ExpressionFactoryImpl extends ExpressionFactory {

    /**
     *
     */
    public ExpressionFactoryImpl() {
        super();
    }

    
    public Object coerceToType(Object obj, Class<?> type) {
        return ELSupport.coerceToType(obj, type);
    }

    
    public MethodExpression createMethodExpression(ELContext context,
            String expression, Class<?> expectedReturnType,
            Class<?>[] expectedParamTypes) {
        ExpressionBuilder builder = newExpressionBuilder(expression, context);
        return builder.createMethodExpression(expectedReturnType,
                expectedParamTypes);
    }

    
    public ValueExpression createValueExpression(ELContext context,
            String expression, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory
                    .get("error.value.expectedType"));
        }
        ExpressionBuilder builder = newExpressionBuilder(expression, context);
        return builder.createValueExpression(expectedType);
    }

    
    public ValueExpression createValueExpression(Object instance,
            Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory
                    .get("error.value.expectedType"));
        }
        return new ValueExpressionLiteral(instance, expectedType);
    }

    
    public ELResolver getStreamELResolver() {
        return new StreamELResolverImpl();
    }
    
    //20110815, Henri Chen: allow override node visiting (see ExpressionBuilder#accept)
    protected ExpressionBuilder newExpressionBuilder(String expression, ELContext context) {
    	return new ExpressionBuilder(expression, context);
    }
}
