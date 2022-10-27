/* ISlider.java

	Purpose:

	Description:

	History:
		Wed Dec 01 16:49:39 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.ScrollData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Slider;

/**
 * Immutable {@link Slider} component
 *
 * <p>A slider component represents a slider with a scale and a knob. It can be
 * used to let user select a value by sliding the knob along the scale.
 * A slider accepts a range of value starting from 0 to certain maximum value.
 * The default maximum value of slider scale is 100. You could change the
 * maximum allowed value by the {@code maxpos} attribute.
 * However the default minimum is 0 and cannot be changed.
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onScroll</td>
 *          <td><strong>ActionData</strong>: {@link ScrollData}
 *          <br>Denotes the content of a scrollable component has been scrolled by the user.</td>
 *       </tr>
 *       <tr>
 *          <td>onScrolling</td>
 *          <td><strong>ActionData</strong>: {@link ScrollData}
 *          <br>Denotes that the user is scrolling a scrollable component.
 *          Notice that the component's content (at the server) won't be changed until
 *          onScroll is received. Thus, you have to invoke the {@code getPos} method in
 *          the ScrollData class to retrieve the temporary position.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Molds</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "default"}</td>
 *          <td><img src="doc-files/ISlider_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "sphere"}</td>
 *          <td><img src="doc-files/ISlider_mold_sphere.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "scale"}</td>
 *          <td><img src="doc-files/ISlider_mold_scale.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "knob"}</td>
 *          <td><img src="doc-files/ISlider_mold_knob.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Slider
 */
