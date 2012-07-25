/* DesktopImpl.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 22 09:50:57     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.io.Serializables;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.CacheMap;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.Media;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.out.AuBookmark;
import org.zkoss.zk.au.out.AuClientInfo;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.device.DeviceNotFoundException;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OrientationEvent;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.RequestQueue;
import org.zkoss.zk.ui.sys.Scheduler;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DesktopActivationListener;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zk.ui.util.DesktopSerializationListener;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.ExecutionMonitor;
import org.zkoss.zk.ui.util.Monitor;
import org.zkoss.zk.ui.util.UiLifeCycle;

/**
 * The implementation of {@link Desktop}.
 *
 * <p>Note: though {@link DesktopImpl} is serializable, it is designed
 * to work with Web container to enable the serialization of sessions.
 * It is not suggested to serialize and desrialize it directly since
 * many fields might be lost.
 *
 * <p>On the other hand, it is OK to serialize and deserialize
 * {@link Component}.
 *
 * @author tomyeh
 */
public class DesktopImpl implements Desktop, DesktopCtrl, java.io.Serializable {
	private static final Log log = Log.lookup(DesktopImpl.class);
    private static final long serialVersionUID = 20101123L;

	/** Represents media stored with {@link #getDownloadMediaURI}.
	 * It must be distinguishable from component's ID.
	 */
	private static final String DOWNLOAD_PREFIX = "dwnmed-";
	/** A session attribute holding the number of server pushes.
	 */
	private static final String ATTR_PUSH_COUNT = "org.zkoss.zk.ui.pushes.count";
	/** A special event for scheduling a task for server push.
	 */
	private static final String ON_SCHEDULE = "onSchedule";

	private transient WebApp _wapp;
	private transient Session _sess;
	private String _id;
	/** The current directory of this desktop. */
	private String _dir = "";
	/** The path of the request that causes this desktop to be created. */
	private final String _path;
	/** The query string. */
	private final String _qs;
	/** The URI to access the update engine. */
	private final String _updateURI;
	/** List<Page>. */
	private final List<Page> _pages = new LinkedList<Page>();
	/** Map (String uuid, Component comp). */
	private transient Map<String, Component> _comps;
	/** A map of attributes. */
	private transient SimpleScope _attrs;
		//don't create it dynamically because PageImp._ip bind it at constructor
	private transient Execution _exec;
	/** A list of ScheduleInfo; must be thread safe */
	private final List<ScheduleInfo<? extends Event>> _schedInfos = new LinkedList<ScheduleInfo<? extends Event>>();
	/** For handling scheduled task in onSchedule. */
	private Component _dummyTarget = null;
	/** Next available key. */
	private int _nextKey;
	/** Next available UUID. */
	private int _nextUuid;
	/** A special prefix to UUID generated by this desktop.
	 * It is used to avoid ID conflicts with other desktops in the same
	 * session.
	 * Since UUID is long enough plus this prefix, the chance to conlict
	 * is almost impossible.
	 */
	private String _uuidPrefix;
	/** The request queue. */
	private transient RequestQueue _rque;
	private String _bookmark = "";
	/** The device type. */
	private String _devType = "ajax";
	/** The device. */
	private transient Device _dev; //it will re-init each time getDevice called
	/** A map of media (String key, Media content). */
	private CacheMap<String, Media> _meds;
	/** ID used to identify what is stored in _meds. */
	private int _medId;
	/** The server push controller, or null if not enabled. */
	private transient ServerPush _spush;
	/** A temporary object being deserialized but not yet activate. */
	private transient ServerPush _spushTemp;
	/** The event interceptors. */
	private final EventInterceptors _eis = new EventInterceptors();
	private transient List<DesktopCleanup> _dtCleans;
	private transient List<ExecutionInit> _execInits;
	private transient List<ExecutionCleanup> _execCleans;
	private transient List<UiLifeCycle> _uiCycles;
	private transient List<AuService> _ausvcs;

	private transient Visualizer _uv;
	private transient Object _uvLock;
	/** List<RecycleInfo>: used to recycle detached component's UUID. */
	private transient List<RecycleInfo> _uuidRecycle;

	private transient ReqResult _lastRes;
	private transient List<AuResponse> _piggyRes;
	/** A set of keys that shall be generated to the client only once per desktop. */
	private transient Set<String> _clientPerDesktops;

	private static final int MAX_RESPONSE_ID = 999;
	/** The response sequence ID. */
	private int _resId; //so next will be 1

	/** Whether any onPiggyback listener is registered. */
	private boolean _piggybackListened;
	/** Whether the server push shall stop after deactivate. */
	private boolean _spushShallStop;

