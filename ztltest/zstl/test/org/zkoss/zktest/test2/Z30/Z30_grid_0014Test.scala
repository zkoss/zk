/* Z30_grid_0014Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 12:17:51 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug grid-0014
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0014.zul,Z30,A,E,Grid,Paging")
class Z30_grid_0014Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<?xml version="1.0" encoding="UTF-8"?>
			<window>
				<grid id="grid" width="300px" mold="paging" pageSize="5">
					<columns>
					
						<column label="Head 1"/>
						<column label="Head 2" align="center"/>
						<column label="Head 3" align="right"/>
					</columns>
					<rows>
					</rows>
				</grid>
				<button id="btnAdd" label="add 5 children" onClick="add(5)"/>
				<button id="btnChangeFirst" label="Change the first label" onClick="changeLabel(0)"/>
				<button id="btnChangeLast" label="Change the last label" onClick="changeLabel(-1)"/>
				<button id="btnInvalide" label="redraw" onClick="grid.invalidate()"/>
				<button id="btnPaging" label="set page size to 10" onClick="grid.paginal.pageSize = 10"/>
				<zscript><![CDATA[
				void add(int cnt) {
					for (int j = 0; ++j <= cnt;) {
						Row r = new Row();
						String prefix = "Item " + (grid.getRows().getChildren().size() + 1);
						new Label(prefix + "-L").setParent(r);
						new Label(prefix + "-C").setParent(r);
						new Label(prefix + "-R").setParent(r);
						r.setParent(grid.getRows());
					}
				}
				void changeLabel(int j) {
					int sz = grid.getRows().getChildren().size();
					if (j < 0) j = sz - 1;
					if (j < 0 || j >= sz) {
						alert("No label to change");
					} else {
						Row r = grid.getRows().getChildren().get(j);
						r.getChildren().get(0).setValue("Updated "+j);
					}
				}
				]]></zscript>
			</window>

	""";

    runZTL(zscript,
        () => {
                  
        def clickThenValidate(selector:String,validator:()=>Unit ){
            click(jq(selector));
        	waitResponse();
        	validator();
        }
        
        def verifyRowContent(iterator:Iterator[String]) ={
	        val verify = iterator;
	        val list =  jq("@row").iterator();
	        while(list.hasNext()){
	          val row = list.next();
			  verifyEquals(row.find(".z-label:first").text(),verify.next());
			}          
        }
        
        def invalidateTest(iterator:Iterator[String]){
          click(jq("$btnInvalide"));
          waitResponse;
          verifyRowContent(iterator);
        }
        
        verifyEquals(jq("@row").length.toString(),"0");
        verifyFalse(jq("@paging").is(":visible"));

        clickThenValidate("$btnAdd",()=>{
        	verifyEquals(jq("@row").length.toString(),"5");
        	verifyRowContent(Iterator("Item 1-L","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))          
        });
        
        clickThenValidate("$btnChangeFirst",()=>{
        	verifyEquals(jq("@cell").eq(0).find(".z-label").text(),"Updated 0");
        });
        
        clickThenValidate("$btnAdd",()=>{
          	verifyFalse(jq("@paging").is(":visible"));
       	  	verifyEquals(jq("@row").length.toString(),"5");
          	verifyRowContent(Iterator("Updated 0","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))
        });
        
        clickThenValidate("$btnChangeLast",()=>{
          	verifyRowContent(Iterator("Updated 0","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))
          	invalidateTest(Iterator("Updated 0","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))
        });
     
        
        clickThenValidate(".z-paging-next",()=>{
   	  		verifyEquals(jq("@row").length.toString(),"5");          
          	verifyRowContent(Iterator("Item 6-L","Item 7-L","Item 8-L","Item 9-L","Updated 9"))
          	invalidateTest(Iterator("Item 6-L","Item 7-L","Item 8-L","Item 9-L","Updated 9"))
        });
        
        clickThenValidate(".z-paging-prev",()=>{
            verifyEquals(jq("@row").length.toString(),"5");
          	verifyRowContent(Iterator("Updated 0","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))
          	invalidateTest(Iterator("Updated 0","Item 2-L","Item 3-L","Item 4-L","Item 5-L"))
        });
        
    }
   );
  }
}