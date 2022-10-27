/* ClientBindTestCase.java

    Purpose:
                
    Description:
            
    History:
        Tue Aug 30 10:33:56 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver;

import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class ClientBindTestCase extends WebDriverTestCase {
	// assertNoAnyError(); in every test bottom?
	@Override
	public WebDriver connect(String location) {
		WebDriver connect = super.connect(location);
		sleep(2000);
		return connect;
	}
}
