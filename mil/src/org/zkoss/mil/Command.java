/* Command.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 1, 2007 11:09:09 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * A command on the Mobile. Whenever a Command is "clicked" on the Mobile, it will fire onCommand to  
 * its parent component.
 * 
 * @author henrichen
 */
public class Command extends MilComponent {
	private final static int BACK = 2;
	private final static int CANCEL = 3;
	private final static int EXIT = 7;
	private final static int HELP = 5;
	private final static int ITEM = 8;
	private final static int OK = 4;
	private final static int SCREEN = 1;
	private final static int STOP = 6;
	
	private final static int SELECT = 0x100; //special command for JavaME List.setSelectCommand() only

	private static final long serialVersionUID = 200706011201L;
	private String _label = "OK";
	private String _llabel;
	private String _type = "ok";
	private int _priority = 1;
	
	public Command() {
	}
	
	/**
	 * A Command on the Mobile (the command priority is default to 1).
	 * 
	 * @param label label appear on the command
	 * @param type The command type on the Mobile (back, cancel, exit, help, item, ok, screen, stop).
	 */
	public Command(String label, String type) {
		this(label, null/*longLabel*/, type, 1);
	}

	/**
	 * A Command on the Mobile.
	 * 
	 * @param label label appear on the command
	 * @param type The command type on the Mobile (back, cancel, exit, help, item, ok, screen, stop).
	 */
	public Command(String label, String type, int priority) {
		this(label, null/*longLabel*/, type, priority);
	}

	/**
	 * A Command on the Mobile.
	 * 
	 * @param label label appear on the command
	 * @param longLabel long label apper on the command
	 * @param type The command type on the Mobile (back, cancel, exit, help, item, ok, screen, stop).
	 */
	public Command(String label, String longLabel, String type, int priority) {
		setLabel(label);
		setLongLabel(longLabel);
		setType(type);
		setPriority(priority);
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		if (label != null && label.length() == 0) {
			label = null;
		}
		if (label == null) {
			throw new NullPointerException("Label cannot be empty or null");
		}
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("lb", label);
		}
	}

	public String getLongLabel() {
		return _llabel;
	}

	public void setLongLabel(String label) {
		if (label != null && label.length() == 0) {
			label = null;
		}
		if (!Objects.equals(_llabel, label)) {
			_llabel = label;
			smartUpdate("ll", label);
		}
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		if (type != null &&  type.length() == 0) {
			type = null;
		}
		if (!Objects.equals(_type, type)) {
			_type = type;
			smartUpdate("tp", getCommandType(type));
		}
	}
	
	public int getPriority() {
		return _priority;
	}
	
	public void setPriority(int priority) {
		if (_priority != priority) {
			_priority = priority;
			smartUpdate("pr", priority);
		}
	}

	//--Component--//
	public void setParent(Component parent) {
		if ((parent != null && !(parent instanceof MilComponent)) || parent instanceof Listitem) {
			throw new UiException("Unsupported parent for command: "+parent);
		}
		super.setParent(parent);
	}

	/** Not childable. */
	public boolean isChildable() {
		return false;
	}

	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "tp", getCommandType(_type));
		HTMLs.appendAttribute(sb, "lb", encodeString(getLabel()));
		HTMLs.appendAttribute(sb, "ll", encodeString(getLongLabel()));
		HTMLs.appendAttribute(sb, "pr", getPriority());
		return sb.toString();
	}
	
	private int getCommandType(String type) {
		if ("back".equals(type)) {
			return BACK;
		} else if ("cancel".equalsIgnoreCase(type)) {
			return CANCEL;
		} else if ("exit".equalsIgnoreCase(type)) {
			return EXIT;
		} else if ("help".equalsIgnoreCase(type)) {
			return HELP;
		} else if ("item".equalsIgnoreCase(type)) {
			return ITEM;
		} else if ("ok".equalsIgnoreCase(type)) {
			return OK;
		} else if ("screen".equalsIgnoreCase(type)) {
			return SCREEN;
		} else if ("stop".equalsIgnoreCase(type)) {
			return STOP;
		} else if ("select".equalsIgnoreCase(type)) {
			return SELECT;
		} else {
			throw new IllegalArgumentException("Unsupport Command type: " + type);
		}
	}
}
