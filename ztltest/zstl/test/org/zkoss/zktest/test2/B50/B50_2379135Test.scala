/* B50_2379135Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 16:06:53 CST 2011 , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.B50

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug 2379135
 * @author benbai
 *
 */
@Tags(tags = "B50-2379135.zul,A,E,Listbox,context menu")
class B50_2379135Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<window>
			<html><![CDATA[  
			<ol>
			<li>Right click the second item, and then it shall be selected</li>
			<li>Right click the third item, and then it shall be selected</li>
			<li>Right click the button, and the fourth item shalln't be selected.</li>
			</ol>
			]]></html>
			<zscript><![CDATA[
			void show() {
				i.value = "selcted: " + l.selectedItem.label;
			}
			]]></zscript>
			<listbox id="l">
				<listitem id="li1" label="First"/>
				<listitem id="li2" label="Second (right-click)" onRightClick="show()"/>
				<listitem id="li3" label="Third (context)" context="editPopup"/>
				<listitem>
					<listcell><button id="btn1" label="context but no select" context="editPopup"/></listcell>
				</listitem>
			</listbox>
			<label id="i" multiline="true"/>
			<menupopup id="editPopup" onOpen="show()">
			    <menuitem label="Undo"/>
			    <menuitem label="Redo"/>
			    <menu label="Sort">
					<menupopup>
				        <menuitem label="Sort by Name" autocheck="true"/>
				        <menuitem label="Sort by Date" autocheck="true"/>
					</menupopup>
			    </menu>
			</menupopup>
			</window>

    }

    def executor = () => {
    	var (li1: Widget,
    	     li2: Widget,
    	     li3: Widget,
    	     btn1: Widget,
    	     l: Widget) = (
    	        engine.$f("li1"),
    	        engine.$f("li2"),
    	        engine.$f("li3"),
    	        engine.$f("btn1"),
    	        engine.$f("l")
    	    );
		waitResponse();

		contextMenu(li2);
		waitResponse();
		verifyTrue(l.$n().get("innerHTML").contains("Second"));
		contextMenu(li3);
		waitResponse();
		verifyTrue(l.$n().get("innerHTML").contains("Third"));
		contextMenu(btn1);
		waitResponse();
		verifyTrue(l.$n().get("innerHTML").contains("Third"));
    }
   // Run syntax 1 
   runZTL(zscript, executor);
   
   // Run syntax 2
   /**
    runZTL(zscript,
        () => {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   );
    */
    /** create widget example
		var tree: Widget = engine.$f("tree");
		var listbox: Widget = engine.$f("listbox");
		waitResponse();
	*/
   /** trigger mouse event example
    Scripts.triggerMouseEventAt(getWebDriver(), inner1, "click", "5,5");
    */
   /** detect whether exception exists example
   		verifyFalse(jq(".z-window-highlighted").exists());
   		verifyFalse(jq(".z-window-modal").exists())
	*/
	/** detect browser
		if (ZK.is("ie6_") || ZK.is("ie7_"))
	*/
  }
}