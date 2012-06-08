package org.zkoss.zktest.test2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Date;

import org.zkoss.image.Image;
import org.zkoss.image.Images;

public class B60_ZK_1178_ZEL_Overload_Model {
	public RenderedImage getRenderedImage(){
		BufferedImage bi = new java.awt.image.BufferedImage(
			200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB
		);

		java.awt.Graphics2D g2 = bi.createGraphics();
		g2.setBackground(Color.RED);
		g2.clearRect(0, 0, 200, 200);
		g2.dispose();
		return bi;
	}
	
	public Image getZkImage(){
		BufferedImage bi = new java.awt.image.BufferedImage(
			100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB
		);

		java.awt.Graphics2D g2 = bi.createGraphics();
		g2.setBackground(Color.BLUE);
		g2.clearRect(0, 0, 200, 200);
		g2.dispose();
		
		try {
			return Images.encode("foo.png", bi);
		} catch (IOException e) {
			return null;
		}
	}
}