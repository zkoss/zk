/* ZKApplicationImpl.java
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
package org.zkoss.seam.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
/**
 * This is a delegate class for Application. You should not use this class directly.<br/>
 * @author Dennis.Chen
 *
 */
/**package**/ class ZKApplicationImpl extends Application {

    protected Application application;

    public Application getDelegate() {
        return application;
    }

    public ZKApplicationImpl(Application app) {
        this.application = app;
    }

    @Override
    public void addComponent(String componentType, String componentClass) {
        application.addComponent(componentType, componentClass);
    }

    @Override
    public void addConverter(String converterId, String converterClass) {
        application.addConverter(converterId, converterClass);
    }

    @Override
    public void addConverter(Class targetClass, String converterClass) {
        application.addConverter(targetClass, converterClass);
    }

    @Override
    public void addValidator(String validatorId, String validatorClass) {
        application.addValidator(validatorId, validatorClass);
    }

    @Override
    public UIComponent createComponent(String componentType)
            throws FacesException {
        return application.createComponent(componentType);
    }

    @Override
    public UIComponent createComponent(ValueBinding componentBinding,
            FacesContext context, String componentType) throws FacesException {
        return application.createComponent(componentBinding, context,
                componentType);
    }

    @Override
    public Converter createConverter(String converterId) {
        /*
          if ( Contexts.isApplicationContextActive() ) { String name =
          Init.instance().getConverters().get(converterId); if (name!=null) {
          return (Converter) Component.getInstance(name); } }
         */
        return application.createConverter(converterId);
    }

    @Override
    public Converter createConverter(Class targetClass) {
        /*
          if ( Contexts.isApplicationContextActive() ) { String name =
          Init.instance().getConvertersByClass().get(targetClass); if
          (name!=null) { return (Converter) Component.getInstance(name); } }
         */
        return application.createConverter(targetClass);
    }

    @Override
    public MethodBinding createMethodBinding(String expression, Class[] params)
            throws ReferenceSyntaxException {
        return application.createMethodBinding(expression, params);
        /*
          if (params!=null && params.length>0) { //TODO: if (
          paramTypes.length==1 && FacesEvent.class.isAssignableFrom(
          paramTypes[0] ) ) // return new
          OptionalParamMethodBinding(expression,params) return
          application.createMethodBinding(expression, params); } else { return
          new ActionParamMethodBinding(application, expression); }
         */
    }

    @Override
    public Validator createValidator(String validatorId) throws FacesException {
        /*
        if (Contexts.isApplicationContextActive()) {
            String name = Init.instance().getValidators().get(validatorId);
            if (name != null) {
                return (Validator) Component.getInstance(name);
            }
        }
        */
        return application.createValidator(validatorId);
    }

    @Override
    public ValueBinding createValueBinding(String ref)
            throws ReferenceSyntaxException {
        return application.createValueBinding(ref);
    }

    @Override
    public ActionListener getActionListener() {
        return application.getActionListener();
    }

    @Override
    public Iterator getComponentTypes() {
        return application.getComponentTypes();
    }

    @Override
    public Iterator getConverterIds() {
        return application.getConverterIds();
    }

    @Override
    public Iterator getConverterTypes() {
        return application.getComponentTypes();
    }

    @Override
    public Locale getDefaultLocale() {
        return application.getDefaultLocale();
    }

    @Override
    public String getDefaultRenderKitId() {
        return application.getDefaultRenderKitId();
    }

    @Override
    public String getMessageBundle() {
        return application.getMessageBundle();
    }

    @Override
    public NavigationHandler getNavigationHandler() {
        return application.getNavigationHandler();
    }

    @Override
    public PropertyResolver getPropertyResolver() {
        return application.getPropertyResolver();
    }

    @Override
    public StateManager getStateManager() {
        return application.getStateManager();
    }

    @Override
    public Iterator getSupportedLocales() {
        return application.getSupportedLocales();
    }

    @Override
    public Iterator getValidatorIds() {
        return application.getValidatorIds();
    }

    @Override
    public VariableResolver getVariableResolver() {
        return application.getVariableResolver();
    }

    @Override
    public ViewHandler getViewHandler() {
        return application.getViewHandler();
    }

    @Override
    public void setActionListener(ActionListener listener) {
        application.setActionListener(listener);
    }

    @Override
    public void setDefaultLocale(Locale locale) {
        application.setDefaultLocale(locale);
    }

    @Override
    public void setDefaultRenderKitId(String renderKitId) {
        application.setDefaultRenderKitId(renderKitId);
    }

    @Override
    public void setMessageBundle(String bundle) {
        application.setMessageBundle(bundle);
    }

    @Override
    public void setNavigationHandler(NavigationHandler handler) {
        application.setNavigationHandler(handler);
    }

    @Override
    public void setPropertyResolver(PropertyResolver resolver) {
        application.setPropertyResolver(resolver);
    }

    @Override
    public void setStateManager(StateManager manager) {
        application.setStateManager(manager);
    }

    @Override
    public void setSupportedLocales(Collection locales) {
        application.setSupportedLocales(locales);
    }

    @Override
    public void setVariableResolver(VariableResolver resolver) {
        application.setVariableResolver(resolver);
    }

    @Override
    public void setViewHandler(ViewHandler handler) {
        application.setViewHandler(handler);
    }
}
