/* Z30_grid_0010Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 15:29:18 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug grid-0010
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-grid-0010.zul,Z30,A,E,Grid")
class Z30_grid_0010Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<window title="Column should not show up here">
				<grid>
					<columns>
						<column/>
						<column align="center"/>
						<column align="right"/>
					</columns>
					<rows>
						<row>Testing whether header is visible</row>
					</rows>
				</grid>
			</window>
    }

    runZTL(zscript,
        () => {
		verifyEquals(String.valueOf(jq("@column:visible").length),"0");
		verifyEquals(String.valueOf(jq("@column").length),"3");
		verifyNotEquals(String.valueOf(jq("@column").length),"4");
    }
   );
  }
}