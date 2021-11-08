/* B96_ZK_5044VM.java

	Purpose:
		
	Description:
		
	History:
		2:38 PM 2021/11/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

/**
 * @author jumperchen
 */
public class B96_ZK_5044VM {
	private String iname, iSize;
	private List<String> names = Arrays.asList("juan", "marcus", "pedro");
	private  List<String> sizes =  Arrays.asList("big", "little", "medium");

	public B96_ZK_5044VM() {
		setIname("juan");
		setiSize("big");
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void setSizes(List<String> sizes) {
		this.sizes = sizes;
	}

	public List<String> getSizes() {
		return sizes;
	}

	public String getIname() {
		return iname;
	}

	public void setIname(String iname) {
		this.iname = iname;
	}

	public String getiSize() {
		return iSize;
	}

	public void setiSize(String iSize) {
		this.iSize = iSize;
	}
}
