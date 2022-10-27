/* Processor.java

	Purpose:
		
	Description:
		
	History:
		1:36 PM 2022/1/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.processor;

import static org.immutables.generator.Intrinsics.$;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.immutables.generator.AbstractGenerator;
import org.immutables.generator.ForwardingFiler;
import org.immutables.generator.ForwardingProcessingEnvironment;
import org.immutables.generator.InvokationHelper;
import org.immutables.generator.Output;
import org.immutables.generator.Templates;
import org.immutables.value.processor.meta.CustomImmutableAnnotations;
import org.immutables.value.processor.meta.ImmutableMirror;
import org.immutables.value.processor.meta.ImmutableRound;
import org.immutables.value.processor.meta.Proto;
import org.immutables.value.processor.meta.Round;
import org.immutables.value.processor.meta.UnshadeGuava;
import org.immutables.value.processor.meta.ValueType;

/**
 * Code generator for Immutable Updater
 * @author jumperchen
 */
@SupportedAnnotationTypes({
		ImmutableMirror.QUALIFIED_NAME,
})
public final class Processor extends AbstractGenerator {
	@Override
	protected void process() {
		prepareOptions();

		Round round = ImmutableRound.builder()
				.addAllAnnotations(annotations())
				.processing(processing())
				.addAllCustomImmutableAnnotations(CustomImmutableAnnotations.annotations())
				.round(round())
				.build();

		Multimap<Proto.DeclaringPackage, ValueType> values = round.collectValues();


		for (Map.Entry<Proto.DeclaringPackage, ValueType> me : values.entries()) {
			Templates.Invokation initial = InvokationHelper.newInstance();
			Proto.DeclaringPackage key = me.getKey();
			ValueType type = me.getValue();
			new Output().java.invoke(initial, type.$$package(),
					type.name() + "Updater", type.element,
					new org.immutables.generator.Templates.Fragment(0) {
						@Override
						public void run(
								org.immutables.generator.Templates.Invokation out) {
							out.dl();
							out.ln();
							$(out, new UpdaterFragment(2), type, true);
							out.ln();
							out.dl();
						}
					});
		}
	}

	private void prepareOptions() {
		UnshadeGuava.overridePrefix(processing().getOptions().get(GUAVA_PREFIX));
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return FluentIterable.from(super.getSupportedAnnotationTypes())
				.append(CustomImmutableAnnotations.annotations())
				.toSet();
	}

	private static final String GRADLE_INCREMENTAL = "immutables.gradle.incremental";
	private static final String GUAVA_PREFIX = "immutables.guava.prefix";

	@Override
	public Set<String> getSupportedOptions() {
		ImmutableSet.Builder<String> options = ImmutableSet.builder();
		options.add(GRADLE_INCREMENTAL);
		if (processingEnv.getOptions().containsKey(GRADLE_INCREMENTAL)) {
			options.add("org.gradle.annotation.processing.isolating");
		}
		options.add(GUAVA_PREFIX);
		return options.build();
	}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(new RestrictingIncrementalProcessingEnvironment(processingEnv));
	}

	private final class RestrictingIncrementalProcessingEnvironment extends
			ForwardingProcessingEnvironment {
		private final ProcessingEnvironment processingEnv;
		boolean incrementalRestrictions;
		private Filer restrictedFiler;

		private RestrictingIncrementalProcessingEnvironment(ProcessingEnvironment processingEnv) {
			this.processingEnv = processingEnv;
			this.incrementalRestrictions = processingEnv.getOptions().containsKey(GRADLE_INCREMENTAL);
		}

		@Override
		protected ProcessingEnvironment delegate() {
			return processingEnv;
		}

		@Override
		public Filer getFiler() {
			final Filer filer = super.getFiler();
			if (incrementalRestrictions) {
				if (restrictedFiler == null) {
					restrictedFiler = new ForwardingFiler() {
						@Override
						protected Filer delegate() {
							return filer;
						}

						@Override
						public FileObject createResource(
								JavaFileManager.Location location,
								CharSequence pkg,
								CharSequence relativeName,
								Element... originatingElements)
								throws IOException {
							String message = String.format("Suppressed writing of resource %s/%s/%s (triggered by enabling -A%s)",
									location,
									pkg,
									relativeName,
									GRADLE_INCREMENTAL);
							getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
							throw new FileNotFoundException(message);
						}
					};
				}
				return restrictedFiler;
			}
			return filer;
		}
	}
}