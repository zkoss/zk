/* IRadiogroup.java

	Purpose:

	Description:

	History:
		Fri Dec 10 01:01:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Radiogroup;

/**
 * Immutable {@link Radiogroup} component
 *
 * <p> A radio group.
 *
 * <p><b>Note:</b> To support the versatile layout, a radio group accepts any
 * kind of children, including {@link IRadio}. On the other hand, the parent of
 * a radio, if any, must be a radio group.
 * </p>
 *
 * <h2>Example</h2>
 * <img src="doc-files/IRadiogroup_example.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IWindow.ofTitle("Radiobox {@literal &} Radio Demo").withWidth("400px")
 *         .withBorder("normal").withChildren(
 *             IVlayout.of(IRadiogroup.of(
 *                 IRadio.of("Apple"),
 *                 IRadio.of("Orange"),
 *                 IRadio.of("Banana")
 *             ).withAction(this::doRadioCheck),
 *             ILabel.of(" You have selected :"),
 *             ILabel.ofId("fruit").withStyle("color:red")
 *         )
 *     );
 * }
 * {@literal @}{@code Action}(type = Events.ON_CHECK)
 * public void doRadioCheck(UiAgent uiAgent, {@literal @}{@code ActionVariable}(targetId = ActionTarget.SELF,
 *     field = "selectedItem.label") String label) {
 *         uiAgent.smartUpdate(Locator.ofId("fruit"), new ILabel.Updater().value(label));
 * }
 * </code></pre>
 * <p><b>Note:</b> To support the versatile layout, a radio group accepts any kind
 * of children ,including Radio. On the other hand, the parent of a radio,
 * if any, must be a radio group.</p>
 *
 * <h4>Radiogroup as an Ancestor of Radio</h4>
 * <p>Groups radio components into the same radiogroup if they share the same ancestor,
 * not just direct parent. It allows a more sophisticated layout. For example,</p>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example/ancestor")
 * public IComponent ancestorExample() {
 *     return IRadiogroup.of(
 *         IVlayout.of(
 *             IHlayout.of(
 *                 IRadio.of("radio 1"),
 *                 IRadio.of("radio 2"),
 *                 IRadio.of("radio 3")
 *             ), IHlayout.of(
 *                 IRadio.of("radio 4"),
 *                 IRadio.of("radio 5"),
 *                 IRadio.of("radio 6")
 *             )
 *         )
 *     );
 * }
 * </code></pre>
 *
 * <h4>A Row of a Grid as a Radio Group</h4>
 * <p>Sometimes it is not possible to make the radiogroup component as an ancestor
 * of all radio components. For example, each row of a grid might be an independent
 * group. To solve this, you have to assign the radiogroup component to the radio
 * component explicitly by the use of {@link IRadio#withRadiogroup(String)}.</p>
 * <img src="doc-files/IRadiogroup_example_grid.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example/grid")
 * public IComponent gridExample() {
 *     return IDiv.of(
 *         IRadiogroup.ofId("popular"),
 *         IRadiogroup.ofId("fun"),
 *         IGrid.of(
 *             IRow.of(
 *                 ILabel.of("Most popular"),
 *                 IRadio.of("Java").withRadiogroup("popular"),
 *                 IRadio.of("Groovy").withRadiogroup("popular"),
 *                 IRadio.of("C#").withRadiogroup("popular"),
 *                 ITextbox.DEFAULT
 *             ),
 *             IRow.of(
 *                 ILabel.of("Most fun"),
 *                 IRadio.of("Open Source").withRadiogroup("fun"),
 *                 IRadio.of("Social Networking").withRadiogroup("fun"),
 *                 IRadio.of("Searching").withRadiogroup("fun"),
 *                 ITextbox.DEFAULT
 *             )
 *         )
 *     );
 * }
 * </code></pre>
 * @author katherine
 * @see Radiogroup
 */
@StatelessStyle
public interface IRadiogroup<I extends IAnyGroup> extends IXulElement<IRadiogroup<I>>,
		IChildable<IRadiogroup<I>, I>, IAnyGroup<IRadiogroup<I>>, IDisable<IRadiogroup<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IRadiogroup<IAnyGroup> DEFAULT = new IRadiogroup.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Radiogroup> getZKType() {
		return Radiogroup.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Radiogroup"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Radiogroup";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkOrient() {
		IComponentChecker.checkOrient(getOrient());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IRadiogroup<I> checkRadioDisabled() {
		if (isDisabled()) {
			List<I> oldChildren = getChildren();
			List<I> newChildren = checkChildrenDisabled((List) oldChildren);
			if (!Objects.equals(newChildren, oldChildren)) {
				return withChildren(newChildren);
			}
		}
		return this;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IComponent> checkChildrenDisabled(List<IComponent> children) {
		List<IComponent> newChildren = new ArrayList<>(children);
		for (int i = 0, j = newChildren.size(); i < j; i++) {
			IComponent iAnyGroup = children.get(i);
			if (iAnyGroup instanceof IChildable) {
				newChildren.set(i, (IComponent) ((IChildable) iAnyGroup).withChildren(
						checkChildrenDisabled(
								((IChildable) iAnyGroup).getChildren())));
			} else if (iAnyGroup instanceof IRadio) {
				newChildren.set(i, ((IRadio) iAnyGroup).withDisabled(true));
			}
		}
		return newChildren;
	}

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
	IRadiogroup<I> withOrient(String orient);

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
	default IRadiogroup<I> withOrient(Orient orient) {
		Objects.requireNonNull(orient);
		return withOrient(orient.value);
	}

	/** Returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link IRadio#getName}).
	 * <p>Default: automatically generated a unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	default String getName() {
		return Strings.encode(new StringBuffer(16).append("_pg"), System.identityHashCode(this)).toString();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code name}.
	 *
	 * <p> Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link IRadio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name The unique name to identify this component
	 * <p>Default: no null.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRadiogroup withName(String name);

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IRadiogroup<I> of(Iterable<? extends I> children) {
		return new IRadiogroup.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IRadiogroup<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IRadiogroup<I> ofId(String id) {
		return new IRadiogroup.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		String _name = getName();
		if (_name != null)
			render(renderer, "name", _name);
		String _orient = getOrient();
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (isDisabled()) {
			render(renderer, "disabled", true);
		}
	}

	/**
	 * Builds instances of type {@link IRadiogroup IRadiogroup}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIRadiogroup.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link IRadiogroup} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IRadiogroupUpdater {}

	/**
	 * Specifies the orient of {@link IRadiogroup} component
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