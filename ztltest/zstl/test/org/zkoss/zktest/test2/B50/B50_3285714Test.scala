/* B50_3285714Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 18:59:08 CST 2011 , Created by benbai
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
 * A test class for bug 3285714
 * @author benbai
 *
 */
@Tags(tags = "B50-3285714.zul,A,E,Grid,ROD")
class B50_3285714Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<html><![CDATA[
					<ol>
						<li>Drag the scroll to the middle. You should see row numbers are around 5,000. Otherwise it is a bug.</li>
					</ol>
				]]></html>
				<zscript><![CDATA[
					rows = new String[10000];
					for(int i = 0; i < 10000; i++)
						rows[i] = "Row " + i;
				]]></zscript>
				<grid id="grid" width="200px" height="300px">
					<rows id="rows">
						<row forEach="${rows}">
							<label value="${each}" />
						</row>
					</rows>
				</grid>
			</zk>

    }
   // Run syntax 2
    runZTL(zscript,
        () => {
        var grid: Widget = engine.$f("grid");
        var rows: Widget = engine.$f("rows");
        waitResponse();

        jq(grid.$n("body")).get(0).eval("scrollTop = 300000");
        waitResponse();
        var fullHeight: Int = Integer.parseInt(jq(grid.$n("body")).get(0).get("scrollTop"));
        jq(grid.$n("body")).get(0).eval("scrollTop = " + (fullHeight/2));
        waitResponse();

        var rowCnt: Int = jq(rows.$n()).find(".z-row").length();
        
        def findTopRow (i: Int, max: Int): Element = {
        	var row: Element = jq(rows.$n()).find(".z-row").get(i);
        	if (Integer.parseInt(row.get("offsetTop")) >= Integer.parseInt(jq(grid.$n("body")).get(0).get("scrollTop"))
        	    || (i+1) >= max)
        		return row;
        	else
        	  return findTopRow(i+1, max);
        }
        var topRow: Element = findTopRow(0, rowCnt);
        var content: String = jq(topRow).find(".z-label").get(0).get("innerHTML");

        var itemCnt: Integer = Integer.parseInt(content.substring(content.length()-4, content.length()));
        verifyTrue(Math.abs(5000-itemCnt) <= 15);
    }
   );

  }
}