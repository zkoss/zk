/* B30_1913290Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 3:55:21 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * @author jumperchen
 *
 */
@Tags(tags = "B30-1913290.zul,B,E,Chart,BI")
class B30_1913290Test extends ZTL4ScalaTestCase {
  def testError() = {
    val zscript = {
      <zk>
        <label value="If you cannot see any error, that is correct!"/>
        <chart id="mychart" type="time_series" width="400" height="200" threeD="false" fgAlpha="128"/>
        <zscript><![CDATA[
        XYModel myModel = new SimpleXYModel();
        myModel.addValue(new Integer(1), new Integer(10000), new Integer(89));
        mychart.setModel(myModel);
    ]]></zscript>
      </zk>
    }
    
    runZTL(zscript, () => {
      verifyTrue(jq("$mychart img").isVisible());
      verifyFalse(jq(".z-error").exists());	
    })
  }
}