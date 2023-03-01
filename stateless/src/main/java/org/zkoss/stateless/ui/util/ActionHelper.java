/* ActionHelper.java

	Purpose:
		
	Description:
		
	History:
		2:57 PM 2022/1/5, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IComponentCtrl;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.IStubComponent;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.impl.Reflections;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.JsContentRenderer;

/**
 * A helper class for {@link Action}
 *
 * @author jumperchen
 */
public class ActionHelper {

	private static final String JS_FUNCTION = "(zk.ausending?zk.afterMount:function(f){f();})(function(){var q=\"%s\",wgs=(q.charAt(0)==\"$\")?[zk.$(q)]:jq(q).map(function(){return zk.$(this)}).get(),args={%s};if(wgs.length){for(var i=0,j=wgs.length;i<j;i++){var w=wgs[i];for(var m in args){if (w) w.set(m,args[m])}}}});";

	/**
	 * Wires the method action handlers from the given {@code richlet}
	 * into each matched client widget.
	 *
	 * @param richlet The lookup richlet
	 * @param iStubComponent The root component(s) that's built by {@code richlet}'s
	 * {@link RichletMapping}.
	 */
	public static void wireAction(StatelessRichlet richlet,
			IStubComponent iStubComponent) {
		Reflections.forMethods(richlet.getClass(), Action.class,
				(clazz, method, annotation) -> {
					if (Strings.isBlank(annotation.from())) {
						return;
					}
					try {
						ActionHandler handler = new SimpleActionHandler(richlet,
								method);
						JsContentRenderer outer = new JsContentRenderer();
						IComponentCtrl.renderActions(Arrays.asList(handler),
								new StatelessContentRenderer(outer,
										iStubComponent.getOwner(),
										iStubComponent));
						Executions.getCurrent().addAuResponse(new AuScript(
								String.format(JS_FUNCTION, Normalizer.normalize(
												method.getAnnotation(Action.class)
														.from()),
										outer.getBuffer().toString())));
					} catch (IOException e) {
						throw UiException.Aide.wrap(e);
					}
				});
	}

	/**
	 * Wires the method action handlers from the given {@code composer} into each
	 * matched component, which is found by finding with {@link Action#from()}.
	 *
	 * @param composer The lookup composer
	 * @param iStubComponent The root component that's built by {@code composer}.
	 */
	public static void wireAction(StatelessComposer composer,
			IStubComponent iStubComponent) {
		Reflections.forMethods(composer.getClass(), Action.class,
				(clazz, method, annotation) -> {
					if (Strings.isBlank(annotation.from())) {
						return;
					}
					try {
						ActionHandler handler = new SimpleActionHandler(
								composer, method);
						JsContentRenderer outer = new JsContentRenderer();
						IComponentCtrl.renderActions(Arrays.asList(handler),
								new StatelessContentRenderer(outer,
										iStubComponent.getOwner(),
										// add all actions into each matched component by Selectors.
										new AggregatedIStubComponent(
												iStubComponent, Selectors.find(
												((ExecutionCtrl) Executions.getCurrent()).getCurrentPage(),
												annotation.from()))));
						Executions.getCurrent().addAuResponse(new AuScript(
								String.format(JS_FUNCTION, Normalizer.normalize(
												method.getAnnotation(Action.class)
														.from()),
										outer.getBuffer().toString())));
					} catch (IOException e) {
						throw UiException.Aide.wrap(e);
					}
				});

	}

	private static class AggregatedIStubComponent extends IStubComponent {
		final private IStubComponent delegator;
		final private List<Component> allQueriedComps;

		AggregatedIStubComponent(IStubComponent delegator,
				List<Component> allQueriedComps) {
			this.delegator = delegator;
			this.allQueriedComps = allQueriedComps;
		}

		public IComponent getOwner() {
			return delegator.getOwner();
		}

		//
		public ComponentDefinition getDefinition() {
			return delegator.getDefinition();
		}

		public void addAction(String name, ActionHandler handler) {
			final Page currentPage = ((ExecutionCtrl) Executions.getCurrent()).getCurrentPage();
			allQueriedComps.stream()
					.filter(component -> component instanceof IStubComponent)
					.map(component -> (IStubComponent) component).forEach(
							iStubComponent -> {
								// if the page is VolatileIPage, it means the page will be
								// removed after all components are created, so we
								// need to add the action to that page and merge to
								// the real page. Otherwise, all the actions are disappeared.
								if (currentPage instanceof VolatileIPage) {
									((VolatileIPage) currentPage).addAction(
											iStubComponent.getOwner(), name,
											handler);
								} else {
									iStubComponent.addAction(name, handler);
								}
							});
		}
	}
}
