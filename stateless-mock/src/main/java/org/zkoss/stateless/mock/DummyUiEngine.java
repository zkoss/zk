/* DummyUiEngine.java

	Purpose:
		
	Description:
		
	History:
		10:17 AM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.mock;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.zkoss.json.JSONArray;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.metainfo.NodeInfo;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.AbortingReason;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.UiEngine;

/**
 * Mock {@link UiEngine} implementation
 * @author jumperchen
 */
public class DummyUiEngine implements UiEngine {
	public void start(WebApp webApp) {

	}

	public void stop(WebApp webApp) {

	}

	public void desktopDestroyed(Desktop desktop) {

	}

	public Component setOwner(Component component) {
		return null;
	}

	public boolean isInvalidated(Component component) {
		return false;
	}

	public void addInvalidate(Page page) {

	}

	public void addInvalidate(Component component) {

	}

	public void addSmartUpdate(Component component, String s, Object o,
			boolean b) {

	}

	public void addSmartUpdate(Component component, String s, Object o, int i) {

	}

	public void clearSmartUpdate(Component comp) {

	}

	public void addResponse(AuResponse auResponse) {

	}

	public void addResponse(String s, AuResponse auResponse) {

	}

	public void addResponse(String s, AuResponse auResponse, int i) {

	}

	public void addMoved(Component component, Component component1, Page page,
			Page page1) {

	}

	public void addUuidChanged(Component component) {

	}

	public void execNewPage(Execution execution, PageDefinition pageDefinition,
			Page page, Writer writer) throws IOException {

	}

	public void execNewPage(Execution execution, Richlet richlet, Page page,
			Writer writer) throws IOException {

	}

	public void recycleDesktop(Execution execution, Page page, Writer writer)
			throws IOException {

	}

	public void execUpdate(Execution execution, List<AuRequest> list,
			AuWriter auWriter) throws IOException {

	}

	public Object startUpdate(Execution execution) throws IOException {
		return null;
	}

	public JSONArray finishUpdate(Object o) throws IOException {
		return null;
	}

	public JSONArray finishUpdate(Object ctx, List<Throwable> errs)
			throws IOException {
		return null;
	}

	public void closeUpdate(Object o) throws IOException {

	}

	public void execRecover(Execution execution,
			FailoverManager failoverManager) {

	}

	public Component[] createComponents(Execution execution,
			PageDefinition pageDefinition, Page page, Component component,
			Component component1, VariableResolver variableResolver,
			Map<?, ?> map) {
		return new Component[0];
	}

	public void sendRedirect(String s, String s1) {

	}

	public void setAbortingReason(AbortingReason abortingReason) {

	}

	public void wait(Object o)
			throws InterruptedException, SuspendNotAllowedException {

	}

	public void notify(Object o) {

	}

	public void notify(Desktop desktop, Object o) {

	}

	public void notifyAll(Object o) {

	}

	public void notifyAll(Desktop desktop, Object o) {

	}

	public void activate(Execution execution) {

	}

	public boolean activate(Execution execution, int i) {
		return false;
	}

	public void deactivate(Execution execution) {

	}

	public void beginUpdate(Execution execution) {

	}

	public void endUpdate(Execution execution) throws IOException {

	}

	public String getNativeContent(Component component, List<NodeInfo> list,
			Native.Helper helper) {
		return null;
	}

	public boolean hasSuspendedThread() {
		return false;
	}

	public Collection<EventProcessingThread> getSuspendedThreads(
			Desktop desktop) {
		return null;
	}

	public boolean ceaseSuspendedThread(Desktop desktop,
			EventProcessingThread eventProcessingThread, String s) {
		return false;
	}

	public boolean disableClientUpdate(Component component, boolean b) {
		return false;
	}
}
