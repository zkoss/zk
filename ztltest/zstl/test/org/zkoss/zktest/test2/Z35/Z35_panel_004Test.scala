/* Z35_panel_004Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 14:56:48 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z35

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug panel-004
 * @author TonyQ
 *
 */
@Tags(tags = "Z35-panel-004.zul,Z35,C,E,Panel")
class Z35_panel_004Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<window>
			<panel id="p1" title="Panel Component" border="normal" width="500px" maximizable="true" minimizable="true">
				<panelchildren>
					<grid width="100%">
						<columns>
							<column label="Name" />
							<column label="Description" />
						</columns>
						<rows>
							<row>
								<label value="Ivan" />
								<label value="MIS" />
							</row>
							<row>
								<label value="ohpizz" />
								<label value="Testing" />
							</row>
						</rows>
					</grid>
				</panelchildren>
			</panel>
					<button id="btn1" label="Change maximized">
						<attribute name="onClick">
								p1.maximized=!p1.maximized;
						</attribute>
					</button>
					<button id="btn2" label="Close">
						<attribute name="onClick">
							p1.open = !p1.open;
							self.label = p1.open == true ? "close" : "open";
						</attribute>
					</button>
					<button id="btn3" label="Change minimized">
						<attribute name="onClick">
								p1.minimized=!p1.minimized;
						</attribute>
					</button>
				<separator />	
				1. Click "Change maximized" button. There shouldn't be a space between panel and bottons.
				<separator />
				2. Then click "Close" button and open it. The space is disappeared.
				<separator />
				3. Then click "Change minimized" twice. The panel's layout shouldn't be out of expected.
			</window>

    """;

    runZTL(zscript,
        () => {
          val width = jq("$p1").outerWidth();
          click(jq("$btn1"));
          waitResponse()
          sleep(1000);
		  verifyNotEquals(width.toString(),jq("$p1").outerWidth().toString());
          verifyTrue(jq(".z-panel-body").isVisible());
          

          click(jq("$btn2"));
          waitResponse();
          sleep(800);
          verifyFalse(jq(".z-panel-body").isVisible());

          
          click(jq("$btn2"));
          waitResponse();
          sleep(800);
          verifyTrue(jq(".z-panel-body").isVisible());

          
          click(jq("$btn3"));
          waitResponse();
          verifyFalse(jq(".z-panel").isVisible());

          
          click(jq("$btn3"));
          waitResponse();
          verifyTrue(jq(".z-panel").isVisible());
          
    }
   );
  }
}