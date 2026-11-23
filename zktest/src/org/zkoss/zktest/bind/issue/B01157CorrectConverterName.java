/* B01157CorrectConverterName.java

	Purpose:
		
	Description:
		
	History:
		Jun 19, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

import java.util.Date;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01157CorrectConverterName {

	private double price = 12345.678;
	private Date creationDate = new Date();
	
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
