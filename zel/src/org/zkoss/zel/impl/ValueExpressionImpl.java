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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.FunctionMapper;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zel.ValueExpression;
import org.zkoss.zel.ValueReference;
import org.zkoss.zel.VariableMapper;
import org.zkoss.zel.impl.lang.ELSupport;
import org.zkoss.zel.impl.lang.EvaluationContext;
import org.zkoss.zel.impl.lang.ExpressionBuilder;
import org.zkoss.zel.impl.parser.AstLiteralExpression;
import org.zkoss.zel.impl.parser.Node;
import org.zkoss.zel.impl.util.ReflectionUtil;


/**
 * An <code>Expression</code> that can get or set a value.
 * 
 * <p>
 * In previous incarnations of this API, expressions could only be read.
 * <code>ValueExpression</code> objects can now be used both to retrieve a
 * value and to set a value. Expressions that can have a value set on them are
 * referred to as l-value expressions. Those that cannot are referred to as
 * r-value expressions. Not all r-value expressions can be used as l-value
 * expressions (e.g. <code>"${1+1}"</code> or
 * <code>"${firstName} ${lastName}"</code>). See the EL Specification for
 * details. Expressions that cannot be used as l-values must always return
 * <code>true</code> from <code>isReadOnly()</code>.
 * </p>
 * 
 * <p>
 * <code>The {@link org.zkoss.zel.ExpressionFactory#createValueExpression} method
 * can be used to parse an expression string and return a concrete instance
 * of <code>ValueExpression</code> that encapsulates the parsed expression.
 * The {@link FunctionMapper} is used at parse time, not evaluation time, 
 * so one is not needed to evaluate an expression using this class.  
 * However, the {@link ELContext} is needed at evaluation time.</p>
 *
 * <p>The {@link #getValue}, {@link #setValue}, {@link #isReadOnly} and
 * {@link #getType} methods will evaluate the expression each time they are
 * called. The {@link org.zkoss.zel.ELResolver} in the <code>ELContext</code> is used
 * to resolve the top-level variables and to determine the behavior of the
 * <code>.</code> and <code>[]</code> operators. For any of the four methods,
 * the {@link org.zkoss.zel.ELResolver#getValue} method is used to resolve all
 * properties up to but excluding the last one. This provides the
 * <code>base</code> object. At the last resolution, the
 * <code>ValueExpression</code> will call the corresponding
 * {@link org.zkoss.zel.ELResolver#getValue}, {@link org.zkoss.zel.ELResolver#setValue},
 * {@link org.zkoss.zel.ELResolver#isReadOnly} or {@link org.zkoss.zel.ELResolver#getType}
 * method, depending on which was called on the <code>ValueExpression</code>.
 * </p>
 *
 * <p>See the notes about comparison, serialization and immutability in 
 * the {@link org.zkoss.zel.Expression} javadocs.
 *
 * @see org.zkoss.zel.ELResolver
 * @see org.zkoss.zel.Expression
 * @see org.zkoss.zel.ExpressionFactory
 * @see org.zkoss.zel.ValueExpression
 * 
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Id: ValueExpressionImpl.java 1026769 2010-10-24 11:55:10Z markt $
 */
public class ValueExpressionImpl extends ValueExpression implements
        Externalizable {

    private Class<?> expectedType;

    private String expr;

    private FunctionMapper fnMapper;

    private VariableMapper varMapper;

    private transient Node node;

    public ValueExpressionImpl() {
        super();
    }

    /**
     * 
     */
    public ValueExpressionImpl(String expr, Node node, FunctionMapper fnMapper,
            VariableMapper varMapper, Class<?> expectedType) {
        this.expr = expr;
        this.node = node;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
        this.expectedType = expectedType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionImpl && obj.hashCode() == this
                .hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#getExpectedType()
     */
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    /**
     * Returns the type the result of the expression will be coerced to after
     * evaluation.
     * 
     * @return the <code>expectedType</code> passed to the
     *         <code>ExpressionFactory.createValueExpression</code> method
     *         that created this <code>ValueExpression</code>.
     * 
     * @see org.zkoss.zel.Expression#getExpressionString()
     */
    public String getExpressionString() {
        return this.expr;
    }

    /**
     * @return Node
     * @throws ELException
     */
    private Node getNode() throws ELException {
        if (this.node == null) {
            this.node = ExpressionBuilder.createNode(this.expr);
        }
        return this.node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#getType(org.zkoss.zel.ELContext)
     */
    public Class<?> getType(ELContext context) throws PropertyNotFoundException,
            ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper,
                this.varMapper);
        return this.getNode().getType(ctx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#getValue(org.zkoss.zel.ELContext)
     */
    public Object getValue(ELContext context) throws PropertyNotFoundException,
            ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper,
                this.varMapper);
        Object value = this.getNode().getValue(ctx);
        if (this.expectedType != null) {
            return ELSupport.coerceToType(value, this.expectedType);
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.getNode().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#isLiteralText()
     */
    public boolean isLiteralText() {
        try {
            return this.getNode() instanceof AstLiteralExpression;
        } catch (ELException ele) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#isReadOnly(org.zkoss.zel.ELContext)
     */
    public boolean isReadOnly(ELContext context)
            throws PropertyNotFoundException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper,
                this.varMapper);
        return this.getNode().isReadOnly(ctx);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.expr = in.readUTF();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
        this.fnMapper = (FunctionMapper) in.readObject();
        this.varMapper = (VariableMapper) in.readObject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zkoss.zel.ValueExpression#setValue(org.zkoss.zel.ELContext,
     *      java.lang.Object)
     */
    public void setValue(ELContext context, Object value)
            throws PropertyNotFoundException, PropertyNotWritableException,
            ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper,
                this.varMapper);
        this.getNode().setValue(ctx, value);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expr);
        out.writeUTF((this.expectedType != null) ? this.expectedType.getName()
                : "");
        out.writeObject(this.fnMapper);
        out.writeObject(this.varMapper);
    }
    
    public String toString() {
        return "ValueExpression["+this.expr+"]";
    }

    /**
     * @since EL 2.2
     */
    public ValueReference getValueReference(ELContext context) {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper,
                this.varMapper);
        return this.getNode().getValueReference(ctx);
    }
    
}
