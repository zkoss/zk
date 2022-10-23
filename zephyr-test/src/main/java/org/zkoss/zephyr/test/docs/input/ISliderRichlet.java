/* ISliderRichlet.java

	Purpose:

	Description:

	History:
		10:33 AM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ISlider;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ISlider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Slider">ISlider</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.ISlider
 */
@RichletMapping("/input/islider")
public class ISliderRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IComponent defaultMold() {
		return ISlider.DEFAULT;
	}
	@RichletMapping("/mold/sphere")
	public IComponent sphereMold() {
		return ISlider.SPHERE;
	}
	@RichletMapping("/mold/scale")
	public IComponent scaleMold() {
		return ISlider.SCALE;
	}
	@RichletMapping("/mold/knob")
	public IComponent knobMold() {
		return ISlider.KNOB;
	}
}
