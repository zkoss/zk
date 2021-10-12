/* B80_ZK_3044Test.java

    Purpose:
        
    Description:
        
    History:
        4:51 PM 30/12/15, Created by Sefi

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.junit.Test;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.image.encoder.ImageEncoder;
import org.zkoss.image.encoder.ImageEncoders;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Popup;

/**
 * @author Sefi
 */
public class B80_ZK_3044Test {
    @Test public void testXulElement() {
        Timebox d = new Timebox();
        Popup popup = new Popup();
        String uuidstr = "uuid(" + popup.getUuid() + ")";

        // test context
        d.getPropertyAccess("context").setValue(d, "test");
        assertEquals("test", d.getContext());
        assertEquals("test", d.getPropertyAccess("context").getValue(d));

        d.getPropertyAccess("context").setValue(d, popup);
        assertEquals(uuidstr, d.getContext());
        assertEquals(uuidstr, d.getPropertyAccess("context").getValue(d));

        // test popup
        d.getPropertyAccess("popup").setValue(d, "test");
        assertEquals("test", d.getPopup());
        assertEquals("test", d.getPropertyAccess("popup").getValue(d));

        d.getPropertyAccess("popup").setValue(d, popup);
        assertEquals(uuidstr, d.getPopup());
        assertEquals(uuidstr, d.getPropertyAccess("popup").getValue(d));

        // test tooltip
        d.getPropertyAccess("tooltip").setValue(d, "test");
        assertEquals("test", d.getTooltip());
        assertEquals("test", d.getPropertyAccess("tooltip").getValue(d));

        d.getPropertyAccess("tooltip").setValue(d, popup);
        assertEquals(uuidstr, d.getTooltip());
        assertEquals(uuidstr, d.getPropertyAccess("tooltip").getValue(d));

    }

    @Test public void testLabelImageElement() throws IOException{
        Listcell d = new Listcell();

        // test imageContent
        Image image = new Image() {
            public boolean isBinary() {
                return false;
            }

            public boolean inMemory() {
                return false;
            }

            public byte[] getByteData() {
                return new byte[0];
            }

            public String getStringData() {
                return null;
            }

            public InputStream getStreamData() {
                return null;
            }

            public Reader getReaderData() {
                return null;
            }

            public String getName() {
                return null;
            }

            public String getFormat() {
                return null;
            }

            public String getContentType() {
                return null;
            }

            public boolean isContentDisposition() {
                return false;
            }

            public int getWidth() {
                return 0;
            }

            public int getHeight() {
                return 0;
            }

            public ImageIcon toImageIcon() {
                return null;
            }
        };

        //test imageContent
        d.getPropertyAccess("imageContent").setValue(d, image);
        assertEquals(image, d.getImageContent());
        assertEquals(image, d.getPropertyAccess("imageContent").getValue(d));

        //test hoverImageContent
        d.getPropertyAccess("hoverImageContent").setValue(d, image);
        assertEquals(image, d.getHoverImageContent());
        assertEquals(image, d.getPropertyAccess("hoverImageContent").getValue(d));


        RenderedImage renderdeImage = null;
        renderdeImage = ImageIO.read(new File("src/archive/test2/img/circle.png"));

        String name = "a.png";
        final int j = name.lastIndexOf('.');
        if (j < 0)
            throw new IllegalArgumentException("Illegal name: "+name+"\nIt must contain the extension as the format, such as foo.png");
        String formatName =  name.substring(j + 1);
        ImageEncoder encoder = ImageEncoders.newInstance(formatName);
        AImage aimage;
        byte[] imageData;

        aimage =  new AImage(name, encoder.encode(renderdeImage));
        imageData = aimage.getByteData();

        AImage res;
        byte[] resData;
        d.getPropertyAccess("imageContent").setValue(d, renderdeImage);
        res = (AImage)d.getImageContent();
        resData = res.getByteData();
        if(Arrays.equals(resData, imageData)) {
            assertEquals(renderdeImage, renderdeImage);
        } else {
            assertEquals(imageData, resData);                
        }

        res = (AImage)(d.getPropertyAccess("imageContent").getValue(d));
        resData = res.getByteData();
        if(Arrays.equals(resData, imageData)) {
            assertEquals(renderdeImage, renderdeImage);
        } else {
            assertEquals(imageData, resData);                
        }

        d.getPropertyAccess("hoverImageContent").setValue(d, renderdeImage);
        res = (AImage)d.getHoverImageContent();
        resData = res.getByteData();
        if(Arrays.equals(resData, imageData)) {
            assertEquals(renderdeImage, renderdeImage);
        } else {
            assertEquals(imageData, resData);                
        }

        res = (AImage)(d.getPropertyAccess("hoverImageContent").getValue(d));
        resData = res.getByteData();
        if(Arrays.equals(resData, imageData)) {
            assertEquals(renderdeImage, renderdeImage);
        } else {
            assertEquals(imageData, resData);                
        }
    }

    @Test
    public void testNumberInputElement() {
        Intbox d = new Intbox();

        // test locale
        d.getPropertyAccess("locale").setValue(d, "zh_TW");
        assertEquals("zh_TW", d.getLocale().toString());
        assertEquals("zh_TW", d.getPropertyAccess("locale").getValue(d));
        
        d.getPropertyAccess("locale").setValue(d, new Locale("zh_TW"));
        assertEquals("zh_tw", d.getLocale().toString());
        assertEquals("zh_tw", d.getPropertyAccess("locale").getValue(d));
    }
}
