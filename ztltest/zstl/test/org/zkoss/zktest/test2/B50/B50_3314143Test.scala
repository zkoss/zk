/* B50_3314143Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 14:41:55 CST 2011 , Created by benbai
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
 * A test class for bug 3314143
 * @author benbai
 *
 */
@Tags(tags = "B50-3314143.zul,A,M,Tree")
class B50_3314143Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				Open the Treeitem. The dotted line should rendered correctly (NOT T shape).
				<tree id="tree" zclass="z-dottree">
					<treechildren>
						<treeitem label="Item" open="false">
							<treechildren>
								<treeitem label="Child Item" />
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

        click(jq(".z-dottree-ico").get(0));
        verifyTrue(jq(".z-dottree-last").exists());
    }
   );

  }
}