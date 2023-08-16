/* WcagTestCase.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 15:45:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Assertions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A basic test case class for accessibility
 * @author rudyhuang
 */
public abstract class WcagTestCase extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		String simple = getClass().getSimpleName();
		String name = getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/").replace(simple, "");
		String file = String.valueOf(simple.charAt(0)).toLowerCase() + simple.substring(1).replace("Test", "");
		return name + file + getFileExtension();
	}

	/**
	 * Verify accessibility issues.
	 * If there is any issue, test will fail.
	 */
	protected void verifyA11y() {
		try {
			String url = getAddress() + getFileLocation(), failmsg = "", data = "", line = "";
			boolean pass = true;
			int count = 1;

			// get whole lighthouse a11y results
			Process process = Runtime.getRuntime().exec("lighthouse " + url + " --output=json --chrome-flags=\"--headless\" --only-categories=\"accessibility\"");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = reader.readLine()) != null) data += line;
			JSONParser parser = new JSONParser();
			JSONObject audits = (JSONObject)((JSONObject) parser.parse(data)).get("audits");

			// check results
			for (Object key : audits.keySet()) {
				JSONObject audit = (JSONObject)audits.get((String)key);
				// Disable color-contrast because it only passes when using wcag theme, general theme does not need to pass
				if (String.valueOf(audit.get("score")).equals("0") && !String.valueOf(audit.get("id")).equals("color-contrast")) {
					pass = false;
					failmsg += "\n================== " + (count++) + " ==================\n"
							+ ">>> id : " + audit.get("id") + "\n"
							+ ">>> title : " + audit.get("title") + "\n"
							+ ">>> description : " + audit.get("description") + "\n";
				}
			}
			if (!pass) Assertions.fail(failmsg);
		} catch (IOException e) {
			Assertions.fail("\n================== Exception ==================\n" + e.getMessage() + "\n");
		}
	}
}