	/**
	 * @param updateURI the URI to access the update engine (no expression allowed).
	 * Note: it is NOT encoded yet.
	 * @param path the path that causes this desktop to create.
	 * If null or empty is specified, it means not available.
	 * @param deviceType the device type.
	 * If null or empty is specified, "ajax" is assumed.
	 * @since 3.0.1
	 */
	public DesktopImpl(WebApp wapp, String updateURI, String path,
	String deviceType, Object request) {
		if (updateURI == null || wapp == null)
			throw new IllegalArgumentException("null");

		//Feature 1811241: we create a temporary exec (in WebManager.newDesktop),
		//so DesktopInit can access Executions.getCurrent
		final Execution exec = Executions.getCurrent();
		if (exec != null)
			((ExecutionCtrl)exec).setDesktop(this);

		_wapp = wapp;
		_updateURI = updateURI;
		init();
		_sess = Sessions.getCurrent(); //must be the current session

		String dir = null;
		if (path != null) {
			_path = path;
			final int j = path.lastIndexOf('/');
			if (j >= 0) dir = path.substring(0, j + 1);
		} else {
			_path = "";
		}
		setCurrentDirectory(dir);
		_qs = getQueryString(request);

		if (deviceType != null && deviceType.length() != 0)
			setDeviceType(deviceType);

		final Configuration config = _wapp.getConfiguration();
		_exec = exec; //fake
		try {
			final WebAppCtrl wappc = (WebAppCtrl)_wapp;
			final DesktopCache dc = _sess != null ? wappc.getDesktopCache(_sess): null;
				//_sess is null if in a working thread
			final IdGenerator idgen = wappc.getIdGenerator();
			if (idgen != null)
				_id = idgen.nextDesktopId(this);
			if (_id == null)
				_id = nextDesktopId(dc);
			else if (idgen != null)
				ComponentsCtrl.checkUuid(_id);
			updateUuidPrefix();

			config.invokeDesktopInits(this, request); //it might throw exception
			if (exec != null && exec.isVoided()) return; //sendredirect or forward

			if (dc != null)
				dc.addDesktop(this); //add to cache after invokeDesktopInits

			final Monitor monitor = config.getMonitor();
			if (monitor != null) {
				try {
					monitor.desktopCreated(this);
				} catch (Throwable ex) {
					log.error(ex);
				}
			}
		} finally {
			_exec = null;
		}
	}
	private static String getQueryString(Object request) {
		try {
			if (request instanceof javax.servlet.http.HttpServletRequest)
				return ((javax.servlet.http.HttpServletRequest)request).getQueryString();
		} catch (Throwable ex) { //ignore any error (such as no servlet at all)
		}
		return null;
	}
	private static final String DESKTOP_ID_PREFIX = "z_";
	private static String nextDesktopId(DesktopCache dc) {
		if (dc != null)
			return ComponentsCtrl.encodeId(
				new StringBuffer(12).append(DESKTOP_ID_PREFIX), dc.getNextKey()).toString();

		final int v;
		synchronized (DesktopImpl.class) {
			v = _keyWithoutDC++;
		}
		return ComponentsCtrl.encodeId(new StringBuffer(12).append("_g"), v).toString();
	}
	private static int _keyWithoutDC;

	/** Initialization for contructor and de-serialized. */
	private void init() {
		_uvLock = new Object();
		_rque = newRequestQueue();
		_comps = new HashMap<String, Component>(64);
		_attrs = new SynchronizedScope(this);
			//Use thread-safe scope, Since some class might access asynchronous,
			//i.e., without locking. Example, AuUploader
	}
	/** Updates _uuidPrefix based on _id. */
	private void updateUuidPrefix() {
		final StringBuffer sb = new StringBuffer();
		int val = _id.hashCode();

		//Thus, the number will 0, 1... max, 0, 1..., max, 0, 1 (less conflict)
		if (val < 0 && (val += Integer.MIN_VALUE) < 0)
			val = -val; //impossible but just in case

		//Note: ComponentsCtrl.isAutoUuid assumes
		//0: lower, 1: digit or upper, 2: letter or digit, 3: upper
		int v = (val % 26) + 36;
		val /= 26;
		sb.append(toLetter(v));
		v = val % 36;
		val /= 36;
		sb.append(toLetter(v));
		v = val % 62;
		val /= 62;
		sb.append(toLetter(v));
		_uuidPrefix = sb.append(toLetter((val % 26) + 10)).toString();
	}
	private static final char toLetter(int v) {
		if (v < 10) {
			return (char)('0' + v);
		} else if (v < 36) {
			return (char)(v + ('A' - 10));
		} else {
			return (char)(v + ('a' - 36));
		}
	}
	public String getId() {
		return _id;
	}

	/** Creates the request queue.
	 * It is called when the desktop is initialized.
	 *
	 * <p>You may override it to provide your implementation of
	 * {@link RequestQueue} to control how to optimize the AU requests.
	 *
	 * <p>Default: creates an instance from {@link RequestQueueImpl};
	 *
	 * @since 2.4.0
	 */
	protected RequestQueue newRequestQueue() {
		return new RequestQueueImpl();
	}

