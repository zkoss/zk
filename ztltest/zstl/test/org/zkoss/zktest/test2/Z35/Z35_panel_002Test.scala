/* Z35_panel_002Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 16:58:43 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z35

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts

/**
 * A test class for bug panel-002
 * @author TonyQ
 *
 */
@Tags(tags = "Z35-panel-002.zul,Z35,A,E,Panel")
class Z35_panel_002Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<window>
			<panel id="p1" title="Panel Component" border="normal" width="500px" height="150px">
				<panelchildren>
					1. Click "Change floatable" button and the panel should float.
					<separator />
					2. Click "Change movable" button and move the panel.(If the panel doesn't float, it shouldn't be able to move.)
					<separator />
					<button id="btnFloat" label="Change floatable">
						<attribute name="onClick">
								p1.floatable=!p1.floatable;
						</attribute>
					</button>
					<button id="btnMove" label="Change movable">
						<attribute name="onClick">	
								p1.movable=!p1.movable;
						</attribute>
					</button>
					<separator />
					3. Click following buttons and it should work well.
				</panelchildren>
			</panel>
					<button id="btn1" label="Change collapsible">
						<attribute name="onClick">
								p1.collapsible=!p1.collapsible;
						</attribute>
					</button>
					<button id="btn2" label="Change minimizable">
						<attribute name="onClick">
								p1.minimizable=!p1.minimizable;
						</attribute>
					</button>
					<button id="btn3" label="Change maximizable">
						<attribute name="onClick">
								p1.maximizable=!p1.maximizable;
						</attribute>
					</button>
					<button id="btn4" label="Change closable">
						<attribute name="onClick">
								p1.closable=!p1.closable;
						</attribute>
					</button>
			</window>
    """;

    runZTL(zscript,
        () => {
        def clickThenValidate(selector:String,validator:()=>Unit ){
            Scripts.triggerMouseEventAt(getWebDriver(), jq(selector), "click", "2,2");        
        	waitResponse();
        	validator();
        }
        
        clickThenValidate("$btn1",()=>{
          	jq(".z-panel-exp").isVisible();
        });
        clickThenValidate("$btn2",()=>{
          jq(".z-panel-exp").isVisible();
          jq(".z-panel-min").isVisible();
        });
        clickThenValidate("$btn3",()=>{
          jq(".z-panel-exp").isVisible();
          jq(".z-panel-min").isVisible();
          jq(".z-panel-max").isVisible();
        });
        clickThenValidate("$btn4",()=>{
          jq(".z-panel-exp").isVisible();
          jq(".z-panel-min").isVisible();
          jq(".z-panel-max").isVisible();
          jq(".z-panel-close").isVisible();
        });

        clickThenValidate("$btn3",()=>{
          jq(".z-panel-exp").isVisible();
          jq(".z-panel-min").isVisible();
          jq(".z-panel-close").isVisible();
        });        
        clickThenValidate("$btn3",()=>{
          jq(".z-panel-exp").isVisible();
          jq(".z-panel-min").isVisible();
          jq(".z-panel-max").isVisible();
          jq(".z-panel-close").isVisible();
        });                
        
        clickThenValidate("$btnFloat",()=>{
          verifyEquals(jq(".z-panel").css("position"),"absolute" );
        });
        
        verifyTrue(jq(".z-panel-header-move").length() == 0 );

        
        clickThenValidate("$btnMove",()=>{
          verifyTrue(jq(".z-panel-header-move").length() ==1 );
        });        
        
        clickThenValidate("$btnFloat",()=>{
          verifyNotEquals(jq(".z-panel").css("position"),"absolute" );
          //We can't move it when it's not in floatable mode.
          verifyTrue(jq(".z-panel-header-move").length() == 0 );
        });

        
        clickThenValidate(".z-panel-exp",()=>{
          sleep(200);
          verifyFalse(jq(".z-panel-body").isVisible());
        });
        
        clickThenValidate(".z-panel-exp",()=>{
          sleep(200);
          verifyTrue(jq(".z-panel-body").isVisible());
        });
        
    }
   );
  }
}