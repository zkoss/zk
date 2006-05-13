/* AuRequest.java

{{IS_NOTE
	$Id: AuRequest.java,v 1.16 2006/04/14 02:55:36 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:31:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.sys.PageCtrl;
import com.potix.zk.ui.sys.ComponentsCtrl;
import com.potix.zk.au.impl.*;

/**
 * A request sent from the client to {@link com.potix.zk.ui.sys.UiEngine}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.16 $ $Date: 2006/04/14 02:55:36 $
 */
public class AuRequest {
	private final Desktop _desktop;
	private Page _page;
	private Component _comp;
	/** Component's UUID. Used only if _comp is not specified directly. */
	private final String _uuid;
	private final Command _cmd;
	private final String[] _data;

	//-- static --//
	private static final Map _cmds = new HashMap();

	/** Returns whether the specified request name is valid.
	 * A request is a  event that are sent from the browser.
	 *
	 * <p>An request name is the ID of a command
	 * ({@link AuRequest.Command#getId}) which starts with "on".
	 */
	public static final boolean hasRequest(String cmdnm) {
		return _cmds.containsKey(cmdnm);
	}
	/** Returns the command of the specified name.
	 * @exception CommandNotFoundException if the command is not found
	 */
	public static final Command getCommand(String name) {
		final Command cmd = (Command)_cmds.get(name);
		if (cmd == null)
			throw new CommandNotFoundException("Unknown command: "+name);
		return cmd;
	}

	/** The onClick event (used with {@link com.potix.zk.ui.event.MouseEvent}).
	 */
	public static final Command ON_CLICK = new MouseCommand("onClick", true);
	/** The onDblClick event (used with {@link com.potix.zk.ui.event.MouseEvent}).
	 */
	public static final Command ON_DBL_CLICK = new MouseCommand("onDblClick", true);
	/** The onOK event (used with {@link com.potix.zk.ui.event.Event}).
	 */
	public static final Command ON_OK = new KeyCommand("onOK", true);
	/** The onCancel event (used with {@link com.potix.zk.ui.event.Event}).
	 */
	public static final Command ON_CANCEL = new KeyCommand("onCancel", true);
	/** The onCtrlKey event (used with {@link com.potix.zk.ui.event.Event}).
	 */
	public static final Command ON_CTRL_KEY = new KeyCommand("onCtrlKey", true);
	/** The onChange event (used with {@link com.potix.zk.ui.event.InputEvent}).
	 */
	public static final Command ON_CHANGE = new InputCommand("onChange", false);
	/** The onChanging event (used with {@link com.potix.zk.ui.event.InputEvent}).
	 */
	public static final Command ON_CHANGING = new InputCommand("onChanging", true);
	/** The onError event (used with {@link com.potix.zk.ui.event.ErrorEvent}).
	 */
	public static final Command ON_ERROR = new ErrorCommand("onError", false);
	/** The onScroll event (used with {@link com.potix.zk.ui.event.ScrollEvent}).
	 */
	public static final Command ON_SCROLL = new ScrollCommand("onScroll", false);
	/** The onScrolling event (used with {@link com.potix.zk.ui.event.ScrollEvent}).
	 */
	public static final Command ON_SCROLLING = new ScrollCommand("onScrolling", true);
	/** The onSelect event (used with {@link com.potix.zk.ui.event.SelectEvent}).
	 */
	public static final Command ON_SELECT = new SelectCommand("onSelect", false);
	/** The onCheck event (used with {@link com.potix.zk.ui.event.CheckEvent}).
	 */
	public static final Command ON_CHECK = new CheckCommand("onCheck", false);
	/** The onMove event (used with {@link com.potix.zk.ui.event.MoveEvent}).
	 */
	public static final Command ON_MOVE = new MoveCommand("onMove", false);
	/** The onZIndex event (used with {@link com.potix.zk.ui.event.ZIndexEvent}).
	 */
	public static final Command ON_Z_INDEX = new ZIndexCommand("onZIndex", false);
	/** The onOpen event (used with {@link com.potix.zk.ui.event.OpenEvent}).
	 */
	public static final Command ON_OPEN = new OpenCommand("onOpen", false);
	/** The onShow event (used with {@link com.potix.zk.ui.event.ShowEvent}).
	 */
	public static final Command ON_SHOW = new ShowCommand("onShow", false);
	/** The onClose event (used with {@link com.potix.zk.ui.event.Event})
	 * used to denote the close button is pressed.
	 */
	public static final Command ON_CLOSE = new GenericCommand("onClose", false);
	/** The onRender event (used with {@link com.potix.zk.ui.ext.Render}).
	 */
	public static final Command ON_RENDER = new RenderCommand("onRender", false);
	/** The onTimer event (used with {@link com.potix.zk.ui.event.Event}).
	 * Sent when a timer is up.
	 */
	public static final Command ON_TIMER = new GenericCommand("onTimer", false);
	/** The onFocus event (used with {@link com.potix.zk.ui.event.Event}).
	 * Sent when a component gets a focus.
	 */
	public static final Command ON_FOCUS = new GenericCommand("onFocus", false);
	/** The onBlur event (used with {@link com.potix.zk.ui.event.Event}).
	 * Sent when a component loses a focus.
	 */
	public static final Command ON_BLUR = new GenericCommand("onBlur", false);
	/** The onDrop event (used with {@link com.potix.zk.ui.event.DropEvent}).
	 * Sent when a component is dragged and drop to another.
	 */
	public static final Command ON_DROP = new DropCommand("onDrop", false);
	/** A request for removing the specified desktop.
	 */
	public static final Command REMOVE_DESKTOP = new RemoveDesktopCommand("rmDesktop", false);
	/** A dummy request.
	 * It does nothing but triggers an execution to process pending
	 * requests, if any.
	 */
	public static final Command DUMMY = new DummyCommand("dummy", true);
	/** Ask to remove the specified component.
	 * <p>data: null
	 */
	public static final Command REMOVE = new RemoveCommand("remove", false);
	/** Asks to invoke {@link com.potix.zk.ui.ext.Updatable#setResult}.
	 * <p>data[0]: the content ID used to retreive the result from
	 * the desktop's attribute.
	 */
	public static final Command DO_UPDATABLE = new DoUpdatableCommand("doUpdatable", false);

