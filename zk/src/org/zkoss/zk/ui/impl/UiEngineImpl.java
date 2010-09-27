/* UiEngineImpl.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  9 13:05:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import java.util.regex.Pattern;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletRequest;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Threads;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;
import org.zkoss.mesg.Messages;
import org.zkoss.util.ArraysX;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.json.*;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.sys.*;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.metainfo.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.xel.Evaluators;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;

/**
 * An implementation of {@link UiEngine} to create and update components.
 *
 * @author tomyeh
 */
public class UiEngineImpl implements UiEngine {
	/*package*/ static final Log log = Log.lookup(UiEngineImpl.class);

	/** The Web application this engine belongs to. */
	private WebApp _wapp;
	/** A pool of idle EventProcessingThreadImpl. */
	private final List<EventProcessingThreadImpl> _idles = new LinkedList<EventProcessingThreadImpl>();
	/** A map of suspended processing:
	 * (Desktop desktop, IdentityHashMap(Object mutex, List(EventProcessingThreadImpl)).
	 */
	private final Map<Desktop, Map<Object, List<EventProcessingThreadImpl>>> _suspended = new HashMap<Desktop, Map<Object, List<EventProcessingThreadImpl>>>();
	/** A map of resumed processing
	 * (Desktop desktop, List(EventProcessingThreadImpl)).
	 */
	private final Map<Desktop, List<EventProcessingThreadImpl>> _resumed = new HashMap<Desktop, List<EventProcessingThreadImpl>>();
	/** # of suspended event processing threads.
	 */
	private int _suspCnt;

	public UiEngineImpl() {
	}

	//-- UiEngine --//
	public void start(WebApp wapp) {
		_wapp = wapp;
	}
	public void stop(WebApp wapp) {
		synchronized (_idles) {
			for (EventProcessingThreadImpl thread: _idles)
				thread.cease("Stop application");
			_idles.clear();
		}

		synchronized (_suspended) {
			for (Map<Object, List<EventProcessingThreadImpl>> map: _suspended.values()) {
				synchronized (map) {
					for (List<EventProcessingThreadImpl> threads: map.values()) {
						for (EventProcessingThreadImpl thread: threads)
							thread.cease("Stop application");
					}
				}
			}
			_suspended.clear();
		}
		synchronized (_resumed) {
			for (List<EventProcessingThreadImpl> threads: _resumed.values()) {
				synchronized (threads) {
					for (EventProcessingThreadImpl thread: threads)
						thread.cease("Stop application");
				}
			}
			_resumed.clear();
		}
	}
	public boolean hasSuspendedThread() {
		if (!_suspended.isEmpty()) {
			synchronized (_suspended) {
				for (Iterator it = _suspended.values().iterator(); it.hasNext();) {
					final Map map = (Map)it.next();
					if (!map.isEmpty())
						return true;
				}
			}
		}
		return false;
	}
		
	public Collection<EventProcessingThread> getSuspendedThreads(Desktop desktop) {
		final Map<Object, List<EventProcessingThreadImpl>> map;
		synchronized (_suspended) {
			map = _suspended.get(desktop);
		}

		if (map == null || map.isEmpty())
			return Collections.emptyList();

		final List<EventProcessingThread> threads = new LinkedList<EventProcessingThread>();
		synchronized (map) {
			for (List<EventProcessingThreadImpl> thds: map.values()) {
				threads.addAll(thds);
			}
		}
		return threads;
	}
	public boolean ceaseSuspendedThread(Desktop desktop,
	EventProcessingThread evtthd, String cause) {
		final Map<Object, List<EventProcessingThreadImpl>> map;
		synchronized (_suspended) {
			map = _suspended.get(desktop);
		}
		if (map == null) return false;

		boolean found = false;
		synchronized (map) {
			for (Iterator<Map.Entry<Object, List<EventProcessingThreadImpl>>>
			it = map.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<Object, List<EventProcessingThreadImpl>> me = it.next();
				final List<EventProcessingThreadImpl> list = me.getValue();
				found = list.remove(evtthd); //found
				if (found) {
					if (list.isEmpty())
						it.remove(); //(mutex, list) no longer useful
					break; //DONE
				}
			}
		}
		if (found)
			((EventProcessingThreadImpl)evtthd).cease(cause);
		return found;
	}

	public void desktopDestroyed(Desktop desktop) {
//		if (log.debugable()) log.debug("destroy "+desktop);

		Execution exec = Executions.getCurrent();
		if (exec == null) {
			//Bug 2015878: exec is null if it is caused by session invalidated
			//while listener (ResumeAbort and so) might need it
			exec = new PhantomExecution(desktop);
			activate(exec);
			try {
				desktopDestroyed0(desktop);
			} finally {
				deactivate(exec);
			}
		} else {
			desktopDestroyed0(desktop);
		}
	}
	private void desktopDestroyed0(Desktop desktop) {
		final Configuration config = _wapp.getConfiguration();
		final Map map;
		synchronized (_suspended) {
			map = (Map)_suspended.remove(desktop);
		}
		if (map != null) {
			synchronized (map) {
				for (Iterator it = map.values().iterator(); it.hasNext();) {
					final List list = (List)it.next();
					for (Iterator i2 = list.iterator(); i2.hasNext();) {
						final EventProcessingThreadImpl evtthd =
							(EventProcessingThreadImpl)i2.next();
						evtthd.ceaseSilently("Destroy desktop "+desktop);
						config.invokeEventThreadResumeAborts(
							evtthd.getComponent(), evtthd.getEvent());
					}
				}
			}
		}

		final List list;
		synchronized (_resumed) {
			list = (List)_resumed.remove(desktop);
		}
		if (list != null) {
			synchronized (list) {
				for (Iterator it = list.iterator(); it.hasNext();) {
					final EventProcessingThreadImpl evtthd =
						(EventProcessingThreadImpl)it.next();
					evtthd.ceaseSilently("Destroy desktop "+desktop);
					config.invokeEventThreadResumeAborts(
						evtthd.getComponent(), evtthd.getEvent());
				}
			}
		}

		((DesktopCtrl)desktop).destroy();
	}

	private static UiVisualizer getCurrentVisualizer() {
		final ExecutionCtrl execCtrl = ExecutionsCtrl.getCurrentCtrl();
		if (execCtrl == null)
			throw new IllegalStateException("Components can be accessed only in event listeners");
		return (UiVisualizer)execCtrl.getVisualizer();
	}
	public Component setOwner(Component comp) {
		return getCurrentVisualizer().setOwner(comp);
	}
	public boolean isInvalidated(Component comp) {
		return getCurrentVisualizer().isInvalidated(comp);
	}
	public void addInvalidate(Page page) {
		if (page == null)
			throw new IllegalArgumentException();
		getCurrentVisualizer().addInvalidate(page);
	}
	public void addInvalidate(Component comp) {
		if (comp == null)
			throw new IllegalArgumentException();
		getCurrentVisualizer().addInvalidate(comp);
	}
	public void addSmartUpdate(Component comp, String attr, Object value, boolean append) {
		if (comp == null)
			throw new IllegalArgumentException();
		getCurrentVisualizer().addSmartUpdate(comp, attr, value, append);
	}
	public void addResponse(AuResponse response) {
		getCurrentVisualizer().addResponse(response);
	}
	public void addResponse(String key, AuResponse response) {
		getCurrentVisualizer().addResponse(key, response);
	}
	public void addMoved(Component comp, Component oldparent, Page oldpg, Page newpg) {
		if (comp == null)
			throw new IllegalArgumentException();
		getCurrentVisualizer().addMoved(comp, oldparent, oldpg, newpg);
	}
	public void addUuidChanged(Component comp) {
		if (comp == null)
			throw new IllegalArgumentException();
		getCurrentVisualizer().addUuidChanged(comp);
	}
	public boolean disableClientUpdate(Component comp, boolean disable) {
		return getCurrentVisualizer().disableClientUpdate(comp, disable);
	}

