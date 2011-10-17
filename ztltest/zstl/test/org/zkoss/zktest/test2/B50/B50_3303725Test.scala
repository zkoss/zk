/* B50_3303725Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 16:42:18 CST 2011 , Created by benbai
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
 * A test class for bug 3303725
 * @author benbai
 *
 */
@Tags(tags = "B50-3303725.zul,A,E,Panel,Portallayout")
class B50_3303725Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<html><![CDATA[
					<ol>
						<li>Drag Panel 2 to the left column (between 1 and 3). If the 3 Panels do not occupy the full height of Portallayout (the green box), it is a bug.</li>
					</ol>
				]]></html>
				<portallayout id="ptl" width="500px" height="500px" style="border: 1px solid green;">
					<portalchildren hflex="1" vflex="1">
						<panel id="p1" title="Panel 1" vflex="1" border="normal">
							<panelchildren>
								Panel Children 1
							</panelchildren>
						</panel>
						<panel id="p3" title="Panel 3" vflex="1" border="normal">
							<panelchildren>
								Panel Children 3
							</panelchildren>
						</panel>
					</portalchildren>
					<portalchildren hflex="1">
						<panel id="p2" title="Panel 2" vflex="1" border="normal">
							<panelchildren>
								Panel Children 2
							</panelchildren>
						</panel>
					</portalchildren>
				</portallayout>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var (ptl: Widget,
             p1: Widget,
             p2: Widget,
             p3: Widget) = (
                 engine.$f("ptl"),
                 engine.$f("p1"),
                 engine.$f("p2"),
                 engine.$f("p3"));
        def dragDrop(from: Element, fromPos: String, to: Element, toPos: String) {
        	mouseDownAt(from, fromPos);
			mouseMoveAt(to, toPos);
			mouseUpAt(to, toPos);
			waitResponse();
        }
        dragDrop(p2.$n("cap"), "100,10", jq(p1.$n()).find(".z-panel-children").get(0), "100,180");

        var h1: Int = jq(ptl.$n("cave")).height();
        var h2: Int = jq(p1.$n()).outerHeight() +
        			  jq(p2.$n()).outerHeight() +
        			  jq(p3.$n()).outerHeight();
        verifyTrue("the sum of the height of the three panel ("+h2
            +") should close to the height of the Portallayout ("+h1
            +")",
            Math.abs(h1-h2)<10);
        verifyEquals(jq(ptl.$n()).find(".z-panel").get(1).get("id"), p2.$n().get("id"));
    }
   );

  }
}