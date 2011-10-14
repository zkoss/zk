/* B50_2890514Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 12:59:15 CST 2011 , Created by benbai
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
 * A test class for bug 2890514
 * @author benbai
 *
 */
@Tags(tags = "B50-2890514.zul,B,E,Grid,Row,Align")
class B50_2890514Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			The "File:" label should align to right.
			<grid>
			<columns sizable="true">
			<column label="Type" sort="auto"/>
			<column label="Content"/>
			</columns>
			<rows>
			<row id="row1" align="right">
			<label id="fl" value="File:"/>
			<textbox width="98%"/>
			</row>
			<row>
			<label value="Type:"/>
			<hbox>
			<listbox rows="1" mold="select">
			<listitem label="Java Files,(*.java)"/>
			<listitem label="All Files,(*.*)"/>
			</listbox>
			<button label="Browse..."/>
			</hbox>
			</row>
			<row>
			<label value="Options:"/>
			<textbox rows="3" width="98%"/>
			</row>
			</rows>
			</grid>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var row1: Widget = engine.$f("row1");
        var fl: Widget = engine.$f("fl");

        var width: Int = jq(jq(row1.$n()).find(".z-row-cnt").get(0)).outerWidth();
        var offsetLeft: Int = Integer.parseInt(fl.$n().get("offsetLeft"));
        waitResponse();

        verifyTrue("the offsetLeft of File label ("+offsetLeft+
            ") should close to the right side of row1 (width = "+width+")",
            offsetLeft > (width-30));
    }
   );

  }
}