/* B50_ZK_261Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 18:39:36 CST 2011 , Created by benbai
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
 * A test class for bug ZK-261
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-261.zul,A,M,Panel,Cluster,BI")
class B50_ZK_261Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<zscript><![CDATA[
					import java.io.*;
					int cnt = 0;
				]]></zscript>
				<div>1. Click "Clone" Button.</div>
				<div>2. Click the 2nd "Check Panelchildren" Button. You shall see the answer be true. Otherwise it is a bug.</div>
				<vlayout id="vb">
					<button label="Clone">
						<attribute name="onClick"><![CDATA[
							ByteArrayOutputStream boa = new ByteArrayOutputStream();
							new ObjectOutputStream(boa).writeObject(panel);
							byte[] bs = boa.toByteArray();
							Object l = new ObjectInputStream(new ByteArrayInputStream(bs)).readObject();
							l.setId("dst" + ++cnt);
							vb.appendChild(new Label(bs.length + " bytes copied"));
							vb.appendChild(l);
						]]></attribute>
					</button>
					<panel id="panel" title="Panel">
						<panelchildren>
							<button label="Check Panelchildren">
								<attribute name="onClick"><![CDATA[
									Component pl = self.getParent().getParent().getPanelchildren();
									alert(pl + ", Same parent: " + (pl == self.parent));
								]]></attribute>
							</button>
						</panelchildren>
					</panel>
				</vlayout>
			</zk>

    }
   // Run syntax 2
    runZTL(zscript,
        () => {
        click(jq(".z-button-cm").get(0));
        waitResponse();
        click(jq(".z-button-cm").get(2));
        waitResponse();
        verifyTrue("The answer should be true",
            jq(".z-messagebox").find(".z-label").get(0)
            	.get("innerHTML").contains("true"));
    }
   );

  }
}