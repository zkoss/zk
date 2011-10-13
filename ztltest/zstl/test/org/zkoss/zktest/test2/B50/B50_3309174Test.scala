/* B50_3309174Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 16:37:39 CST 2011 , Created by benbai
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
 * A test class for bug 3309174
 * @author benbai
 *
 */
@Tags(tags = "B50-3309174.zul,A,E,Grid,Model")
class B50_3309174Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<html><![CDATA[
					<ol>
						<li>Click "remove 20" button.</li>
						<li>The first of item shall be "item 20"</li>
					</ol>
				]]></html>
				<window>
					<custom-attributes org.zkoss.zul.grid.rod="false" />
					<zscript><![CDATA[
						import java.util.*;
						import org.zkoss.zul.*;
						import org.zkoss.zk.ui.event.*;
						import org.zkoss.zk.ui.event.EventListener;
						List list = new ArrayList();
						for (int i = 0; i < 200; i++)
							list.add("item " + i);
						ListModelList model = new ListModelList(list);
					]]></zscript>
					<button id="btn" label="remove 20">
						<attribute name="onClick"><![CDATA[
							for (int i = 0; i < 20; i++)
								model.remove(0);
						]]></attribute>
					</button>
					<grid id="grid" model="${model}" width="100px" height="250px" />
				</window>
			</zk>

    }

    def executor = () => {
    	var (btn: Widget,
    	    grid: Widget) = (
    	        engine.$f("btn"),
    	        engine.$f("grid")
    	    );
		waitResponse();

		click(btn);
		waitResponse();
		verifyTrue(jq(jq(grid.$n("body")).find(".z-row-cnt").get(0)).find(".z-label").get(0).get("innerHTML").contains("20"));
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