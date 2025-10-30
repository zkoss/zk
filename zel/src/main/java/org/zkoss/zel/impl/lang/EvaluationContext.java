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

package org.zkoss.zel.impl.lang;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.EvaluationListener;
import org.zkoss.zel.FunctionMapper;
import org.zkoss.zel.ImportHandler;
import org.zkoss.zel.VariableMapper;

public final class EvaluationContext extends ELContext {

    private final ELContext elContext;

    private final FunctionMapper fnMapper;

    private final VariableMapper varMapper;

    public EvaluationContext(ELContext elContext, FunctionMapper fnMapper,
            VariableMapper varMapper) {
        this.elContext = elContext;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
    }

    public ELContext getELContext() {
        return elContext;
    }

    
    public FunctionMapper getFunctionMapper() {
        return fnMapper;
    }

    
    public VariableMapper getVariableMapper() {
        return varMapper;
    }

    
    // Can't use Class<?> because API needs to match specification in superclass
    public Object getContext(@SuppressWarnings("rawtypes") Class key) {
        return elContext.getContext(key);
    }

    
    public ELResolver getELResolver() {
        return elContext.getELResolver();
    }

    
    public boolean isPropertyResolved() {
        return elContext.isPropertyResolved();
    }

    
    // Can't use Class<?> because API needs to match specification in superclass
    public void putContext(@SuppressWarnings("rawtypes") Class key,
            Object contextObject) {
        elContext.putContext(key, contextObject);
    }

    
    public void setPropertyResolved(boolean resolved) {
        elContext.setPropertyResolved(resolved);
    }

    
    public Locale getLocale() {
        return elContext.getLocale();
        }

    
    public void setLocale(Locale locale) {
        elContext.setLocale(locale);
    }

    
    public void setPropertyResolved(Object base, Object property) {
        elContext.setPropertyResolved(base, property);
    }

    
    public ImportHandler getImportHandler() {
        return elContext.getImportHandler();
    }

    
    public void addEvaluationListener(EvaluationListener listener) {
        elContext.addEvaluationListener(listener);
    }

    
    public List<EvaluationListener> getEvaluationListeners() {
        return elContext.getEvaluationListeners();
    }

    
    public void notifyBeforeEvaluation(String expression) {
        elContext.notifyBeforeEvaluation(expression);
    }

    
    public void notifyAfterEvaluation(String expression) {
        elContext.notifyAfterEvaluation(expression);
    }

    
    public void notifyPropertyResolved(Object base, Object property) {
        elContext.notifyPropertyResolved(base, property);
    }

    
    public boolean isLambdaArgument(String name) {
        return elContext.isLambdaArgument(name);
    }

    
    public Object getLambdaArgument(String name) {
        return elContext.getLambdaArgument(name);
    }

    
    public void enterLambdaScope(Map<String, Object> arguments) {
        elContext.enterLambdaScope(arguments);
    }

    
    public void exitLambdaScope() {
        elContext.exitLambdaScope();
    }

    
    public Object convertToType(Object obj, Class<?> type) {
        return elContext.convertToType(obj, type);
    }
}
