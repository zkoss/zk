package org.zkoss.zktest.test2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.Image;
import org.zkoss.image.Images;

public class B60_ZK_1178_Model {
	private int showIndex = 0;
	
	public RenderedImage getRenderedImage(){
		if(showIndex<2){
			return null;
		}
		
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
		if(showIndex<1){
			return null;
		}
		
		BufferedImage bi = new java.awt.image.BufferedImage(
			200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB
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
	
	@Command("update")
	@NotifyChange("*")
	public void update(){
		showIndex++;
		if(showIndex>2){
			showIndex=2;
		}
	}
}