	//-- Creating a new page --//
	public void execNewPage(Execution exec, Richlet richlet, Page page,
	Writer out) throws IOException {
		execNewPage0(exec, null, richlet, page, out);
	}
	public void execNewPage(Execution exec, PageDefinition pagedef,
	Page page, Writer out) throws IOException {
		execNewPage0(exec, pagedef, null, page, out);
	}
	/** It assumes exactly one of pagedef and richlet is not null. */
	private void execNewPage0(final Execution exec, final PageDefinition pagedef,
	final Richlet richlet, final Page page, final Writer out) throws IOException {
		//Update the device type first. If this is the second page and not
		//belonging to the same device type, an exception is thrown
		final Desktop desktop = exec.getDesktop();
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		final LanguageDefinition langdef = //default page
			pagedef != null ? pagedef.getLanguageDefinition():
			richlet != null ? richlet.getLanguageDefinition(): null;
		if (langdef != null)
			desktop.setDeviceType(langdef.getDeviceType()); //set and check!

		final WebApp wapp = desktop.getWebApp();
		final Configuration config = wapp.getConfiguration();
		PerformanceMeter pfmeter = config.getPerformanceMeter();
		final long startTime = pfmeter != null ? System.currentTimeMillis(): 0;
				//snapshot time since activate might take time

		//It is possible this method is invoked when processing other exec
		final Execution oldexec = Executions.getCurrent();
		final ExecutionCtrl oldexecCtrl = (ExecutionCtrl)oldexec;
		final UiVisualizer olduv =
			oldexecCtrl != null ? (UiVisualizer)oldexecCtrl.getVisualizer(): null;

		final UiVisualizer uv;
		if (olduv != null) {
			uv = doReactivate(exec, olduv);
			pfmeter = null; //don't count included pages
		} else {
			uv = doActivate(exec, false, false, null);
		}

		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		final Page old = execCtrl.getCurrentPage();
		final PageDefinition olddef = execCtrl.getCurrentPageDefinition();
		execCtrl.setCurrentPage(page);
		execCtrl.setCurrentPageDefinition(pagedef);

		final String pfReqId =
			pfmeter != null ? meterLoadStart(pfmeter, exec, startTime): null;
		AbortingReason abrn = null;
		boolean cleaned = false;
		try {
			config.invokeExecutionInits(exec, oldexec);
			desktopCtrl.invokeExecutionInits(exec, oldexec);

			if (olduv != null) {
				final Component owner = olduv.getOwner();
				if (owner != null) {
					((PageCtrl)page).setOwner(owner);
//					if (D.ON && log.finerable()) log.finer("Set owner of "+page+" to "+owner);
				}
			}

			//Cycle 1: Creates all components

			//Note:
			//1) stylesheet, tablib are inited in Page's contructor
			//2) we add variable resolvers before init because
			//init's zscirpt might depend on it.
			if (pagedef != null) {
				((PageCtrl)page).preInit();
				pagedef.initXelContext(page);

				final Initiators inits = Initiators.doInit(pagedef, page);
					//Request 1472813: sendRedirect in init; test: sendRedirectNow.zul
				try {
					pagedef.init(page, !uv.isEverAsyncUpdate() && !uv.isAborting());

					final Component[] comps;
					final String uri = pagedef.getForwardURI(page);
					if (uri != null) {
						comps = new Component[0];
						exec.forward(uri);
					} else {
						comps = uv.isAborting() || exec.isVoided() ? null:
							execCreate(new CreateInfo(
								((WebAppCtrl)wapp).getUiFactory(), exec, page,
								config.getComposer(page)),
							pagedef, null);
					}

					inits.doAfterCompose(page, comps);
					afterCreate(comps);
				} catch(Throwable ex) {
					if (!inits.doCatch(ex))
						throw UiException.Aide.wrap(ex);
				} finally {
					inits.doFinally();
				}
			} else {
				//FUTURE: a way to allow richlet to set page ID
				((PageCtrl)page).preInit();
				((PageCtrl)page).init(new PageConfig() {
					public String getId() {return null;}
					public String getUuid() {return null;}
					public String getTitle() {return null;}
					public String getStyle() {return null;}
					public String getBeforeHeadTags() {return "";}
					public String getAfterHeadTags() {return "";}
					public Collection<Object[]> getResponseHeaders() {return Collections.emptyList();}
				});
				final Composer composer = config.getComposer(page);
				try {
					richlet.service(page);

					for (Component root = page.getFirstRoot(); root != null;
					root = root.getNextSibling()) {
						if (composer != null)
							composer.doAfterCompose(root);
						afterCreate(new Component[] {root});
							//root's next sibling might be changed
					}
				} catch (Throwable t) {
					if (composer instanceof ComposerExt)
						if (((ComposerExt)composer).doCatch(t))
							t = null; //ignored
					if (t != null)
						throw t;
				} finally {
					if (composer instanceof ComposerExt)
						((ComposerExt)composer).doFinally();
				}
			}
			if (exec.isVoided())
				return; //don't generate any output

			//Cycle 2: process pending events
			//Unlike execUpdate, execution is aborted here if any exception
			Event event = nextEvent(uv);
			do {
				for (; event != null; event = nextEvent(uv))
					process(desktop, event);
				resumeAll(desktop, uv, null);
			} while ((event = nextEvent(uv)) != null);

			//Cycle 2a: Handle aborting reason
			abrn = uv.getAbortingReason();
			if (abrn != null)
				abrn.execute(); //always execute even if !isAborting

			//Cycle 3: Redraw the page (and responses)
			List<AuResponse> responses = uv.getResponses();

			if (olduv != null && olduv.addToFirstAsyncUpdate(responses))
				responses = null;
				//A new ZK page might be included by an async update
				//(example: ZUL's include).
				//If so, we cannot generate the responses in the page.
				//Rather, we shall add them to the async update.
			else
				execCtrl.setResponses(responses);

			redrawNewPage(page, out);
		} catch (Throwable ex) {
			cleaned = true;
			final List<Throwable> errs = new LinkedList<Throwable>();
			errs.add(ex);

			desktopCtrl.invokeExecutionCleanups(exec, oldexec, errs);
			config.invokeExecutionCleanups(exec, oldexec, errs);
				//CONSIDER: whether to pass cleanup's error to users

			if (!errs.isEmpty()) {
				ex = (Throwable)errs.get(0);
				if (ex instanceof IOException) throw (IOException)ex;
				throw UiException.Aide.wrap(ex);
			}
		} finally {
			if (!cleaned) {
				desktopCtrl.invokeExecutionCleanups(exec, oldexec, null);
				config.invokeExecutionCleanups(exec, oldexec, null);
				//CONSIDER: whether to pass cleanup's error to users
			}
			if (abrn != null) {
				try {
					abrn.finish();
				} catch (Throwable t) {
					log.warning(t);
				}
			}

			execCtrl.setCurrentPage(old); //restore it
			execCtrl.setCurrentPageDefinition(olddef); //restore it

			if (olduv != null) doDereactivate(exec, olduv);
			else doDeactivate(exec);

			if (pfmeter != null)
				meterLoadServerComplete(pfmeter, pfReqId, exec);
		}
	}
	public void recycleDesktop(Execution exec, Page page, Writer out)
	throws IOException {
		PerformanceMeter pfmeter = page.getDesktop().getWebApp().getConfiguration().getPerformanceMeter();
		final long startTime = pfmeter != null ? System.currentTimeMillis(): 0;
		final String pfReqId =
			pfmeter != null ? meterLoadStart(pfmeter, exec, startTime): null;

		final UiVisualizer uv = doActivate(exec, false, false, null);
		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		execCtrl.setCurrentPage(page);
		try {
			Events.postEvent(new Event(Events.ON_DESKTOP_RECYCLE));

			final Desktop desktop = exec.getDesktop();
			Event event = nextEvent(uv);
			do {
				for (; event != null; event = nextEvent(uv))
					process(desktop, event);
				resumeAll(desktop, uv, null);
			} while ((event = nextEvent(uv)) != null);

			execCtrl.setResponses(uv.getResponses());

			((PageCtrl)page).redraw(out);
		} finally {
			doDeactivate(exec);
			if (pfmeter != null)
				meterLoadServerComplete(pfmeter, pfReqId, exec);
		}
	}
	/** Called after the whole component tree has been created by
	 * this engine.
	 * <p>Default: does nothing.
	 * <p>Derived class might override this method to process the components
	 * if necessary.
	 * @param comps the components being created. It is never null but
	 * it might be a zero-length array.
	 * @since 5.0.4
	 */
	protected void afterCreate(Component[] comps) {
	}
	/** Called to render the result of a page to the (HTTP) output,
	 * when a new page is created and processed.
	 * <p>Default: it invokes {@link PageCtrl#redraw}.
	 * <p>Notice that it is called in the rendering phase (the last phase),
	 * so it is not allowed to post events or to invoke invalidate or smartUpdate
	 * in this method.
	 * <p>Notice that it is not called if an old page is redrawn.
	 * @since 5.0.4
	 * @see #execNewPage
	 */
	protected void redrawNewPage(Page page, Writer out) throws IOException {
		((PageCtrl)page).redraw(out);
	}

	private static final Event nextEvent(UiVisualizer uv) {
		final Event evt = ((ExecutionCtrl)uv.getExecution()).getNextEvent();
		return evt != null && !uv.isAborting() ? evt: null;
	}

