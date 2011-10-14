/* B50_3100455Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 11:10:46 CST 2011 , Created by benbai
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
 * A test class for bug 3100455
 * @author benbai
 *
 */
@Tags(tags = "B50-3100455.zul,A,E,Frozen,Grid")
class B50_3100455Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<html><![CDATA[
					<ul>
						<li>Scroll to make C visible</li>
						<li>Click "load data" to load data and check if C is still visible</li>
						<li>Click "load data" again to reload data and check if C is still visible</li>
						<li>Click "invalidate header" to see if C is still visible</li>
						<li>Click "add model data" to see if C is still visible</li>
					</ul>
				]]></html>
			
				<zscript><![CDATA[
					import java.util.*;
					
					void loadData() {
						List datas = new ArrayList();
						for(int i=1;i<4;i++)
							datas.add(new String[]{"A"+i,"B"+i,"C"+i});
						grid.setModel(new ListModelList(datas));
						listbox.setModel(new ListModelList(datas));
					}
					void addModelData() {
						List datas = new ArrayList();
						for(int i=1;i<4;i++)
							datas.add(new String[]{"A"+i,"B"+i,"C"+i});
						((ListModelList)grid.getModel()).add(new String[]{"A","B","C"});
						((ListModelList)listbox.getModel()).add(new String[]{"A","B","C"});
					}
					
					void addRow(){
						Row row = new Row();
						new Label("A").setParent(row);
						new Label("B").setParent(row);
						new Label("C").setParent(row);
						rows.appendChild(row);
						
						Listitem item = new Listitem();
						new Listcell("A").setParent(item);
						new Listcell("B").setParent(item);
						new Listcell("C").setParent(item);
						listbox2.appendChild(item);
					}
					
					RowRenderer rdr = new RowRenderer() {
						public void render(Row row, Object data0) throws Exception {
							String[] data = (String[])data0;
							new Label(data[0]).setParent(row);
							new Label(data[1]).setParent(row);
							new Label(data[2]).setParent(row);
						}
					};
					
					ListitemRenderer idr = new ListitemRenderer() {
						public void render(Listitem item, Object data0) throws Exception {
							String[] data = (String[])data0;
							new Listcell(data[0]).setParent(item);
							new Listcell(data[1]).setParent(item);
							new Listcell(data[2]).setParent(item);
						}
					};
				]]></zscript>
			
				<button id="btn1" label="load data" onClick="loadData()"></button>
				<button id="btn2" label="invalidate header" onClick="cols.invalidate();listhead.invalidate()"/>
				<button id="btn3" label="add model data" onClick="addModelData()"/>
				<hlayout>
					<grid id="grid" width="300px" height="200px" rowRenderer="${rdr}">
						<custom-attributes org.zkoss.zul.grid.rod="false"/>
						<frozen columns="1"/>
						<columns id="cols">
							<column label="A" width="150px"/>
							<column label="B" width="150px"/>
							<column label="C" width="150px"/>
						</columns>
					</grid>
					<listbox id="listbox" width="300px" height="200px" itemRenderer="${idr}">
						<custom-attributes org.zkoss.zul.listbox.rod="false"/>
						<frozen columns="1"/>
						<listhead id="listhead">
							<listheader label="A" width="150px"/>
							<listheader label="B" width="150px"/>
							<listheader label="C" width="150px"/>
						</listhead>
					</listbox>
				</hlayout>
				<html><![CDATA[
					<ul>
						<li>Scroll to make C visible</li>
						<li>Click "add Row" to see if C is still visible</li>
					</ul>
				]]></html>
				<button id="btn4" label="add Row" onClick="addRow()"/>
				<hlayout>
					<grid id="grid2" width="300px" height="100px" rowRenderer="${rdr}">
						<custom-attributes org.zkoss.zul.grid.rod="false"/>
						<frozen columns="1"/>
						<columns>
							<column label="A" width="150px"/>
							<column label="B" width="150px"/>
							<column label="C" width="150px"/>
						</columns>
						<rows id="rows">
							<row>
								<label value="A"/>
								<label value="B"/>
								<label value="C"/>
							</row>
						</rows>
					</grid>
					<listbox id="listbox2" width="300px" height="100px">
						<custom-attributes org.zkoss.zul.listbox.rod="false"/>
						<frozen columns="1"/>
						<listhead >
							<listheader label="A" width="150px"/>
							<listheader label="B" width="150px"/>
							<listheader label="C" width="150px"/>
						</listhead>
						<listitem>
							<listcell label="A"/>
							<listcell label="B"/>
							<listcell label="C"/>
						</listitem>
					</listbox>
				</hlayout>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var (grid: Widget,
            grid2: Widget,
    	    listbox: Widget,
    	    listbox2: Widget,
    	    btn1: Widget,
    	    btn2: Widget,
    	    btn3: Widget,
    	    btn4: Widget) = (
    	        engine.$f("grid"),
    	        engine.$f("grid2"),
    	        engine.$f("listbox"),
    	        engine.$f("listbox2"),
    	        engine.$f("btn1"),
    	        engine.$f("btn2"),
    	        engine.$f("btn3"),
    	        engine.$f("btn4")
    	    );
        var bodyWidth1: Int = jq(grid.$n("body")).outerWidth();
        var bodyWidth2: Int = jq(grid2.$n("body")).outerWidth();
        var bodyWidth3: Int = jq(listbox.$n("body")).outerWidth();
        var bodyWidth4: Int = jq(listbox2.$n("body")).outerWidth();

        jq(grid.$n("frozen")).find(".z-frozen-inner").get(0).eval("scrollLeft = 300");
        jq(listbox.$n("frozen")).find(".z-frozen-inner").get(0).eval("scrollLeft = 300");
        jq(grid2.$n("frozen")).find(".z-frozen-inner").get(0).eval("scrollLeft = 300");
        jq(listbox2.$n("frozen")).find(".z-frozen-inner").get(0).eval("scrollLeft = 300");

        def checkAllVisible() {
        	verifyTrue("the offsetLeft of third column= " +
        	    Integer.parseInt(jq(grid.$n("body")).find(".z-grid-faker").find(".z-column").get(2).get("offsetLeft")) +
        	    "should smaller then the tltalWdith/3*2 ("+(bodyWidth1/3*2)+")",
        	    Integer.parseInt(jq(grid.$n("body")).find(".z-grid-faker").find(".z-column").get(2).get("offsetLeft")) < (bodyWidth1/3*2));
        	verifyTrue("the offsetLeft of third column= " +
        	    Integer.parseInt(jq(listbox.$n("body")).find(".z-listheader").get(2).get("offsetLeft")) +
        	    "should smaller then the tltalWdith/3*2 ("+(bodyWidth2/3*2)+")",
        	    Integer.parseInt(jq(listbox.$n("body")).find(".z-listheader").get(2).get("offsetLeft")) < (bodyWidth2/3*2));
        	verifyTrue("the offsetLeft of third column= " +
        	    Integer.parseInt(jq(grid2.$n("body")).find(".z-row-inner").get(2).get("offsetLeft")) +
        	    "should smaller then the tltalWdith/3*2 ("+(bodyWidth3/3*2)+")",
        	    Integer.parseInt(jq(grid2.$n("body")).find(".z-row-inner").get(2).get("offsetLeft")) < (bodyWidth3/3*2));
        	verifyTrue("the offsetLeft of third column= " +
        	    Integer.parseInt(jq(listbox2.$n("rows")).find(".z-listcell").get(2).get("offsetLeft")) +
        	    "should smaller then the tltalWdith/3*2 ("+(bodyWidth4/3*2)+")",
        	    Integer.parseInt(jq(listbox2.$n("rows")).find(".z-listcell").get(2).get("offsetLeft")) < (bodyWidth4/3*2));
        }
        checkAllVisible();

        click(btn1);
        waitResponse();
        checkAllVisible();

        click(btn2);
        waitResponse();
        checkAllVisible();

        click(btn3);
        waitResponse();
        checkAllVisible();

        click(btn4);
        waitResponse();
        checkAllVisible();
    }
   );

  }
}