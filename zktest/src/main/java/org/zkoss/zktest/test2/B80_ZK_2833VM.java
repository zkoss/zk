/* B80_ZK_2833VM.java

	Purpose:
		
	Description:
		
	History:
		9:26 AM 8/3/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.DependsOn;

/**
 * @author jumperchen
 */
public class B80_ZK_2833VM {
	private String word;
	private String reverse;

	@DependsOn("reverse")
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
		reverse = new StringBuilder(word).reverse().toString();
	}

	@DependsOn("word")
	public String getReverse() {
		return reverse;
	}
	public void setReverse(String reverse) {
		this.reverse = reverse;
		word = new StringBuilder(reverse).reverse().toString();
	}
}