	/** Cycle 1:
	 * Creates all child components defined in the specified definition.
	 * @return the first component being created.
	 */
	private static final Component[] execCreate(
	CreateInfo ci, NodeInfo parentInfo, Component parent) {
		String fulfillURI = null;
		if (parentInfo instanceof ComponentInfo) {
			final ComponentInfo pi = (ComponentInfo)parentInfo;
			String fulfill = pi.getFulfill();
			if (fulfill != null) { //defer the creation of children
				fulfill = fulfill.trim();
				if (fulfill.length() > 0) {
					if (fulfill.startsWith("=")) {
						fulfillURI = fulfill.substring(1).trim();
					} else {
						new FulfillListener(fulfill, pi, parent);
						return new Component[0];
					}
				}
			}
		}

		Component[] cs = execCreate0(ci, parentInfo, parent);

		if (fulfillURI != null) {
			fulfillURI = (String)Evaluators.evaluate(
				((ComponentInfo)parentInfo).getEvaluator(),
				parent, fulfillURI, String.class);
			if (fulfillURI != null) {
				final Component c =
					ci.exec.createComponents(fulfillURI, parent, null);
				if (c != null) {
					cs = (Component[])ArraysX.resize(cs, cs.length + 1);
					cs[cs.length - 1] = c;
				}
			}
		}

		return cs;
	}
	private static final Component[] execCreate0(
	CreateInfo ci, NodeInfo parentInfo, Component parent) {
		final List<Component> created = new LinkedList<Component>();
		final Page page = ci.page;
		final PageDefinition pagedef = parentInfo.getPageDefinition();
			//note: don't use page.getDefinition because createComponents
			//might be called from a page other than instance's
		final ReplaceableText replaceableText = new ReplaceableText();
		for (Iterator it = parentInfo.getChildren().iterator(); it.hasNext();) {
			final Object meta = it.next();
			if (meta instanceof ComponentInfo) {
				final ComponentInfo childInfo = (ComponentInfo)meta;
				final ForEach forEach = childInfo.resolveForEach(page, parent);
				if (forEach == null) {
					if (isEffective(childInfo, page, parent)) {
						final Component[] children =
							execCreateChild(ci, parent, childInfo, replaceableText);
						for (int j = 0; j < children.length; ++j)
							created.add(children[j]);
					}
				} else {
					while (forEach.next()) {
						if (isEffective(childInfo, page, parent)) {
							final Component[] children =
								execCreateChild(ci, parent, childInfo, replaceableText);
							for (int j = 0; j < children.length; ++j)
								created.add(children[j]);
						}
					}
				}
			} else if (meta instanceof TextInfo) {
				//parent must be a native component
				final String s = ((TextInfo)meta).getValue(parent);
				if (s != null && s.length() > 0)
					parent.appendChild(
						((Native)parent).getHelper().newNative(s));
			} else {
				execNonComponent(ci, parent, meta);
			}
		}
		return created.toArray(new Component[created.size()]);
	}
	private static Component[] execCreateChild(
	CreateInfo ci, Component parent, ComponentInfo childInfo,
	ReplaceableText replaceableText) {
		if (childInfo instanceof ZkInfo) {
			final ZkInfo zkInfo = (ZkInfo)childInfo;
			return zkInfo.withSwitch() ?
				execSwitch(ci, zkInfo, parent):
				execCreate0(ci, childInfo, parent);
		}

		final ComponentDefinition childdef = childInfo.getComponentDefinition();
		if (childdef.isInlineMacro()) {
			final Map<String, Object> props = new HashMap<String, Object>();
			props.put("includer", parent);
			childInfo.evalProperties(props, ci.page, parent, true);
			return new Component[] {
				ci.exec.createComponents(childdef.getMacroURI(), parent, props)};
		} else {
			String rt = null;
			if (replaceableText != null) {
				rt = replaceableText.text;
				replaceableText.text = childInfo.getReplaceableText();;
				if (replaceableText.text != null)
					return new Component[0];
				//Note: replaceableText is one-shot only
				//So, replaceable text might not be generated
				//and it is ok since it is onl blank string
			}

			Component child = execCreateChild0(ci, parent, childInfo, rt);
			return child != null ? new Component[] {child}: new Component[0];
		}
	}
	private static Component execCreateChild0(CreateInfo ci,
	Component parent, ComponentInfo childInfo, String replaceableText) {
		Composer composer = childInfo.resolveComposer(ci.page, parent);
		ComposerExt composerExt = null;
		boolean bPopComposer = false;
		if (composer instanceof FullComposer) {
			ci.pushFullComposer(composer);
			bPopComposer = true;
			composer = null; //ci will handle it
		} else if (composer instanceof ComposerExt) {
			composerExt = (ComposerExt)composer;
		}

		Component child = null;
		final boolean bRoot = parent == null;
		try {
			if (composerExt != null) {
				childInfo = composerExt.doBeforeCompose(ci.page, parent, childInfo);
				if (childInfo == null)
					return null;
			}
			childInfo = ci.doBeforeCompose(ci.page, parent, childInfo, bRoot);
			if (childInfo == null)
				return null;

			child = ci.uf.newComponent(ci.page, parent, childInfo);

			if (replaceableText != null) {
				final Object xc = ((ComponentCtrl)child).getExtraCtrl();
				if (xc instanceof PrologAllowed)
					((PrologAllowed)xc).setPrologContent(replaceableText);
			}

			final boolean bNative = childInfo instanceof NativeInfo;
			if (bNative)
				setProlog(ci, child, (NativeInfo)childInfo);

			if (composerExt != null)
				composerExt.doBeforeComposeChildren(child);
			ci.doBeforeComposeChildren(child, bRoot);

			execCreate(ci, childInfo, child); //recursive

			if (bNative)
				setEpilog(ci, child, (NativeInfo)childInfo);

			if (child instanceof AfterCompose)
				((AfterCompose)child).afterCompose();

			if (composer != null)
				composer.doAfterCompose(child);
			ci.doAfterCompose(child, bRoot);

			ComponentsCtrl.applyForward(child, childInfo.getForward());
				//applies the forward condition
				//1) we did it after all child created, so it may reference
				//to it child (thought rarely happens)
				//2) we did it after afterCompose, so what specified
				//here has higher priority than class defined by app dev

			if (Events.isListened(child, Events.ON_CREATE, false))
				Events.postEvent(
					new CreateEvent(Events.ON_CREATE, child, ci.exec.getArg()));

			return child;
		} catch (Throwable ex) {
			boolean ignore = false;
			if (composerExt != null) {
				try {
					ignore = composerExt.doCatch(ex);
				} catch (Throwable t) {
					log.error("Failed to invoke doCatch for "+childInfo, t);
				}
			}
			if (!ignore) {
				ignore = ci.doCatch(ex, bRoot);
				if (!ignore)
					throw UiException.Aide.wrap(ex);
			}

			return child != null && child.getPage() != null ? child: null;
				//return child only if attached successfully
		} finally {
			try {
				if (composerExt != null)
					composerExt.doFinally();
				ci.doFinally(bRoot);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			} finally {
				if (bPopComposer) ci.popFullComposer();
			}
		}
	}
	/** Handles <zk switch>. */
	private static Component[] execSwitch(CreateInfo ci, ZkInfo switchInfo,
	Component parent) {
		final Page page = ci.page;
		final Object switchCond = switchInfo.resolveSwitch(page, parent);
		for (Iterator it = switchInfo.getChildren().iterator(); it.hasNext();) {
			final ZkInfo caseInfo = (ZkInfo)it.next();
			final ForEach forEach = caseInfo.resolveForEach(page, parent);
			if (forEach == null) {
				if (isEffective(caseInfo, page, parent)
				&& isCaseMatched(caseInfo, page, parent, switchCond)) {
					return execCreateChild(ci, parent, caseInfo, null);
				}
			} else {
				final List<Component> created = new LinkedList<Component>();
				while (forEach.next()) {
					if (isEffective(caseInfo, page, parent)
					&& isCaseMatched(caseInfo, page, parent, switchCond)) {
						final Component[] children =
							execCreateChild(ci, parent, caseInfo, null);
						for (int j = 0; j < children.length; ++j)
							created.add(children[j]);
						return (Component[])created.toArray(new Component[created.size()]);
							//only once (AND condition)
					}
				}
			}
		}			
		return new Component[0];
	}
	private static boolean isCaseMatched(ZkInfo caseInfo, Page page,
	Component parent, Object switchCond) {
		if (!caseInfo.withCase())
			return true; //default clause

		final Object[] caseValues = caseInfo.resolveCase(page, parent);
		for (int j = 0; j < caseValues.length; ++j) {
			if (caseValues[j] instanceof String && switchCond instanceof String) {
				final String casev = (String)caseValues[j];
				final int len = casev.length();
				if (len >= 2 && casev.charAt(0) == '/'
				&& casev.charAt(len - 1) == '/') { //regex
					if (Pattern.compile(casev.substring(1, len - 1))
					.matcher((String)switchCond).matches())
						return true;
					else
						continue;
				}
			}
			if (Objects.equals(switchCond, caseValues[j]))
				return true; //OR condition
		}
		return false;
	}
	/** Executes a non-component object, such as ZScript, AttributesInfo...
	 */
	private static final void execNonComponent(
	CreateInfo ci, Component comp, Object meta) {
		final Page page = ci.page;
		if (meta instanceof ZScript) {
			final ZScript zscript = (ZScript)meta;
			if (zscript.isDeferred()) {
				((PageCtrl)page).addDeferredZScript(comp, zscript);
					//isEffective is handled later
			} else if (isEffective(zscript, page, comp)) {
				final Scope scope =
					Scopes.beforeInterpret(comp != null ? (Scope)comp: page);
				try {
					page.interpret(zscript.getLanguage(),
						zscript.getContent(page, comp), scope);
				} finally {
					Scopes.afterInterpret();
				}
			}
		} else if (meta instanceof AttributesInfo) {
			final AttributesInfo attrs = (AttributesInfo)meta;
			if (comp != null) attrs.apply(comp); //it handles isEffective
			else attrs.apply(page);
		} else if (meta instanceof VariablesInfo) {
			final VariablesInfo vars = (VariablesInfo)meta;
			if (comp != null) vars.apply(comp); //it handles isEffective
			else vars.apply(page);
		} else {
			//Note: we don't handle ComponentInfo here, because
			//getNativeContent assumes no child component
			throw new IllegalStateException(meta+" not allowed in "+comp);
		}
	}
	private static final boolean isEffective(Condition cond,
	Page page, Component comp) {
		return comp != null ? cond.isEffective(comp): cond.isEffective(page);
	}

	public Component[] createComponents(Execution exec,
	PageDefinition pagedef, Page page, Component parent, Map arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef");

		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		boolean fakeIS = false;
		if (parent != null) {
			if (parent.getPage() != null)
				page = parent.getPage();
			else
				fakeIS = true;
			if (page == null) {
				fakeIS = true;
				page = execCtrl.getCurrentPage();
			}
		}

		if (!execCtrl.isActivated())
			throw new IllegalStateException("Not activated yet");

		final boolean fakepg = page == null;
		if (fakepg) {
			fakeIS = true;
			page = new PageImpl(pagedef);
		}

		final Desktop desktop = exec.getDesktop();
		final Page prevpg = execCtrl.getCurrentPage();
		if (page != null && page != prevpg)
			execCtrl.setCurrentPage(page);
		final PageDefinition olddef = execCtrl.getCurrentPageDefinition();
		execCtrl.setCurrentPageDefinition(pagedef);
		exec.pushArg(arg != null ? arg: Collections.EMPTY_MAP);

		//Note: we add taglib, stylesheets and var-resolvers to the page
		//it might cause name pollution but we got no choice since they
		//are used as long as components created by this method are alive
		if (fakepg) ((PageCtrl)page).preInit();
		pagedef.initXelContext(page);

		//Note: the forward directives are ignore in this case

		final Initiators inits = Initiators.doInit(pagedef, page);
		final IdSpace prevIS = fakeIS ?
			ExecutionsCtrl.setVirtualIdSpace(
				fakepg ? (IdSpace)page: new SimpleIdSpace()): null;
		try {
			if (fakepg) pagedef.init(page, false);

			final Component[] comps = execCreate(
				new CreateInfo(
					((WebAppCtrl)desktop.getWebApp()).getUiFactory(),
					exec, page, null), //technically sys composer can be used but we don't (to make it simple)
				pagedef, parent);
			inits.doAfterCompose(page, comps);

			if (fakepg)
				for (int j = 0; j < comps.length; ++j) {
					comps[j].detach();
					if (parent != null)
						parent.appendChild(comps[j]);
				}

			afterCreate(comps);
			return comps;
		} catch (Throwable ex) {
			inits.doCatch(ex);
			throw UiException.Aide.wrap(ex);
		} finally {
			exec.popArg();
			execCtrl.setCurrentPage(prevpg); //restore it
			execCtrl.setCurrentPageDefinition(olddef); //restore it
			if (fakeIS)
				ExecutionsCtrl.setVirtualIdSpace(prevIS);

			inits.doFinally();

			if (fakepg) {
				try {
					((DesktopCtrl)desktop).removePage(page);
				} catch (Throwable ex) {
					log.warningBriefly(ex);
				}
			}
		}
	}

