/* Browser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov 27 17:22:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;

import org.zkoss.zkmob.impl.Zk;
import org.zkoss.zkmob.impl.ZkList;
import org.zkoss.zkmob.impl.ZkListItem;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The ZK Mobile Browser for Raw Mobile Interactive Language(RMIL).
 *
 * @author tomyeh
 */
public class Browser extends MIDlet {
	private static final int HISTORY_MAX = 10; //miximum history record
	
	private Display _display; //display of this MIDlet

	private Displayable _prev; //previous Displayable before pause
	private Displayable _curr; //current Displayble before go Home

	private Zk _zkMobile; //zkMobile desktop
	private Form _home; //zkMobile home page
	private Command _backCommand; //home page back command
	private Alert _exitAlert; //zkMobile exit alert
	private List _history; //zkMobile history list (most ten most recent record)
	private CommandListener _commandListener;
	private ItemCommandListener _itemCommandListener;
	
	private Zk _desktop; //current browsed desktop
	
	//super//
	protected void startApp() throws MIDletStateChangeException {
		init();
	}
	protected void pauseApp() {
		System.out.println("**pause");
		if (_display != null) {
			_prev = _display.getCurrent();
		}
	}
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		exit();
	}
	
	public Display getDisplay() {
		return _display;
	}
	
	public void setDesktop(Zk zk, String url) {
		_desktop = zk;
	    zk.setBrowser(this);
	    
	    if (url != null) {
	    	adjustHistory(url);
	    }
	}
	
	//private//
	/** Initializes this MIDlet. */
	private void init() {
		if (_prev != null) {
			System.out.println("++ resume");
			_display.setCurrent(_prev);
		} else if (_display == null) {
			//prepare the display
			_display = Display.getDisplay(this);
			
			InputStream is = null;
			try {
				//prepare the CommandListener
				_commandListener = new CommandListener() {
					public void commandAction(Command c, Displayable d) {
						final ZkComponent cmd = (ZkComponent) c;
						final String cmdid = cmd.getId();
						//home page
						if ("back".equals(cmd.getId())) {
							if (_curr != null) {
								_display.setCurrent(_curr);
							}
						//exitalert
						} else if ("yes".equals(cmdid)) {
							exit();
						} else if ("no".equals(cmdid)) {
							_display.setCurrent(_home);
						//hlist
						} else if ("hhome".equals(cmd.getId())) {
							_display.setCurrent(_home);
						} else if ("hgo".equals(cmdid)) {
							final int index = _history.getSelectedIndex();
							if (index >= 0) {
								final String url = _history.getString(index);
								UiManager.loadPageOnThread(Browser.this, url);
							}
						}
						
					}
				};
				
				//prepare the ItemCommandListener
				_itemCommandListener = new ItemCommandListener() {
					public void commandAction(Command c, Item i) {
						final ZkComponent cmd = (ZkComponent) c;
						final String cmdid = cmd.getId();
						if ("go".equals(cmdid)) {
							final TextField tf = (TextField) i;
							final String url = tf.getString();
							UiManager.loadPageOnThread(Browser.this, url);
						} else if ("hcmd".equals(cmdid)) {
							_display.setCurrent(_history);
						} else if ("ecmd".equals(cmdid)) {
							_display.setCurrent(_exitAlert);
						}
					}
				};
				
				//load the browser template
				is = getClass().getResourceAsStream("/pages/browser.rmil");
				_zkMobile = UiManager.loadPage(this, is, null, "~.");
				
				//prepare browser home page
				_home = (Form) _zkMobile.lookupUi("home");
				_home.setItemStateListener(null);
				_home.setCommandListener(_commandListener);
				
				//prepare browser home page back command
				_backCommand = (Command) _zkMobile.lookupUi("back");
				_home.removeCommand(_backCommand);
				
				//prepare exitAlert
				_exitAlert = (Alert) _zkMobile.lookupUi("exitalert");
				_exitAlert.setTimeout(Alert.FOREVER);
				_exitAlert.setCommandListener(_commandListener);
				
				//prepare history list
				_history = (List) _zkMobile.lookupUi("hlist");
				_history.setCommandListener(_commandListener);
				for (int j = 0; j < HISTORY_MAX; ++j) {
					final String historyURL = getAppProperty("zkmob-history"+j);
					if (historyURL == null) {
						break;
					}
					_history.append(historyURL, null);
				}

				//prepare items in the home page
				final Item url = (Item) _zkMobile.lookupUi("url");
				url.setItemCommandListener(_itemCommandListener);
				final Item history = (Item) _zkMobile.lookupUi("hist");
				history.setItemCommandListener(_itemCommandListener);
				final Item exit = (Item) _zkMobile.lookupUi("exit");
				exit.setItemCommandListener(_itemCommandListener);
				
				//preapre the zkmob.targetURL
				final String targetURL = getAppProperty("zkmob-targetURL");
				if (targetURL != null) {
					((TextField)url).setString(targetURL);
					
					final String autoRun = getAppProperty("zkmob-autoRun");
					if ("true".equals(autoRun)) {
						UiManager.loadPageOnThread(this, targetURL);
					}
				}
			} catch(SAXException ex) {
				alert(ex);
				exit();
			} catch(IOException ex) {
				alert(ex);
				exit();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch(IOException ex) {
						//ignore
					}
				}
			}
		}
	}

	//adjust history after successfully go to the specified URL
	private void adjustHistory(String url) {
		final int sz = _history.size();
		int j = 0;
		for(; j < sz; ++j) {
			final String old = _history.getString(j);
			if (old.equals(url)) {
				((ZkList)_history).superDelete(j);
				break;
			}
		}
		_history.insert(0, url, null);
		if (_history.size() > HISTORY_MAX) { //most ten records
			((ZkList)_history).superDelete(HISTORY_MAX);
		}
	}
	
	public void goHome(String url) {
		if (url != null) {
			final TextField urlText = (TextField) _zkMobile.lookupUi("url");
			urlText.setString(url);
		}
		Displayable curr = _display.getCurrent();
		if (curr != null && curr != _home && !(curr instanceof Alert)) {
			_curr = curr; //so home page's back command can back here
			if (_curr != null) { //home page add "back" command
				_home.addCommand(_backCommand);
			}
		}
		_display.setCurrent(_home);
	}
	
	//alert
	private void alert(Throwable ex) {
		final Alert al = new Alert("Exception:", "Cannot start ZK Mobile: "+ex.toString(), null /*image*/, AlertType.ERROR);
		al.setTimeout(Alert.FOREVER);
		_display.setCurrent(al);
	}
	
	//exit the midlet
	private void exit() {
		//clean up
		notifyDestroyed();
	}
}
