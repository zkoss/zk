/* UpdaterFragment.java

	Purpose:

	Description:

	History:
		3:43 PM 2022/1/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.processor;

import org.immutables.generator.Templates;
import org.immutables.value.processor.meta.ValueAttribute;
import org.immutables.value.processor.meta.ValueType;

/**
 * Code generates for Immutable Updater
 * @author jumperchen
 */
public class UpdaterFragment extends org.immutables.generator.Templates.Fragment {
	/**
	 * @param arity number of fragment parameters
	 */
	protected UpdaterFragment(int arity) {
		super(arity);
	}

	public void run(Templates.Invokation invokation) {
		ValueType type = (ValueType) invokation.param(0);
		writePackage(invokation, type);
		writeImports(invokation);
		writeClassJavaDoc(invokation, type);
		writeClass(invokation, type);
		invokation.ln().dl();
	}

	private void writePackage(Templates.Invokation __, ValueType type) {
		__.out("// **************************************************************************").ln();
		__.out("// Code Generator: DO NOT modify the content directly").ln();
		__.out("// **************************************************************************")
				.ln().ln();
		__.out("package ", type.$$package(), ';').ln().ln();
	}
	private void writeImports(Templates.Invokation __) {
		boolean isStatelessEx = ((ValueType) __.param(0)).$$package().contains("statelessex");
		__.out("import java.util.LinkedHashMap;").ln();
		__.out("import java.util.LinkedHashSet;").ln();
		__.out("import java.util.Map;").ln();
		__.out("import java.util.Set;").ln();
		__.out("import org.zkoss.lang.reflect.Fields;").ln();
		__.out("import org.zkoss.stateless.ui.SmartUpdater;").ln();
		if (isStatelessEx)
			__.out("import org.zkoss.stateless.sul.IComponent;").ln();
		__.out("import org.zkoss.zk.ui.UiException;").ln().ln();
	}
	private void writeClassJavaDoc(Templates.Invokation __, ValueType type) {
		__.out("/**").ln();
		__.out(" * A smart updater for {@link ", type.name(), "}.").ln();
		__.out(" */").ln();
	}
	private void writeClass(Templates.Invokation __, ValueType type) {
		__.out("/*package*/ class ", type.name(), "Updater implements SmartUpdater {").ln();
		writeClassMembers(__, type);
		writeMethods(__, type);
		__.out("}").ln().dl();
	}
	private void writeClassMembers(Templates.Invokation __, ValueType type) {
		__.out("\tprivate final ", type.name(), ".Builder builder = new ", type.name(),
				".Builder();").ln();
		__.out("\tprivate final Set<String> keys = new LinkedHashSet<>();").ln().ln();
	}
	private void writeMethods(Templates.Invokation __, ValueType type) {
		for (ValueAttribute attribute : type.attributes) {
			if (!attribute.isGenerateDerived && !attribute.isGenerateLazy) {

				boolean hasFlag = false;
				// ignores IComponent, ActionHandler, and any ZK package.
				// also ignores List<I> for children, but the detection here is weak.
				// we use "<" as a signal to detect it, which is not accurate
				if (attribute.getImplementedInterfacesNames()
						// ignore  org.zkoss.json.JSONAware
						.stream().filter(in -> in.startsWith("org.zkoss.") && !in.startsWith("org.zkoss.json")).findAny().isPresent()
				|| (hasFlag = attribute.getType().contains("<") || attribute.getType().length() == 1 /*ignore generic type*/)) {
					if (!hasFlag) {
						// ignore Image/Audio for withContent()
						if (!attribute.getImplementedInterfacesNames().stream().filter(in -> in.startsWith("org.zkoss.util.media.Media")).findAny().isPresent()) {
							continue; // ignore;
						}
					} else {
						continue; // ignore;
					}
				}
				String name = attribute.name();
				String setter = String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
				__.out("\t/**").ln();
				__.out("\t * Sets the given value of {@code ", name, "} to update to client widget").ln();
				__.out("\t *").ln();
				__.out("\t * @param ", name , " The new value for {@code ", name, "}").ln();
				__.out("\t * @see ", type.name() , "#with", setter, "(", attribute.getElementType() , ")").ln();
				__.out("\t */").ln();
				__.out("\tpublic ", type.name(), ".Updater ", name, '(');
				__.out(attribute.getElementType(), ' ', name, ") {").ln();
				__.out("\t\tkeys.add(\"", name, "\");").ln();
				__.out("\t\tbuilder.set", setter, "(", name, ");").ln();
				__.out("\t\treturn (", type.name(), ".Updater) this;").ln();
				__.out("\t}").ln().ln();
			}
		}
		__.out("\t@Override").ln();
		__.out("\tpublic Map<String, Object> marshal() {").ln();
		__.out("\t\tIComponent icmp = builder.build();").ln();
		__.out("\t\tMap<String, Object> map = new LinkedHashMap<>(keys.size());").ln();
		__.out("\t\ttry {").ln();
		__.out("\t\t\tfor (String key : keys) {").ln();
		__.out("\t\t\t\tmap.put(key, Fields.get(icmp, key));").ln();
		__.out("\t\t\t}").ln();
		__.out("\t\t} catch (NoSuchMethodException e) {").ln();
		__.out("\t\t\t throw UiException.Aide.wrap(e);").ln();
		__.out("\t\t}").ln();
		__.out("\t\treturn map;").ln();
		__.out("\t}").ln();
	}
}