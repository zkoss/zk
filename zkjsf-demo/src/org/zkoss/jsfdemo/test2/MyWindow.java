package org.zkoss.jsfdemo.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class MyWindow extends Window {
	
	Component comp;
	
	public void onCreate() { 
		comp = this.getFellow("tb");
		changeDisplayText(getFellow("tbc"),((Textbox)comp).getValue());
		changeDisplayText(this.comp,"create");
	}

	public void onOK() { 
		changeDisplayText(this.comp,"OK");
	}

	public void onCancel() { 
		changeDisplayText(this.comp,"Cancel");
	}
	
	private void  changeDisplayText(Component tb, String text){
		((Textbox)tb).setValue(text);
	}
}
