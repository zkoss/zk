/* Z35_button_001Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 09:48:17 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z35

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug button-001
 * @author TonyQ
 *
 */
@Tags(tags = "Z35-button-001.zul,Z35,C,E,Button")
class Z35_button_001Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<?page id="testZul" title=" New ZUL Title" cacheable="false" 
				language="xul/html" zscriptLanguage="Java" contentType="text/html;charset=UTF-8"?>
			<?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit"?>
			<zk xmlns="http://www.zkoss.org/2005/zul" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.zkoss.org/2005/zul/zul.xsd">
			  <window title="Testing buttons basic onClick functionality in window" border="normal" width="600px">
			
			1.Click the following button, it should show a message box.
			<button label="Click Here" onClick='Messagebox.show("If you see this message, test is completed ok.");'/>
			  </window>
			  <window>
			2.This is a button in window with no border.
			<button label="Click Here" onClick='Messagebox.show("If you see this message, test is completed ok.");'/>
			  </window>
			  <window border="normal" width="600px">
			    <vbox>
			3.A button in bordered window with vbox 
			<button label="Click Here" onClick='Messagebox.show("If you see this message, test is completed ok.");'/>
			    </vbox>
			  </window>
			</zk>

    """;

    runZTL(zscript,
        () => {
        def clickAlert ={
          click(jq(".z-messagebox-btn"));
        };
          
        click(jq("@button").eq(0));
        waitResponse
        verifyEquals("If you see this message, test is completed ok.",jq(".z-messagebox .z-label").text());
        clickAlert
        waitResponse
        
        click(jq("@button").eq(1));
        waitResponse
        verifyEquals("If you see this message, test is completed ok.",jq(".z-messagebox .z-label").text());
        clickAlert
        waitResponse
        
        click(jq("@button").eq(2));
        waitResponse
        verifyEquals("If you see this message, test is completed ok.",jq(".z-messagebox .z-label").text());
        clickAlert
        
    }
   );
  }
}