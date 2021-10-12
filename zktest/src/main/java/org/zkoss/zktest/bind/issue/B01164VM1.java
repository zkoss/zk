/* B01164VM1.java

	Purpose:
		
	Description:
		
	History:
		Jun 1, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

import java.awt.image.RenderedImage;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01164VM1 {
	public RenderedImage getImage(){
		java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(
				200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB);

			java.awt.Graphics2D g2 = bi.createGraphics();
			g2.setBackground(java.awt.Color.RED);
			g2.clearRect(0, 0, 200, 200);
			g2.dispose();
		return bi;
	}
}
