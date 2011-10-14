/* B50_3161535Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 10:49:33 CST 2011 , Created by benbai
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
 * A test class for bug 3161535
 * @author benbai
 *
 */
@Tags(tags = "B50-3161535.zul,A,E,Grid,Column")
class B50_3161535Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<html><![CDATA[
				    <b style="color:red;">Breeze Theme and IE 7 only</b>
					<ol>
						<li>The first column shall align left</li>
						<li>The second column shall align center</li>
						<li>The third column shall align right</li>
					</ol>
				]]></html>
				<grid >
					<columns menupopup="auto">
						<column id="c1" label="Align Left" align="left" />
						<column label="Align Center" sort="auto" align="center" />
						<column label="Align Right" sort="auto" align="right" />
					</columns>
					<rows>
						<row>
							<label id="l1" value="1"></label>
							<label id="l2" value="1"></label>
							<label id="l3" value="1"></label>
						</row>
					</rows>
				</grid>
			</zk>

    }
   // Run syntax 2
    runZTL(zscript,
        () => {
        var (c1: Widget,
    	     l1: Widget,
    	     l2: Widget,
    	     l3: Widget) = (
    	        engine.$f("c1"),
    	        engine.$f("l1"),
    	        engine.$f("l2"),
    	        engine.$f("l3")
    	    );
        var width = jq(c1.$n()).outerWidth();
        verifyTrue("first column shall align left, \noffsetLeft of first label= "+
        	Integer.parseInt(l1.$n().get("offsetLeft"))+" should smaller then 10", 
            Integer.parseInt(l1.$n().get("offsetLeft")) <= 20);
        verifyTrue("second column shall align center,\n offsetLeft of second label= "+
        	Integer.parseInt(l2.$n().get("offsetLeft"))+" should larger then width/3 ("+(width/3)+") and\n smaller then (width/3)*2 ("+(width/3*2)+")",
            Integer.parseInt(l2.$n().get("offsetLeft")) >= (width/3) && Integer.parseInt(l2.$n().get("offsetLeft")) <= (width/3*2));
        verifyTrue("The third column shall align right,\n offsetLeft of third label= "+
        	Integer.parseInt(l3.$n().get("offsetLeft"))+" should larger then width - 10 ("+(width-10)+")", 
            Integer.parseInt(l3.$n().get("offsetLeft")) >= width-20);
    }
   );

  }
}