	/** The onNotify event (used with {@link com.potix.zk.ui.event.Event}).
	 * It is not used by any component, but it is, rather, designed to
	 * let users add customized events.
	 */
	public static final Command ON_NOTIFY = new GenericCommand("onNotify", false);

	/** Constructor for a request sent from a component.
	 * Since we cannot invoke {@link Desktop#getComponentByUuid} without
	 * activating an execution, we have to use this method.
	 *
	 * @param desktop the desktop containing the component; never null.
	 * @param uuid the component ID (never null)
	 * @param cmd the command; never null.
	 * @param data the data; might be null.
	 */
	public AuRequest(Desktop desktop, String uuid, Command cmd, String[] data) {
		if (desktop == null || uuid == null || cmd == null)
			throw new IllegalArgumentException("null");

		_desktop = desktop;
		_uuid = uuid;
		_cmd = cmd;
		_data = data;
	}
	/** Constructor for a general request sent from client.
	 * This is usully used to ask server to log or report status.
	 *
	 * @param cmd the command; never null.
	 * @param data the data; might be null.
	 */
	public AuRequest(Desktop desktop, Command cmd, String[] data) {
		if (desktop == null || cmd == null)
			throw new IllegalArgumentException("null");
		_desktop = desktop;
		_uuid = null;
		_cmd = cmd;
		_data = data;
	}

	/** Returns the desktop; never null. */
	public final Desktop getDesktop() {
		return _desktop;
	}
	/** Returns the page that this request is applied for, or null
	 * if this reqeuest is a general request -- regardless any page or
	 * component.
	 * @exception ComponentNotFoundException if the page is not found
	 */
	public final Page getPage() {
		init();
		return _page;
	}
	private void init() {
		if (_page == null && _uuid != null) {
			_comp = _desktop.getComponentByUuidIfAny(_uuid);
			if (_comp != null) {
				_page = _comp.getPage();
			} else if (!ComponentsCtrl.isUuid(_uuid)) {
				_page = _desktop.getPage(_uuid);
			} else {
				throw new ComponentNotFoundException("Component not found: "+_uuid);
			}
		}
	}
	/** Returns the component that this request is applied for, or null
	 * if it applies to the whole page or a general request.
	 * @exception ComponentNotFoundException if the component is not found
	 */
	public final Component getComponent() {
		init();
		return _comp;
	}
	/** Returns the UUID.
	 */
	public final String getComponentUuid() {
		return _uuid != null ? _uuid: _comp != null ? _comp.getUuid(): null;
	}
	/** Returns the command; never null.
	 */
	public final Command getCommand() {
		return _cmd;
	}
	/** Returns the data of the command, might be null.
	 */
	public final String[] getData() {
		return _data;
	}

	//-- Object --//
	public final boolean equals(Object o) { //prevent override
		return this == o;
	}
	public String toString() {
		if (_uuid != null)
			return "[uuid="+_uuid+", cmd="+_cmd+']';
		else if (_comp != null)
			return "[comp="+_comp+", cmd="+_cmd+']';
		else
			return "[page="+_page+", cmd="+_cmd+']';
	}

	/** Represents a command of a request.
	 * Each command is unique no matter {@link #getId} is the same or not.
	 * All commands must be declared as static members of {@link AuRequest}
	 * such that they will be initialized sequentially at begining.
	 * Also, if a command will generate an event, it shall register the
	 * event in its constructor.
	 */
	abstract public static class Command {
		private final String _id;
		private final boolean _skipIfEverError;
		/**
		 * @param skipIfEverError whether to skip this command if
		 * previous commands ever caused an error. In other words,
		 * whether to skip this command when {@link #process(AuRequest,boolean)} is called
		 * with errorEver = true;
		 * This argument is used to control the behavior of {@link #process(AuRequest,boolean)}.
		 */
		protected Command(String id, boolean skipIfEverError) {
			_id = id;
			_skipIfEverError = skipIfEverError;

			if (_cmds.put(id, this) != null)
				throw new InternalError("Replicated command: "+id);
		}
		/** Returns ID of this command. */
		public final String getId() {
			return _id;
		}
		public final String toString() {
			return _id;
		}
		public final boolean equals(Object o) { //prevent override
			return this == o;
		}

		/** Derived must override this method to process this command.
		 */
		abstract protected void process(AuRequest request);
		/** Called to process the specified request.
		 * You don't override this method directly.
		 * Rather, override {@link #process(AuRequest)}.
		 *
		 * @param everError whether any error ever occured before
		 * processing this command.
		 * Notice that multiple commands might be processed in one
		 * execution.
		 */
		public final void process(AuRequest request, boolean everError) {
			if (!_skipIfEverError || !everError)
				process(request);
		}
	}
}
