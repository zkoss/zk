/* ImageContentConverter.java

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
package org.zkoss.bind.converter.sys;

import java.awt.image.RenderedImage;
import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.image.Images;
import org.zkoss.zk.ui.UiException;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class ImageContentConverter implements Serializable, Converter<Object, Object, org.zkoss.zul.Image>{

	@Override
	public Object coerceToUi(Object beanProp, org.zkoss.zul.Image imgComp, BindContext ctx) {
		if(beanProp==null){
			return null;
			
		} else if(beanProp instanceof org.zkoss.image.Image){
			return beanProp;
			
		}else if(beanProp instanceof RenderedImage){
			RenderedImage awtImg = (RenderedImage)beanProp;
			try {
				 return Images.encode("a.png", awtImg);
				
			} catch (java.io.IOException ex) {
				throw new UiException(ex);
			}
		}
		return beanProp;
	}

	@Override
	public Object coerceToBean(Object compAttr, org.zkoss.zul.Image imgComp, BindContext ctx) {
		return compAttr;
	}

}
