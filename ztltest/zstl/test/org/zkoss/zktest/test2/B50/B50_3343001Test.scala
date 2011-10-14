/* B50_3343001Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 13:37:01 CST 2011 , Created by benbai
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
 * A test class for bug 3343001
 * @author benbai
 *
 */
@Tags(tags = "B50-3343001.zul,A,E,Tree,IE6,IE7")
class B50_3343001Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<div>IE 6/7 only.</div>
				<div>Open any Treeitem. If the Tree height does not increase, it is a bug.</div>
				<tree id="tree" width="100px">
					<treecols>
						<treecol width="200px" />
					</treecols>
					<treechildren>
						<treeitem forEach="1,2,3,4,5" label="item" open="false">
							<treechildren>
								<treeitem forEach="1,2,3,4,5" label="item" />
							</treechildren>
						</treeitem>
					</treechildren>
				</tree>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var tree: Widget = engine.$f("tree");

        def clickThenVerify () {
        	click(jq(tree.$n("rows")).find(".z-tree-root-close").get(0));
        	waitResponse();
        	var bodyHeight: Int = jq(tree.$n("body")).outerHeight();
        	var rowsHeight: Int = jq(tree.$n("rows")).outerHeight();
        	verifyTrue("body height ("+bodyHeight+") should larger then rows height ("+rowsHeight+")",
        	    bodyHeight > rowsHeight);
        }
        clickThenVerify();
        clickThenVerify();
        clickThenVerify();
        clickThenVerify();
        clickThenVerify();
    }
   );

  }
}