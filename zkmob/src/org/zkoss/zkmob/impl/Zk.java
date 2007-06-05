/* Page.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 9:41:29 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;

import org.xml.sax.SAXException;
import org.zkoss.zkmob.Event;
import org.zkoss.zkmob.Inputable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.UpdateHandler;
import org.zkoss.zkmob.UpdateRequest;
import org.zkoss.zkmob.ZkComponent;

/**
 * A virtual component that holds information regarding ZK server's Desktop, Page, Device, and other things.
 * @author henrichen
 *
 */
public class Zk implements ZkComponent {
	private Display _display;
	private Displayable _current;
	private Hashtable _uiMap = new Hashtable(16);
	private Inputable _toSendOnChange;

	private String _dtid; //desktop id
	private String _action; //zk_action
	private int _procto; //zk_procto, millisecond to show progress meter
	private int _tipto; //zk_tipto, millisecond to popup the tip
	private String _ver; //zk_ver
	private String _hostURL; //URL prefix for this desktop
	private String _jsessionid; //session id
	
	private Vector _events = new Vector(); //event list

	private CommandListener _cmdListener;
	private ItemStateListener _itemStateListener;
	private ItemCommandListener _itemCommandListener;
	
	public Zk(String dtid, String action, int procto, int tipto, String ver, String hostURL) {
		_dtid = dtid;

		int j = action.indexOf(";jsessionid");
		if (j >= 0) {
			_jsessionid = action.substring(j);
//			action = action.substring(0, j);
		}
		_hostURL = hostURL;
		_action = _hostURL + action;
		_procto = procto;
		_tipto = tipto;
		_ver = ver;
		
		_cmdListener = new CommandListener() {
			public void commandAction(Command command, Displayable disp) {
				final ZkComponent comp = (ZkComponent) disp;
				System.out.println("CommandListener, displayable="+ comp);
				sendOnChange(comp);

				final String id = comp.getId();
				System.out.println("id="+ id+", command="+((ZkCommand)command).getId());

				final ZkCommand cmd = (ZkCommand) command;
				send(new Event(comp.getId(), "onCommand", new Object[] {cmd.getId()}), true);
			}
		};

		_itemCommandListener = new ItemCommandListener() {
			public void commandAction(Command command, Item item) {
				final ZkComponent comp = (ZkComponent) item;
				System.out.println("ItemCommandListener, item="+ comp);
				sendOnChange(comp);

				final String id = comp.getId();
				System.out.println("id="+ id+", command="+((ZkCommand)command).getId());

				final ZkCommand cmd = (ZkCommand) command;
				send(new Event(comp.getId(), "onCommand", new Object[] {cmd.getId()}), true);
			}
		};

		_itemStateListener = new ItemStateListener() {
			public void itemStateChanged(Item item) {
				ZkComponent comp = (ZkComponent) item;
				System.out.println("ItemStateListener, item="+ comp);
				sendOnChange(comp);
				final String id = comp.getId();
				if (comp instanceof ChoiceGroup) {
					System.out.println("onSelect,id="+ id);
				} else if (comp instanceof Gauge) {
					System.out.println("onSlide,id="+ id);
				} else if (comp instanceof TextField) {
					final ZkTextField tf = (ZkTextField) comp;
					final Boolean onChanging = tf.getOnChanging();
					if (onChanging != null) {
						send(new Event(comp.getId(), "onChanging", new Object[] {((TextField)comp).getString()}), onChanging.booleanValue());
					}
					System.out.println("onChanging,id="+ id);
				} else if (comp instanceof DateField) {
					System.out.println("onChange,id="+ id);
				} else if (comp instanceof CustomItem) {
					System.out.println("onNotify,id="+ id);
				}
			}
		};
	}
	
	public String getHostURL() {
		return _hostURL;
	}
	
	//will be called whenever possible
	private void sendOnChange(ZkComponent comp) {
		if (_toSendOnChange != null && _toSendOnChange != comp) {
			final Inputable toSend = _toSendOnChange; 
			_toSendOnChange = null;
			send(new Event(((ZkComponent)toSend).getId(), "onChange"
					, new Object[] {toSend.getString()}), toSend.getOnChange().booleanValue());
		}
		
		if (comp instanceof Inputable) {
			final Inputable icomp = (Inputable) comp;
			final Boolean onChange = icomp.getOnChange();
			if (onChange != null) {
				_toSendOnChange = icomp; //prepare to send the onChange event
			}
		}
	}

	/** register an Ui Object to an id.
	 * @param id the component id
	 * @param comp the component
	 */
	public void registerUi(String id, ZkComponent comp) {
		_uiMap.put(id, comp);
	}
	
	/** unregister an Ui Object.
	 * @param id the component id
	 */
	public void unregisterUi(String id) {
		_uiMap.remove(id);
	}
	