@StatelessStyle
public interface ISlider extends IXulElement<ISlider>, IAnyGroup<ISlider> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISlider DEFAULT = new ISlider.Builder().build();
	/**
	 * Constant for sphere mold attributes of this immutable component.
	 */
	ISlider SPHERE = new ISlider.Builder().setMold("sphere").build();
	/**
	 * Constant for scale mold attributes of this immutable component.
	 */
	ISlider SCALE = new ISlider.Builder().setMold("scale").build();
	/**
	 * Constant for knob mold attributes of this immutable component.
	 */
	ISlider KNOB = new ISlider.Builder().setMold("knob").build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Slider> getZKType() {
		return Slider.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Slider"}</p>
	 *
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.inp.Slider";
	}

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkValue() {
		IComponentChecker.checkOrient(getOrient());
		//IComponentChecker.checkMode(getMode(), "integer", "decimal");
		double angleArc = getAngleArc();
		if (angleArc > 360 || angleArc <= 0)
			throw new WrongValueException("The value of angleArc should be between 0 and 360 degree");
		if (getStrokeWidth() <= 0)
			throw new WrongValueException("The value of strokeWidth should be larger than 0");
		double scaleInput = getScaleInput();
		if (scaleInput <= 0)
			throw new WrongValueException("The value of scaleInput should be larger than 0");
		if (getMinpos() < 0 || getMinposInDouble() < 0)
			throw new WrongValueException("Nonpositive is not allowed.");
		if (getMaxpos() <= 0 || getMaxposInDouble() <= 0)
			throw new WrongValueException("Nonpositive is not allowed.");
	}

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ISlider checkCurpos() {
		int curpos = getCurpos();
		int maxpos = getMaxpos();
		int minpos = getMinpos();
		boolean hasChange = false;
		if (curpos < minpos) {
			curpos = minpos;
			hasChange = true;
		}
		else if (curpos > maxpos) {
			curpos = maxpos;
			hasChange = true;
		}
		if (hasChange) {
			return new ISlider.Builder().from(this).setCurposInDouble(curpos).build();
		}
		return this;
	}

	/** Returns the name of this component.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code name}.
	 *
	 * <p> Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @param name The name of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withName(@Nullable String name);

	/** Returns the orient.
	 * <p>Default: {@code "horizontal"}.
	 */
	default String getOrient() {
		return "horizontal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of component
	 *
	 * @param orient Either {@code "horizontal"} or {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of component
	 *
	 * @param orient The {@link Orient orient}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withOrient(Orient orient) {
		Objects.requireNonNull(orient);
		return withOrient(orient.value);
	}

	/**
	 * Returns the sliding text.
	 * <p>Default : {@code "{0}"}
	 */
	default String getSlidingtext() {
		return "{0}";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code slidingtext}.
	 *
	 * <p> Sets the sliding text.
	 *
	 * @param slidingtext The sliding text.
	 * <p>Default: {@code "{0}"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withSlidingtext(String slidingtext);

	/** Returns the current position of the slider.
	 *
	 * <p>Default: {@code 0}.
	 */
	@Value.Lazy
	default int getCurpos() {
		return (int)getCurposInDouble();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code curpos}.
	 *
	 * <p> Sets the current position of the slider.
	 *
	 * @param curpos The current position of the slider.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withCurpos(int curpos) {
		return withCurposInDouble(curpos);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code curpos}.
	 *
	 * <p> Sets the current position of the slider.
	 *
	 * @param curpos The current position of the slider.
	 * <p>Default: {@code 0.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withCurpos(double curpos) {
		return withCurposInDouble(curpos);
	}

	/** Returns the double value of slider's current position.
	 *
	 * <p>Default: {@code 0.0}
	 */
	default double getCurposInDouble() {
		return 0.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code curpos}.
	 *
	 * <p> Sets the current position of the slider.
	 *
	 * @param curpos The current position of the slider.
	 * <p>Default: {@code 0.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withCurposInDouble(double curpos);

	/** Returns the minimum position of the slider.
	 *
	 * <p>Default: {@code 0}.
	 */
	@Value.Lazy
	default int getMinpos() {
		return (int)getMinposInDouble();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minpos}.
	 *
	 * <p> Sets the value of slider's minimum position.
	 *
	 * @param minpos The value of slider's minimum position.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withMinpos(int minpos) {
		return withMinposInDouble(minpos);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minpos}.
	 *
	 * <p> Sets the value of slider's minimum position.
	 *
	 * @param minpos The value of slider's minimum position.
	 * <p>Default: {@code 0.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withMinpos(double minpos) {
		return withMinposInDouble(minpos);
	}

	/** Returns the double value of slider's minimum position.
	 *
	 * <p>Default: {@code 0.0}.
	 */
	default double getMinposInDouble() {
		return 0.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minpos}.
	 *
	 * <p> Sets the value of slider's minimum position.
	 *
	 * @param minpos The value of slider's minimum position.
	 * <p>Default: {@code 0.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withMinposInDouble(double minpos);

	/** Returns the value of slider's maximum position.
	 *
	 * <p>Default: {@code 100}.
	 */
	@Value.Lazy
	default int getMaxpos() {
		return (int)getMaxposInDouble();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxpos}.
	 *
	 * <p> Sets the value of slider's maximum position.
	 *
	 * @param maxpos The value of slider's minimum position.
	 * <p>Default: {@code 100}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withMaxpos(int maxpos) {
		return withMaxposInDouble(maxpos);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxpos}.
	 *
	 * <p> Sets the value of slider's maximum position.
	 *
	 * @param maxpos The value of slider's minimum position.
	 * <p>Default: {@code 100.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withMaxpos(double maxpos) {
		return withMaxposInDouble(maxpos);
	}

	/** Returns the double value of slider's maximum position.
	 *
	 * <p>Default: {@code 100.0}.
	 */
	default double getMaxposInDouble() {
		return 100.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxpos}.
	 *
	 * <p> Sets the value of slider's maximum position.
	 *
	 * @param maxpos The value of slider's maximum position.
	 * <p>Default: {@code 0.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withMaxposInDouble(double maxpos);

	/**
	 * Returns the step of slider
	 *
	 * <p>Default: {@code -1} (means it will scroll to the position the user clicks).
	 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
	 */
	@Value.Lazy
	default int getStep() {
		return (int)getStepInDouble();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code step}.
	 *
	 * <p> Sets the step of slider
	 *
	 * @param step The step of slider
	 * <p>Default: {@code -1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withStep(int step) {
		return withStepInDouble(step);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code step}.
	 *
	 * <p> Sets the step of slider
	 *
	 * @param step The step of slider
	 * <p>Default: {@code -1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withStep(double step) {
		return withStepInDouble(step);
	}

	/**
	 * Returns the step of slider
	 *
	 * <p>Default: {@code -1.0} (means it will scroll to the position the user clicks).
	 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
	 */
	default double getStepInDouble() {
		return -1.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code step}.
	 *
	 * <p> Sets the step of slider
	 *
	 * @param step The step of slider
	 * <p>Default: {@code -1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withStepInDouble(double step);

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * <p>Default: {@code -1} (means it will scroll to the position the user clicks).
	 */
	@Value.Lazy
	default int getPageIncrement() {
		return (int)getPageIncrementInDouble();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code pageIncrement}.
	 *
	 * <p> Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * @param pageIncrement The page increment value of slider
	 * <p>Default: {@code -1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withPageIncrement(int pageIncrement) {
		return withPageIncrementInDouble(pageIncrement);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code pageIncrement}.
	 *
	 * <p> Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * @param pageIncrement The page increment value of slider
	 * <p>Default: {@code -1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISlider withPageIncrement(double pageIncrement) {
		return withPageIncrementInDouble(pageIncrement);
	}

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * <p>Default: {@code -1.0} (means it will scroll to the position the user clicks).
	 */
	default double getPageIncrementInDouble() {
		return -1.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code pageIncrement}.
	 *
	 * <p> Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * @param pageIncrement The page increment value of slider
	 * <p>Default: {@code -1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withPageIncrementInDouble(double pageIncrement);

	/** Returns the stroke width of the knob slider.
	 * <p>Default: {@code 10.0}
	 */
	default double getStrokeWidth() {
		return 10.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code strokeWidth}.
	 *
	 * <p> Sets the stroke width of the knob slider.
	 *
	 * @param strokeWidth The stroke width of the knob slider
	 * <p>Default: {@code 10.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withStrokeWidth(double strokeWidth);

	/** Returns the degree of arc of the knob slider.
	 * <p>Default: {@code 360.0}
	 */
	default double getAngleArc() {
		return 360.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code angleArc}.
	 *
	 * <p> Sets the degree of arc of the knob slider.
	 *
	 * @param angleArc The degree of arc of the knob slider.
	 * <p>Default: {@code 360.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withAngleArc(double angleArc);

	/**
	 * Returns the scale ratio of the input in knob mold.
	 * <p>Default: {@code 1.0}</p>
	 */
	default double getScaleInput() {
		return 1.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code scaleInput}.
	 *
	 * <p> Sets the scale ratio of the input in knob mold.
	 *
	 * @param scaleInput The scale ratio of the input in knob mold.
	 * <p>Default: {@code 1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISlider withScaleInput(double scaleInput);

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component.
	 */
	static ISlider ofId(String id) {
		return new ISlider.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		String _orient = getOrient();
		if (!"horizontal".equals(_orient))
			renderer.render("orient", _orient);
		String _slidingtext = getSlidingtext();
		if (!"{0}".equals(_slidingtext))
			renderer.render("slidingtext", _slidingtext);
		double _curpos = getCurposInDouble() != 0.0 ? getCurposInDouble() : getCurpos();
		if (_curpos != 0)
			renderer.render("curpos", _curpos);
		double _maxpos = getMaxposInDouble() != 100.0 ? getMaxposInDouble() : getMaxpos();
		if (_maxpos != 100)
			renderer.render("maxpos", _maxpos);
		double _minpos = getMinposInDouble() != 0.0 ? getMinposInDouble() : getMinpos();
		if (_minpos != 0)
			renderer.render("minpos", _minpos);
		double _pginc = getPageIncrementInDouble() != 0.0 ? getPageIncrementInDouble() : getPageIncrement();
		if (_pginc >= 0)
			renderer.render("pageIncrement", _pginc);
		double _step = getStepInDouble() != 0.0 ? getStepInDouble() : getStep();
		if (_step > 0)
			renderer.render("step", _step);
		double _strokeWidth = getStrokeWidth();
		if (_strokeWidth != 10.0)
			renderer.render("strokeWidth", _strokeWidth);
		double _angleArc = getAngleArc();
		if (_angleArc != 360.0)
			renderer.render("angleArc", _angleArc);
		double _scaleInput = getScaleInput();
		if (_scaleInput != 1.0)
			renderer.render("scaleInput", _scaleInput);
		String _name = getName();
		if (_name != null)
			renderer.render("name", _name);
	}

	/**
	 * Builds instances of type {@link ISlider ISlider}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableISlider.Builder {
	}

	/**
	 * Builds an updater of type {@link ISlider} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ISliderUpdater {}

	/**
	 * Specifies the orient of {@link ISlider} component
	 */
	enum Orient {
		/**
		 * The horizontal orient.
		 */
		HORIZONTAL("horizontal"),

		/**
		 * The vertical orient.
		 */
		VERTICAL("vertical");
		final String value;

		Orient(String value) {
			this.value = value;
		}
	}
}