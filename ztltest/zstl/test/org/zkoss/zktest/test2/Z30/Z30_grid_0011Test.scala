/* Z30_grid_0011Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 15:37:39 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug grid-0011
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0011.zul,Z30,B,E,Grid,Paging")
class Z30_grid_0011Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<window title="Test the paging mold" width="500px">
				<grid mold="paging" onCreate="self.pageSize=3">
					<columns>
						<column label="Index"/>
						<column label="Head 1"/>
						<column label="Head 2" align="center"/>
						<column label="Head 3" align="right"/>
					</columns>
					<rows>
						<row>
							1
							<listbox mold="select">
								<listitem label="AB"/>
								<listitem label="CD"/>
							</listbox>
							<datebox/>
							<textbox rows="3"/>
						</row>
						<row>
							2
							<label value="A11"/>
							<label value="A12"/>
							<label value="A13"/>
						</row>
						<row>
							3
							<checkbox checked="true" label="Option 1"/>
							<checkbox label="Option 2"/>
							<radiogroup>
								<radio label="Apple"/>
								<radio label="Orange" checked="true"/>
								<radio label="Lemon"/>
							</radiogroup>
						</row>
						<row>
							4
							<checkbox checked="true" label="Option 1"/>
							<checkbox label="Option 2"/>
							<radiogroup orient="vertical">
								<radio label="Apple"/>
								<radio label="Orange" checked="true"/>
								<radio label="Lemon"/>
							</radiogroup>
						</row>
						<row>
							5
							<listbox mold="select">
								<listitem label="AB"/>
								<listitem label="CD"/>
							</listbox>
							<datebox/>
							<textbox rows="3"/>
						</row>
						<row>
							6
							<label value="A11"/>
							<label value="A12"/>
							<label value="A13"/>
						</row>
						<row>
							7
							<checkbox checked="true" label="Option 1"/>
							<checkbox label="Option 2"/>
							<radiogroup>
								<radio label="Apple"/>
								<radio label="Orange" checked="true"/>
								<radio label="Lemon"/>
							</radiogroup>
						</row>
						<row>
							8
							<checkbox checked="true" label="Option 1"/>
							<checkbox label="Option 2"/>
							<radiogroup orient="vertical">
								<radio label="Apple"/>
								<radio label="Orange" checked="true"/>
								<radio label="Lemon"/>
							</radiogroup>
						</row>
					</rows>
				</grid>
			</window>"""
    runZTL(zscript,
        () => {
        def verifyRowContent(iterator:Iterator[String]) ={
	        val verify = iterator;
	        val list =  jq("@row").iterator();
	        while(list.hasNext()){
	          val row = list.next();
			  verifyEquals(row.find(".z-label:first").text().replaceAll("[\t \n]+",""),verify.next());
			  
			}          
        }          
         
        verifyEquals(String.valueOf(jq("@row").length),"3");
        verifyNotEquals(String.valueOf(jq("@row").length),"8");
        
        verifyRowContent(Iterator("1","2","3"));
        
		click(jq("@checkbox:first"));
		verifyFalse(jq("@checkbox:first").get(0).is("checked"));
        
        
		click(jq(".z-paging-next"));
        waitResponse
        
        verifyRowContent(Iterator("4","5","6"));
		click(jq("@checkbox:first"));
		verifyFalse(jq("@checkbox:first").get(0).is("checked"));

		
		click(jq(".z-paging-next"));
        waitResponse
		
        
        verifyRowContent(Iterator("7","8"));
		click(jq("@checkbox:first"));
		verifyFalse(jq("@checkbox:first").get(0).is("checked"));
        
        
		click(jq(".z-paging-prev"));
        waitResponse
		verifyRowContent(Iterator("4","5","6"));
		verifyFalse(jq("@checkbox:first").get(0).is("checked"));		

		
		click(jq(".z-paging-prev"));
        waitResponse
		verifyRowContent(Iterator("1","2","3"));
		verifyFalse(jq("@checkbox:first").get(0).is("checked"));
		
    }
   );
  }
}