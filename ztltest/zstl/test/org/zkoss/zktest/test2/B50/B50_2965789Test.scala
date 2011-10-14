/* B50_2965789Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 15:21:24 CST 2011 , Created by benbai
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
 * A test class for bug 2965789
 * @author benbai
 *
 */
@Tags(tags = "B50-2965789.zul,A,E,Treecell")
class B50_2965789Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			<tree>
			<treechildren>
			<treeitem>
			<treerow>
			<treecell id="tc" label="Test"/>
			</treerow>
			</treeitem>
			</treechildren>
			</tree>
			<button id="btn" label="Click me, you should not see any error." onClick='tc.style =
			"background:red;"'/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");
        
        click(btn);
        waitResponse();
        verifyFalse(jq(".z-error").exists());
    }
   );

  }
}