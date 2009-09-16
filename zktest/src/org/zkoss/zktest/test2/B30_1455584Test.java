/* B30_1455584Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.Test;

import com.thoughtworks.selenium.Selenium;
/**
 * 
 * @author sam
 *
 */
public class B30_1455584Test extends ZKTestCase{

	private String _target="B30-1455584.zul";
	private String _testComp="zk_comp_4";
	private List<Selenium> _browsers;
	private String _url;
	
	public B30_1455584Test(){
		super();
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}
	

	@Test(expected=AssertionError.class)
	public void test1(){

		for(Selenium browser : _browsers){
			try{
				browser.start();
				try{	
					browser.open(_url);
					Thread.sleep(1000);
					String strClickBefor = browser.getText(_testComp);
					
					browser.focus(_testComp);
					Thread.sleep(1000);
					browser.click(_testComp);
					Thread.sleep(1000);
					
					String strClickAfter = browser.getText(_testComp);
					assertNotEquals(strClickBefor, strClickAfter);
					
					browser.close();
					Thread.sleep(1000);
				}catch(InterruptedException e){	}
			}finally{
				browser.stop();
			}
		}
	}
	

}
