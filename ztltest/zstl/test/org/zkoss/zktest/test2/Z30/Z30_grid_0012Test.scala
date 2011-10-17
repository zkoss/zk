/* Z30_grid_0012Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 16:06:42 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.ZK

/**
 * A test class for bug grid-0012
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0012.zul,Z30,C,E,Grid,Row,Style")
class Z30_grid_0012Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript =""" 
			<window title="test row height">
				<grid width="400px">
					<rows>
						<row height="50px">1, height = 50px</row>
						<row height="100px">2, height = 100px</row>
						<row>3</row>
						<row>4</row>
						<row>5</row>
						<row>6</row>
						<row>7</row>
						<row>8</row>
						<row>9</row>
						<row>10</row>
					</rows>
				</grid>
			</window>
    """;

    runZTL(zscript,
        () => {
          verifyEquals(jq("@row").eq(0).outerHeight().toString(), if (ZK is "ie7_") "60" else "50")
          verifyNotEquals(jq("@row").eq(0).outerHeight().toString(),"51")
          verifyEquals(jq("@row").eq(1).outerHeight().toString(), if (ZK is "ie7_") "110" else "100")
          verifyNotEquals(jq("@row").eq(1).outerHeight().toString(),"101")
    }
   );
  }
}