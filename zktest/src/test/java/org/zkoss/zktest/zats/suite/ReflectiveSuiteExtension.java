/* ReflectiveSuiteExtension.java

	Purpose:
		
	Description:
		
	History:
		12:05 PM 2022/8/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.suite;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A customized JUnit 5 extension that uses reflection to get the test execution list at runtime.
 * The annotated class must have a static {@code suite()} method that returns {@code Class<?>[]}.
 *
 * <pre><code>
 * {@literal @}ExtendWith(ReflectiveSuiteExtension.class)
 * public class MySuite {
 *     public static Class<?>[] suite() {
 *
 *     }
 *
 *    {@literal @}Test
 *     public void dummy() {}
 * }
 * </code></pre>
 * @author jumperchen
 */
public class ReflectiveSuiteExtension implements TestInstancePostProcessor {
	private final static Logger logger = LoggerFactory.getLogger(ReflectiveSuiteExtension.class);

	SummaryGeneratingListener listener = new SummaryGeneratingListener();
	public void postProcessTestInstance(Object testInstance,
			ExtensionContext context) throws Exception {
		Class setupClass = context.getRequiredTestClass();
		final Method suiteMethod = setupClass.getDeclaredMethod("suite");
		Class<?>[] testcases = (Class<?>[]) suiteMethod.invoke(testInstance);
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(Arrays.stream(testcases).map(DiscoverySelectors::selectClass).collect(
						Collectors.toList()))
				.build();
		Launcher launcher = LauncherFactory.create();
		launcher.registerTestExecutionListeners(listener, LoggingListener.forBiConsumer((t, messageSupplier) -> {
			logger.info(messageSupplier.get(), t);
		}));
		TestPlan testPlan = launcher.discover(request);
		launcher.execute(request);
		TestExecutionSummary summary = listener.getSummary();
		summary.printTo(new PrintWriter(System.out));
	}
}