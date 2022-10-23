/* SimpleBenchmark.java

	Purpose:
		
	Description:
		
	History:
		12:46 PM 2021/10/4, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.benchmark;

import static org.zkoss.zephyr.action.ActionType.onClick;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;

/**
 * A simple benchmark for ZK vs Zephyr and Setter vs Immutable.
 * @author jumperchen
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SimpleBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(SimpleBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}

	private ZephyrTestBase setup;

	@Setup
	public void setUp() {
		setup = new ZephyrTestBase(false);
		setup.beforeEach();
	}

	@TearDown
	public void tearDown() {
		setup.afterEach();
	}

	@Benchmark
	public String timeOfCreationOfZKWithListener() {
		Label label = newLabel();
		label.addEventListener(Events.ON_CLICK, this::onClickListener);
		return setup.zul(() -> label);
	}

	private void onClickListener(Event event) {
	}

	@Benchmark
	public String timeOfCreationOfZK() {
		return setup.zul(SimpleBenchmark::newLabel);
	}

	@Benchmark
	public String timeOfSetterOfZK() {
		return setup.zul(() -> {
			Label label = new Label();
			label.setId("myId");
			label.setValue("myValue");
			label.setPre(true);
			label.setVisible(true);
			label.setMaxlength(50);
			return label;
		});
	}

	@Benchmark
	public String timeOfCreationOfZephyr() {
		return setup.composer(SimpleBenchmark::newLabel);
	}

	@Benchmark
	public String timeOfCreationOfImmutable() {
		return setup.richlet(() -> ILabel.of("Label"));
	}

	@Benchmark
	public String timeOfCreationOfImmutableWithAction() {
		return setup.richlet(() -> ILabel.of("Label").withAction(onClick(this::doClick)));
	}

	private void doClick() {}

	@Benchmark
	public String timeOfSetterOfImmutable() {
		return setup.richlet(
				() -> ILabel.of("Label").withId("myId").withValue("myValue")
						.withPre(true).withVisible(true).withMaxlength(50));
	}


	private static Label newLabel() {
		return new Label("Label");
	}
}