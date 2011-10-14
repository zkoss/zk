/* Z30_grid_0015Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 12:56:21 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug grid-0015
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0015.zul,Z30,A,E,Grid,Paging")
class Z30_grid_0015Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<window>
				Use external paging component in two grids , when you click next page ,both the two grid should change to next page .
			<vbox>
			<paging id="pg" pageSize="4"/>
			<hbox>
				<grid id="grid1" width="300px" mold="paging" paginal="${pg}">
					<columns>
						<column label="Left"/>
						<column label="Right"/>
					</columns>
					<auxhead>
						<auxheader label="Whole" colspan="2"/>
					</auxhead>
					<rows>
						<row>
							<label value="Item 1.1"/>
							<label value="Item 1.2"/>
						</row>
						<row>
							<label value="Item 2.1"/>
							<label value="Item 2.2"/>
						</row>
						<row>
							<label value="Item 3.1"/>
							<label value="Item 3.2"/>
						</row>
						<row>
							<label value="Item 4.1"/>
							<label value="Item 4.2"/>
						</row>
						<row>
							<label value="Item 5.1"/>
							<label value="Item 5.2"/>
						</row>
						<row>
							<label value="Item 6.1"/>
							<label value="Item 6.2"/>
						</row>
						<row>
							<label value="Item 7.1"/>
							<label value="Item 7.2"/>
						</row>
					</rows>
				</grid>
				<grid  id="grid2" width="300px" mold="paging" paginal="${pg}">
					<columns>
						<column label="Left"/>
						<column label="Right"/>
					</columns>
					<rows>
						<row>
							<label value="Item A.1"/>
							<label value="Item A.2"/>
						</row>
						<row>
							<label value="Item B.1"/>
							<label value="Item B.2"/>
						</row>
						<row>
							<label value="Item C.1"/>
							<label value="Item C.2"/>
						</row>
						<row>
							<label value="Item D.1"/>
							<label value="Item D.2"/>
						</row>
						<row>
							<label value="Item E.1"/>
							<label value="Item E.2"/>
						</row>
						<row>
							<label value="Item F.1"/>
							<label value="Item F.2"/>
						</row>
					</rows>
				</grid>
			</hbox>
			<zscript><![CDATA[
				void addgd(int cnt) {
					for (int j = 0; ++j <= cnt;) {
						Row r = new Row();
						String prefix = "Item " + (grid.getRows().getChildren().size() + 1);
						new Label(prefix + "-L").setParent(r);
						new Label(prefix + "-C").setParent(r);
						new Label(prefix + "-R").setParent(r);
						r.setParent(gd.getRows());
					}
				}
				]]>
			</zscript>
			</vbox>
			</window>

    """

    runZTL(zscript,
        () => {
	        def clickThenValidate(selector:String,validator:()=>Unit ){
	            click(jq(selector));
	        	waitResponse()
	        	validator()
	        }          
	        def verifyRowContent(gridSelector:String,iterator:Iterator[String]) ={
		        val verify = iterator;
		        val list =  jq(gridSelector).find(".z-row").iterator();
		        while(list.hasNext()){
		          val row = list.next() ;
		          var text = verify.next();
//		          println(row.find(".z-label:first").text(),text)
				  verifyEquals(row.find(".z-label:first").text(),text);
				}          
	        }
	        verifyRowContent("$grid1",Iterator(
	        	"Item 1.1",
	        	"Item 2.1",
	        	"Item 3.1",
	        	"Item 4.1"
	        ));
	        
	        verifyRowContent("$grid2",Iterator(
	        	"Item A.1",
	        	"Item B.1",
	        	"Item C.1",
	        	"Item D.1"
	        ));	        
	        
	        clickThenValidate(".z-paging-next",()=>{
		        verifyRowContent("$grid1",Iterator(
		        	"Item 5.1",
		        	"Item 6.1",
		        	"Item 7.1"
		        ));
		        
		        verifyRowContent("$grid2",Iterator(
		        	"Item E.1",
		        	"Item F.1"
		        ));	          
	        });

	        clickThenValidate(".z-paging-prev",()=>{
		        verifyRowContent("$grid1",Iterator(
		        	"Item 1.1",
		        	"Item 2.1",
		        	"Item 3.1",
		        	"Item 4.1"
		        ));
		        
		        verifyRowContent("$grid2",Iterator(
		        	"Item A.1",
		        	"Item B.1",
		        	"Item C.1",
		        	"Item D.1"
		        ));	        
	          
	        });	        
    }
   );
  }
}