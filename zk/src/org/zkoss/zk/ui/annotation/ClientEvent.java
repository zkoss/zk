package org.zkoss.zk.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.event.Event;

/**
 * @author rudyhuang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ClientEvents.class)
public @interface ClientEvent {
	/**
	 * Event name. Must start with 'on' and use an uppercase on the first letter (e.g. {@code onClick}).
	 * For name containing hyphen (-), just use it (e.g. {@code my-event} should use {@code onMy-event}).
	 */
	String value();

	/**
	 * Event flags.
	 */
	int flags() default 0;

	/**
	 * Custom event class.
	 * The event class should contain one constructor which the additional arguments
	 * are annotated with {@link ClientEventParam}.
	 */
	Class<? extends Event> event() default Event.class;
}

