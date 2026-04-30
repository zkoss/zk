/* B104_ZK_6043Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 16:10:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6043: in a Grid {@code <columns menupopup="auto">} popup, the checkmark
 * icons were rendered at the same 24px font-size as the surrounding 24px
 * blue checkbox container on the tablet UI, so the glyph overflowed and sat
 * off-centre. The fix shrinks the icon and centres it inside the box.
 */
@ForkJVMTestOnly
public class B104_ZK_6043Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	private static final int TOLERANCE_PX = 1;

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation",
						Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void checkmarkContainedInsideMenuItemImageBox() {
		connect();
		waitResponse();

		// Open the column header menupopup so the menuitems exist in the DOM.
		click(jq(".z-column-button").first());
		waitResponse();

		assertTrue(jq(".z-menupopup .z-menuitem-icon").exists(),
				"column auto-menupopup must render menuitem icons");

		// Force every menuitem-icon visible so geometry can be measured even
		// before the user clicks one (the icon defaults to display:none until
		// the menuitem becomes checked). Containment of the icon inside its
		// .z-menuitem-image sibling is what defines "centred in the box".
		String script
				= "(function(tol){"
				+ "var pad='ZK6043_'+Math.random();"
				+ "var st=document.createElement('style');"
				+ "st.id=pad;"
				+ "st.textContent='.z-menupopup .z-menuitem-icon{display:block!important}';"
				+ "document.head.appendChild(st);"
				+ "try{"
				+ "var checkable=document.querySelectorAll('.z-menupopup .z-menuitem-checkable');"
				+ "if(!checkable.length)return 'no-checkable-items';"
				+ "var n=0,bad=null;"
				+ "for(var i=0;i<checkable.length;i++){"
				+ "  var img=checkable[i].querySelector('.z-menuitem-image');"
				+ "  var ico=checkable[i].querySelector('.z-menuitem-icon');"
				+ "  if(!img||!ico)continue;"
				+ "  var ir=img.getBoundingClientRect();"
				+ "  var cr=ico.getBoundingClientRect();"
				+ "  if(ir.width===0||cr.width===0)continue;"
				+ "  n++;"
				+ "  if(cr.left<ir.left-tol||cr.top<ir.top-tol"
				+ "     ||cr.right>ir.right+tol||cr.bottom>ir.bottom+tol){"
				+ "    bad='icon['+i+'] '+JSON.stringify({"
				+ "      img:[ir.left,ir.top,ir.right,ir.bottom],"
				+ "      ico:[cr.left,cr.top,cr.right,cr.bottom]"
				+ "    });"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if(n===0)return 'no-measurable-items';"
				+ "return bad?'overflow:'+bad:'ok';"
				+ "}finally{var s=document.getElementById(pad);if(s)s.parentNode.removeChild(s);}"
				+ "})(" + TOLERANCE_PX + ")";
		String result = getEval(script);
		assertEquals("ok", result,
				"ZK-6043: every menuitem-icon must be contained within its"
						+ " sibling .z-menuitem-image box (centred checkmark)."
						+ " Result: " + result);
	}
}
