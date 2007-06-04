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
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * The ZK Mobile Browser for Raw Mobile Interactive Language(RMIL).
 *
 * @author tomyeh
 */
public class Browser extends MIDlet {
	private Display _disp;
	private UiManager _uiManager;
	private TextBox _urlTextBox;
	private Alert _exitAlert;
	
	private Command _exitCommand = new Command("Exit", Command.EXIT, 1);
	private Command _backCommand = new Command("Back", Command.BACK, 1);
	private Command _cancelCommand = new Command("Cancel", Command.CANCEL, 1);
	private Command _itemCommand = new Command("Item", Command.ITEM, 1);
	private Command _screenCommand = new Command("Screen", Command.SCREEN, 1);
	private Command _stopCommand = new Command("Stop", Command.STOP, 1);
	private Command _okCommand = new Command("OK", Command.OK, 1);
	private Command _helpCommand = new Command("Help", Command.HELP, 1);
	
	//super//
	protected void startApp() throws MIDletStateChangeException {
		init();
	}
	protected void pauseApp() {
	}
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		exit();
	}
	//package//
	UiManager getUiManager() {
		return _uiManager;
	}
	
	//private//
	/** Initializes this MIDlet. */
	private void init() {
		if (_disp == null) {
			//prepare the display
			_disp = Display.getDisplay(this);
			
			//prepare the UiManager
			_uiManager = new UiManager();
			
			//location textbox
			_urlTextBox = new TextBox("URL", "http://localhost:8080/zkdemo-all/test.mil", 256, TextField.URL);
			_urlTextBox.addCommand(_exitCommand);
			_urlTextBox.addCommand(_okCommand);
			_urlTextBox.setCommandListener(new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					if (c == _exitCommand) {
						_disp.setCurrent(_exitAlert);
					} else if (c == _okCommand) {
						String url = ((TextBox)d).getString();
						//load the page
						_uiManager.loadPageOnThread(_disp, url);
					}
				}
			});
			_disp.setCurrent(_urlTextBox);

			//prepare a exit confirm alert
			_exitAlert = new Alert("Are you sure?", "This will make you leave the zmobi Client. Are you sure?", null, AlertType.CONFIRMATION);
			_exitAlert.setTimeout(Alert.FOREVER);
			_exitAlert.addCommand(_backCommand);
			_exitAlert.addCommand(_exitCommand);
			_exitAlert.setCommandListener(new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					if (c == _exitCommand) {
						exit();
					} else if (c == _backCommand){
						_disp.setCurrent(_urlTextBox);
					}
				}
			});
		}
	}

	private void exit() {
		//clean up
		notifyDestroyed();
	}
}
