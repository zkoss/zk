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
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zel.ValueExpression;
import org.zkoss.zel.impl.util.MessageFactory;
import org.zkoss.zel.impl.util.ReflectionUtil;


public final class ValueExpressionLiteral extends ValueExpression implements
        Externalizable {

    private static final long serialVersionUID = 1L;

    private Object value;

    private Class<?> expectedType;

    public ValueExpressionLiteral() {
        super();
    }

    public ValueExpressionLiteral(Object value, Class<?> expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    
    public Object getValue(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        Object result;
        if (this.expectedType != null) {
            result = context.convertToType(this.value, this.expectedType);
        } else {
            result = this.value;
        }
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    
    public void setValue(ELContext context, Object value) {
        context.notifyBeforeEvaluation(getExpressionString());
        throw new PropertyNotWritableException(MessageFactory.get(
                "error.value.literal.write", this.value));
    }

    
    public boolean isReadOnly(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return true;
    }

    
    public Class<?> getType(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        Class<?> result = (this.value != null) ? this.value.getClass() : null;
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    
    public String getExpressionString() {
        return (this.value != null) ? this.value.toString() : null;
    }

    
    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral && this
                .equals((ValueExpressionLiteral) obj));
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return (ve != null && (this.value != null && ve.value != null && (this.value == ve.value || this.value
                .equals(ve.value))));
    }

    
    public int hashCode() {
        return (this.value != null) ? this.value.hashCode() : 0;
    }

    
    public boolean isLiteralText() {
        return true;
    }

    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF((this.expectedType != null) ? this.expectedType.getName()
                : "");
    }

    
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}
