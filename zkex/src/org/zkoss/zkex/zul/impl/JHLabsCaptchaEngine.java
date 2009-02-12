/* JHLabsCaptchaEngine.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul.impl;

import org.zkoss.zul.*;
import org.zkoss.zul.impl.CaptchaEngine;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.image.AImage;
import org.zkoss.util.TimeZones;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;

import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.ShadowFilter;

import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

/**
 * A captcha engine implemented with JH Labs libraries.
 *
 * <p>See also <a href="http://www.jhlabs.com/">JH Labs</a>
 * @author henrichen
 * @since 3.0.0
 */
public class JHLabsCaptchaEngine implements CaptchaEngine, java.io.Serializable {
	private static Random _random = new Random();
	private static final double[] sin = { //sin degree 0 ~ 10
		Math.sin(Math.toRadians(0)),
		Math.sin(Math.toRadians(1)),
		Math.sin(Math.toRadians(2)),
		Math.sin(Math.toRadians(3)),
		Math.sin(Math.toRadians(4)),
		Math.sin(Math.toRadians(5)),
/*		Math.sin(Math.toRadians(6)),
		Math.sin(Math.toRadians(7)),
		Math.sin(Math.toRadians(8)),
		Math.sin(Math.toRadians(9)),
		Math.sin(Math.toRadians(10)),
*/	};
	private static final double[] cos = { //cos degree 0 ~ 10
		Math.cos(Math.toRadians(0)),
		Math.cos(Math.toRadians(1)),
		Math.cos(Math.toRadians(2)),
		Math.cos(Math.toRadians(3)),
		Math.cos(Math.toRadians(4)),
		Math.cos(Math.toRadians(5)),
/*		Math.cos(Math.toRadians(6)),
		Math.cos(Math.toRadians(7)),
		Math.cos(Math.toRadians(8)),
		Math.cos(Math.toRadians(9)),
		Math.cos(Math.toRadians(10)),
*/	};

