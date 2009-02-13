/* AuRequest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:31:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.in.*;

/**
 * A request sent from the client to {@link org.zkoss.zk.ui.sys.UiEngine}.
 *
 * <p>Notice that {@link #activate} must be called in the activated execution.
 * Before calling this method, {@link #getPage}, {@link #getComponent}
 * and {@link #getCommand()} cannot be called.
 *
 * @author tomyeh
 */
public class AuRequest {
	private final Desktop _desktop;
	private Page _page;
	private Component _comp;
	private Command _cmd;
	private final String[] _data;
	/** Component's UUID. Used only if _comp is not specified directly. */
	private String _uuid;
	/** Command's ID. Used only if _cmd is not specified directly. */
	private String _cmdId;

	//-- static --//
	private static final Map _cmds = new HashMap();

	/** Returns whether the specified request name is valid.
	 * A request is a  event that are sent from the browser.
	 *
	 * <p>An request name is the ID of a command
	 * ({@link Command#getId}) which starts with "on".
	 */
	public static final boolean hasRequest(String cmdnm) {
		return _cmds.containsKey(cmdnm);
	}
	/** Returns the command of the specified name.
	 * It looks for the global commands only.
	 * For component-specific commands, use {@link ComponentCtrl#getCommand}.
	 * @exception CommandNotFoundException if the command is not found
	 */
	public static final Command getCommand(String name) {
		final Command cmd = (Command)_cmds.get(name);
		if (cmd == null)
			throw new CommandNotFoundException("Unknown command: "+name);
		return cmd;
	}
	/** Adds a new command. Called only by Command's contructor. */
	/*package*/ static final void addCommand(Command cmd) {
		if (_cmds.put(cmd.getId(), cmd) != null)
			throw new InternalError("Replicated command: "+cmd);
	}

	/** Constructor for a request sent from a component.
	 *
	 * @param desktop the desktop containing the component; never null.
	 * @param uuid the component ID (never null)
	 * @param cmdId the command ID; never null.
	 * @param data the data; might be null.
	 * @since 3.0.5
	 */
	public AuRequest(Desktop desktop, String uuid, String cmdId, String[] data) {
		if (desktop == null || uuid == null || cmdId == null)
			throw new IllegalArgumentException();
		_desktop = desktop;
		_uuid = uuid;
		_cmdId = cmdId;
		_data = data;
	}
	/** Constructor for a request sent from a component.
	 *
	 * @param desktop the desktop containing the component; never null.
	 * @param uuid the component ID (never null)
	 * @param cmd the command; never null.
	 * @param data the data; might be null.
	 */
	public AuRequest(Desktop desktop, String uuid, Command cmd, String[] data) {
		if (desktop == null || uuid == null || cmd == null)
			throw new IllegalArgumentException();
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
			throw new IllegalArgumentException();
		_desktop = desktop;
		_cmd = cmd;
		_data = data;
	}

	/** Activates this request.
	 * <p>It can be accessed only in the activated execution.
	 * Before calling this method, {@link #getPage}, {@link #getComponent}
	 * and {@link #getCommand} cannot be called.
	 * @since 3.0.5
	 */
	public void activate()
	throws ComponentNotFoundException, CommandNotFoundException {
		if (_uuid != null) {
			_comp = _desktop.getComponentByUuidIfAny(_uuid);

			if (_comp != null) {
				_page = _comp.getPage();
			} else {
				_page = _desktop.getPageIfAny(_uuid); //it could be page UUID
				if (_page == null)
					throw new ComponentNotFoundException("Component not found: "+_uuid);
			}
			_uuid = null;
		}

		if (_cmdId != null) {
			if (_comp != null)
				_cmd = ((ComponentCtrl)_comp).getCommand(_cmdId);
			if (_cmd == null)
				_cmd = AuRequest.getCommand(_cmdId);
			_cmdId = null;
		}
	}

	/** Returns the desktop; never null.
	 */
	public Desktop getDesktop() {
		return _desktop;
	}
	/** Returns the page that this request is applied for, or null
	 * if this reqeuest is a general request -- regardless any page or
	 * component.
	 */
	public Page getPage() {
		return _page;
	}
	/** Returns the component that this request is applied for, or null
	 * if it applies to the whole page or a general request.
	 * @exception ComponentNotFoundException if the component is not found
	 */
	public Component getComponent() {
		return _comp;
	}
	/** @deprecated As of release 3.0.5, use {@link #getComponent}
	 * instead.
	 */
	public String getComponentUuid() {
		return _comp != null ? _comp.getUuid(): _uuid;
	}
	/** Returns the command; never null.
	 */
	public Command getCommand() {
		return _cmd;
	}
	/** Returns the data of the command, might be null.
	 */
	public String[] getData() {
		return _data;
	}

