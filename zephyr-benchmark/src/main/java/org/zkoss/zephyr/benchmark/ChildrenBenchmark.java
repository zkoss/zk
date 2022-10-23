/* DivBenchmark.java

	Purpose:

	Description:

	History:
		Mon Oct 18 13:02:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.benchmark;

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
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ChildrenBenchmark {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(ChildrenBenchmark.class.getSimpleName())
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
	public String timeOfCreationOfZK() {
		return setup.zul(ChildrenBenchmark::newDiv);
	}

	@Benchmark
	public String timeOfCreationOfZephyr() {
		return setup.composer(ChildrenBenchmark::newDiv);
	}

	@Benchmark
	public String timeOfCreationOfImmutable() {
		List children = new ArrayList();
		children.add(ILabel.of("1"));
		List children2 = new ArrayList();
		children2.add(IDiv.of(children));
		return setup.richlet(() -> IDiv.of(children2));
	}

	private static Div newDiv() {
		Div div = new Div();
		Div div2 = new Div();
		div.appendChild(new Label("1"));
		div2.appendChild(div);
		return div2;
	}
}
