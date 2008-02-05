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

	/**
	 * Default fill style (background) is "White".
	 */
	private static int _defaultFillStyleR = 255,
						_defaultFillStyleG = 255,
						_defaultFillStyleB = 255;
	
	/**
	 * Default fill style is "Black".
	 */
	private static int _fillStyleR=0,
						_fillStyleG=0,
						_fillStyleB=0;
	
	/**
	 * Default stroke style is "Black".
	 */
	private static int _strokeStyleR=0,
						_strokeStyleG=0,
						_strokeStyleB=0;
	
	private static double _fillStyleAlpha = 1.0,
							_strokeStyleAlpha = 1.0;
	
	/**
	 * If width and height of the canvas is not assigned
	 * in zul file, they are assigned 300px and 150px.
	 */
	private static int _defaultWidth = 300,
						_defaultHeight = 150;
	
	/**
	 * Set the filled-in color, the alpha value will be set to 1.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 */
	public void setFillStyleRGB(int r,int g,int b){
		
		_fillStyleR=r;
		_fillStyleG=g;
		_fillStyleB=b;
		_fillStyleAlpha=1.0;
				
		response(null, new AuInvoke(this, "fillStyle", 
											_fillStyleR+","+
											_fillStyleG+","+
											_fillStyleB+","+
											_fillStyleAlpha));
	}
	
	/**
	 * Set the filled-in color.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 * @param a	Alpha Value, 0-1.
	 */
	public void setFillStyleRGBA(int r,int g,int b, double a){
		
		_fillStyleR=r;
		_fillStyleG=g;
		_fillStyleB=b;
		_fillStyleAlpha=a;
				
		response(null, new AuInvoke(this, "fillStyle", 
											_fillStyleR+","+
											_fillStyleG+","+
											_fillStyleB+","+
											_fillStyleAlpha));
	}

	/**
	 * Set the stroke color, the alpha value will be set to 1.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 */
	public void setStrokeStyleRGB(int r,int g,int b){
		
		_strokeStyleR=r;
		_strokeStyleG=g;
		_strokeStyleB=b;
		_strokeStyleAlpha=1.0;
			
		response(null, new AuInvoke(this, "strokeStyle", 
											_strokeStyleR+","+
											_strokeStyleG+","+
											_strokeStyleB+","+
											_strokeStyleAlpha));
	}

	/**
	 * Set the stroke color.
	 * @param r Red Color, 1-255.
	 * @param g	Green Color, 1-255.
	 * @param b	Blue Color, 1-255.
	 * @param a	Alpha Value, 0-1.
	 */
	public void setStrokeStyleRGBA(int r,int g,int b, double a){
		
		_strokeStyleR=r;
		_strokeStyleG=g;
		_strokeStyleB=b;
		_strokeStyleAlpha=a;
		
		response(null, new AuInvoke(this, "strokeStyle", 
											_strokeStyleR+","+
											_strokeStyleG+","+
											_strokeStyleB+","+
											_strokeStyleAlpha));
	}
	
	
	/**
	 * Draw a rectangle and fill it in with the current filled-in style.
	 * @param x Position in X-axis of the left-top point.
	 * @param y Position in Y-axis of the left-top point.
	 * @param width Width of the rectangle.
	 * @param height Height of the rectangle.
	 */
	public void fillRect(int x,int y,int width,int height){
		
		response(null, 
				new AuInvoke(this, "fillRect", x+","+y+","+width+","+height));
		
	}
	
	/**
	 * Draw a rectangle with the current stroke style.
	 * @param x Position in X-axis of the left-top point.
	 * @param y Position in Y-axis of the left-top point.
	 * @param width Width of the rectangle.
	 * @param height Height of the rectangle.
	 */
	public void strokeRect(int x,int y,int width,int height){
		response(null, 
				new AuInvoke(this, "strokeRect", x+","+y+","+width+","+height));
		
	}
	
	/**
	 * Draw a line with the current stroke style.
	 * @param x1 Position in X-axis of the left-top point. 
	 * @param y1 Position in Y-axis of the left-top point.
	 * @param x2 Position in X-axis of the right-down point. 
	 * @param y2 Position in Y-axis of the right-down point. 
	 */
	public void drawLine(int x1,int y1,int x2,int y2){
		
		response(null, 
				new AuInvoke(this, "drawLine", x1+","+y1+","+x2+","+y2));		
		
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
		response(null, 
				new AuInvoke(this, "fillArc", x+","+y+","+radius+","+startAngle+","+endAngle+","+anticlockwise));
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
		response(null, 
				new AuInvoke(this, "strokeArc", x+","+y+","+radius+","+startAngle+","+endAngle+","+anticlockwise));
	}
	
	/**
	 * Draw an external image file.
	 * @param imgPath Path to the image, can be a relative path in the web's folder or an URL starts with "http://". 
	 * @param x Position in X-axis of the center point of the image.
	 * @param y Position in Y-axis of the center point of the image.
	 */
	public void drawImage(String imgPath, int x, int y){
		
		response(null, 
				new AuInvoke(this, "drawImage", imgPath+","+x+","+y));
		
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
		
		response(null, 
				new AuInvoke(this, "drawImage", imgPath+","+x+","+y+","+width+","+height));
		
	}
	
	/**
	 * Clear the canvas and fill in with white color.
	 */
	public void clear(){
		
		//set fill style
		this.setFillStyleRGB(_defaultFillStyleR,
							_defaultFillStyleG,
							_defaultFillStyleB);
		
		int _width = _getWidth()
			,_height = _getHeight();
		
		if(super.getWidth() == null){
			
			_width = _defaultWidth;
			_height = _defaultHeight;
			
		}
		
		//draw a rectangle to clear the canvas
		this.fillRect(0,0,_width,_height);
		
		//restore fill style
		this.setFillStyleRGBA(_fillStyleR,
							_fillStyleG,
							_fillStyleB,
							_fillStyleAlpha);
	}
	
	/**
	 * Get the width of the canvas, if not specified in zul file, the default value is 300.
	 * @return Width of the canvas.
	 */
	private int _getWidth(){
		int _width = _defaultWidth;
		String _tmpWidth;
		
		try{
			if((_tmpWidth = super.getWidth()) != null){
				_width=Integer.parseInt(_myComp(_tmpWidth));
			}
		}catch(Exception e){
			return _width;
		}
		
		return _width;
	}
	
	/**
	 * Get the height of the canvas, if not specified in zul file, the default value is 150.
	 * @return Height of the canvas.
	 */
	private int _getHeight(){
		int _height = _defaultHeight;
		String _tmpHeight;
		
		try{
			if((_tmpHeight = super.getWidth()) != null){
				_height=Integer.parseInt(_myComp(_tmpHeight));
			}
		}catch(Exception e){
			return _height;
		}
				
		return _height;
	}
	
	/**
	 * Remove "px" of the width or height value.
	 * @param str Width or height value.
	 * @return Width or height value without "px".
	 */
	private String _myComp(String str){
		
		String _outStr = str;

		if(str.endsWith("px")){
			int i = str.lastIndexOf("px");
			_outStr = str.substring(0,i); 
		}
		
		return _outStr;
	}

}
