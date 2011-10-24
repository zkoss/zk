/* B50_ZK_391Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 15:34:50 CST 2011 , Created by benbai
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
 * A test class for bug ZK-391
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-391.zul,A,E,Popup,onOpen")
class B50_ZK_391Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			<html><![CDATA[
			<ol>
			<li>mouseover and wait until the tootip showed for each label below twice</li>
			<li>the right side of tooltip should align with the right side of label it belongs.</li>
			</ol>
			]]></html>
				<window>
					<popup id="zulPu1"><label>content here</label></popup>
					<popup id="zulPu2">
						<attribute name="onOpen"><![CDATA[
							if (zulPu2.getChildren().size() == 0) { zulPu2.appendChild(new Label("content here")); }
						]]></attribute>
					</popup>
					<popup id="zulPu3">
						<attribute name="onOpen"><![CDATA[
							self.appendChild(new Label("content here"));
						]]></attribute>
					</popup>
					<vlayout id="container">
						<label id="lb1" tooltip="zulPu1, position=after_end">Popup created in zul - good positioning</label>
						<label id="lb2" tooltip="zulPu2, position=after_end">Popup created in zul, check for children and add child in Java - bad positioning, good on second hover</label>
						<label id="lb3" tooltip="zulPu3, position=after_end">Popup created in zul 2, add child with no check for children in Java - bad positioning, good on second hover</label>
						<label id="javaLbl">
							<attribute name="onCreate"><![CDATA[
								final Popup javaPu = new Popup();
								container.appendChild(javaPu);
								javaPu.setId("javaPu");
								javaPu.addEventListener("onOpen", new EventListener() {
								
									public void onEvent(Event e) throws Exception {
										if (javaPu.getChildren().size() == 0) {
											javaPu.appendChild(new Label("content here"));
										}
									}
								});
								javaLbl.setTooltip("javaPu, position=after_end");
							]]></attribute>
							Popup created in java, check for children and add child in Java - bad positioning, good on second hover
						</label>
					</vlayout>
				</window>
			</zk>

    }
 
   // Run syntax 2
    runZTL(zscript,
        () => {
        var (lb1: Widget,
    	     lb2: Widget,
    	     lb3: Widget,
    	     javaLbl: Widget) = (
    	        engine.$f("lb1"),
    	        engine.$f("lb2"),
    	        engine.$f("lb3"),
    	        engine.$f("javaLbl")
    	    );
        def checkPopup (lb: Widget, ppName: String) {
            mouseOver(lb);
            var t1: Long = System.currentTimeMillis();
	        var pp: Widget = engine.$f(ppName);

	        // wait at most 3 seconds
	        while (!pp.exists() && System.currentTimeMillis()-t1 <= 3000) {
	            sleep(300);
	        }
	        verifyTrue("popup should exist and visible",
	            pp.exists() && "visible".equals(pp.$n().get("style.visibility")) &&
	            	!pp.$n().get("style.display").contains("none"));
	        var ppRight: Int = Integer.parseInt(pp.$n().get("offsetLeft"))+jq(pp.$n()).outerWidth();
	        var lbRight: Int = Integer.parseInt(lb.$n().get("offsetLeft")) + jq(lb.$n()).outerWidth();
	        if (!ZK.is("ie6_"))
		        verifyTrue("the right side of popup should close to and slightly over the right side of label except IE6",
		            ppRight > lbRight && (ppRight - lbRight <= 10));
	        mouseOut(lb);
        }
        checkPopup(lb1, "zulPu1");
        checkPopup(lb2, "zulPu2");
        checkPopup(lb3, "zulPu3");
        checkPopup(javaLbl, "javaPu");
        
    }
   );

  }
}