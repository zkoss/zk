/* Canvas.java

 {{IS_NOTE
 Purpose: ZK Canvas Component
 
 Description:
 
 History:
 Jan 24, 2008 , Created by willychiang
 }}IS_NOTE

 Copyright (C) 2008 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.au.out.AuInvoke;

/**
 * A generic canvas component.
 * 
 * <p>Non XUL extension.
 * 
 * @author willychiang
 * @since 3.1.0
 */
public class Canvas extends HtmlBasedComponent{
		
	private static final long serialVersionUID = 1L;
	
	private int _fillStyleR, _fillStyleG, _fillStyleB,
				_strokeStyleR, _strokeStyleG, _strokeStyleB;
	
	private double _fillStyleAlpha,	_strokeStyleAlpha;
	
	/**
	 * Set the filled-in color, the alpha value will be set to 1.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 */
	public void setFillStyle(int r,int g,int b){		
		setFillStyle(r,g,b,1.0);
	}
	
	/**
	 * Set the filled-in color.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 * @param a	Alpha Value, 0-1.
	 */
	public void setFillStyle(int r,int g,int b, double a){
		
		_fillStyleR=r;
		_fillStyleG=g;
		_fillStyleB=b;
		_fillStyleAlpha=a;
				
		response("fillStyle",_fillStyleR+","+
								_fillStyleG+","+
								_fillStyleB+","+
								_fillStyleAlpha);
	}

	/**
	 * Set the stroke color, the alpha value will be set to 1.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 */
	public void setStrokeStyle(int r,int g,int b){
		setStrokeStyle(r,b,g,1.0);
	}

	/**
	 * Set the stroke color.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 * @param a	Alpha Value, 0-1.
	 */
	public void setStrokeStyle(int r,int g,int b, double a){
		
		_strokeStyleR=r;
		_strokeStyleG=g;
		_strokeStyleB=b;
		_strokeStyleAlpha=a;
		
		response("strokeStyle",_strokeStyleR+","+
								_strokeStyleG+","+
								_strokeStyleB+","+
								_strokeStyleAlpha);
	}
	
	
	/**
	 * Draw a rectangle and fill it in with the current filled-in style.
	 * @param x Position in X-axis of the left-top point.
	 * @param y Position in Y-axis of the left-top point.
	 * @param width Width of the rectangle.
	 * @param height Height of the rectangle.
	 */
	public void fillRect(int x,int y,int width,int height){		
		response("fillRect", x+","+y+","+width+","+height);		
	}
	
	/**
	 * Draw a rectangle with the current stroke style.
	 * @param x Position in X-axis of the left-top point.
	 * @param y Position in Y-axis of the left-top point.
	 * @param width Width of the rectangle.
	 * @param height Height of the rectangle.
	 */
	public void strokeRect(int x,int y,int width,int height){
		response("strokeRect", x+","+y+","+width+","+height);		
	}
	
	/**
	 * Draw a line with the current stroke style.
	 * @param x1 Position in X-axis of the left-top point. 
	 * @param y1 Position in Y-axis of the left-top point.
	 * @param x2 Position in X-axis of the right-down point. 
	 * @param y2 Position in Y-axis of the right-down point. 
	 */
	public void drawLine(int x1,int y1,int x2,int y2){		
		response("drawLine", x1+","+y1+","+x2+","+y2);			
	}
	
	/**
	 * Draw a circle and fill it in with the current filled-in style.
	 * @param x Position in X-axis of the center point of the circle. 
	 * @param y Position in Y-axis of the center point of the circle.
	 * @param radius Radius of the circle.
	 */
	public void fillCircle(int x,int y,int radius){
		fillArc(x,y,radius,0,Math.PI*2,true);
	}
	
	/**
	 * Draw a circle with the current stroke style.
	 * @param x Position in X-axis of the center point of the circle. 
	 * @param y Position in Y-axis of the center point of the circle.
	 * @param radius Radius of the circle.
	 */
	public void strokeCircle(int x,int y,int radius){
		strokeArc(x,y,radius,0,Math.PI*2,true);
	}
	
	/**
	 * Draw an arc and fill it in with the current filled-in style.
	 * @param x Position in X-axis of the center point of the arc.
	 * @param y Position in Y-axis of the center point of the arc.
	 * @param radius Radius of the arc.
	 * @param startAngle Sets the starting angle of this arc.
	 * @param endAngle Sets the ending angle of this arc.
	 * @param anticlockwise True if drawing in anti-clockwise orientation.
	 */
	public void fillArc(int x,int y,int radius,double startAngle, double endAngle, boolean anticlockwise){
		response("fillArc", x+","+y+","+radius+","+startAngle+","+endAngle+","+anticlockwise);
	}
	
	/**
	 * Draw an arc with the current stroke style.
	 * @param x Position in X-axis of the center point of the arc.
	 * @param y Position in Y-axis of the center point of the arc.
	 * @param radius Radius of the arc.
	 * @param startAngle Sets the starting angle of this arc.
	 * @param endAngle Sets the ending angle of this arc.
	 * @param anticlockwise True if drawing in anti-clockwise orientation.
	 */
	public void strokeArc(int x,int y,int radius,double startAngle, double endAngle, boolean anticlockwise){
		response("strokeArc", x+","+y+","+radius+","+startAngle+","+endAngle+","+anticlockwise);
	}
	
	/**
	 * Draw an external image file.
	 * @param imgPath Path to the image, can be a relative path in the web's folder or an URL starts with "http://". 
	 * @param x Position in X-axis of the center point of the image.
	 * @param y Position in Y-axis of the center point of the image.
	 */
	public void drawImage(String imgPath, int x, int y){
		response("drawImage", imgPath+","+x+","+y);		
	}
	
	/**
	 * Draw an external image file with the specified width and height.
	 * @param imgPath Path to the image, can be a relative path in the web's folder or an URL starts with "http://". 
	 * @param x Position in X-axis of the center point of the image.
	 * @param y Position in Y-axis of the center point of the image.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 */
	public void drawImage(String imgPath, int x, int y, int width, int height){		
		response("drawImage", imgPath+","+x+","+y+","+width+","+height);		
	}
	
	/**
	 * Clear the canvas and fill in with white color.
	 */
	public void clear(){		
		response("clear", "");
	}
	
	private void response(String function,String arg){
		super.response(null, new AuInvoke(this, function, arg));
	}
}
