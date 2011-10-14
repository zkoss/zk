/* B50_2943533Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 12:19:48 CST 2011 , Created by benbai
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
 * A test class for bug 2943533
 * @author benbai
 *
 */
@Tags(tags = "B50-2943533.zul,A,E,Grid,Column,Menupopup,Messagebox")
class B50_2943533Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<window title="" border="normal" width="400">
			    <grid>
			        <columns id="cols" sizable="true" menupopup="auto" >
			            <column label="A" />
			            <column label="B" />
			            <column label="C" />
			            <column label="D" />
			        </columns>
			        <rows>
			            <row>
			                <cell>
			                    <label value="a" />
			                </cell>
			                <cell>
			                    <label value="b" />
			                </cell>
			                <cell>
			                    <label value="c" />
			                </cell>
			                <cell>
			                    <label value="d" />
			                </cell>
			            </row>
			        </rows>
			    </grid>
			    <button id="btn" label="Click me should alert a message."></button>
			    <zscript><![CDATA[
			        import org.zkoss.zk.ui.event.EventListener;
			import org.zkoss.zk.ui.event.Events;
			
			btn.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
			Messagebox.show("If you can see the message, the bug is fixed.");
			}
			});
			    ]]></zscript>
			</window>
			

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");

        verifyFalse(jq(".z-window-highlighted").exists());
        click(btn);
        waitResponse();
        verifyTrue(jq(".z-window-highlighted").exists());
    }
   );
  }
}