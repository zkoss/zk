/* B50_2935509Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 12:28:40 CST 2011 , Created by benbai
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
 * A test class for bug 2935509
 * @author benbai
 *
 */
@Tags(tags = "B50-2935509.zul,A,E,Grid")
class B50_2935509Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<window title="Test" width="610px">
			Click the button and the headers shall change (from 1 to 11, 2 to 22, and 3 to 33)
			<button id="btn" label="new columns">
			<attribute name="onClick">
			gg.getColumns().detach();
			Columns c = new Columns();
			c.appendChild(new Column("11",null,"100px"));
			c.appendChild(new Column("22",null,"150px"));
			c.appendChild(new Column("33"));
			gg.appendChild(c);
			</attribute>
			</button>
			<grid id="gg" width="600px">
			<columns>
			<column label="1" width="100px"/>
			<column label="2" width="150px"/>
			<column label="3"/>
			</columns>
			<rows>
			<row>
			<label value="foo1"/>
			<label value="foo2"/>
			<label value="foo3"/>
			</row>
			</rows>
			</grid>
			</window>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");
        var gg: Widget = engine.$f("gg");

        click(btn);
        waitResponse();

        def checkHeader (i: Int, content: String) {
        	verifyTrue("header "+(i+1)+" should contains "+content,
        			jq(gg.$n("head")).find(".z-columns").find(".z-column-cnt").get(i).get("innerHTML").contains(content));
        }
        checkHeader(0, "11");
        checkHeader(1, "22");
        checkHeader(2, "33");
    }
   );

  }
}