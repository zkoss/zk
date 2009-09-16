/* B30_1486556Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author sam
 *
 */
public class B30_1486556Test extends ZKTestCase{
	private String _target="B30-1486556.zul";

	
	private List<Selenium> _browsers;
	private String _url;
	
	public B30_1486556Test(){
		super();
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
		
	}
	
	
	@Test(expected=AssertionError.class)
	public void test1() throws IOException, Exception{
		String inputComp= getCompId(4);
		String clickComp= getCompId(2);
		
		for(Selenium browser : _browsers){
			try{
				browser.start();
				try{		
					browser.open(_url);
					Thread.sleep(1000);
					
					browser.focus(inputComp);
					Thread.sleep(1000);
					
					browser.mouseDown(inputComp);
					Thread.sleep(1000);
					browser.mouseOut(inputComp);
					Thread.sleep(1000);
					
					browser.mouseDown(clickComp);
					Thread.sleep(1000);
					browser.mouseOut(clickComp);
					Thread.sleep(1000);
					
					browser.close();
				}catch(InterruptedException e){
				}
			}finally{
				browser.stop();
			}
		}
	}
}