	//-- Object --//
	public final boolean equals(Object o) { //prevent override
		return this == o;
	}
	public String toString() {
		final String cmdId = _cmd!=null ? _cmd.getId(): _cmdId;
		if (_comp != null)
			return "[comp="+_comp+", cmd="+cmdId+']';
		else if (_page != null)
			return "[page="+_page+", cmd="+cmdId+']';
		else
			return "[uuid="+_uuid+", cmd="+cmdId+']';
	}

	//-- predefined commands --//
	static {
		new BookmarkChangedCommand(Events.ON_BOOKMARK_CHANGE,
			Command.IGNORE_OLD_EQUIV);
		new CheckCommand(Events.ON_CHECK, 0);
		new ClientInfoCommand(Events.ON_CLIENT_INFO, Command.IGNORE_OLD_EQUIV);
		new UpdateResultCommand("updateResult", 0);
		new DropCommand(Events.ON_DROP, 0);

		new DummyCommand("dummy",
			Command.IGNORABLE|Command.IGNORE_OLD_EQUIV|Command.SKIP_IF_EVER_ERROR);
		new EchoCommand("echo", Command.SKIP_IF_EVER_ERROR);

		new ErrorCommand(Events.ON_ERROR, Command.IGNORE_OLD_EQUIV);

		new GenericCommand(Events.ON_BLUR, Command.IGNORE_OLD_EQUIV);
		new GenericCommand(Events.ON_CLOSE, 0);
		new GenericCommand(Events.ON_FOCUS, Command.IGNORE_OLD_EQUIV);
		new GenericCommand(Events.ON_NOTIFY, 0);
		new GenericCommand(Events.ON_SORT,
			Command.SKIP_IF_EVER_ERROR|Command.IGNORE_OLD_EQUIV);
		new TimerCommand(Events.ON_TIMER, Command.IGNORE_OLD_EQUIV);
		new GenericCommand(Events.ON_USER, 0);

		new GetUploadInfoCommand("getUploadInfo", Command.IGNORABLE);

		new InputCommand(Events.ON_CHANGE, Command.IGNORE_IMMEDIATE_OLD_EQUIV);
		new InputCommand(Events.ON_CHANGING,
			Command.SKIP_IF_EVER_ERROR|Command.IGNORABLE);

		new KeyCommand(Events.ON_CANCEL,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);
		new KeyCommand(Events.ON_CTRL_KEY,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);
		new KeyCommand(Events.ON_OK,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);

		new MoveCommand(Events.ON_MOVE, Command.IGNORE_OLD_EQUIV);
		new SizeCommand(Events.ON_SIZE, Command.IGNORE_OLD_EQUIV);
		new InnerWidthCommand("onInnerWidth", Command.IGNORE_OLD_EQUIV);

		new MouseCommand(Events.ON_CLICK,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);
		new MouseCommand(Events.ON_DOUBLE_CLICK,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);
		new MouseCommand(Events.ON_RIGHT_CLICK,
			Command.SKIP_IF_EVER_ERROR|Command.CTRL_GROUP);

		new OpenCommand(Events.ON_OPEN, 0);
		new RemoveCommand("remove", 0);
		new RedrawCommand("redraw", 0);
		new RemoveDesktopCommand("rmDesktop", 0);
		new RenderCommand(Events.ON_RENDER, Command.IGNORE_OLD_EQUIV);
			//z.loaded is set only if replied from server, so it is OK
			//to drop if any previous -- which means users are scrolling fast

		new ScrollCommand(Events.ON_SCROLLING,
			Command.SKIP_IF_EVER_ERROR|Command.IGNORABLE);
		new ScrollCommand(Events.ON_SCROLL, Command.IGNORE_IMMEDIATE_OLD_EQUIV);

		new SelectCommand(Events.ON_SELECT, 0);
		new SelectionCommand(Events.ON_SELECTION, Command.IGNORE_OLD_EQUIV);

		new ZIndexCommand(Events.ON_Z_INDEX, Command.IGNORE_OLD_EQUIV);
	}
}
