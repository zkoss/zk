/* B50_ZK_361Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 17 10:16:13 CST 2011 , Created by benbai
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
 * A test class for bug ZK-361
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-361.zul,A,E,Include,Fulfill,Portallayout")
class B50_ZK_361Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<?xml version="1.0" encoding="UTF-8"?>
			<?component name="include" extends="include" mode="defer" ?>
			<!--
			B50-ZK-361.zul
			
				Purpose:
					
				Description:
					
				History:
					Fri Aug 26 14:26:01 TST 2011, Created by jumperchen
			
			Copyright (C) 2011 Potix Corporation. All Rights Reserved.
			
			-->
			<window title="Grid with Group feature" border="normal">
				<html><![CDATA[  
					Please click the fill button, then you should not see any Javascript error.
				]]></html>
				
				<button id="btn" label="fill"/>
				<portallayout>
					<portalchildren width="50%" fulfill="btn.onClick">
						<panel height="200px" title="Popup">
							<panelchildren>
								<include src="/test2/Z35-portallayout-comp.zul?popup1=true" />
							</panelchildren>
						</panel>
						<panel height="500px" title="Chart">
							<panelchildren>
								<include src="/test2/Z35-portallayout-comp.zul?chart1=true" />
							</panelchildren>
						</panel>
					</portalchildren>
					<portalchildren width="50%" fulfill="btn.onClick">
						<panel height="120px" title="Fisheye">
							<panelchildren>
								<include src="/test2/Z35-portallayout-comp.zul?fisheye1=true" />
							</panelchildren>
						</panel>
					</portalchildren>
			
				</portallayout>
			
			</window>

    """

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");
        click(btn);
        waitResponse();

        verifyFalse("should not see any Javascript error",
            jq(".z-error").exists());
    }
   );

  }
}