	/** lookup a registered component by id.
	 * @param id the component id
	 */
	public ZkComponent lookupUi(String id) {
		return (ZkComponent) _uiMap.get(id);
	}
	
	public Displayable getCurrent() {
		return _current;
	}
	
	public void setCurrent(Displayable current) {
		_current = current;
		if (_display != null) {
			_display.setCurrent(_current);
		}
	}

	public Vector getEvents() {
		return _events;
	}
	
	public CommandListener getCommandListener() {
		return _cmdListener;
	}
	
	public ItemStateListener getItemStateListener() {
		return _itemStateListener;
	}
	
	public ItemCommandListener getItemCommandListener() {
		return _itemCommandListener;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _dtid;
	}
	
	public Zk getZk() {
		return this;
	}
	
	public void setAttr(String attr, String val) {
		//do nothing
	}
	
	public void addCommand(Command cmd) {
		//do nothing
	}
	
	//utility to send MIL event from ZK Mobile to ZK Server
	private void send(Event evt, boolean asap) {
		_events.addElement(evt);
		if (!asap) {
			evt.setImplicit(true);
		} else {
			sendEvents();
		}
	}
	
	private void sendEvents() {
		final StringBuffer sb = new StringBuffer(64);
		int j = 0;
		for (Enumeration e = _events.elements(); e.hasMoreElements(); ++j) {
			final Event evt = (Event) e.nextElement();
			sb.append("&cmd."+j+"="+evt.getCmd()+"&uuid."+j+"="+evt.getUuid());
			if (evt.getData() != null) {
				for (int k = 0; k < evt.getData().length; ++k) {
					Object data = evt.getData()[k];
					sb.append("&data."+j+"="
						+ (data != null ? encodeURIComponent(data.toString()): "zk_null~q"));
				}
			}
		}
		
		if (sb.length() == 0) return; //nothing to do
		_events.removeAllElements();

		final String content = "dtid=" + _dtid + sb.toString();
		updateOnThread(_action, content);
	}

	private void updateOnThread(String url, String request) {
		new Thread(new UpdateRequest(this, url, request)).start();
	}
	
