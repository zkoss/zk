/* Z30_grid_0006Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:54:15 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug grid-0006
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0006.zul,Z30,B,E,Grid,Sorting")
class Z30_grid_0006Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<window title="the odd row's color shall be yellow (controlled by the style component).	test sorting">
				<style><![CDATA[
				tr.z-grid-odd td.z-row-inner, tr.z-grid-odd {
					background: yellow;
				}
				]]></style>
				<zscript><![CDATA[
				class Comp implements Comparator {
					private boolean _asc;
					public Comp(boolean asc) {
						_asc = asc;
					}
					public int compare(Object o1, Object o2) {
						String s1 = o1.getChildren().get(0).getValue(),
							s2 = o2.getChildren().get(0).getValue();
						int v = s1.compareTo(s2);
						return _asc ? v: -v;
					}
				}
				Comp asc = new Comp(true), dsc = new Comp(false);
				]]></zscript>
				<grid id="grid" width="500px" height="100px">
					<columns sizable="true">
						<column id="col" label="Order"  sortAscending="${asc}" sortDescending="${dsc}"/>
						<column label="Head 1"/>
						<column label="Head 2" align="center"/>
						<column label="Head 3" align="right"/>
					</columns>
					<rows>
						<row>
							<label value="Orange"/>
							<listbox mold="select">
								<listitem label="AB"/>
								<listitem label="CD"/>
							</listbox>
							<datebox/>
							<textbox rows="3"/>
						</row>
						<row>
							<label value="Apple"/>
							<label value="A11"/>
							<label value="A12"/>
							<label value="A13"/>
						</row>
						<row>
							<label value="Lemon"/>
							<checkbox checked="true" label="Option 1"/>
							<checkbox label="Option 2"/>
							<radiogroup>
								<radio label="Apple"/>
								<radio label="Orange" checked="true"/>
								<radio label="Lemon"/>
							</radiogroup>
						</row>
						<row>
							<label value="Tomato"/>
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
				<button id="btnWid" label="Change width" onClick="changeWidth()"/>
				<button id="addRow" label="Add row" onClick="addRow()"/>
				<button id="insRow" label="Insert row" onClick="insRow()"/>
				<zscript><![CDATA[
				void changeWidth() {
					col.setWidth("200px");
				}
				void addRow() {
					Row r = new Row();
					new Label("A31").setParent(r);
					new Label("A32").setParent(r);
					grid.getRows().appendChild(r);
				}
				void insRow() {
					Row r = new Row();
					new Label("Ins1").setParent(r);
					new Label("Ins2").setParent(r);
					grid.getRows().insertBefore(r, grid.getRows().getChildren().get(0));
				}
				addRow();
				]]></zscript>
			</window>
    }

    runZTL(zscript,
        () => {
        verifyTolerant(jq("$col").outerWidth(),120, 2);
        
        click(jq("$btnWid"))
        waitResponse
        println(jq("$col").outerWidth());
        verifyNotEquals(String.valueOf(jq("$col").outerWidth()),"120");
        verifyEquals(String.valueOf(jq("$col").outerWidth()),"200");
        

        click(jq("$col"))
        waitResponse


        def verifyRowContent(iterator:Iterator[String]) ={
	        val verify = iterator;
	        val list =  jq("@row").iterator();
	        while(list.hasNext()){
	          val row = list.next();
//	          println(row.find(".z-label:first").text());
			  verifyEquals(row.find(".z-label:first").text(),verify.next());
			}          
        }

        verifyRowContent(Iterator("A31","Apple","Lemon","Orange","Tomato"));
        
        click(jq("$addRow"))
        waitResponse
        verifyRowContent(Iterator("A31","Apple","Lemon","Orange","Tomato","A31"));

        click(jq("$insRow"))
        waitResponse
        verifyRowContent(Iterator("Ins1","A31","Apple","Lemon","Orange","Tomato","A31"));
        
        click(jq("$col"))
        waitResponse
        verifyRowContent(Iterator("Tomato","Orange","Lemon","Ins1","Apple","A31","A31"));
        
       	click(jq("$col"))
        waitResponse
        verifyRowContent(Iterator("A31","A31","Apple","Ins1","Lemon","Orange","Tomato"));
    }
   );
  }
}