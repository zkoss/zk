/* UiAgentImpl.java

	Purpose:
		
	Description:
		
	History:
		2:34 PM 2021/10/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.zkoss.json.JSONValue;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.stateless.ui.util.VolatileComponent;
import org.zkoss.stateless.ui.util.VolatileIPage;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.function.CheckedConsumer;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.AuAppendChild;
import org.zkoss.zk.au.out.AuOuter;
import org.zkoss.zk.au.out.AuRemove;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.StubsComponent;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Implementation of {@link UiAgent} for {@link StatelessComposer} case.
 *
 * @author jumperchen
 */
/*package*/ class UiAgentImpl implements UiAgent {
	/*package*/ Execution _exec;

	private UiAgentImpl(Execution execution) {
		_exec = execution;
	}

	/*package*/
	static UiAgent of(Execution execution) {
		Objects.requireNonNull(execution);
		return new UiAgentImpl(execution);
	}

	public <I extends IComponent> UiAgent appendChild(Locator locator,
													  I newChild) {
		checkActivated();
		Objects.requireNonNull(newChild);
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);
			final IStubComponent newIComp = IStubComponent.of(newChild);

			_exec.addAuResponse(new AuAppendChild(locatorComp,
					Arrays.asList(IStubComponent.redraw(newIComp))));

			// after redraw
			doMergeStub(locatorComp, newIComp);
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public <I extends IComponent> UiAgent insertBefore(Locator locator, I newChild,
			int childIndex) {
		checkActivated();
		Objects.requireNonNull(newChild);
		if (childIndex < 0) {
			throw new IllegalArgumentException("Negative is not allowed");
		}
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);
			final IStubComponent newIComp = IStubComponent.of(newChild);

			_exec.addAuResponse(new AuResponse(
					"addBfrChd", locatorComp, toArray(locatorComp.getUuid(),
					String.valueOf(childIndex), IStubComponent.redraw(newIComp))));

			// after redraw
			doMergeStub(locatorComp, newIComp);
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public <I extends IComponent> UiAgent replaceChild(Locator locator, I newChild,
			int childIndex) {
		checkActivated();
		Objects.requireNonNull(newChild);
		if (childIndex < 0) {
			throw new IllegalArgumentException("Negative is not allowed");
		}
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);
			final IStubComponent newIComp = IStubComponent.of(newChild);

			_exec.addAuResponse(new AuResponse(
					"replaceChd", locatorComp, toArray(locatorComp.getUuid(),
						String.valueOf(childIndex), IStubComponent.redraw(newIComp))));

			// after redraw
			doMergeStub(locatorComp, newIComp);
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public <I extends IComponent> UiAgent replaceWith(Locator locator,
			I newComp) {
		checkActivated();
		Objects.requireNonNull(newComp);
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);
			final IStubComponent newIComp = IStubComponent.of(newComp);

			// clean up all smartUpdates
			((WebAppCtrl)_exec.getDesktop().getWebApp()).getUiEngine().clearSmartUpdate(locatorComp);

			_exec.addAuResponse(new AuOuter(locatorComp, IStubComponent.redraw(newIComp)));

			// after redraw
			doMergeStub(locatorComp, newIComp);
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public <I extends IComponent> UiAgent replaceChildren(Locator locator,
			I... children) {

		checkActivated();
		Objects.requireNonNull(children);
		replaceChildren(locator, Arrays.asList(children));
		return this;
	}

	public <I extends IComponent> UiAgent replaceChildren(Locator locator) {

		checkActivated();
		replaceChildren(locator, Collections.EMPTY_LIST);
		return this;
	}

	public <I extends IComponent> UiAgent replaceChildren(Locator locator,
			List<I> children) {
		checkActivated();
		Objects.requireNonNull(children);
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);

			_exec.addAuResponse(new AuResponse("rmChd", locatorComp, locatorComp.getUuid()));

			Collection<JavaScriptValue> output = redraw(locator, children);
			// empty means to remove all children
			if (!output.isEmpty()) {
				_exec.addAuResponse(new AuAppendChild(locatorComp,
						output.stream()
								.map(javaScriptValue -> javaScriptValue.toJSONString())
								.collect(Collectors.toList())));
			}
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	/*package*/ static <I extends IComponent> Collection<JavaScriptValue> redraw(Locator locator, Collection<I> children) {
		Objects.requireNonNull(children);
		try {
			final Execution execution = Executions.getCurrent();
			final VolatileComponent0 locatorComp = new VolatileComponent0(execution, locator);
			List<JavaScriptValue> output = new ArrayList<>(children.size());
			Component locatedComp = lookupComponent(locatorComp);

			// locatedComp is null in cloud mode
			Page page = locatedComp == null ? ((ExecutionCtrl) execution).getCurrentPage() : locatedComp.getPage();
			boolean isAsyncUpdate = execution.isAsyncUpdate(page);
			for (I i : children) {
				final IStubComponent newIComp = IStubComponent.of(i);

				// check newIComp whether has page if it's not in async update
				boolean shallReset = false;
				if (!isAsyncUpdate) {
					shallReset = true;
					VolatileIPage iPage = new VolatileIPage(page);
					iPage.preInit();
					newIComp.setPage(iPage);
				}
				try {
					output.add(new JavaScriptValue(IStubComponent.redraw(newIComp)));

					// after redraw
					doMergeStubDirectly(locatedComp, newIComp, !isAsyncUpdate);
				} finally {
					// reset
					if (!isAsyncUpdate && shallReset) {
						newIComp.setPage(null);
					}
				}

			}
			return output;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	private static void doMergeStub(Component locator, IStubComponent newIComp) {
		final Component locatedComp = lookupComponent(locator);
		doMergeStubDirectly(locatedComp, newIComp, false);
	}

	private static void doMergeStubDirectly(Component locatedComp, IStubComponent newIComp, boolean useVolatile) {
		if (locatedComp instanceof IStubsComponent) {
			( (IStubsComponent) locatedComp).mergeIComponent(newIComp);
		} else if (locatedComp instanceof StubsComponent) {
			// ZK EE version
			IStubsComponent iStubsComponent = new IStubsComponent();
			iStubsComponent.replace(locatedComp, true, true, false);
			iStubsComponent.mergeIComponent(newIComp);
		} else if (locatedComp instanceof IStubComponent) {
			IStubsComponent iStubsComponent = useVolatile ? new VolatileIStubsComponent() : new IStubsComponent();
			iStubsComponent.replace(locatedComp, true, true, false);
			iStubsComponent.mergeIComponent(newIComp);
		} else if (locatedComp != null) {
			if (isStubnly(locatedComp)) {
				IStubsComponent iStubsComponent = new IStubsComponent();
				iStubsComponent.replace(IStubComponent.of(locatedComp), true, true, false);
				iStubsComponent.mergeIComponent(newIComp);
			}
			throw new UnsupportedOperationException("No allowed non-IComponent target here [" + locatedComp + "]");
		} else {
			// null case
			UiEngine uiEngine = ((WebAppCtrl) Executions.getCurrent()
					.getDesktop().getWebApp()).getUiEngine();
			uiEngine.disableClientUpdate(newIComp, true);
			try {
				newIComp.setPage(((ExecutionCtrl) Executions.getCurrent()).getCurrentPage());
			} finally {
				uiEngine.disableClientUpdate(newIComp, false);
			}
		}
	}

	public UiAgent smartUpdate(Locator locator, SmartUpdater updater) {
		Map<String, Object> marshal = updater.marshal();
		if (!marshal.isEmpty()) {
			marshal.forEach(
					(key, value) -> UiAgentCtrl.smartUpdate(locator, key,
							value));
		}
		return this;
	}

	private static boolean isStubnly(Component component) {
		if (component == null) {
			return false;
		}
		String so = component.getStubonly();
		if ("inherit".equals(so)) {
			return isStubnly(component.getParent());
		}
		return "true".equals(so);
	}

	private static Component lookupComponent(Component cmp) {
		String uuid = cmp.getUuid();

		// trim selector parts
		uuid = uuid.split(" ")[0];

		Component target = null;
		if (uuid.startsWith("$")) {
			final Page page = cmp.getPage();
			final String id = uuid.substring(1);
			target = page.getFellowIfAny(id);
			if (target == null) {
				for (Component parent : page.getRoots()) {
					if ( (target = parent.getFellowIfAny(id)) != null) {
						break;
					}
				}
			}
		} else {
			if (uuid.startsWith("#")) {
				uuid = uuid.substring(1);
			}
			target = cmp.getDesktop()
					.getComponentByUuid(uuid);
		}
//		Objects.requireNonNull(target,
//				"Target component of the given locator is not found ["
//						+ (uuid.startsWith("$") ?
//						"id=" + uuid.substring(1) :
//						"uuid=" + uuid) + "]");
		return target;
	}

	public <I extends IComponent> UiAgent insertAdjacentComponent(Locator locator,
			Position position, I newChild) {
		checkActivated();
		Objects.requireNonNull(newChild);
		try {
			final VolatileComponent0 locatorComp = new VolatileComponent0(_exec, locator);
			final IStubComponent newIComp = IStubComponent.of(newChild);

			_exec.addAuResponse(new AuResponse("insertAdjacent", locatorComp,
					toArray(locatorComp.getUuid(), JSONValue.toJSONString(position.name()),
							IStubComponent.redraw(newIComp))));

			// after redraw
			doMergeStub(locatorComp, newIComp);
			return this;
		} catch (Throwable e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public UiAgent remove(Locator locator) {
		checkActivated();
		_exec.addAuResponse(new AuRemove(locator.toExternalForm()));
		return this;
	}

	void checkActivated() {
		if (!isActivated()) {
			throw new IllegalStateException("Not activated yet");
		}
	}

	public boolean isActivated() {
		return _exec != null && ((ExecutionCtrl) _exec).isActivated();
	}

	public CompletableFuture<UiAgent> ofAsync() {
		checkActivated();
		final Desktop desktop = _exec.getDesktop();
		desktop.enableServerPush(true);
		final CompletableFuture<UiAgent> t = CompletableFuture.<UiAgent>supplyAsync(() -> {
			try {
				Executions.activate(desktop);
				UiAgentImpl uiAgent = new UiAgentImpl(Executions.getCurrent());
				return uiAgent;
			} catch (InterruptedException e) {
				UiException.Aide.wrap(e);
			}
			return null;
		});
		t.whenComplete((uiAgent, throwable)-> {
			UiAgentCtrl.deactivate(uiAgent);
			Executions.deactivate(desktop);
		});
		return t;
	}

	public CompletableFuture<UiAgent> runAsync(
			CheckedConsumer<UiAgent> consumer) {
		return ofAsync().thenApply(uiAgent -> {
			try {
				consumer.accept(uiAgent);
			} catch (Throwable e) {
				UiException.Aide.wrap(e);
			}
			return uiAgent;
		});
	}

	public <I extends IComponent> UiAgent insertBeforeBegin(Locator locator,
			I newChild) {
		insertAdjacentComponent(locator, Position.beforeBegin, newChild);
		return this;
	}

	public <I extends IComponent> UiAgent insertBeforeEnd(Locator locator,
			I newChild) {
		insertAdjacentComponent(locator, Position.beforeEnd, newChild);
		return this;
	}

	public <I extends IComponent> UiAgent insertAfterBegin(Locator locator,
			I newChild) {
		insertAdjacentComponent(locator, Position.afterBegin, newChild);
		return this;
	}

	public <I extends IComponent> UiAgent insertAfterEnd(Locator locator,
			I newChild) {
		insertAdjacentComponent(locator, Position.afterEnd, newChild);
		return this;
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			Object value) {
		smartUpdate(locator, attr, value, false);
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			Object value, boolean append) {
		((WebAppCtrl) Executions.getCurrent().getDesktop()
				.getWebApp()).getUiEngine()
				.addSmartUpdate(locator.toComponent(), attr, value, append);
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			int value) {
		smartUpdate(locator, attr, Integer.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			long value) {
		smartUpdate(locator, attr, Long.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			byte value) {
		smartUpdate(locator, attr, Byte.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			char value) {
		smartUpdate(locator, attr, Character.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			boolean value) {
		smartUpdate(locator, attr, Boolean.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			float value) {
		smartUpdate(locator, attr, Float.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void smartUpdate(Locator locator, String attr,
			double value) {
		smartUpdate(locator, attr, Double.valueOf(value));
	}

	/*package*/ static <I extends IComponent> void response(String key, AuResponse response, int priority) {
		((WebAppCtrl) Executions.getCurrent().getDesktop()
				.getWebApp()).getUiEngine().addResponse(key, response, priority);
	}

	/*package*/ static <I extends IComponent> void response(String key, AuResponse response) {
		response(key, response, 0);
	}

	private static class VolatileComponent0 extends VolatileComponent {
		private final Desktop _desktop;
		private final Page _page;
		private final String _locator;

		public VolatileComponent0(Execution execution, Locator locator) {
			_desktop = execution.getDesktop();
			_page = ((ExecutionCtrl) execution).getCurrentPage();
			_locator = locator.toExternalForm();
		}

		public Desktop getDesktop() {
			return _desktop;
		}

		public Page getPage() {
			return _page;
		}

		public String getUuid() {
			return _locator;
		}
	}
	/*package*/ static Object[] toArray(String uuid, String... contents) {
		final List<Object> list = new LinkedList<Object>();
		list.add(uuid);
		stringToJS(list, contents);
		return list.toArray(new Object[list.size()]);
	}

	/** Converts the contents (a collection of strings) to an array of JavaScriptValue. */
	private static void stringToJS(Collection<Object> result, String... contents) {
		for (String content : contents)
			result.add(new JavaScriptValue(content));
	}
}