	public void sendRedirect(String uri, String target) {
		if (uri != null && uri.length() == 0)
			uri = null;
		final UiVisualizer uv = getCurrentVisualizer();
		uv.setAbortingReason(
			new AbortBySendRedirect(
				uri != null ? uv.getExecution().encodeURL(uri): "", target));
	}
	public void setAbortingReason(AbortingReason aborting) {
		final UiVisualizer uv = getCurrentVisualizer();
		uv.setAbortingReason(aborting);
	}

	//-- Recovering desktop --//
	public void execRecover(Execution exec, FailoverManager failover) {
		final Desktop desktop = exec.getDesktop();
		final Session sess = desktop.getSession();

		doActivate(exec, false, true, null); //it must not return null
		try {
			failover.recover(sess, exec, desktop);
		} finally {
			doDeactivate(exec);
		}
	}

	//-- Asynchronous updates --//
	public void beginUpdate(Execution exec) {
		final UiVisualizer uv = doActivate(exec, true, false, null);
		final Desktop desktop = exec.getDesktop();
		desktop.getWebApp().getConfiguration().invokeExecutionInits(exec, null);
		((DesktopCtrl)desktop).invokeExecutionInits(exec, null);
	}
	public void endUpdate(Execution exec)
	throws IOException {
		boolean cleaned = false;
		final Desktop desktop = exec.getDesktop();
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		final Configuration config = desktop.getWebApp().getConfiguration();
		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		final UiVisualizer uv = (UiVisualizer)execCtrl.getVisualizer();
		try {
			Event event = nextEvent(uv);
			do {
				for (; event != null; event = nextEvent(uv))
					process(desktop, event);
				resumeAll(desktop, uv, null);
			} while ((event = nextEvent(uv)) != null);

			desktopCtrl.piggyResponse(uv.getResponses(), false);

			cleaned = true;
			desktopCtrl.invokeExecutionCleanups(exec, null, null);
			config.invokeExecutionCleanups(exec, null, null);
		} finally {
			if (!cleaned) {
				desktopCtrl.invokeExecutionCleanups(exec, null, null);
				config.invokeExecutionCleanups(exec, null, null);
			}

			doDeactivate(exec);
		}
	}
	public void execUpdate(Execution exec, List<AuRequest> requests, AuWriter out)
	throws IOException {
		if (requests == null)
			throw new IllegalArgumentException();
		assert D.OFF || ExecutionsCtrl.getCurrentCtrl() == null:
			"Impossible to re-activate for update: old="+ExecutionsCtrl.getCurrentCtrl()+", new="+exec;

		final Desktop desktop = exec.getDesktop();
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		final Configuration config = desktop.getWebApp().getConfiguration();

		final PerformanceMeter pfmeter = config.getPerformanceMeter();
		long startTime = 0;
		if (pfmeter != null) {
			startTime = System.currentTimeMillis();
				//snapshot time since activate might take time
			meterAuClientComplete(pfmeter, exec);
		}

		final Object[] resultOfRepeat = new Object[1];
		final UiVisualizer uv = doActivate(exec, true, false, resultOfRepeat);
		if (resultOfRepeat[0] != null) {
			out.resend(resultOfRepeat[0]);
			doDeactivate(exec);
			return;
		}

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.beforeUpdate(desktop, requests);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}