	//-- Desktop --//
	public String getDeviceType() {
		return _devType;
	}
	public Device getDevice() {
		if (_dev == null)
			_dev = Devices.getDevice(_devType);
		return _dev;
	}
	public void setDeviceType(String deviceType) {
		//Note: we check _comps.isEmpty() only if device type diffs, because
		//a desktop might have several richlet and each of them will call
		//this method once
		if (!_devType.equals(deviceType)) {
			if (deviceType == null || deviceType.length() == 0)
				throw new IllegalArgumentException("empty");
			if (!Devices.exists(deviceType))
				throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);

			if (!_comps.isEmpty())
				throw new UiException("Unable to change the device type since some components are attached.");
			_devType = deviceType;
			_dev = null;

			if (_sess != null) //not in a working thread
				((SessionCtrl)_sess).setDeviceType(_devType);
		}
	}
	public Execution getExecution() {
		return _exec;
	}
	public final Session getSession() {
		return _sess;
	}
	public String getUpdateURI(String pathInfo) {
		final String uri;
		if (pathInfo == null || pathInfo.length() == 0) {
			uri = _updateURI;
		} else {
			if (pathInfo.charAt(0) != '/')
				pathInfo = '/' + pathInfo;
			uri = _updateURI + pathInfo;
		}
		return _exec.encodeURL(uri);
	}
	public String getDynamicMediaURI(Component comp, String pathInfo) {
		if (!(((ComponentCtrl)comp).getExtraCtrl() instanceof DynamicMedia))
			throw new UiException(DynamicMedia.class+" not implemented by getExtraCtrl() of "+comp);

		final StringBuffer sb = new StringBuffer(64)
			.append("/view/").append(getId())
			.append('/').append(comp.getUuid()).append('/');
		Strings.encode(sb, System.identityHashCode(comp) & 0xffff);

		if (pathInfo != null && pathInfo.length() > 0) {
			if (pathInfo.charAt(0) != '/') sb.append('/');
			sb.append(pathInfo);
		}
		return getUpdateURI(sb.toString());
	}
	public String getDownloadMediaURI(Media media, String pathInfo) {
		if (media == null)
			throw new IllegalArgumentException("null media");

		if (_meds == null) {
			_meds = new CacheMap<String, Media>();
			_meds.setMaxSize(500);
			_meds.setLifetime(15 * 60 * 1000);
				//15 minutes (CONSIDER: configurable)
		} else {
			housekeep();
		}

		String medId = Strings.encode(
			new StringBuffer(12).append(DOWNLOAD_PREFIX), _medId++).toString();
		_meds.put(medId, media);

		final StringBuffer sb = new StringBuffer(64)
			.append("/view/").append(getId())
			.append('/').append(medId).append('/');
		Strings.encode(sb, System.identityHashCode(media) & 0xffff);

		if (pathInfo != null && pathInfo.length() > 0) {
			if (pathInfo.charAt(0) != '/') sb.append('/');
			sb.append(pathInfo);
		}
		return getUpdateURI(sb.toString());
	}
	public Media getDownloadMedia(String medId, boolean reserved) {
		return _meds != null ? _meds.get(medId): null;
	}
	/** Cleans up redudant data. */
	private void housekeep() {
		if (_meds != null) _meds.expunge();
	}

	public Page getPage(String pageId) {
		//Spec: we allow user to access this method concurrently
		final Page page = getPageIfAny(pageId);
		if (page == null)
			throw new ComponentNotFoundException("Page not found: "+pageId);
		return page;
	}
	public Page getPageIfAny(String pageId) {
		//Spec: we allow user to access this method concurrently, so
		//synchronized is required
		Page page = null;
		synchronized (_pages) {
			for (Page pg: _pages) {
				if (Objects.equals(pageId, pg.getId()))
					return pg;
				if (Objects.equals(pageId, pg.getUuid()))
					page = pg;
			}
		}
		return page;
	}
	public boolean hasPage(String pageId) {
		return getPageIfAny(pageId) != null;
	}
	public Collection<Page> getPages() {
		//No synchronized is required because it cannot be access concurrently
		return Collections.unmodifiableCollection(_pages);
	}
	public Page getFirstPage() {
		return _pages.isEmpty() ? null: _pages.get(0);
	}

	public String getBookmark() {
		return _bookmark;
		//Notice: since the bookmark (#xx) not sent from the HTTP request,
		//we can only assume "" when the page is loading
	}
	public void setBookmark(String name) {
		setBookmark(name, false);
	}
	public void setBookmark(String name, boolean replace) {
		if (_exec == null)
			throw new IllegalStateException("Not the current desktop: " + this);
		//B50-ZK-58: question mark is legal char
		//if (name.indexOf('#') >= 0 || name.indexOf('?') >= 0)
		//	throw new IllegalArgumentException("Illegal character: # ?");

		_bookmark = name;
		addResponse(new AuBookmark(name, replace));
	}
	private void addResponse(AuResponse response) {
		((WebAppCtrl) _wapp).getUiEngine().addResponse(response);
	}

	public Collection<Component> getComponents() {
		return _comps.values();
	}
	public Component getComponentByUuid(String uuid) {
		final Component comp = _comps.get(uuid);
		if (comp == null)
			throw new ComponentNotFoundException("Component not found: "+uuid);
		return comp;
	}
	public Component getComponentByUuidIfAny(String uuid) {
		return _comps.get(uuid);
	}
	public void addComponent(Component comp) {
		//to avoid misuse, check whether new comp belongs to the same device type
		final LanguageDefinition langdef =
			comp.getDefinition().getLanguageDefinition();
		if (langdef != null && !_devType.equals(langdef.getDeviceType()))
			throw new UiException("Component, "+comp+", does not belong to the same device type of the desktop, "+_devType);
		final String uuid = comp.getUuid();
		final Component old = _comps.put(uuid, comp);
		if (old != comp && old != null) {
			_comps.put(uuid, old); //recover
			throw new InternalError("Caller shall prevent it: Register a component twice: "+comp);
		}/* For performance reason, we don't check if a component is
			detached and attached back (in another execution). Rather, reset
			_uuid when it is recycled (refer to AbstractComponent.setPage0
			(the caller of removeComponent has to reset)
		 else if (_uuidRecycle != null && !_uuidRecycle.isEmpty()) {
			for (RecycleInfo ri: _uuidRecycle) {
				final List<String> uuids = ri.uuids;
				if (uuids.remove(uuid)) {
					if (uuids.isEmpty())
						it.remove();
					break;
				}
			}
		}*/
	}
	public boolean removeComponent(Component comp, boolean recycleAllowed) {
		final String uuid = comp.getUuid();
		if (_comps.remove(uuid) == null || !recycleAllowed || recycleUuidDisabled())
			return false; //not recycled

		//Bug 3002611: don't recycle UUID if RawId, since addUuidChanged will
		//cause AuRemove to be sent
		if (comp instanceof RawId &&
		(!ComponentsCtrl.isAutoUuid(uuid) || ((WebAppCtrl)_wapp).getIdGenerator() != null))
			return false; //not recycled

		final int execId = getExecId();
		RecycleInfo ri = null;
		if (_uuidRecycle == null) {
			_uuidRecycle = new LinkedList<RecycleInfo>();
		} else {
			for (RecycleInfo r: _uuidRecycle)
				if (r.execId == execId) {
					ri = r; //found
					break;
				}
		}
		if (ri == null)
			_uuidRecycle.add(ri = new RecycleInfo(execId));
		ri.uuids.add(uuid);
		return true; //recycled
	}
	public Component mapComponent(String uuid, Component comp) {
		if (uuid == null)
			throw new IllegalArgumentException("null");
		return comp != null ?
			_comps.put(uuid, comp): _comps.remove(uuid);
			//no recycle, no check
	}
	private static int getExecId() {
		final Execution exec = Executions.getCurrent();
		return exec != null ? System.identityHashCode(exec): 0;
	}
	private static boolean recycleUuidDisabled() {
		if (_recycleUuidDisabled == null)
			_recycleUuidDisabled = Boolean.valueOf(
				"true".equals(Library.getProperty(Attributes.UUID_RECYCLE_DISABLED)));
		return _recycleUuidDisabled.booleanValue();
	}
	private static Boolean _recycleUuidDisabled;

	public Map<String, Object> getAttributes() {
		return _attrs.getAttributes();
	}
	public Object getAttribute(String name) {
		return _attrs.getAttribute(name);
	}
	public boolean hasAttribute(String name) {
		return _attrs.hasAttribute(name);
	}
	public Object setAttribute(String name, Object value) {
		return _attrs.setAttribute(name, value);
	}
	public Object removeAttribute(String name) {
		return _attrs.removeAttribute(name);
	}
	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		if (val != null || !recurse || hasAttribute(name))
			return val;
		if (_sess != null) return _sess.getAttribute(name, true);
		if (_wapp != null) return _wapp.getAttribute(name, true);
		return null;
	}
	public boolean hasAttribute(String name, boolean recurse) {
		if (hasAttribute(name))
			return true;
		if (recurse) {
			if (_sess != null) return _sess.hasAttribute(name, true);
			if (_wapp != null) return _wapp.hasAttribute(name, true);
		}
		return false;
	}
	public Object setAttribute(String name, Object value, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_sess != null) {
				if (_sess.hasAttribute(name, true))
					return _sess.setAttribute(name, value, true);
			} else if (_wapp != null) {
				if (_wapp.hasAttribute(name, true))
					return _wapp.setAttribute(name, value, true);
			}
		}
		return setAttribute(name, value);
	}
	public Object removeAttribute(String name, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_sess != null) {
				if (_sess.hasAttribute(name, true))
					return _sess.removeAttribute(name, true);
			} else if (_wapp != null) {
				if (_wapp.hasAttribute(name, true))
					return _wapp.removeAttribute(name, true);
			}
			return null;
		}
		return removeAttribute(name);
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _attrs.addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _attrs.removeScopeListener(listener);
	}

	public WebApp getWebApp() {
		return _wapp;
	}

	public String getRequestPath() {
		return _path;
	}
	public String getQueryString() {
		return _qs;
	}
	public String getCurrentDirectory() {
		return _dir;
	}
	public void setCurrentDirectory(String dir) {
		if (dir == null) {
			dir = "";
		} else {
			final int len = dir.length() - 1;
			if (len >= 0 && dir.charAt(len) != '/')
				dir += '/';
		}
		_dir = dir;
	}

	//-- DesktopCtrl --//
	public RequestQueue getRequestQueue() {
		housekeep();
		return _rque;
	}
	public void setExecution(Execution exec) {
		_exec = exec;
	}

	public void service(AuRequest request, boolean everError) {
		if (_ausvcs != null) {
			//Note: removeListener might be called when invoking svc.service()
			for (Iterator<AuService> it = CollectionsX.comodifiableIterator(_ausvcs); it.hasNext();)
				if (it.next().service(request, everError))
					return;
		}

		final Component comp = request.getComponent();
		if (comp != null) {
			final AuService svc = comp.getAuService();
			if (svc == null || !svc.service(request, everError))
				((ComponentCtrl)comp).service(request, everError);
			return; //done (it's comp's job to handle it)
		}

		final String cmd = request.getCommand();
		if (Events.ON_BOOKMARK_CHANGE.equals(cmd)) {
			BookmarkEvent evt = BookmarkEvent.getBookmarkEvent(request);
			_bookmark = evt.getBookmark();
			Events.postEvent(evt);
		} else if (Events.ON_CLIENT_INFO.equals(cmd)) {
			Events.postEvent(ClientInfoEvent.getClientInfoEvent(request));
		} else if (Events.ON_ORIENTATION_CHANGE.equals(cmd)) {
			Events.postEvent(OrientationEvent.getOrientationEvent(request));
		} else if ("rmDesktop".equals(cmd)) {
			((WebAppCtrl)request.getDesktop().getWebApp())
				.getUiEngine().setAbortingReason(
					new org.zkoss.zk.ui.impl.AbortByRemoveDesktop());
				//to avoid surprise, we don't remove it now
				//rather, it is done by AbortByRemoveDesktop.getResponse
		} else if ("redraw".equals(cmd)) {
			invalidate();
		} else if ("error".equals(cmd)) {
			final Map<String, Object> data = request.getData();
			if (data != null)
				log.error(this+" client error: " + data.get("message"));
		} else
			Events.postEvent(Event.getEvent(request));
	}

	public Visualizer getVisualizer() {
		return _uv;
	}
	public void setVisualizer(Visualizer uv) {
		_uv = uv;
	}
	public Object getActivationLock() {
		return _uvLock;
	}

	public int getNextKey() {
		return _nextKey++;
	}
	public String getNextUuid(Page page) {
		final IdGenerator idgen = ((WebAppCtrl)_wapp).getIdGenerator();
		String uuid = idgen != null ? idgen.nextPageUuid(page): null;
		if (uuid == null)
			return nextUuid();

		ComponentsCtrl.checkUuid(uuid);
		return uuid;
	}
	public String getNextUuid(Component comp) {
		//The reason to recycle UUID is to keep it short (since _nextUuid won't grow too fast)
		//Thus, it takes fewer memory at the client
		if (_uuidRecycle != null && !_uuidRecycle.isEmpty()) {
			final int execId = getExecId();
			for (Iterator<RecycleInfo> it = _uuidRecycle.iterator(); it.hasNext();) {
				final RecycleInfo ri = it.next();
				if (ri.execId != execId) { //reuse if diff
					final String uuid = ri.uuids.remove(0);
					if (ri.uuids.isEmpty())
						it.remove();
					return uuid;
				}
			}
		}

		final IdGenerator idgen = ((WebAppCtrl)_wapp).getIdGenerator();
		String uuid = null;
		if(idgen != null){
			try{
				uuid = idgen.nextComponentUuid(
					this, comp, Utils.getComponentInfo(comp));
			}catch(AbstractMethodError ex){
				try {
					Method method = idgen.getClass().getMethod("nextComponentUuid", Desktop.class,Component.class);
					Fields.setAccessible(method, true);
					uuid = (String) method.invoke(idgen, this, comp);
				} catch (Exception e) {
					throw UiException.Aide.wrap(e);
				}
			}
		}
		if (uuid == null)
			return nextUuid();

		ComponentsCtrl.checkUuid(uuid);
		return uuid;
	}
	
	private String nextUuid() {
		return ComponentsCtrl.toAutoId(_uuidPrefix, _nextUuid++);
	}

	public void addPage(Page page) {
		//We have to synchronize it due to getPage allows concurrent access
		synchronized (_pages) {
			_pages.add(page);
//			if (log.debugable()) log.debug("After added, pages: "+_pages);
		}
		afterPageAttached(page, this);
		_wapp.getConfiguration().afterPageAttached(page, this);
	}
	public void removePage(Page page) {
		synchronized (_pages) {
			if (!_pages.remove(page))
				return;
				//Both UiVisualizer.getResponses and Include.setChildPage
				//might calll removePage

//			if (log.debugable()) log.debug("After removed, pages: "+_pages);
		}
		removeComponents(page.getRoots());

		afterPageDetached(page, this);
		_wapp.getConfiguration().afterPageDetached(page, this);

		((PageCtrl)page).destroy();
	}
	private void removeComponents(Collection<Component> comps) {
		for (Component comp: comps) {
			removeComponents(comp.getChildren()); //recursive
			removeComponent(comp, true);
		}
	}

	public void setId(String id) {
		if (!((ExecutionCtrl)_exec).isRecovering())
			throw new IllegalStateException("Callable only in recovring");
		if (id == null || id.length() <= 1 || id.charAt(0) != 'g')
			throw new IllegalArgumentException("Invalid desktop ID. You have to recover to the original value, not creating a new value: "+id);

		//_sess and dc are null if in a working thread
		final DesktopCache dc = _sess != null ? ((WebAppCtrl)_wapp).getDesktopCache(_sess): null;
		if (dc != null)
			dc.removeDesktop(this);

		_id = id;
		updateUuidPrefix();

		if (dc != null)
			dc.addDesktop(this);
	}
	public void recoverDidFail(Throwable ex) {
		((WebAppCtrl)_wapp).getDesktopCache(_sess).removeDesktop(this);
	}

	public void recycle() {
		_clientPerDesktops = null; //re-gen is required
	}
	/** Marks the per-desktop information of the given key will be generated,
	 * and returns true if the information is not generated yet
	 * (i.e., this method is NOT called with the given key).
	 * You could use this method to minimize the bytes to be sent to
	 * the client if the information is required only once per desktop.
	 */
	/*package*/ boolean markClientInfoPerDesktop(String key) {
		if (_clientPerDesktops == null)
			_clientPerDesktops= new HashSet<String>(32);
		return _clientPerDesktops.add(key);
	}

	public boolean isAlive() {
		return _rque != null;
	}
	public void destroy() {
		final ExecutionMonitor execmon = _wapp != null ? //just in case
			_wapp.getConfiguration().getExecutionMonitor(): null;

		_rque = null; //denote it is destroyed

		final ServerPush sp = _spush; //avoid racing
		if (sp != null) {
			_spush = null;
			try {
				sp.stop();
			} catch (Throwable ex) {
				log.warning("Failed to stop server-push, "+sp, ex);
			}
		}

		try {
			final List<Page> pages = new ArrayList<Page>(_pages);
			_pages.clear();
			for (Page page: pages) {
				try {
					((PageCtrl)page).destroy();
				} catch (Throwable ex) {
					log.warning("Failed to destroy "+page, ex);
				}
			}
		} catch (Throwable ex) {
			log.warning("Failed to clean up pages of "+this, ex);
		}

		if (execmon != null)
			execmon.desktopDestroy(this);

		//theorectically, the following is not necessary, but, to be safe...
		_attrs.getAttributes().clear();
		_comps = new HashMap<String, Component>(2); //not clear() since # of comps might huge
		_meds = null;
		//_sess = null; => not sure whether it can be nullify
		//_wapp = null; => SimpleDesktopCache.desktopDestroyed depends on it
	}

	public Collection<EventProcessingThread> getSuspendedThreads() {
		return ((WebAppCtrl)_wapp).getUiEngine().getSuspendedThreads(this);
	}
	public boolean ceaseSuspendedThread(EventProcessingThread evtthd, String cause) {
		return ((WebAppCtrl)_wapp).getUiEngine()
			.ceaseSuspendedThread(this, evtthd, cause);
	}

	//-- Object --//
	public String toString() {
		return "[Desktop "+_id+':'+_path+']';
	}

	public void sessionWillPassivate(Session sess) {
		Execution exec = Executions.getCurrent();
		if (exec != null) { //not possible, but just in case
			sessWillPassivate();
		} else {
			exec = new org.zkoss.zk.ui.impl.PhantomExecution(this);
			safeActivate(exec);
			try {
				sessWillPassivate();
			} finally {
				safeDeactivate(exec);
			}
		}
	}
	public void sessionDidActivate(Session sess) {
		_sess = sess;
		_wapp = sess.getWebApp();

		Execution exec = Executions.getCurrent();
		if (exec != null) { //not possible, but just in case
			sessDidActivate();
		} else {
			exec = new org.zkoss.zk.ui.impl.PhantomExecution(this);
			safeActivate(exec);
			try {
				sessDidActivate();
				if (_spushTemp != null)
					enableServerPush0(_spushTemp, true);
			} finally {
				safeDeactivate(exec);
				_spushTemp = null;
			}
		}
	}
	/** Safe to be called even if the Web application has been destroyed
	 */
	private void safeActivate(Execution exec) {
		final UiEngine uieng = ((WebAppCtrl)_wapp).getUiEngine();
		if (uieng != null) {
			uieng.activate(exec);
		} else {
			_exec = exec;
			ExecutionsCtrl.setCurrent(exec);
		}
	}
	/** Safe to be called even if the Web application has been destroyed
	 */
	private void safeDeactivate(Execution exec) {
		final UiEngine uieng = ((WebAppCtrl)_wapp).getUiEngine();
		if (uieng != null) {
			uieng.deactivate(exec);
		} else {
			_exec = null;
			ExecutionsCtrl.setCurrent(null);
		}
	}
	private void sessWillPassivate() {
		for (Page page: _pages)
			((PageCtrl)page).sessionWillPassivate(this);

		if (_dev != null) _dev.sessionWillPassivate(this);

		willPassivate(_attrs.getAttributes().values());
		willPassivate(_attrs.getListeners());

		willPassivate(_dtCleans);
		willPassivate(_execInits);
		willPassivate(_execCleans);
		willPassivate(_uiCycles);
	}
	private void sessDidActivate() {
		if (_dev != null) _dev.sessionDidActivate(this);

		for (Page page: _pages)
			((PageCtrl)page).sessionDidActivate(this);

		didActivate(_attrs.getAttributes().values());
		didActivate(_attrs.getListeners());

		didActivate(_dtCleans);
		didActivate(_execInits);
		didActivate(_execCleans);
		didActivate(_uiCycles);
	}
	private void willPassivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willPassivate(it.next());
	}
	private void willPassivate(Object o) {
		if (o instanceof DesktopActivationListener)
			((DesktopActivationListener)o).willPassivate(this);
	}
	private void didActivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didActivate(it.next());
	}
	private void didActivate(Object o) {
		if (o instanceof DesktopActivationListener)
			((DesktopActivationListener)o).didActivate(this);
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		final Map<String, Object> attrs = _attrs.getAttributes();
		willSerialize(attrs.values());
		Serializables.smartWrite(s, attrs);
		final List<ScopeListener> lns = _attrs.getListeners();
		willSerialize(lns);
		Serializables.smartWrite(s, lns);

		willSerialize(_dtCleans);
		Serializables.smartWrite(s, _dtCleans);
		willSerialize(_execInits);
		Serializables.smartWrite(s, _execInits);
		willSerialize(_execCleans);
		Serializables.smartWrite(s, _execCleans);
		willSerialize(_uiCycles);
		Serializables.smartWrite(s, _uiCycles);
		willSerialize(_ausvcs);
		Serializables.smartWrite(s, _ausvcs);

		if (_spush == null || _spush instanceof java.io.Serializable
		|| _spush instanceof java.io.Externalizable)
			s.writeObject(_spush);
		else
			s.writeObject(_spush.getClass());
	}
	private void willSerialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willSerialize(it.next());
	}
	private void willSerialize(Object o) {
		if (o instanceof DesktopSerializationListener)
			((DesktopSerializationListener)o).willSerialize(this);
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		//get back _comps from _pages
		for (Page page: _pages)
			for (Component root = page.getFirstRoot(); root != null;
			root = root.getNextSibling())
				addAllComponents(root);

		final Map<String, Object> attrs = _attrs.getAttributes();
		Serializables.smartRead(s, attrs);
		final List<ScopeListener> lns = _attrs.getListeners();
		Serializables.smartRead(s, lns);

		_dtCleans = Serializables.smartRead(s, _dtCleans);
		_execInits = Serializables.smartRead(s, _execInits);
		_execCleans = Serializables.smartRead(s, _execCleans);
		_uiCycles = Serializables.smartRead(s, _uiCycles);
		_ausvcs = Serializables.smartRead(s, _ausvcs);

		didDeserialize(attrs.values());
		didDeserialize(lns);
		didDeserialize(_dtCleans);
		didDeserialize(_execInits);
		didDeserialize(_execCleans);
		didDeserialize(_uiCycles);
		didDeserialize(_ausvcs);

		Object o = s.readObject();
		if (o != null) {
			ServerPush sp = null;
			if (o instanceof Class) {
				try {
					sp = (ServerPush)((Class)o).newInstance();
				} catch (Throwable ex) {
				}
			} else
				sp = (ServerPush)o;
			_spushTemp = sp;
		}
	}
	private void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}
	private void didDeserialize(Object o) {
		if (o instanceof DesktopSerializationListener)
			((DesktopSerializationListener)o).didDeserialize(this);
	}
	private void addAllComponents(Component comp) {
		addComponent(comp);
		for (Component child: comp.getChildren())
			addAllComponents(child);
	}

	public void addListener(Object listener) {
		boolean added = false;
		if (listener instanceof EventInterceptor) {
			_eis.addEventInterceptor((EventInterceptor)listener);
			added = true;
		}

		if (listener instanceof DesktopCleanup) {
			_dtCleans = addListener0(_dtCleans, (DesktopCleanup)listener);
			added = true;
		}

		if (listener instanceof ExecutionInit) {
			_execInits = addListener0(_execInits, (ExecutionInit)listener);
			added = true;
		}
		if (listener instanceof ExecutionCleanup) {
			_execCleans = addListener0(_execCleans, (ExecutionCleanup)listener);
			added = true;
		}

		if (listener instanceof UiLifeCycle) {
			_uiCycles = addListener0(_uiCycles, (UiLifeCycle)listener);
			added = true;
		}

		if (listener instanceof AuService) {
			_ausvcs = addListener0(_ausvcs, (AuService)listener);
			added = true;
		}

		if (!added)
			throw new IllegalArgumentException("Unknown listener: "+listener);
	}
	private <T> List<T> addListener0(List<T> list, T listener) {
		if (list == null)
			list = new LinkedList<T>();
		list.add(listener);
		return list;
	}
	public boolean removeListener(Object listener) {
		boolean found = false;
		if (listener instanceof EventInterceptor
		&& _eis.removeEventInterceptor((EventInterceptor)listener))
			found = true;

		if (listener instanceof DesktopCleanup
		&& removeListener0(_dtCleans, listener))
			found = true;

		if (listener instanceof ExecutionInit
		&& removeListener0(_execInits, listener))
			found = true;

		if (listener instanceof ExecutionCleanup
		&& removeListener0(_execCleans, listener))
			found = true;

		if (listener instanceof UiLifeCycle
		&& removeListener0(_uiCycles, listener))
			found = true;

		if (listener instanceof AuService
		&& removeListener0(_ausvcs, listener))
			found = true;
		return found;
	}
	private boolean removeListener0(List list, Object listener) {
		//Since 3.0.6: To be consistent with Configuration,
		//use equals instead of ==
		if (list != null && listener != null)
			for (Iterator it = list.iterator(); it.hasNext();) {
				if (it.next().equals(listener)) {
					it.remove();
					return true;
				}
			}
		return false;
	}
	public Event beforeSendEvent(Event event) {
		event = _eis.beforeSendEvent(event);
		if (event != null)
			event = _wapp.getConfiguration().beforeSendEvent(event);
		return event;
	}
	public Event beforePostEvent(Event event) {
		event = _eis.beforePostEvent(event);
		if (event != null)
			event = _wapp.getConfiguration().beforePostEvent(event);
		return event;
	}
	public Event beforeProcessEvent(Event event) throws Exception {
		event = _eis.beforeProcessEvent(event);
		if (event != null)
			event = _wapp.getConfiguration().beforeProcessEvent(event);
		return event;
	}
	public void afterProcessEvent(Event event) throws Exception {
		_eis.afterProcessEvent(event);
		_wapp.getConfiguration().afterProcessEvent(event);

		if (Events.ON_DESKTOP_RECYCLE.equals(event.getName())) {
			if (_bookmark.length() > 0)
				addResponse(new AuBookmark(_bookmark));

			l_out:
			for (Page page: _pages)
				for (Component root = page.getFirstRoot();
				root != null; root = root.getNextSibling())
					if (Events.isListened(root, Events.ON_CLIENT_INFO, false)) {
						addResponse(new AuClientInfo(this));
						break l_out;
					}
		}
	}

	public void invokeDesktopCleanups() {
		if (_dtCleans != null) {
			for (Iterator<DesktopCleanup> it = CollectionsX.comodifiableIterator(_dtCleans); it.hasNext();) {
				final DesktopCleanup listener = it.next();
				try {
					listener.cleanup(this);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}

	public void invokeExecutionInits(Execution exec, Execution parent)
	throws UiException {
		if (_execInits != null) {
			for (Iterator<ExecutionInit> it = CollectionsX.comodifiableIterator(_execInits); it.hasNext();) {
				try {
					it.next().init(exec, parent);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the creation of a session
				}
			}
		}
	}
	public void invokeExecutionCleanups(Execution exec, Execution parent, List<Throwable> errs) {
		if (_execCleans != null) {
			for (Iterator<ExecutionCleanup> it = CollectionsX.comodifiableIterator(_execCleans); it.hasNext();) {
				final ExecutionCleanup listener = it.next();
				try {
					listener.cleanup(exec, parent, errs);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
					if (errs != null) errs.add(ex);
				}
			}
		}
	}

	public void afterComponentAttached(Component comp, Page page) {
		if (_uiCycles != null) {
			for (Iterator<UiLifeCycle> it = CollectionsX.comodifiableIterator(_uiCycles); it.hasNext();) {
				final UiLifeCycle listener = it.next();
				try {
					listener.afterComponentAttached(comp, page);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}
	public void afterComponentDetached(Component comp, Page prevpage) {
		if (_uiCycles != null) {
			for (Iterator<UiLifeCycle> it = CollectionsX.comodifiableIterator(_uiCycles); it.hasNext();) {
				final UiLifeCycle listener = it.next();
				try {
					listener.afterComponentDetached(comp, prevpage);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}
	public void afterComponentMoved(Component parent, Component child, Component prevparent) {
		if (_uiCycles != null) {
			for (Iterator<UiLifeCycle> it = CollectionsX.comodifiableIterator(_uiCycles); it.hasNext();) {
				final UiLifeCycle listener = it.next();
				try {
					listener.afterComponentMoved(parent, child, prevparent);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}
	private void afterPageAttached(Page page, Desktop desktop) {
		if (_uiCycles != null) {
			for (Iterator<UiLifeCycle> it = CollectionsX.comodifiableIterator(_uiCycles); it.hasNext();) {
				final UiLifeCycle listener = it.next();
				try {
					listener.afterPageAttached(page, desktop);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}
	private void afterPageDetached(Page page, Desktop prevdesktop) {
		if (_uiCycles != null) {
			for (Iterator<UiLifeCycle> it = CollectionsX.comodifiableIterator(_uiCycles); it.hasNext();) {
				final UiLifeCycle listener = it.next();
				try {
					listener.afterPageDetached(page, prevdesktop);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+listener, ex);
				}
			}
		}
	}

	//Server Push//
	public boolean enableServerPush(boolean enable) {
		return enableServerPush0(null, enable);
	}
	private boolean enableServerPush0(ServerPush sp, boolean enable) {
		if (_sess == null)
			throw new IllegalStateException("Server push cannot be enabled in a working thread");

		final boolean old = _spush != null;
		if (old != enable) {
			final Integer icnt = (Integer)_sess.getAttribute(ATTR_PUSH_COUNT);
			int cnt = icnt != null ? icnt.intValue(): 0;
			if (enable) {
				if (Executions.getCurrent() == null)
					throw new IllegalStateException("Server Push cannot be started without execution");

				final int maxcnt = _wapp.getConfiguration().getSessionMaxPushes();
				if (maxcnt >= 0 && cnt >= maxcnt)
					throw new UiException(cnt > 0 ? "Too many concurrent push connections per session: "+cnt:
						"Server push is disabled");

				if (sp != null) {
					_spush = sp;
				} else {
					final Class cls = getDevice().getServerPushClass();
					if (cls == null)
						throw new UiException("No server push defined. Make sure you are using ZK PE or EE, or you have configured your own implementation");

					_spush = ((WebAppCtrl)_wapp).getUiFactory().newServerPush(this, cls);
				}
				_spush.start(this);
				++cnt;
			} else if (_spush.isActive()) {
				_spushShallStop = true;
				--cnt;
			} else {
				_spush.stop();
				_spush = null;
				--cnt;
			}
			_sess.setAttribute(ATTR_PUSH_COUNT, new Integer(cnt));
		}
		return old;
	}
	public boolean enableServerPush(ServerPush serverpush) {
		final boolean old = _spush != null,
			enable = serverpush != null;
		if (old != enable || serverpush != _spush) {
			if (old)
				enableServerPush(false);
			if (enable)
				enableServerPush0(serverpush, true);
		}
		return old;
	}
	public boolean isServerPushEnabled() {
		return _spush != null;
	}
	public ServerPush getServerPush() {
		return _spush;
	}
	public <T extends Event> void scheduleServerPush(EventListener<T> listener, T event) {
		if (listener == null)
			throw new IllegalArgumentException("null listener");
		checkSeverPush("schedule");

		_spush.schedule(listener, event, new Scheduler<T>() {
			public void schedule(EventListener<T> listener, T event) {
				synchronized (_schedInfos) { //must be thread safe
					if (_dummyTarget == null) {
						_dummyTarget = new AbstractComponent();
						_dummyTarget.addEventListener(ON_SCHEDULE, new ScheduleListener());
					}
					_schedInfos.add(new ScheduleInfo<T>(listener, event));
				}
			}
		});
	}
	public boolean scheduledServerPush() {
		return !_schedInfos.isEmpty(); //no need to sync
	}
	private void checkSeverPush(String what) {
		if (_spush == null)
			if (isAlive())
				throw new IllegalStateException("Before calling Executions."+what+"(), the server push must be enabled for "+this);
			else
				throw new DesktopUnavailableException("Stopped");
	}
	public boolean activateServerPush(long timeout)
	throws InterruptedException {
		checkSeverPush("activate");
		if (Events.inEventListener()
		&& Executions.getCurrent().getDesktop() == this)
			throw new IllegalStateException("No need to invoke Executions.activate() in an event listener");

		return _spush.activate(timeout);
	}
	public void deactivateServerPush() {
		if (_spush != null)
			if (_spush.deactivate(_spushShallStop)) {
				_spushShallStop = false;
				_spush = null;
			}
	}

	public void onPiggybackListened(Component comp, boolean listen) {
		//we don't cache comp to avoid the risk of memory leak (maybe not
		//a problem)
		//On the other hand, most pages don't listen onPiggyback at all,
		//so _piggybackListened is good enough to improve the performance
		if (listen) _piggybackListened = true;
	}
	public void onPiggyback() {
		//Note: we don't post ON_PIGGYBACK twice in an execution
		//(performance concern and back-compatibility).
		if (_piggybackListened
		&& Executions.getCurrent().getAttribute(ATTR_PIGGYBACK_POSTED) == null) {
			for (Page page: _pages) {
				if (Executions.getCurrent().isAsyncUpdate(page)) { //ignore new created pages
					for (Component root = page.getFirstRoot();
					root != null; root = root.getNextSibling()) {
						if (Events.isListened(root, Events.ON_PIGGYBACK, false)) { //asap+deferrable
							Events.postEvent(new Event(Events.ON_PIGGYBACK, root));
							Executions.getCurrent().setAttribute(ATTR_PIGGYBACK_POSTED, Boolean.TRUE);
						}
					}
				}
			}
		}

		if (!_schedInfos.isEmpty())
			Events.postEvent(ON_SCHEDULE, _dummyTarget, null);
			//we could not process them here (otherwise, event handling, thread
			//might not work)
			//Thus, we post an event and handle it in _dummyTarget

		if (_spush != null)
			_spush.onPiggyback();
	}
	private static final String ATTR_PIGGYBACK_POSTED =
		"org.zkoss.zk.ui.impl.piggyback.posted";

	//AU Response//
	public void responseSent(String reqId, Object response) {
		if (reqId != null)
			_lastRes = new ReqResult(reqId, response);
	}
	public Object getLastResponse(String reqId) {
		return _lastRes != null && _lastRes.id.equals(reqId) ?
			_lastRes.response: null;
	}
	public int getResponseId(boolean advance) {
		if (advance && ++_resId > MAX_RESPONSE_ID)
			_resId = 1;
		return _resId;
	}
	public void setResponseId(int resId) {
		if (resId > MAX_RESPONSE_ID)
			throw new IllegalArgumentException("Invalid response ID: "+resId);
		_resId = resId < 0 ? 0: resId;
	}
	public List<AuResponse> piggyResponse(Collection<AuResponse> response, boolean reset) {
		if (response != null) {
			if (_piggyRes == null)
				_piggyRes = new LinkedList<AuResponse>();
			_piggyRes.addAll(response);
		}

		List<AuResponse> l = _piggyRes;
		if (reset) _piggyRes = null;
		return l;
	}

	public void invalidate() {
		for (Page page: _pages)
			if (((PageCtrl)page).getOwner() == null)
				page.invalidate();
	}
	private static class ReqResult {
		private final String id;
		private final Object response;
		private ReqResult(String id, Object response) {
			this.id = id;
			this.response = response;
		}
	}
	private static class RecycleInfo implements java.io.Serializable {
		private final int execId;
		private final List<String> uuids = new LinkedList<String>();
		private RecycleInfo(int execId) {
			this.execId = execId;
		}
		public String toString() {
			return '[' + execId + ": " + uuids + ']';
		}
	}
	private static class ScheduleInfo<T extends Event> implements java.io.Serializable {
		private final EventListener<T> _listener;
		private final T _event;
		private ScheduleInfo(EventListener<T> listener, T event) {
			_listener = listener;
			_event = event;
		}
		private void invoke() throws Exception {
			_listener.onEvent(_event);
		}
	}
	private class ScheduleListener implements EventListener<Event>, java.io.Serializable {
		public void onEvent(Event event) throws Exception {
			final long max = System.currentTimeMillis() + getMaxSchedTime();
			while (!_schedInfos.isEmpty()) {
				final List<ScheduleInfo<? extends Event>> schedInfos;
				synchronized (_schedInfos) { //must be thread safe
					schedInfos = new ArrayList<ScheduleInfo<? extends Event>>(_schedInfos);
					_schedInfos.clear();
				}
				for (Iterator<ScheduleInfo<? extends Event>> it = schedInfos.iterator(); it.hasNext();) {
					final ScheduleInfo<? extends Event> si = it.next();
					try {
						si.invoke();
					} catch (Throwable t) {
						synchronized (_schedInfos) { //add back not called
							int j = 0;
							while (it.hasNext())
								_schedInfos.add(j++, it.next());
						}
						if (t instanceof Exception)
							throw (Exception)t;
						throw (Error)t;
					}
				}
				if (System.currentTimeMillis() > max)
					break; //avoid if server push is coming too fast
			}
		}
	}
	/** The maximal allowed time to run scheduled listeners.
	 */
	private static long getMaxSchedTime() {
		if (_maxSchedTime == null) {
			//no need to be synchronized
			final String PROP = "org.zkoss.zk.ui.maxScheduleTime";
			final String val = Library.getProperty(PROP);
			if (val != null) {
				try {
					int v = Integer.parseInt(val); //unit: seconds
					if (v > 0)
						_maxSchedTime = ((long)v) * 1000;
				} catch (Throwable t) {
					log.warning("Ignored library property, "+PROP+": not a number, "+val);
				}
			}
			if (_maxSchedTime == null)
				_maxSchedTime = 5000L; //default: 5 seconds
		}
		return _maxSchedTime.longValue();
	}
	private static Long _maxSchedTime;
}
