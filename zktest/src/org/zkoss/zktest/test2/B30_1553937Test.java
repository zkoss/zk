/* B30_1553937Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 �U��5:01:13 , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author sam
 *
 */
public class B30_1553937Test extends ZKTestCase{
	
	private String _target = "B30-1553937.zul";
	private List<Selenium> _browsers;
	private String _url;
	
	public B30_1553937Test(){
		super();
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}
	
	@Test(expected=AssertionError.class)
	public void test1(){
		
		String comp1 = uuid(4);
		String comp2 = uuid(7);
		
		for(Selenium browser : _browsers){
			try{
				browser.start();
				try{
					browser.open(_url);
					Thread.sleep(2000);
					
//					int comp1Right = getCompPos(browser, comp1).getRight();
//					int comp2Left = getCompPos()
					
				}catch(InterruptedException e){	}
				
			}finally{
				browser.stop();
			}
		}
		
	}
	
}