		final String pfReqId =
			pfmeter != null ? meterAuStart(pfmeter, exec, startTime): null;
		Collection doneReqIds = null; //request IDs that have been processed
		AbortingReason abrn = null;
		boolean cleaned = false;
		try {
			final RequestQueue rque = desktopCtrl.getRequestQueue();
			rque.addRequests(requests);

			config.invokeExecutionInits(exec, null);
			desktopCtrl.invokeExecutionInits(exec, null);

			if (pfReqId != null) rque.addPerfRequestId(pfReqId);

			final List<Throwable> errs = new LinkedList<Throwable>();
			//Process all; ignore getMaxProcessTime();
			//we cannot handle them partially since UUID might be recycled
			for (AuRequest request; (request = rque.nextRequest()) != null;) {
				//Cycle 1: Process one request
				//Don't process more such that requests will be queued
				//and we have the chance to optimize them
				try {
					process(exec, request, !errs.isEmpty());
				} catch (Throwable ex) {
					handleError(ex, uv, errs);
					//we don't skip request to avoid mis-match between c/s
				}

				//Cycle 2: Process any pending events posted by components
				Event event = nextEvent(uv);
				do {
					for (; event != null; event = nextEvent(uv)) {
						try {
							process(desktop, event);
						} catch (Throwable ex) {
							handleError(ex, uv, errs);
							break; //skip the rest of events! 
						}
					}

					resumeAll(desktop, uv, errs);
				} while ((event = nextEvent(uv)) != null);
			}

			//Cycle 2a: Handle aborting reason
			abrn = uv.getAbortingReason();
			if (abrn != null)
				abrn.execute(); //always execute even if !isAborting

			//Cycle 3: Generate output
			List<AuResponse> responses;
			try {
				//Note: we have to call visualizeErrors before uv.getResponses,
				//since it might create/update components
				if (!errs.isEmpty())
					visualizeErrors(exec, uv, errs);

				responses = uv.getResponses();
			} catch (Throwable ex) {
				responses = new LinkedList<AuResponse>();
				responses.add(new AuAlert(Exceptions.getMessage(ex)));

				log.error(ex);
			}

			doneReqIds = rque.clearPerfRequestIds();

			List<AuResponse> prs = desktopCtrl.piggyResponse(null, true);
			if (prs != null) responses.addAll(0, prs);

			out.writeResponseId(desktopCtrl.getResponseId(true));
			out.write(responses);

//			if (log.debugable())
//				if (responses.size() < 5 || log.finerable()) log.finer("Responses: "+responses);
//				else log.debug("Responses: "+responses.subList(0, 5)+"...");

			cleaned = true;
			desktopCtrl.invokeExecutionCleanups(exec, null, errs);
			config.invokeExecutionCleanups(exec, null, errs);

			final String seqId = ((ExecutionCtrl)exec).getRequestId();
			if (seqId != null)
				desktopCtrl.responseSent(seqId, out.complete());
		} catch (Throwable ex) {
			if (!cleaned) {
				cleaned = true;
				final List<Throwable> errs = new LinkedList<Throwable>();
				errs.add(ex);
				desktopCtrl.invokeExecutionCleanups(exec, null, errs);
				config.invokeExecutionCleanups(exec, null, errs);
				ex = errs.isEmpty() ? null: (Throwable)errs.get(0);
			}

			if (ex != null) {
				if (ex instanceof IOException) throw (IOException)ex;
				throw UiException.Aide.wrap(ex);
			}
		} finally {
			if (!cleaned) {
				desktopCtrl.invokeExecutionCleanups(exec, null, null);
				config.invokeExecutionCleanups(exec, null, null);
			}

			if (abrn != null) {
				try {
					abrn.finish();
				} catch (Throwable t) {
					log.warning(t);
				}
			}
			if (monitor != null) {
				try {
					monitor.afterUpdate(desktop);
				} catch (Throwable ex) {
					log.error(ex);
				}
			}

			doDeactivate(exec);

			if (pfmeter != null && doneReqIds != null)
				meterAuServerComplete(pfmeter, doneReqIds, exec);
		}
	}
	public Object startUpdate(Execution exec) throws IOException {
		final Desktop desktop = exec.getDesktop();
		UiVisualizer uv = doActivate(exec, true, false, null);
		desktop.getWebApp().getConfiguration().invokeExecutionInits(exec, null);
		((DesktopCtrl)desktop).invokeExecutionInits(exec, null);
		return new UpdateInfo(uv);
	}
	public JSONArray finishUpdate(Object ctx) throws IOException {
		final UpdateInfo ui = (UpdateInfo)ctx;
		final Execution exec = ui.uv.getExecution();
		final Desktop desktop = exec.getDesktop();
		final List<Throwable> errs = new LinkedList<Throwable>();

		//1. process events
		Event event = nextEvent(ui.uv);
		do {
			for (; event != null; event = nextEvent(ui.uv)) {
				try {
					process(desktop, event);
				} catch (Throwable ex) {
					handleError(ex, ui.uv, errs);
					break; //skip the rest of events! 
				}
			}

			resumeAll(desktop, ui.uv, errs);
		} while ((event = nextEvent(ui.uv)) != null);

		//2. Handle aborting reason
		ui.abrn = ui.uv.getAbortingReason();
		if (ui.abrn != null)
			ui.abrn.execute(); //always execute even if !isAborting

		//3. Retrieve responses
		List<AuResponse> responses;
		try {
			if (!errs.isEmpty())
				visualizeErrors(exec, ui.uv, errs);

			responses = ui.uv.getResponses();
		} catch (Throwable ex) {
			responses = new LinkedList<AuResponse>();
			responses.add(new AuAlert(Exceptions.getMessage(ex)));

			log.error(ex);
		}

		final JSONArray rs = new JSONArray();
		for (Iterator it = responses.iterator(); it.hasNext();)
			rs.add(AuWriters.toJSON((AuResponse)it.next()));
		return rs;
	}
	public void closeUpdate(Object ctx) throws IOException {
		final UpdateInfo ui = (UpdateInfo)ctx;
		final Execution exec = ui.uv.getExecution();

		final Desktop desktop = exec.getDesktop();
		((DesktopCtrl)desktop).invokeExecutionCleanups(exec, null, null);
		desktop.getWebApp().getConfiguration().invokeExecutionCleanups(exec, null, null);

		if (ui.abrn != null) {
			try {
				ui.abrn.finish();
			} catch (Throwable t) {
				log.warning(t);
			}
		}

		doDeactivate(exec);
	}
	private static class UpdateInfo {
		private final UiVisualizer uv;
		private AbortingReason abrn;
		private UpdateInfo(UiVisualizer uv) {
			this.uv = uv;
		}
	}

	/** Handles each error. The erros will be queued to the errs list
	 * and processed later by {@link #visualizeErrors}.
	 */
	private static final
	void handleError(Throwable ex, UiVisualizer uv, List<Throwable> errs) {
		final Throwable t = Exceptions.findCause(ex, Expectable.class);
		if (t == null) {
			if (ex instanceof org.xml.sax.SAXException
			|| ex instanceof org.zkoss.zk.ui.metainfo.PropertyNotFoundException)
				log.error(Exceptions.getMessage(ex));
			else
				log.realCauseBriefly(ex);
		} else {
			ex = t;
			if (log.debugable()) log.debug(Exceptions.getRealCause(ex));
		}

		if (ex instanceof WrongValueException) {
			WrongValueException wve = (WrongValueException)ex;
			final Component comp = wve.getComponent();
			if (comp != null) {
				wve = ((ComponentCtrl)comp).onWrongValue(wve);
				if (wve != null) {
					Component c = wve.getComponent();
					if (c == null) c = comp;
					uv.addResponse(
						new AuWrongValue(c, Exceptions.getMessage(wve)));
				}
				return;
			}
		} else if (ex instanceof WrongValuesException) {
			final WrongValueException[] wves =
				((WrongValuesException)ex).getWrongValueExceptions();
			final LinkedList<String> infs = new LinkedList<String>();
			for (int i = 0; i < wves.length; i++) {
				final Component comp = wves[i].getComponent();
				if (comp != null) {
					WrongValueException wve = ((ComponentCtrl)comp).onWrongValue(wves[i]);
					if (wve != null) {
						Component c = wve.getComponent();
						if (c == null) c = comp;
						infs.add(c.getUuid());
						infs.add(Exceptions.getMessage(wve));
					}
				}
			}
			uv.addResponse(
				new AuWrongValue(infs.toArray(new String[infs.size()])));
			return;
		}

		errs.add(ex);
	}
	/** Post-process the errors to represent them to the user.
	 * Note: errs must be non-empty
	 */
	private final
	void visualizeErrors(Execution exec, UiVisualizer uv, List<Throwable> errs) {
		final StringBuffer sb = new StringBuffer(128);
		for (Throwable t: errs) {
			if (sb.length() > 0) sb.append('\n');
			sb.append(Exceptions.getMessage(t));
		}
		final String msg = sb.toString();

		final Throwable err = (Throwable)errs.get(0);
		final Desktop desktop = exec.getDesktop();
		final Configuration config = desktop.getWebApp().getConfiguration();
		final String location = config.getErrorPage(desktop.getDeviceType(), err);
		if (location != null) {
			try {
				exec.setAttribute("javax.servlet.error.message", msg);
				exec.setAttribute("javax.servlet.error.exception", err);
				exec.setAttribute("javax.servlet.error.exception_type", err.getClass());
				exec.setAttribute("javax.servlet.error.status_code", new Integer(500));
				exec.setAttribute("javax.servlet.error.error_page", location);

				//Future: consider to go thru UiFactory for the richlet
				//for the error page.
				//Challenge: how to call UiFactory.isRichlet
				final Richlet richlet = config.getRichletByPath(location);
				if (richlet != null)
					richlet.service(((ExecutionCtrl)exec).getCurrentPage());
				else
					exec.createComponents(location, null, null);

				//process pending events
				//the execution is aborted if an exception is thrown
				Event event = nextEvent(uv);
				do {
					for (; event != null; event = nextEvent(uv)) {
						try {
							process(desktop, event);
						} catch (SuspendNotAllowedException ex) {
							//ignore it (possible and reasonable)
						}
					}
					resumeAll(desktop, uv, null);
				} while ((event = nextEvent(uv)) != null);
				return; //done
			} catch (Throwable ex) {
				log.realCause("Unable to generate custom error page, "+location, ex);
			}
		}

		uv.addResponse(new AuAlert(msg)); //default handling
	}

	/** Processing the request and stores result into UiVisualizer.
	 * @param everError whether any error ever occured before processing this
	 * request.
	 */
	private void process(Execution exec, AuRequest request, boolean everError) {
//		if (log.finable()) log.finer("Processing request: "+request);

		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		execCtrl.setCurrentPage(request.getPage());
		((DesktopCtrl)request.getDesktop()).service(request, everError);
	}
	/** Processing the event and stores result into UiVisualizer. */
	private void process(Desktop desktop, Event event) {
//		if (log.finable()) log.finer("Processing event: "+event);

		final Component comp = event.getTarget();
		if (comp != null) {
			processEvent(desktop, comp, event);
		} else {
			//since an event might change the page/desktop/component relation,
			//we copy roots first
			final List<Component> roots = new LinkedList<Component>();
			for (Page page: desktop.getPages()) {
				roots.addAll(page.getRoots());
			}
			for (Component c: roots) {
				if (c.getPage() != null) //might be removed, so check first
					processEvent(desktop, c, event);
			}
		}
	}

	public void wait(Object mutex)
	throws InterruptedException, SuspendNotAllowedException {
		if (mutex == null)
			throw new IllegalArgumentException("null mutex");

		final Thread thd = Thread.currentThread();
		if (!(thd instanceof EventProcessingThreadImpl))
			throw new UiException("This method can be called only in an event listener, not in paging loading.");
//		if (log.finerable()) log.finer("Suspend "+thd+" on "+mutex);

		final EventProcessingThreadImpl evtthd = (EventProcessingThreadImpl)thd;
		evtthd.newEventThreadSuspends(mutex);
			//it may throw an exception, so process it before updating _suspended

		final Execution exec = Executions.getCurrent();
		final Desktop desktop = exec.getDesktop();

		incSuspended();

		Map<Object, List<EventProcessingThreadImpl>> map;
		synchronized (_suspended) {
			map = _suspended.get(desktop);
			if (map == null)
				_suspended.put(desktop, map = new IdentityHashMap<Object, List<EventProcessingThreadImpl>>(4));
					//note: we have to use IdentityHashMap because user might
					//use Integer or so as mutex
		}
		synchronized (map) {
			List<EventProcessingThreadImpl> list = map.get(mutex);
			if (list == null)
				map.put(mutex, list = new LinkedList<EventProcessingThreadImpl>());
			list.add(evtthd);
		}

		try {
			EventProcessingThreadImpl.doSuspend(mutex);
		} catch (Throwable ex) {
			//error recover
			synchronized (map) {
				final List list = (List)map.get(mutex);
				if (list != null) {
					list.remove(evtthd);
					if (list.isEmpty()) map.remove(mutex);
				}
			}

			if (ex instanceof InterruptedException)
				throw (InterruptedException)ex;
			throw UiException.Aide.wrap(ex, "Unable to suspend "+evtthd);
		} finally {
			decSuspended();
		}
	}
	private void incSuspended() {
		final int v = _wapp.getConfiguration().getMaxSuspendedThreads();
		synchronized (this) {
			if (v >= 0 && _suspCnt >= v)
				throw new SuspendNotAllowedException(MZk.TOO_MANY_SUSPENDED);
			++_suspCnt;
		}
	}
	private void decSuspended() {
		synchronized (this) {
			--_suspCnt;
		}
	}
	public void notify(Object mutex) {
		notify(Executions.getCurrent().getDesktop(), mutex);
	}
	public void notify(Desktop desktop, Object mutex) {
		if (desktop == null || mutex == null)
			throw new IllegalArgumentException("desktop and mutex cannot be null");

		final Map map;
		synchronized (_suspended) {
			map = (Map)_suspended.get(desktop);
		}
		if (map == null) return; //nothing to notify

		final EventProcessingThreadImpl evtthd;
		synchronized (map) {
			final List list = (List)map.get(mutex);
			if (list == null) return; //nothing to notify

			//Note: list is never empty
			evtthd = (EventProcessingThreadImpl)list.remove(0);
			if (list.isEmpty()) map.remove(mutex); //clean up
		}
		addResumed(desktop, evtthd);
	}
	public void notifyAll(Object mutex) {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new UiException("resume can be called only in processing a request");
		notifyAll(exec.getDesktop(), mutex);
	}
	public void notifyAll(Desktop desktop, Object mutex) {
		if (desktop == null || mutex == null)
			throw new IllegalArgumentException("desktop and mutex cannot be null");

		final Map<Object, List<EventProcessingThreadImpl>> map;
		synchronized (_suspended) {
			map = _suspended.get(desktop);
		}
		if (map == null) return; //nothing to notify

		final List<EventProcessingThreadImpl> list;
		synchronized (map) {
			list = map.remove(mutex);
		}
		if (list == null) return; //nothing to notify

		for (EventProcessingThreadImpl thread: list)
			addResumed(desktop, thread);
	}
	/** Adds to _resumed */
	private void addResumed(Desktop desktop, EventProcessingThreadImpl evtthd) {
//		if (log.finerable()) log.finer("Ready to resume "+evtthd);

		List<EventProcessingThreadImpl> list;
		synchronized (_resumed) {
			list = _resumed.get(desktop);
			if (list == null)
				_resumed.put(desktop, list = new LinkedList<EventProcessingThreadImpl>());
		}
		synchronized (list) {
			list.add(evtthd);
		}
	}

	/** Does the real resume.
	 * <p>Note 1: the current thread will wait until the resumed threads, if any, complete
	 * <p>Note 2: {@link #resume} only puts a thread into a resume queue in execution.
	 */
	private void resumeAll(Desktop desktop, UiVisualizer uv, List<Throwable> errs) {
		//We have to loop because a resumed thread might resume others
		for (;;) {
			final List<EventProcessingThreadImpl> list;
			synchronized (_resumed) {
				list = _resumed.remove(desktop);
				if (list == null) return; //nothing to resume; done
			}

			synchronized (list) {
				for (EventProcessingThreadImpl evtthd: list) {
					if (uv.isAborting()) {
						evtthd.ceaseSilently("Resume aborted");
					} else {
//						if (log.finerable()) log.finer("Resume "+evtthd);
						try {
							if (evtthd.doResume()) //wait it complete or suspend again
								recycleEventThread(evtthd); //completed
						} catch (Throwable ex) {
							recycleEventThread(evtthd);
							if (errs == null) {
								log.error("Unable to resume "+evtthd, ex);
								throw UiException.Aide.wrap(ex);
							}
							handleError(ex, uv, errs);
						}
					}
				}
			}
		}
	}

	/** Process an event. */
	private void processEvent(Desktop desktop, Component comp, Event event) {
		final Configuration config = desktop.getWebApp().getConfiguration();
		if (config.isEventThreadEnabled()) {
			EventProcessingThreadImpl evtthd = null;
			synchronized (_idles) {
				while (!_idles.isEmpty() && evtthd == null) {
					evtthd = _idles.remove(0);
					if (evtthd.isCeased()) //just in case
						evtthd = null;
				}
			}

			if (evtthd == null)
				evtthd = new EventProcessingThreadImpl();

			try {
				if (evtthd.processEvent(desktop, comp, event))
					recycleEventThread(evtthd);
			} catch (Throwable ex) {
				recycleEventThread(evtthd);
				throw UiException.Aide.wrap(ex);
			}
		} else { //event thread disabled
			//Note: we don't need to call proc.setup() and cleanup(),
			//since they are in the same thread
			EventProcessor proc = new EventProcessor(desktop, comp, event);
				//Note: it also checks the correctness
			List<EventThreadCleanup> cleanups = null;
			List<Throwable> errs = null;
			try {
				final List<EventThreadInit> inits = config.newEventThreadInits(comp, event);
				EventProcessor.inEventListener(true);
				if (config.invokeEventThreadInits(inits, comp, event)) //false measn ignore
					proc.process();
			} catch (Throwable ex) {
				errs = new LinkedList<Throwable>();
				errs.add(ex);
				cleanups = config.newEventThreadCleanups(comp, event, errs, false);

				if (!errs.isEmpty())
					throw UiException.Aide.wrap((Throwable)errs.get(0));
			} finally {
				EventProcessor.inEventListener(false);
				if (errs == null) //not cleanup yet
					cleanups = config.newEventThreadCleanups(comp, event, null, false);
				config.invokeEventThreadCompletes(cleanups, comp, event, errs, false);
			}
		}
	}
	private void recycleEventThread(EventProcessingThreadImpl evtthd) {
		if (!evtthd.isCeased()) {
			if (evtthd.isIdle()) {
				final int max = _wapp.getConfiguration().getMaxSpareThreads();
				synchronized (_idles) {
					if (max < 0 || _idles.size() < max) {
						_idles.add(evtthd); //return to pool
						return; //done
					}
				}
			}
			evtthd.ceaseSilently("Recycled");
		}
	}

	public void activate(Execution exec) {
		assert D.OFF || ExecutionsCtrl.getCurrentCtrl() == null:
			"Impossible to re-activate for update: old="+ExecutionsCtrl.getCurrentCtrl()+", new="+exec;
		doActivate(exec, false, false, null);
	}
	public void deactivate(Execution exec) {
		doDeactivate(exec);
	}

	//-- Common private utilities --//
	/** Activates the specified execution.
	 *
	 * @param asyncupd whether it is for asynchronous update.
	 * Note: it doesn't support if both asyncupd and recovering are true.
	 * @param recovering whether it is in recovering, i.e.,
	 * cause by {@link FailoverManager#recover}.
	 * If true, the requests argument must be null.
	 * @param resultOfRepeat a single element array to return a value, or null
	 * if it is not called by execUpdate.
	 * If a non-null value is assigned to the first element of the array,
	 * it means it is a repeated request, and the caller shall return
	 * the result directly without processing the request.
	 * @return the visualizer once the execution is granted (never null).
	 */
	private static
	UiVisualizer doActivate(Execution exec, boolean asyncupd,
	boolean recovering, Object[] resultOfRepeat) {
		if (Executions.getCurrent() != null)
			throw new IllegalStateException("Use doReactivate instead");
		assert D.OFF || !recovering || !asyncupd; 
			//Not support both asyncupd and recovering are true yet

		final Desktop desktop = exec.getDesktop();
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		final Session sess = desktop.getSession();
		final ExecutionMonitor execmon = desktop.getWebApp()
			.getConfiguration().getExecutionMonitor();
		final String seqId =
			resultOfRepeat != null ? ((ExecutionCtrl)exec).getRequestId(): null;
//		if (log.finerable()) log.finer("Activating "+desktop);

		//lock desktop
		final UiVisualizer uv;
		final Object uvlock = desktopCtrl.getActivationLock();
		synchronized (uvlock) {
			for (;;) {
				final Visualizer old = desktopCtrl.getVisualizer();
				if (old == null) break; //grantable

				if (seqId != null) {
					final String oldSeqId =
						((ExecutionCtrl)old.getExecution()).getRequestId();
					if (oldSeqId != null && !oldSeqId.equals(seqId))
						throw new RequestOutOfSequenceException(seqId, oldSeqId);
				}

				if (execmon != null)
					execmon.executionWait(exec, desktop);

				try {
					uvlock.wait(getRetryTimeout());
				} catch (InterruptedException ex) {
					if (execmon != null)
						execmon.executionAbort(exec, desktop, ex);
					throw UiException.Aide.wrap(ex);
				}
			}

			if (!desktop.isAlive())
				throw new org.zkoss.zk.ui.DesktopUnavailableException("Unable to activate destroyed desktop, "+desktop);

			//grant
			desktopCtrl.setVisualizer(uv = new UiVisualizer(exec, asyncupd, recovering));
			desktopCtrl.setExecution(exec);
		}

//		if (log.finerable()) log.finer("Activated "+desktop);

		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		ExecutionsCtrl.setCurrent(exec);
		try {
			execCtrl.onActivate();
		} catch (Throwable ex) { //just in case
			ExecutionsCtrl.setCurrent(null);
			synchronized (uvlock) {
				desktopCtrl.setVisualizer(null);
				desktopCtrl.setExecution(null);
				uvlock.notify(); //wakeup pending threads
			}
			if (execmon != null)
				execmon.executionAbort(exec, desktop, ex);
			throw UiException.Aide.wrap(ex);
		}

		if (seqId != null) {
			if (log.debugable()) {
				final Object req = exec.getNativeRequest();
				log.debug("replicate request, SID: " + seqId
					+(req instanceof ServletRequest ? "\n" + Servlets.getDetail((ServletRequest)req): ""));
			}
			resultOfRepeat[0] = desktopCtrl.getLastResponse(seqId);
		}

		if (execmon != null)
			execmon.executionActivate(exec, desktop);
		return uv;
	}

	private static Integer _retryTimeout;
	private static final int getRetryTimeout() {
		if (_retryTimeout == null) {
			int v = 0;
			final String s = Library.getProperty(Attributes.ACTIVATE_RETRY_DELAY);
			if (s != null) {
				try {
					v = Integer.parseInt(s);
				} catch (Throwable t) {
				}
			}
			_retryTimeout = new Integer(v > 0 ? v: 120*1000);
		}
		return _retryTimeout.intValue();
	}

	/** Returns whether the desktop is being recovered.
	 */
	private static final boolean isRecovering(Desktop desktop) {
		final Execution exec = desktop.getExecution();
		return exec != null && ((ExecutionCtrl)exec).isRecovering();
	}
	/** Deactivates the execution. */
	private static final void doDeactivate(Execution exec) {
//		if (log.finerable()) log.finer("Deactivating "+desktop);

		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		final Desktop desktop = exec.getDesktop();
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		try {
			//Unlock desktop
			final Object uvlock = desktopCtrl.getActivationLock();
			synchronized (uvlock) {
				desktopCtrl.setVisualizer(null);
				desktopCtrl.setExecution(null);
				uvlock.notify(); //wakeup doActivate's wait
			}
		} finally {
			try {
				execCtrl.onDeactivate();
			} catch (Throwable ex) {
				log.warningBriefly("Failed to deactive", ex);
			}
			ExecutionsCtrl.setCurrent(null);
			execCtrl.setCurrentPage(null);

			final ExecutionMonitor execmon = desktop.getWebApp()
				.getConfiguration().getExecutionMonitor();
			if (execmon != null)
				execmon.executionDeactivate(exec, desktop);
		}

		final SessionCtrl sessCtrl = (SessionCtrl)desktop.getSession();
		if (sessCtrl.isInvalidated()) sessCtrl.invalidateNow();
	}
	/** Re-activates for another execution. It is callable only for
	 * creating new page (execNewPage). It is not allowed for async-update.
	 * <p>Note: doActivate cannot handle reactivation. In other words,
	 * the caller has to detect which method to use.
	 */
	private static UiVisualizer doReactivate(Execution curExec, UiVisualizer olduv) {
		final Desktop desktop = curExec.getDesktop();
		final Session sess = desktop.getSession();
//		if (log.finerable()) log.finer("Re-activating "+desktop);

		assert D.OFF || olduv.getExecution().getDesktop() == desktop:
			"old dt: "+olduv.getExecution().getDesktop()+", new:"+desktop;

		final UiVisualizer uv = new UiVisualizer(olduv, curExec);
		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		desktopCtrl.setVisualizer(uv);
		desktopCtrl.setExecution(curExec);

		final ExecutionCtrl curCtrl = (ExecutionCtrl)curExec;
		ExecutionsCtrl.setCurrent(curExec);
		try {
			curCtrl.onActivate();
		} catch (Throwable ex) { //just in case
			ExecutionsCtrl.setCurrent(olduv.getExecution());
			desktopCtrl.setVisualizer(olduv);
			desktopCtrl.setExecution(olduv.getExecution());
			throw UiException.Aide.wrap(ex);
		}
		return uv;
	}
	/** De-reactivated exec. Work with {@link #doReactivate}.
	 */
	private static void doDereactivate(Execution curExec, UiVisualizer olduv) {
		if (olduv == null) throw new IllegalArgumentException("null");

		final ExecutionCtrl curCtrl = (ExecutionCtrl)curExec;
		final Execution oldexec = olduv.getExecution();
		try {
			final Desktop desktop = curExec.getDesktop();
	//		if (log.finerable()) log.finer("Deactivating "+desktop);

			try {
				curCtrl.onDeactivate();
			} catch (Throwable ex) {
				log.warningBriefly("Failed to deactive", ex);
			}

			final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
			desktopCtrl.setVisualizer(olduv);
			desktopCtrl.setExecution(oldexec);
		} finally {
			ExecutionsCtrl.setCurrent(oldexec);
			curCtrl.setCurrentPage(null);
		}
	}

	//Handling Native Component//
	/** Sets the prolog of the specified native component.
	 */
	private static final
	void setProlog(CreateInfo ci, Component comp, NativeInfo compInfo) {
		final Native nc = (Native)comp;
		final Native.Helper helper = nc.getHelper();
		StringBuffer sb = null;
		final List<Object> prokids = compInfo.getPrologChildren();
		if (!prokids.isEmpty()) {
			sb = new StringBuffer(256);
			getNativeContent(ci, sb, comp, prokids, helper);
		}

		final NativeInfo splitInfo = compInfo.getSplitChild();
		if (splitInfo != null && splitInfo.isEffective(comp)) {
			if (sb == null) sb = new StringBuffer(256);
			getNativeFirstHalf(ci, sb, comp, splitInfo, helper);
		}

		if (sb != null && sb.length() > 0)
			nc.setPrologContent(
				sb.insert(0, (String)nc.getPrologContent()).toString());
	}
	/** Sets the epilog of the specified native component.
	 * @param comp the native component
	 */
	private static final
	void setEpilog(CreateInfo ci, Component comp, NativeInfo compInfo) {
		final Native nc = (Native)comp;
		final Native.Helper helper = nc.getHelper();
		StringBuffer sb = null;
		final NativeInfo splitInfo = compInfo.getSplitChild();
		if (splitInfo != null && splitInfo.isEffective(comp)) {
			sb = new StringBuffer(256);
			getNativeSecondHalf(ci, sb, comp, splitInfo, helper);
		}

		final List<Object> epikids = compInfo.getEpilogChildren();
		if (!epikids.isEmpty()) {
			if (sb == null) sb = new StringBuffer(256);
			getNativeContent(ci, sb, comp, epikids, helper);
		}

		if (sb != null && sb.length() > 0)
			nc.setEpilogContent(
				sb.append(nc.getEpilogContent()).toString());
	}
	public String getNativeContent(Component comp, List<Object> children,
	Native.Helper helper) {
		final StringBuffer sb = new StringBuffer(256);
		getNativeContent(
			new CreateInfo(((WebAppCtrl)_wapp).getUiFactory(),
				Executions.getCurrent(), comp.getPage(), null),
			sb, comp, children, helper);
		return sb.toString();
	}
	/**
	 * @param comp the native component
	 */
	private static final void getNativeContent(CreateInfo ci,
	StringBuffer sb, Component comp, List<Object> children, Native.Helper helper) {
		for (Object meta: children) {
			if (meta instanceof NativeInfo) {
				final NativeInfo childInfo = (NativeInfo)meta;
				final ForEach forEach = childInfo.resolveForEach(ci.page, comp);
				if (forEach == null) {
					if (childInfo.isEffective(comp)) {
						getNativeFirstHalf(ci, sb, comp, childInfo, helper);
						getNativeSecondHalf(ci, sb, comp, childInfo, helper);
					}
				} else {
					while (forEach.next()) {
						if (childInfo.isEffective(comp)) {
							getNativeFirstHalf(ci, sb, comp, childInfo, helper);
							getNativeSecondHalf(ci, sb, comp, childInfo, helper);
						}
					}
				}
			} else if (meta instanceof TextInfo) {
				final String s = ((TextInfo)meta).getValue(comp);
				if (s != null) helper.appendText(sb, s);
			} else if (meta instanceof ZkInfo) {
				ZkInfo zkInfo = (ZkInfo)meta;
				if (zkInfo.withSwitch())
					throw new UnsupportedOperationException("<zk switch> in native not allowed yet");

				final ForEach forEach = zkInfo.resolveForEach(ci.page, comp);
				if (forEach == null) {
					if (isEffective(zkInfo, ci.page, comp)) {
						getNativeContent(ci, sb, comp,
							zkInfo.getChildren(), helper);
					}
				} else {
					while (forEach.next())
						if (isEffective(zkInfo, ci.page, comp))
							getNativeContent(ci, sb, comp,
								zkInfo.getChildren(), helper);
				}
			} else {
				execNonComponent(ci, comp, meta);
			}
		}
	}
	/** Before calling this method, childInfo.isEffective must be examined
	 */
	private static final void getNativeFirstHalf(CreateInfo ci,
	StringBuffer sb, Component comp, NativeInfo childInfo, Native.Helper helper) {
		helper.getFirstHalf(sb, childInfo.getTag(),
				evalProperties(comp, childInfo.getProperties()),
				childInfo.getDeclaredNamespaces());

		final List<Object> prokids = childInfo.getPrologChildren();
		if (!prokids.isEmpty())
			getNativeContent(ci, sb, comp, prokids, helper);

		final NativeInfo splitInfo = childInfo.getSplitChild();
		if (splitInfo != null && splitInfo.isEffective(comp))
			getNativeFirstHalf(ci, sb, comp, splitInfo, helper); //recursive
	}
	/** Before calling this method, childInfo.isEffective must be examined
	 */
	private static final void getNativeSecondHalf(CreateInfo ci,
	StringBuffer sb, Component comp, NativeInfo childInfo, Native.Helper helper) {
		final NativeInfo splitInfo = childInfo.getSplitChild();
		if (splitInfo != null && splitInfo.isEffective(comp))
			getNativeSecondHalf(ci, sb, comp, splitInfo, helper); //recursive

		final List<Object> epikids = childInfo.getEpilogChildren();
		if (!epikids.isEmpty())
			getNativeContent(ci, sb, comp, epikids, helper);

		helper.getSecondHalf(sb, childInfo.getTag());
	}
	/** Returns a map of properties, (String name, String value).
	 */
	private static final
	Map<String, Object> evalProperties(Component comp, List<Property> props) {
		if (props == null || props.isEmpty())
			return Collections.emptyMap();

		final Map<String, Object> map = new LinkedHashMap<String, Object>(props.size() * 2);
		for (Property prop: props) {
			if (prop.isEffective(comp))
				map.put(prop.getName(),
					Classes.coerce(String.class, prop.getValue(comp)));
		}
		return map;
	}

	//Supporting Classes//
	/** The listener to create children when the fulfill condition is
	 * satisfied.
	 */
	private static class FulfillListener
	implements EventListener, Express, java.io.Serializable, Cloneable,
		ComponentCloneListener {
		private String[] _evtnms;
		private Component[] _targets;
		private Component _comp;
		private final ComponentInfo _compInfo;
		private final String _fulfill;
		private transient String _uri;

		private FulfillListener(String fulfill, ComponentInfo compInfo,
		Component comp) {
			_fulfill = fulfill;
			_compInfo = compInfo;
			_comp = comp;

			init();

			for (int j = _targets.length; --j >= 0;)
				_targets[j].addEventListener(_evtnms[j], this);
		}
		private void init() {
			_uri = null;
			final List<Object[]> results = new LinkedList<Object[]>();
			for (int j = 0, len = _fulfill.length();;) {
				int k = j;
				for (int elcnt = 0; k < len; ++k) {
					final char cc = _fulfill.charAt(k);
					if (elcnt == 0) {
						if (cc == ',') break;
						if (cc == '=')  {
							_uri = _fulfill.substring(k + 1).trim();
							break;
						}
					} else if (cc == '{') {
						++elcnt;
					} else if (cc == '}') {
						if (elcnt > 0) --elcnt;
					}
				}

				String sub =
					(k >= 0 ? _fulfill.substring(j, k): _fulfill.substring(j)).trim();
				if (sub.length() > 0)
					results.add(ComponentsCtrl
						.parseEventExpression(_comp, sub, _comp, false));

				if (_uri != null || k < 0 || (j = k + 1) >= len) break;
			}

			int j = results.size();
			_targets = new Component[j];
			_evtnms = new String[j];
			j = 0;
			for (Iterator<Object[]> it = results.iterator(); it.hasNext(); ++j) {
				final Object[] result = it.next();
				_targets[j] = (Component)result[0];
				_evtnms[j] = (String)result[1];
			}
		}
		public void onEvent(Event evt) throws Exception {
			for (int j = _targets.length; --j >= 0;)
				_targets[j].removeEventListener(_evtnms[j], this); //one shot only

			final Execution exec = Executions.getCurrent();
			execCreate0(
				new CreateInfo(
					((WebAppCtrl)exec.getDesktop().getWebApp()).getUiFactory(),
					exec, _comp.getPage(), null), //technically sys composer can be used but we don't (to simplify it)
				_compInfo, _comp);

			if (_uri != null) {
				final String uri = (String)Evaluators.evaluate(
					_compInfo.getEvaluator(),
					_comp, _uri, String.class);
				if (uri != null)
					exec.createComponents(uri, _comp, null);
			}

			Events.sendEvent(new FulfillEvent(Events.ON_FULFILL, _comp, evt));
				//Use sendEvent so onFulfill will be processed before
				//the event triggers the fulfill (i.e., evt)
		}

		//ComponentCloneListener//
		public Object willClone(Component comp) {
			final FulfillListener clone;
			try {
				clone = (FulfillListener)clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
			clone._comp = comp;
			clone.init();
			return clone;
		}
	}
	private static class ReplaceableText {
		private String text;
	}

	//performance meter//
	/** Handles the client complete of AU request for performance measurement.
	 */
	private static void meterAuClientComplete(PerformanceMeter pfmeter,
	Execution exec) {
		//Format of ZK-Client-Complete and ZK-Client-Receive:
		//	request-id1=time1,request-id2=time2
		String hdr = exec.getHeader("ZK-Client-Receive");
		if (hdr != null) meterAuClient(pfmeter, exec, hdr, false);
		hdr = exec.getHeader("ZK-Client-Complete");
		if (hdr != null) meterAuClient(pfmeter, exec, hdr, true);
	}
	private static void meterAuClient(PerformanceMeter pfmeter,
	Execution exec, String hdr, boolean complete) {
		for (int j = 0;;) {
			int k = hdr.indexOf(',', j);
			String ids = k >= 0 ? hdr.substring(j, k):
				j == 0 ? hdr: hdr.substring(j);

			int x = ids.lastIndexOf('=');
			if (x > 0) {
				try {
					long time = Long.parseLong(ids.substring(x + 1));

					ids = ids.substring(0, x);
					for (int y = 0;;) {
						int z = ids.indexOf(' ', y);
						String pfReqId = z >= 0 ? ids.substring(y, z):
							y == 0 ? ids: ids.substring(y);
						if (complete)
							pfmeter.requestCompleteAtClient(pfReqId, exec, time);
						else
							pfmeter.requestReceiveAtClient(pfReqId, exec, time);

						if (z < 0) break; //done
						y = z + 1;
					}
				} catch (NumberFormatException ex) {
					log.warning("Ingored: unable to parse "+ids);
				} catch (Throwable ex) {
					if (complete)
						log.warning("Ingored: failed to invoke "+pfmeter, ex);
				}
			}

			if (k < 0) break; //done
			j = k + 1;
		}
	}
	/** Handles the client and server start of AU request
	 * for the performance measurement.
	 *
	 * @return the request ID from the ZK-Client-Start header,
	 * or null if not found.
	 */
	private static String meterAuStart(PerformanceMeter pfmeter,
	Execution exec, long startTime) {
		//Format of ZK-Client-Start:
		//	request-id=time
		String hdr = exec.getHeader("ZK-Client-Start");
		if (hdr != null) {
			final int j = hdr.lastIndexOf('=');
			if (j > 0) {
				final String pfReqId = hdr.substring(0, j);
				try {
					pfmeter.requestStartAtClient(pfReqId, exec,
						Long.parseLong(hdr.substring(j + 1)));
					pfmeter.requestStartAtServer(pfReqId, exec, startTime);
				} catch (NumberFormatException ex) {
					log.warning("Ingored: failed to parse ZK-Client-Start, "+hdr);
				} catch (Throwable ex) {
					log.warning("Ingored: failed to invoke "+pfmeter, ex);
				}
				return pfReqId;
			}
		}
		return null;
	}
	/** Handles the server complete of the AU request for the performance measurement.
	 * It sets the ZK-Client-Complete header.
	 *
	 * @param pfReqIds a collection of request IDs that are processed
	 * at the server
	 */
	private static void meterAuServerComplete(PerformanceMeter pfmeter,
	Collection pfReqIds, Execution exec) {
		final StringBuffer sb = new StringBuffer(256);
		long time = System.currentTimeMillis();
		for (Iterator it = pfReqIds.iterator(); it.hasNext();) {
			final String pfReqId = (String)it.next();
			if (sb.length() > 0) sb.append(' ');
			sb.append(pfReqId);

			try {
				pfmeter.requestCompleteAtServer(pfReqId, exec, time);
			} catch (Throwable ex) {
				log.warning("Ingored: failed to invoke "+pfmeter, ex);
			}
		}

		exec.setResponseHeader("ZK-Client-Complete", sb.toString());
			//tell the client what are completed
	}

	/** Handles the (client and) server start of load request
	 * for the performance measurement.
	 *
	 * @return the request ID
	 */
	private static String meterLoadStart(PerformanceMeter pfmeter,
	Execution exec, long startTime) {
		//Future: handle the zkClientStart paramter
		final String pfReqId = exec.getDesktop().getId();
		try {
			pfmeter.requestStartAtServer(pfReqId, exec, startTime);
		} catch (Throwable ex) {
			log.warning("Ingored: failed to invoke "+pfmeter, ex);
		}
		return pfReqId;
	}
	/** Handles the server complete of the AU request for the performance measurement.
	 * It sets the ZK-Client-Complete header.
	 *
	 * @param pfReqId the request ID that is processed at the server
	 */
	private static void meterLoadServerComplete(PerformanceMeter pfmeter,
	String pfReqId, Execution exec) {
		try {
			pfmeter.requestCompleteAtServer(pfReqId, exec, System.currentTimeMillis());
		} catch (Throwable ex) {
			log.warning("Ingored: failed to invoke "+pfmeter, ex);
		}
	}
}

