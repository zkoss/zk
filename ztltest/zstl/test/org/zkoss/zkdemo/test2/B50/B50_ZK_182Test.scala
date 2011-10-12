/* B50_ZK_182Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 16:12:14 PM , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.B50

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug ZK-421
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-182.zul,A,E,Grid,Scrollbar")
class B50_ZK_182Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<separator/>
				1.click "add frozen"
				<separator/>
				2.click "delete frozen"
				<separator/>
				drag the horizontal scrollbar, see if the column header scroll as the content
				<separator/>
    			If not, it's bug
				
				<separator/>
				<button id="btn1" label="add Frozen ">
						<attribute name="onClick"><![CDATA[
						Frozen fz = new Frozen();
						fz.setColumns(1);
						grid.appendChild(fz);
					]]></attribute>
				</button>
				<button id="btn2" label="delete Frozen ">
					<attribute name="onClick"><![CDATA[
						grid.getFrozen().detach();
					]]></attribute>
				</button>
				<grid id="grid" width="250px">
					<columns>
						<column label="column ${each}" forEach="1,2,3,4,5"
							width="100px" />
					</columns>
					<rows>
						<row forEach="1,2,3,4,5">
							<label value="item ${each}" forEach="1,2,3,4,5" />
						</row>
					</rows>
				</grid>
			</zk>
    }

    def executor() = {
    	var btn1: Widget = engine.$f("btn1");
    	var btn2: Widget = engine.$f("btn2");
    	var grid: Widget = engine.$f("grid");
    	waitResponse();
    	click(btn1);
    	waitResponse();
    	click(btn2);
    	waitResponse();
    	for (i <- 0 until 6) {
    	  jq(grid.$n("body")).get(0).eval("scrollLeft += "+33);
    	  waitResponse();
    	  verifyTrue(Integer.parseInt((jq(grid.$n("head")).get(0).get("scrollLeft")))
    	      .equals(Integer.parseInt(jq(grid.$n("body")).get(0).get("scrollLeft")))
    	  );
    	}
    }
   // Run syntax 1 
   runZTL(zscript, executor);
   
   // Run syntax 2
   /**
    runZTL(zscript,
        () => {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   );
    */
  }
}