	//-- CaptchaEngine --//
	public byte[] generateCaptcha(Object data) {
		final Captcha captcha = (Captcha) data;
		final BufferedImage bi = drawCaptcha(captcha);

		//encode the image to jpeg format
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			if (!ImageIO.write(bi, "png", baos))
				throw new UiException("Don't know how to generate PNG");
		} catch(java.io.IOException ex) {
				throw UiException.Aide.wrap(ex);
		} finally {
			try {
				baos.close();
			} catch(java.io.IOException ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return baos.toByteArray();
	}

	private BufferedImage drawCaptcha(Captcha captcha) {
		final int width = captcha.getIntWidth();
		final int height = captcha.getIntHeight();
		final int bgRGB = captcha.getBgRGB();
		final int fontRGB = captcha.getFontRGB();

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bitgt = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = null;
		Graphics2D gtgt = null;
		try {
			g2D = bi.createGraphics();

			//draw text
			final Color fontColor = new Color(fontRGB);
			g2D.setColor(fontColor);
			int textwidth = drawText(g2D, captcha);

			//distortion
			bi = distortion(bi);

			//box background
			final Color bgColor = new Color(bgRGB, false);
			GradientPaint grbgColor = new GradientPaint(0, 0, bgColor, width, height, Color.WHITE);
			gtgt = bitgt.createGraphics();
			gtgt.setPaint(grbgColor);
			gtgt.fillRect(0, 0, width, height);

			int wgap = width - textwidth;
			int left = wgap <= 0 ? 0 : _random.nextInt(wgap);
			
			//draw the distortion image on the box
			gtgt.drawImage(bi, left, 0, null);

			//draw the noise
			if (captcha.isNoise()) {
				genNoise(bitgt, .1f, .1f, .25f, .25f, fontColor);
				genNoise(bitgt, .1f, .25f, .5f, .9f, fontColor);
			}

			//box border
			gtgt.setColor(fontColor);
			gtgt.drawRect(0, 0, width-1, height-1);

			return bitgt;
		} finally {
			if (g2D != null) g2D.dispose();
			if (gtgt != null) gtgt.dispose();
		}
	}

	protected int drawText(Graphics2D g2D, Captcha captcha) {
		final int width = captcha.getIntWidth();
		final int height = captcha.getIntHeight();
		final String text = captcha.getValue();
		final int len = text.length();
		final int fontSize = captcha.getFonts().isEmpty() ?
			captcha.getDefaultFonts().length : captcha.getFonts().size();
		final int bgRGB = captcha.getBgRGB();
		final int fontRGB = captcha.getFontRGB();
		final Color bgColor = new Color(bgRGB, true);
		final Color fontColor = new Color(fontRGB);

		int avgwidth = width / len;
		int left = 0;
		int top = 0;
		int werror = 0; //adjust width error, so all words can be show correctly
		for(int j = 0; j < len; ++j) {
			final Font font = captcha.getFont(_random.nextInt(fontSize));
			g2D.setFont(font);
			final String substr = text.substring(j, j+1);
			final FontRenderContext frc = g2D.getFontRenderContext();
			final Rectangle2D r2D = font.getStringBounds(substr, frc);
			BufferedImage charImg = 
			  rotateChar(substr, (int) r2D.getWidth(), (int) r2D.getHeight(), font, fontColor, bgColor);
			final int charWidth = charImg.getWidth();
			final int charHeight = charImg.getHeight();

			//gap to be move around
			final int wgap = avgwidth - charWidth + werror;
			if (wgap <= 0) {
				werror = wgap;
			}
			final int hgap = height - charHeight;
			left += (wgap <= 0 ? 0 : _random.nextInt(wgap));
			top = (hgap <= 0 ? 0 : _random.nextInt(hgap));
			g2D.drawImage(charImg, left, top, bgColor, null);
			left += charWidth;
		}
		return left;
	}

	protected BufferedImage rotateChar(String substr, int width, int height, Font font, Color fontColor, Color bgColor) {
		final int angle = _random.nextInt(11) - 5; //-5 ~ 5 degree
		final int[] resultxy = newXy(width, height, angle);
		final int newWidth = resultxy[0];
		final int newHeight = resultxy[1];

		Graphics2D g2D = null;
		try {
			BufferedImage bi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			g2D = bi.createGraphics();
			RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			hints.add(new RenderingHints(
				RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
			g2D.setRenderingHints(hints);

			//background
			g2D.setColor(bgColor);
			g2D.fillRect(0, 0, newWidth, newHeight);

			//draw text with rotate transformation
			g2D.setFont(font);
			int xRot = newWidth / 2;
			int yRot = newHeight / 2;
			AffineTransform xform = g2D.getTransform();
			xform.rotate(Math.toRadians(angle), xRot, yRot);
			g2D.setTransform(xform);

			g2D.setColor(fontColor);
			int left = (newWidth-width) / 2;
			int top = (newHeight-height)/ 2 + (height * 2 / 3);
			g2D.drawString(substr, left, top);

			return bi;
		} finally {
			if (g2D != null) {
				g2D.dispose();
			}
		}
	}

	private BufferedImage distortion(BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();

		//RippleFilter
		RippleFilter rfilter = new RippleFilter();
		rfilter.setWaveType(RippleFilter.SINE); //SINE or NOISE
		rfilter.setXWavelength(_random.nextInt(8)+9);
		rfilter.setYWavelength(_random.nextInt(3)+2);
		rfilter.setXAmplitude(5.6f);
		rfilter.setYAmplitude(_random.nextFloat()+1.0f);

		image = rfilter.filter(image, null);

		//ShadowFilter
		ShadowFilter sfilter = new ShadowFilter();
		sfilter.setRadius(height/4);
		image = sfilter.filter(image, null);

		return image;
	}

	private void genNoise(BufferedImage image, 
	float factorOne,float factorTwo, float factorThree, float factorFour, Color fontColor){
		final int width = image.getWidth();
		final int height = image.getHeight();

		//the curve from where the points are taken
		CubicCurve2D cc = new CubicCurve2D.Float(
			width*factorOne*_random.nextFloat(), height*_random.nextFloat(),
			width*factorTwo, height*_random.nextFloat(),
			width*factorThree, height*_random.nextFloat(),
			width*factorFour, height*_random.nextFloat());

		// creates an iterator to define the boundary of the flattened curve
		PathIterator pathIt = cc.getPathIterator(null, 2);
		List points = new ArrayList(256);

		// while pathIt is iterating the curve, remember the points.
		for(; !pathIt.isDone(); pathIt.next()) {
			float[] coords = new float[6];
			switch ( pathIt.currentSegment(coords) ) {
				case PathIterator.SEG_MOVETO:
				case PathIterator.SEG_LINETO:
				  points.add(new Point2D.Float(coords[0], coords[1]));
			}
		}

		Graphics2D g2D = null;
		try {
			g2D = (Graphics2D)image.getGraphics();
			g2D.setRenderingHints(new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));

			g2D.setColor(fontColor);

			final int count = points.size() - 1;
			for(int j = 0; j < count; ++j) {
				//for the first 3 point change the stroke and direction
				if(j < 3) {
					g2D.setStroke(new BasicStroke(0.9f * (3-j)));
				}
				Point2D thisPoint = (Point2D) points.get(j);
				Point2D nextPoint = (Point2D) points.get(j+1);
				g2D.drawLine((int)thisPoint.getX(), (int) thisPoint.getY(), (int)nextPoint.getX(), (int)nextPoint.getY());
			}
		} finally {
			if (g2D != null) {
				g2D.dispose();
			}
		}
	}

	/** Given original x and y, and rotating degree, return the new x and y after rotation. */
	private int[] newXy(int x, int y, int degree) {
		//x' = s . cos(-org + degree), x' = x . cos(degree) + y . sin(degree)
		//y' = s . sin( org + degree), y' = y . cos(degree) + x . sin(degree)
		//x' = s . cos(-org - degree) = s . cos(org + degree), x' = x . cos(degree) - y . sin(degree)
		//y' = s . sin( org - degree), y' = y . cos(degree) + x . sin(degree)
		if (degree < 0) {
			degree = -degree;
			x = (int) (x * cos[degree] + y * sin[degree]);
			y = (int) (y * cos[degree] - x * sin[degree]);
		} else {
			x = (int) (x * cos[degree] - y * sin[degree]);
			y = (int) (y * cos[degree] + x * sin[degree]);
		}
		final int[] result = new int[2];
		result[0] = x;
		result[1] = y;

		return result;
	}
}