/** Info used with execCreate
 */
/*package*/ class CreateInfo {
	/*package*/ final Execution exec;
	/*package*/ final Page page;
	/*package*/ final UiFactory uf;
	private List<Composer> _composers, _composerExts;
	private Composer _syscomposer;
	/*package*/ CreateInfo(UiFactory uf, Execution exec, Page page, Composer composer) {
		this.exec = exec;
		this.page = page;
		this.uf = uf;
		if (composer instanceof FullComposer)
			pushFullComposer(composer);
		else
			_syscomposer = composer;
	}

	/*package*/ void pushFullComposer(Composer composer) {
		assert composer instanceof FullComposer; //illegal state
		if (_composers == null)
			_composers = new LinkedList<Composer>();
		_composers.add(0, composer);
		if (composer instanceof ComposerExt) {
			if (_composerExts == null)
				_composerExts = new LinkedList<Composer>();
			_composerExts.add(0, composer);
		}
	}
	/*package*/ void popFullComposer() {
		Composer o = _composers.remove(0);
		if (_composers.isEmpty()) _composers = null;
		if (o instanceof ComposerExt) {
			_composerExts.remove(0);
			if (_composerExts.isEmpty()) _composerExts = null;
		}
	}

	/** Invoke setFullComposerOnly to ensure only composers that implement
	 * FullComposer are called.
	 */
	private static boolean beforeInvoke(Composer composer, boolean bRoot) {
		//If bRoot (implies system-level composer), always invoke (no setFullxxx)
		return !bRoot && composer instanceof MultiComposer &&
			((MultiComposer)composer).setFullComposerOnly(true);
	}
	private static void afterInvoke(Composer composer, boolean bRoot, boolean old) {
		if (!bRoot && composer instanceof MultiComposer)
			((MultiComposer)composer).setFullComposerOnly(old);
		}
	/*package*/ void doAfterCompose(Component comp, boolean bRoot)
	throws Exception {
		if (_composers != null)
			for (Composer composer: _composers) {
				final boolean old = beforeInvoke(composer, bRoot);

				composer.doAfterCompose(comp);

				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer != null)
			_syscomposer.doAfterCompose(comp);
	}
	/*package*/ ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo, boolean bRoot) throws Exception {
		if (_composerExts != null)
			for (Composer composer: _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);

				compInfo = ((ComposerExt)composer)
					.doBeforeCompose(page, parent, compInfo);

				afterInvoke(composer, bRoot, old);
				if (compInfo == null)
					return null;
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			compInfo = ((ComposerExt)_syscomposer)
				.doBeforeCompose(page, parent, compInfo);
		return compInfo;
	}
	/*package*/ void doBeforeComposeChildren(Component comp, boolean bRoot)
	throws Exception {
		if (_composerExts != null)
			for (Composer composer: _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);

				((ComposerExt)composer).doBeforeComposeChildren(comp);

				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			((ComposerExt)_syscomposer).doBeforeComposeChildren(comp);
	}
	/*package*/ boolean doCatch(Throwable ex, boolean bRoot) {
		if (_composerExts != null)
			for (Composer composer: _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);
				try {
					final boolean ret = ((ComposerExt)composer).doCatch(ex);
					afterInvoke(composer, bRoot, old);
					if (ret) return true; //ignore
				} catch (Throwable t) {
					UiEngineImpl.log.error("Failed to invoke doCatch for "+composer, t);
				}
			}
			
		if (bRoot && _syscomposer instanceof ComposerExt)
			try {
				return ((ComposerExt)_syscomposer).doCatch(ex);
			} catch (Throwable t) {
				UiEngineImpl.log.error("Failed to invoke doCatch for "+_syscomposer, t);
			}
		return false;
	}
	/*package*/ void doFinally(boolean bRoot) throws Exception {
		if (_composerExts != null)
			for (Composer composer: _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);

				((ComposerExt)composer).doFinally();

				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			((ComposerExt)_syscomposer).doFinally();
	}
}
