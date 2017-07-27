package org.zkoss.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.event.HistoryPopStateEvent;

/**
 * <p>Marker annotation to identify a HistoryPopState method in View Model. <br/>
 * Only one (could be zero) HistoryPopState method is allowed in a particular class.
 * A HistoryPopState method could also use with {@link ContextParam} to get the {@link HistoryPopStateEvent}. <br/>
 * </p>
 * <p>This annotation is for history state in ZK MVVM.
 * </p>
 * <p>Example:<br/>
 * <code>@HistoryPopState</code><br/>
 * <code>public void method(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event)</code>
 *
 * @author rudyhuang
 * @since 8.5.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryPopState {
}
