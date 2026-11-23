/* F80_ZK_2546Test.java

	Purpose:
		
	Description:
		
	History:
		4:51 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import javax.swing.*;

import org.junit.Test;
import org.zkoss.image.Image;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

/**
 * @author jumperchen
 */
public class F80_ZK_2546Test {
	@Test public void testAbstractComponent() {
		Div d = new Div();

		// test id
		d.getPropertyAccess("id").setValue(d, "myvalue");
		assertEquals("myvalue", d.getId());
		assertEquals("myvalue", d.getPropertyAccess("id").getValue(d));

		// test mold
		d.getPropertyAccess("mold").setValue(d, "default");
		assertEquals("default", d.getMold());
		assertEquals("default", d.getPropertyAccess("mold").getValue(d));

		// test visible
		d.getPropertyAccess("visible").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isVisible());
		assertEquals(Boolean.FALSE, d.getPropertyAccess("visible").getValue(d));

	}

	@Test public void testHtmlBasedComponent() {
		Div d = new Div();

		// test width
		d.getPropertyAccess("width").setValue(d, "10px");
		assertEquals("10px", d.getWidth());
		assertEquals("10px", d.getPropertyAccess("width").getValue(d));

		// test height
		d.getPropertyAccess("height").setValue(d, "10px");
		assertEquals("10px", d.getHeight());
		assertEquals("10px", d.getPropertyAccess("height").getValue(d));

		// test sclass
		d.getPropertyAccess("sclass").setValue(d, "default");
		assertEquals("default", d.getSclass());
		assertEquals("default", d.getPropertyAccess("sclass").getValue(d));

		// test zclass
		d.getPropertyAccess("zclass").setValue(d, "default");
		assertEquals("default", d.getZclass());
		assertEquals("default", d.getPropertyAccess("zclass").getValue(d));

		// test style
		d.getPropertyAccess("style").setValue(d, "default");
		assertEquals("default", d.getStyle());
		assertEquals("default", d.getPropertyAccess("style").getValue(d));

		// test left
		d.getPropertyAccess("left").setValue(d, "10px");
		assertEquals("10px", d.getLeft());
		assertEquals("10px", d.getPropertyAccess("left").getValue(d));

		// test top
		d.getPropertyAccess("top").setValue(d, "12px");
		assertEquals("12px", d.getTop());
		assertEquals("12px", d.getPropertyAccess("top").getValue(d));

		// test droppable
		d.getPropertyAccess("droppable").setValue(d, "droppable");
		assertEquals("droppable", d.getDroppable());
		assertEquals("droppable", d.getPropertyAccess("droppable").getValue(d));

		// test draggable
		d.getPropertyAccess("draggable").setValue(d, "draggable");
		assertEquals("draggable", d.getDraggable());
		assertEquals("draggable", d.getPropertyAccess("draggable").getValue(d));

		// test tooltiptext
		d.getPropertyAccess("tooltiptext").setValue(d, "tooltiptext");
		assertEquals("tooltiptext", d.getTooltiptext());
		assertEquals("tooltiptext",
				d.getPropertyAccess("tooltiptext").getValue(d));

		// test zindex
		d.getPropertyAccess("zindex").setValue(d, 1);
		assertEquals(1, d.getZindex());
		assertEquals(1, d.getPropertyAccess("zindex").getValue(d));

		// test renderdefer
		d.getPropertyAccess("renderdefer").setValue(d, 1);
		assertEquals(1, d.getRenderdefer());
		assertEquals(1, d.getPropertyAccess("renderdefer").getValue(d));

		// test action
		d.getPropertyAccess("action").setValue(d, "action");
		assertEquals("action", d.getAction());
		assertEquals("action", d.getPropertyAccess("action").getValue(d));

		// reset
		d = new Div();
		// test hflex
		d.getPropertyAccess("hflex").setValue(d, "3");
		assertEquals("3", d.getHflex());
		assertEquals("3", d.getPropertyAccess("hflex").getValue(d));

		// test vflex
		d.getPropertyAccess("vflex").setValue(d, "3");
		assertEquals("3", d.getVflex());
		assertEquals("3", d.getPropertyAccess("vflex").getValue(d));
	}

	@Test public void testAbstractTag() {
		Li d = new Li();

		// test id
		d.getPropertyAccess("id").setValue(d, "myvalue");
		assertEquals("myvalue", d.getId());
		assertEquals("myvalue", d.getPropertyAccess("id").getValue(d));

		// test sclass
		d.getPropertyAccess("sclass").setValue(d, "default");
		assertEquals("default", d.getSclass());
		assertEquals("default", d.getPropertyAccess("sclass").getValue(d));

		// test style
		d.getPropertyAccess("style").setValue(d, "default");
		assertEquals("default", d.getStyle());
		assertEquals("default", d.getPropertyAccess("style").getValue(d));

		// test visible
		d.getPropertyAccess("visible").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isVisible());
		assertEquals(Boolean.FALSE, d.getPropertyAccess("visible").getValue(d));

	}

	@Test public void testHtmlNativeComponent() {
		HtmlNativeComponent d = new HtmlNativeComponent();

		// test id
		d.getPropertyAccess("id").setValue(d, "myvalue");
		assertEquals("myvalue", d.getId());
		assertEquals("myvalue", d.getPropertyAccess("id").getValue(d));

		// test tag
		d.getPropertyAccess("tag").setValue(d, "tag");
		assertEquals("tag", d.getTag());
		assertEquals("tag", d.getPropertyAccess("tag").getValue(d));

		// test epilogContent
		d.getPropertyAccess("epilogContent").setValue(d, "epilogContent");
		assertEquals("epilogContent", d.getEpilogContent());
		assertEquals("epilogContent",
				d.getPropertyAccess("epilogContent").getValue(d));

		// test epilogContent
		d.getPropertyAccess("prologContent").setValue(d, "prologContent");
		assertEquals("prologContent", d.getPrologContent());
		assertEquals("prologContent",
				d.getPropertyAccess("prologContent").getValue(d));
	}

	@Test public void testCombobox() {
		Combobox d = new Combobox();

		// test id
		d.getPropertyAccess("id").setValue(d, "myvalue");
		assertEquals("myvalue", d.getId());
		assertEquals("myvalue", d.getPropertyAccess("id").getValue(d));

		// test autocomplete
		d.getPropertyAccess("autocomplete").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isAutocomplete());
		assertEquals(Boolean.FALSE,
				d.getPropertyAccess("autocomplete").getValue(d));

		// test autodrop
		d.getPropertyAccess("autodrop").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isAutodrop());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("autodrop").getValue(d));

		// test buttonVisible
		d.getPropertyAccess("buttonVisible").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isButtonVisible());
		assertEquals(Boolean.FALSE,
				d.getPropertyAccess("buttonVisible").getValue(d));

	}

	@Test public void testDatebox() {
		Datebox d = new Datebox();

		Date date = new Date();
		// test value
		d.getPropertyAccess("value").setValue(d, date);
		assertEquals(date, d.getValue());
		assertEquals(date, d.getPropertyAccess("value").getValue(d));

		// test lenient
		d.getPropertyAccess("lenient").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isLenient());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("lenient").getValue(d));

		// test buttonVisible
		d.getPropertyAccess("buttonVisible").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isButtonVisible());
		assertEquals(Boolean.FALSE,
				d.getPropertyAccess("buttonVisible").getValue(d));

	}

	@Test public void testXulElement() {
		Timebox d = new Timebox();

		// test context
		d.getPropertyAccess("context").setValue(d, "test");
		assertEquals("test", d.getContext());
		assertEquals("test", d.getPropertyAccess("context").getValue(d));

		// test popup
		d.getPropertyAccess("popup").setValue(d, "test");
		assertEquals("test", d.getPopup());
		assertEquals("test", d.getPropertyAccess("popup").getValue(d));

		// test tooltip
		d.getPropertyAccess("tooltip").setValue(d, "test");
		assertEquals("test", d.getTooltip());
		assertEquals("test", d.getPropertyAccess("tooltip").getValue(d));

		// test ctrlKeys
		d.getPropertyAccess("ctrlKeys").setValue(d, "ctrlKeys");
		assertEquals("ctrlKeys", d.getCtrlKeys());
		assertEquals("ctrlKeys", d.getPropertyAccess("ctrlKeys").getValue(d));
	}

	@Test public void testTimebox() {
		Timebox d = new Timebox();

		Date date = new Date();
		// test value
		d.getPropertyAccess("value").setValue(d, date);
		assertEquals(date, d.getValue());
		assertEquals(date, d.getPropertyAccess("value").getValue(d));

		// test buttonVisible
		d.getPropertyAccess("buttonVisible").setValue(d, Boolean.FALSE);
		assertEquals(Boolean.FALSE, d.isButtonVisible());
		assertEquals(Boolean.FALSE,
				d.getPropertyAccess("buttonVisible").getValue(d));

	}

	@Test public void testFormatInput() {
		Datebox d = new Datebox();
		// test format
		d.getPropertyAccess("format").setValue(d, "00.00");
		assertEquals("00.00", d.getFormat());
		assertEquals("00.00", d.getPropertyAccess("format").getValue(d));
	}

	@Test public void testInputElement() {
		Textbox d = new Textbox();

		// test name
		d.getPropertyAccess("name").setValue(d, "00.00");
		assertEquals("00.00", d.getName());
		assertEquals("00.00", d.getPropertyAccess("name").getValue(d));

		// test rawValue
		d.getPropertyAccess("rawValue").setValue(d, "rawValue.00");
		assertEquals("rawValue.00", d.getRawValue());
		assertEquals("rawValue.00",
				d.getPropertyAccess("rawValue").getValue(d));

		// test disabled
		d.getPropertyAccess("disabled").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isDisabled());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("disabled").getValue(d));

		// test readonly
		d.getPropertyAccess("readonly").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isReadonly());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("readonly").getValue(d));

		// test placeholder
		d.getPropertyAccess("placeholder").setValue(d, "00.00");
		assertEquals("00.00", d.getPlaceholder());
		assertEquals("00.00", d.getPropertyAccess("placeholder").getValue(d));

		// test inplace
		d.getPropertyAccess("inplace").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isInplace());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("inplace").getValue(d));

		// test instant
		d.getPropertyAccess("instant").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isInstant());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("instant").getValue(d));

		// test cols
		d.getPropertyAccess("cols").setValue(d, 3);
		assertEquals(3, d.getCols());
		assertEquals(3, d.getPropertyAccess("cols").getValue(d));

		// test maxlength
		d.getPropertyAccess("maxlength").setValue(d, 3);
		assertEquals(3, d.getMaxlength());
		assertEquals(3, d.getPropertyAccess("maxlength").getValue(d));

		// test tabindex
		d.getPropertyAccess("tabindex").setValue(d, 3);
		assertEquals(3, d.getTabindex());
		assertEquals(3, d.getPropertyAccess("tabindex").getValue(d));

	}

	@Test public void testDoublebox() {
		Doublebox d = new Doublebox();
		Double dd = 0.0;
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(dd, d.getValue());
		assertEquals(dd, d.getPropertyAccess("value").getValue(d));
	}

	@Test public void testLongbox() {
		Longbox d = new Longbox();
		Long dd = 0l;
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(dd, d.getValue());
		assertEquals(dd, d.getPropertyAccess("value").getValue(d));
	}

	@Test public void testIntbox() {
		Intbox d = new Intbox();
		Integer dd = new Integer(20);

		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(dd, d.getValue());
		assertEquals(dd, d.getPropertyAccess("value").getValue(d));
	}

	@Test public void testLabel() {
		Label d = new Label();

		// test value
		d.getPropertyAccess("value").setValue(d, "myvalue");
		assertEquals("myvalue", d.getValue());
		assertEquals("myvalue", d.getPropertyAccess("value").getValue(d));

		// test maxlength
		d.getPropertyAccess("maxlength").setValue(d, 3);
		assertEquals(3, d.getMaxlength());
		assertEquals(3, d.getPropertyAccess("maxlength").getValue(d));

		// test multiline
		d.getPropertyAccess("multiline").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isMultiline());
		assertEquals(Boolean.TRUE,
				d.getPropertyAccess("multiline").getValue(d));

	}

	@Test public void testLabelElement() {
		Listcell d = new Listcell();

		// test label
		d.getPropertyAccess("label").setValue(d, "myvalue");
		assertEquals("myvalue", d.getLabel());
		assertEquals("myvalue", d.getPropertyAccess("label").getValue(d));
	}

	@Test public void testLabelImageElement() {
		Listcell d = new Listcell();

		// test iconSclass
		d.getPropertyAccess("iconSclass").setValue(d, "myvalue");
		assertEquals("myvalue", d.getIconSclass());
		assertEquals("myvalue", d.getPropertyAccess("iconSclass").getValue(d));

		// test image
		d.getPropertyAccess("image").setValue(d, "myvalue");
		assertEquals("myvalue", d.getImage());
		assertEquals("myvalue", d.getPropertyAccess("image").getValue(d));

		// test hoverImage
		d.getPropertyAccess("hoverImage").setValue(d, "hoverImage");
		assertEquals("hoverImage", d.getHoverImage());
		assertEquals("hoverImage",
				d.getPropertyAccess("hoverImage").getValue(d));

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
		d.getPropertyAccess("imageContent").setValue(d, image);
		assertEquals(image, d.getImageContent());
		assertEquals(image, d.getPropertyAccess("imageContent").getValue(d));

		d.getPropertyAccess("hoverImageContent").setValue(d, image);
		assertEquals(image, d.getHoverImageContent());
		assertEquals(image,
				d.getPropertyAccess("hoverImageContent").getValue(d));
	}

	@Test
	public void testText() {
		Text d = new Text();

		// test value
		d.getPropertyAccess("value").setValue(d, "value");
		assertEquals("value", d.getValue());
		assertEquals("value", d.getPropertyAccess("value").getValue(d));

		// test encode
		d.getPropertyAccess("encode").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isEncode());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("encode").getValue(d));
	}


	@Test
	public void testTextbox() {
		Textbox d = new Textbox();

		// test value
		d.getPropertyAccess("value").setValue(d, "value");
		assertEquals("value", d.getValue());
		assertEquals("value", d.getPropertyAccess("value").getValue(d));

		// test type
		d.getPropertyAccess("type").setValue(d, "email");
		assertEquals("email", d.getType());
		assertEquals("email", d.getPropertyAccess("type").getValue(d));

		// test tabbable
		d.getPropertyAccess("tabbable").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isTabbable());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("tabbable").getValue(d));

		// test multiline
		d.getPropertyAccess("multiline").setValue(d, Boolean.TRUE);
		assertEquals(Boolean.TRUE, d.isMultiline());
		assertEquals(Boolean.TRUE, d.getPropertyAccess("multiline").getValue(d));

		// test rows
		d.getPropertyAccess("rows").setValue(d, 3);
		assertEquals(3, d.getRows());
		assertEquals(3, d.getPropertyAccess("rows").getValue(d));
	}

	@Test
	public void testNumberInputElement() {
		Intbox d = new Intbox();

		// test locale
		d.getPropertyAccess("locale").setValue(d, "zh_TW");
		assertEquals("zh_TW", d.getLocale().toString());
		assertEquals("zh_TW", d.getPropertyAccess("locale").getValue(d));
	}
}
