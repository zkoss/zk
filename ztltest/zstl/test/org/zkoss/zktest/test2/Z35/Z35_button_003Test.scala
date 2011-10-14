/* Z35_button_003Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 11:09:24 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z35

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug button-003
 * @author TonyQ
 *
 */
@Tags(tags = "Z35-button-003.zul,Z35,A,E,Button")
class Z35_button_003Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<?page id="testZul" title=" New ZUL Title" cacheable="false" 
				language="xul/html" zscriptLanguage="Java" contentType="text/html;charset=UTF-8"?>
			<?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit"?>
			<zk xmlns="http://www.zkoss.org/2005/zul" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.zkoss.org/2005/zul/zul.xsd">
			  <window title="Test button reference to processes" border="normal" width="400px">
			  Test button basic functions: onclick, disable, rightclick, doubleclick, onFocus(click near button and TAB into button), onBlur (TAB away from button), href should all be working, if any failed, its bug.
			    <zscript><![CDATA[
			  public void setMyLabel(String str){
			    self.setLabel(str);
			}
			
			  public void setDisableMe(){
			    self.setDisabled(true);
			    self.setLabel("Disabled OK");
			}
			
			]]></zscript>
			    <vbox>
			      <hbox>
			        <button id="btn1" label="ClickMe" onClick='setMyLabel("OnClick OK")'/>Single Click test</hbox>
			      <hbox>
			        <button id="btn2" label="ClickMe" onClick="setDisableMe()"/> Disable Test </hbox>
			      <hbox>
			        <button id="btn3" label="RightClickMe" onRightClick='setMyLabel("RightClick OK")'/> Right Click Test </hbox>
			      <hbox>
			        <button id="btn4" label="DoubleClickMe" onDoubleClick='setMyLabel("DoubleClick OK")'/> Double Click Test</hbox>
			      <hbox>
			        <button id="btn5" label="FocusOnMe" onFocus='setMyLabel("Focused OK")' tabindex="1"/>Focus gained Test </hbox>
			      <hbox>
			        <button id="btn6" label="BlurMe" onBlur='setMyLabel("Blurred OK")' tabindex="2"/>Focus lost Test </hbox>
			      <hbox>
			        <button id="btn7" label="http://www.google.com" href="http://www.google.com"/> Hyperlink Test </hbox>
			    </vbox>
			  </window>
			</zk>

    """;

    runZTL(zscript,
        () => {
        
        click(jq("$btn1"))
        waitResponse
        verifyEquals(widget("$btn1").get("label"),"OnClick OK");
        
        click(jq("$btn2"))
        waitResponse
        verifyTrue(widget("$btn2").is("disabled"));
        
        contextMenu(jq("$btn3"));
        waitResponse
        verifyEquals(widget("$btn3").get("label"),"RightClick OK");
        
        doubleClick(jq("$btn4"))
        waitResponse
        verifyEquals(widget("$btn4").get("label"),"DoubleClick OK");
        
        click(jq("$btn5"))
        waitResponse
        verifyEquals(widget("$btn5").get("label"),"Focused OK");
        
        click(jq("$btn6"))
        waitResponse
        verifyEquals(widget("$btn6").get("label"),"BlurMe");
        click(jq("$btn5"))
        waitResponse
        verifyEquals(widget("$btn6").get("label"),"Blurred OK");
        
    }
   );
  }
}