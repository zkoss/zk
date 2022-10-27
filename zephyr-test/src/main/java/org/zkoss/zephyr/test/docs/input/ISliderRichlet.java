/* ISliderRichlet.java

	Purpose:

	Description:

	History:
		10:33 AM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ISlider;

/**
 * A set of example for {@link ISlider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Slider">ISlider</a>,
 * if any.
 * @author jumperchen
 * @see ISlider
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
