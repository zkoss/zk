/* B50_ZK_332Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 18:15:14 PM , Created by benbai
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
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug ZK-332
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-332.zul,A,E,Tree,disabled,open")
class B50_ZK_332Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
						<zk>
			<html><![CDATA[
			<ul>
			<li>Click the open icon of the B node, and you shall be able to close and open it freely.</li>
			</ul>
			]]></html>

			<tree id="tree">
				<treechildren>
					<treeitem label="A">
					</treeitem>
					<treeitem disabled="true">
						<treerow id="tr" label="B"/>
						<treechildren>
							<treeitem label="C"/>
							<treeitem label="D"/>
						</treechildren>
					</treeitem>
				</treechildren>
			</tree>
			</zk>
    }

    def executor = () => {
    	var tree: Widget = engine.$f("tree");
    	var tr: Widget = engine.$f("tr");
    	waitResponse();

    	click(jq(tr.$n()).find(".z-tree-ico"));
    	waitResponse();
    	verifyTrue("none".equals(jq(tree.$n("rows")).find(".z-treerow").get(2).get("style.display")));
    	verifyTrue("none".equals(jq(tree.$n("rows")).find(".z-treerow").get(3).get("style.display")));
    	click(jq(tr.$n()).find(".z-tree-ico"));
    	waitResponse();
    	verifyTrue("".equals(jq(tree.$n("rows")).find(".z-treerow").get(2).get("style.display")));
    	verifyTrue("".equals(jq(tree.$n("rows")).find(".z-treerow").get(3).get("style.display")));
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