	public void update(String url, String request) {
		try {
			myUpdate(url, request);
		} catch (IOException e) {
			final Alert alert = new Alert("Exception:", e.toString(), null, AlertType.ERROR);
			alert.setTimeout(Alert.FOREVER);
			_display.setCurrent(alert);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void myUpdate(String url, String request) throws IOException, SAXException {
		HttpConnection conn = null;
		InputStream is = null;

		try {
		    conn = (HttpConnection)Connector.open(url);
		    is = UiManager.request(conn, request);
		    // Load the update response
		    UiManager.getSAXParser().parse(is, new UpdateHandler(this));
		} finally {
			if (is != null)	is.close();
			if (conn != null) conn.close();
		}
	}

	public void setDisplay(Display disp) {
		_display = disp;
		if (_current != null) {
			_display.setCurrent(_current);
		}
	}
	
	public void executeResponse(RTag rtag) {
		final String cmd = rtag.getCommand();
		final String[] data = rtag.getData();
		
		if ("setAttr".equals(cmd)) {
			executeSetAttr(data);
		} else if ("alert".equals(cmd)) {
			executeAlert(data);
		} else if ("rm".equals(cmd)) {
			executeRm(data);
		} else if ("addAft".equals(cmd)) {
			executeAddAft(data);
		} else {
			System.out.println("Unknown response command: "+cmd);
//			throw new IllegalArgumentException("Unknown response command: "+cmd);
		}
	}

	//setAttr command
	private void executeSetAttr(String[] data) {
		final String uuid = data[0];
		final String attr = data[1];
		final String val = data[2];
		
		final ZkComponent comp = lookupUi(uuid);
		if (comp != null) {
			comp.setAttr(attr, val);
		}
	}
	
	//alert command
	private void executeAlert(String[] data) {
		//TODO: exception alert can add a local image icon.
		final Alert alert = new Alert("Exception", data[0], null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER); //15 seconds
		_display.setCurrent(alert);
	}
	
	//rm command
	private void executeRm(String[] data) {
		final String uuid = data[0];
		
		final ZkComponent comp = lookupUi(uuid);
		if (comp instanceof ZkListItem) {
			final Listable owner = ((ZkListItem)comp).getOwner();
			final int index = owner.indexOf(comp);
			owner.delete(index);
		} else if (comp instanceof Itemable) {
			final ZkForm form = (ZkForm) ((Itemable)comp).getForm();
			form.removeItem((Item) comp);
		} else if (comp instanceof Displayable) {
			final Displayable disp = (Displayable) comp;
			if (_display.getCurrent() == disp) { //the current showing display
				_current = null;
				_display.setCurrent(new Form(null)); //blank Form
			}
		}
		
		unregisterUi(uuid);
	}
	
	//addAft command
	private void executeAddAft(String[] data) {
		final String uuid = data[0];
		final String rmil = data[1].trim();
		final ZkComponent ref = lookupUi(uuid);
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(rmil.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
		Vector comps = null;
		try {
			comps = UiManager.createComponents(this, is, _hostURL);
		} catch (IOException e) {
			// ignore
		} catch (SAXException e) {
			throw new IllegalArgumentException("addAft failed: " + e.toString());
		} //load the rmil and generate a set of components

		if (ref instanceof ZkListItem) {
			final Listable owner = ((ZkListItem)ref).getOwner();
			final int index = owner.indexOf(ref);
			if (index == owner.size()) { //append to the last one
				for(Enumeration it = comps.elements(); it.hasMoreElements(); ) {
					final ZkListItem comp = (ZkListItem) it.nextElement();
					final String label = comp.getLabel();
					final String imagesrc = comp.getImage();
					owner.append(comp, label, null);
					
					//we drop the ZkTmpListItem to register the smaller ZkListItem
					UiManager.loadImageOnThread(comp, imagesrc);
				}
			} else {
				int k = 0;
				for(Enumeration it = comps.elements(); it.hasMoreElements(); ++k) {
					final ZkListItem comp = (ZkListItem) it.nextElement();
					final String label = comp.getLabel();
					final String imagesrc = comp.getImage();
					owner.insert(index+k, comp, label, null);

					UiManager.loadImageOnThread(comp, imagesrc);
				}
			}
		} else if (ref instanceof Item) {
			final ZkForm form = (ZkForm) ((Itemable)ref).getForm();
			final int index = form.indexOf((Item)ref) + 1;
			if (index == form.size()) { //append to the last one
				for(Enumeration it = comps.elements(); it.hasMoreElements(); ) {
					final Item comp = (Item) it.nextElement();
					form.append(comp);
				}
			} else {
				int k = 0;
				for(Enumeration it = comps.elements(); it.hasMoreElements(); ++k) {
					final Item comp = (Item) it.nextElement();
					form.insert(index+k, comp);
				}
			}
		} else if (ref instanceof Displayable) {
			final Displayable disp = (Displayable) ref;
			if (_display.getCurrent() == disp) { //the current showing display
				_current = null;
				_display.setCurrent(new Form(null)); //blank Form
			}
		}
	}
	
	/** Does the HTTP encoding for the URI location.
	 * For example, '%' is translated to '%25'.
	 *
	 * @param s the string to encode; null is OK
	 * @return the encoded string or null if s is null
	 * @see #encodeURIComponent
	 */
	public static final String encodeURI(String s) {
		try {
			return encodeURI0(s, URI_UNSAFE);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	/** Does the HTTP encoding for a URI query parameter.
	 * For example, '/' is translated to '%2F'.
	 * Both name and value must be encoded seperately. Example,
	 * <code>encodeURIComponent(name) + '=' + encodeURIComponent(value)</code>.
	 *
	 * @param s the string to encode; null is OK
	 * @return the encoded string or null if s is null
	 * @see #addToQueryString(StringBuffer,String,Object)
	 * @see #encodeURI
	 */
	public static final String encodeURIComponent(String s) {
		try {
			return encodeURI0(s, URI_COMP_UNSAFE);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/** Encodes a string to HTTP URI compliant by use of
	 * {@link Charsets#getURICharset}.
	 *
	 * <p>Besides two-byte characters, it also encodes any character found
	 * in unsafes.
	 *
	 * @param unsafes the set of characters that must be encoded; never null.
	 * It must be sorted.
	 */
	private static final String encodeURI0(String s, String unsafes)
	throws UnsupportedEncodingException {
		if (s == null)
			return null;

		final String charset = "UTF-8";
		final byte[] in = s.getBytes(charset);
		final byte[] out = new byte[in.length * 3];//at most: %xx
		int j = 0, k = 0;
		for (; j < in.length; ++j) {
			//Though it is ok to use '+' for ' ', Jetty has problem to
			//handle space between chinese characters.
			final char cc = (char)(((int)in[j]) & 0xff);
			if (cc >= 0x80 || cc <= ' ' || unsafes.indexOf(cc) >= 0) {
				out[k++] = (byte)'%';
				String cvt = Integer.toHexString(cc);
				if (cvt.length() == 1) {
					out[k++] = (byte)'0';
					out[k++] = (byte)cvt.charAt(0);
				} else {
					out[k++] = (byte)cvt.charAt(0);
					out[k++] = (byte)cvt.charAt(1);
				}
			} else {
				out[k++] = in[j];
			}
		}
		return j == k ? s: new String(out, 0, k, charset);
	}
	/** unsafe character when that are used in url's localtion. */
	private static final String URI_UNSAFE = "`%^{}[]\\\"<>|";
	/** unsafe character when that are used in url's query. */
	private static final String URI_COMP_UNSAFE = "`%^{}[]\\\"<>|$&,/:;=?";
}
