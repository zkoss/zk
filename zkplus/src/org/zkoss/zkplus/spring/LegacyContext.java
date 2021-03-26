/* ******************************************************************
 * Copyright (c) 2003-2021 by PROSTEP AG                             *
 * All rights reserved                                               *
 *                                                                   *
 *   ___  ___  ___  _  _  ___  __   _    _                           *
 *  | _ || _ || __|| \| || _ || _ \| \  / |                          *
 *  ||_||| /_|| _/ |  ' || /_|||_ ||  ''  |                          *
 *  |___||_|  |___||_|\_||_|  |__ /|_|\/|_|                          *
 *                                                                   *
 * This software is furnished under a license and may be used and    *
 * copied only in accordance with the terms of such license and      *
 * with the inclusion of the above copyright notice. This            *
 * software or any other copies thereof may not be provided or       *
 * otherwise made available to a third person. No title to and       *
 * ownership of the software is hereby transferred.                  *
 *                                                                   *
 * The information in this software is subject to change without     *
 * notice and should not be construed as a commitment by ProSTEP     *
 *                                                                   *
 * The PROSTEP AG assumes only responsibility defined in a contract  *
 * and no responsibility for the use or reliability of its software  *
 * on equipment which is not supplied by the PROSTEP AG.             *
 *                                                                   *
 *********************************************************************/

//
// Author      : dkraemer
// Start       : 24.03.2021
//
package org.zkoss.zkplus.spring;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author dkraemer
 *
 */
public class LegacyContext implements ServletContext {

    private final jakarta.servlet.ServletContext _delegate;

    /**
     * @param servletContext
     */
    public LegacyContext(jakarta.servlet.ServletContext servletContext) {
        _delegate = servletContext;
    }

    @Override
    public ServletContext getContext(String uripath) {
        return new LegacyContext(_delegate.getContext(uripath));
    }

    @Override
    public int getMajorVersion() {
        return _delegate.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {

        return _delegate.getMinorVersion();
    }

    @Override
    public String getMimeType(String file) {

        return _delegate.getMimeType(file);
    }

    @Override
    public Set getResourcePaths(String path) {

        return _delegate.getResourcePaths(path);
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {

        return _delegate.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {

        return _delegate.getResourceAsStream(path);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {

        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {

        return null;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {

        return null;
    }

    @Override
    public Enumeration getServlets() {

        return null;
    }

    @Override
    public Enumeration getServletNames() {

        return _delegate.getServletNames();
    }

    @Override
    public void log(String msg) {
        _delegate.log(msg);

    }

    @Override
    public void log(Exception exception, String msg) {
        _delegate.log(exception, msg);

    }

    @Override
    public void log(String message, Throwable throwable) {
        _delegate.log(message, throwable);

    }

    @Override
    public String getRealPath(String path) {

        return _delegate.getRealPath(path);
    }

    @Override
    public String getServerInfo() {

        return _delegate.getServerInfo();
    }

    @Override
    public String getInitParameter(String name) {

        return _delegate.getInitParameter(name);
    }

    @Override
    public Enumeration getInitParameterNames() {

        return _delegate.getInitParameterNames();
    }

    @Override
    public Object getAttribute(String name) {

        return _delegate.getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {

        return _delegate.getAttributeNames();
    }

    @Override
    public void setAttribute(String name, Object object) {
        _delegate.setAttribute(name, object);

    }

    @Override
    public void removeAttribute(String name) {
        _delegate.removeAttribute(name);

    }

    @Override
    public String getServletContextName() {
        return _delegate.getServletContextName();
    }

}
