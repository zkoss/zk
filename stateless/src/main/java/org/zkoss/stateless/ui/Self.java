/* Self.java

	Purpose:

	Description:

	History:
		12:24 PM 2021/10/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.zkoss.stateless.ui.util.ObjectMappers;
import org.zkoss.stateless.ui.util.VolatileComponent;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IComponentCtrl;
import org.zkoss.util.Maps;
import org.zkoss.stateless.function.CheckedConsumer2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * A target to locate a location at client for {@link IComponent}
 * @author jumperchen
 */
public final class Self implements Locator {
	private final String _locator;
	private Component _component;
	private Self(String selector) {
		this._locator = selector;
	}
	/*package*/ static Self of(String uuid) {
		return new Self("#" + uuid);
	}
	/*package*/ static Self ofId(String id) {
		return new Self("$" + id);
	}

	public Component toComponent() {
		return toComponent(null);
	}

	public Component toComponent(CheckedConsumer2<Event, Scope> eventHandlers) {
		if (_component == null || eventHandlers != null) {
			_component = new VolatileComponent() {
				public Desktop getDesktop() {
					return Executions.getCurrent().getDesktop();
				}

				public Page getPage() {
					return ((ExecutionCtrl) Executions.getCurrent()).getCurrentPage();
				}

				public String getUuid() {
					return toExternalForm();
				}

				public int hashCode() {
					return Self.this.hashCode();
				}

				public boolean equals(Object o) {
					if (this == o)
						return true;
					if (o == null || getClass() != o.getClass())
						return false;
					VolatileComponent self = (VolatileComponent) o;
					return Objects.equals(self.getUuid(), getUuid());
				}

				public void service(Event event, Scope scope) throws Exception {
					if (eventHandlers != null) {
						try {
							eventHandlers.accept(event, scope);
						} catch (Throwable t) {
							throw UiException.Aide.wrap(t);
						}
					}
					String uuidIfAny = toUuidIfAny();
					if (uuidIfAny != null) {
						Component byUuidIfAny = getDesktop().getComponentByUuidIfAny(
								uuidIfAny);
						if (byUuidIfAny instanceof ComponentCtrl) {
							((ComponentCtrl) byUuidIfAny).service(toStubEvent(event), scope);
						}
					} else {
						String idIfAny = toIdIfAny();
						Component fellowIfAny = getPage().getFellowIfAny(
								idIfAny);
						if (fellowIfAny instanceof ComponentCtrl) {
							((ComponentCtrl) fellowIfAny).service(toStubEvent(event), scope);
						}
					}
				}

				private StubEvent toStubEvent(Event event) {
					if (event instanceof StubEvent) {
						return (StubEvent) event;
					} else {
						Object data = Optional.ofNullable(event.getData()).orElse(
								Collections.EMPTY_MAP);
						Map eventData = data instanceof Map ?
								(Map) data : (IComponentCtrl.isPojo(data.getClass()) ?
								ObjectMappers.convert(data, Map.class) :
								Maps.of("", data));
						return new StubEvent(event.getName(), event.getTarget(),
								event.getName(), toUuidIfAny(), toIdIfAny(), eventData);
					}
				}
			};
		}
		return _component;
	}

	public String toExternalForm() {
		return _locator;
	}

	public String toUuidIfAny() {
		if (_locator.startsWith("#")) {
			return _locator.substring(1).split(" ")[0];
		}
		return null;
	}

	public String toIdIfAny() {
		if (_locator.startsWith("$")) {
			return _locator.substring(1).split(" ")[0];
		}
		return null;
	}

	public Locator find(Class<? extends IComponent> selector) {
		return new Self(_locator + " @" + getWidgetName(selector.getSimpleName()));
	}

	public Locator findChild(Class<? extends IComponent> selector) {
		return new Self(_locator + " > @" + getWidgetName(selector.getSimpleName()));
	}

	private static String getWidgetName(String name) {
		if (name.startsWith("I")) {
			return name.toLowerCase().substring(1);
		}
		return name.toLowerCase();
	}

	public Locator child(int nth) {
		if (nth < 0) {
			throw new WrongValueException("non-negative only");
		}
		return new Self(_locator + ":nth-child(" + (nth  + 1)+ ")");// 1-based on CSS
	}

	public Locator previousSibling() {
		return new Self(_locator + " .previousSibling");
	}

	public Locator nextSibling() {
		return new Self(_locator + " .nextSibling");
	}

	public Locator firstChild() {
		return new Self(_locator + " .firstChild");
	}

	public Locator lastChild() {
		return new Self(_locator + " .lastChild");
	}

	public Locator closest(Class<? extends IComponent> selector) {
		return new Self(_locator + " < @" + getWidgetName(selector.getSimpleName()));
	}

	public String toString() {
		return "Self{" + "locator='" + _locator + '\'' + '}';
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Self self = (Self) o;
		return Objects.equals(_locator, self._locator);
	}

	public int hashCode() {
		return _locator.hashCode();
	}
}