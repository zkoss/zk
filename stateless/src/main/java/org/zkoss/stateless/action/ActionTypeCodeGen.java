/* ActionTypeCodeGen.java

	Purpose:
		
	Description:
		
	History:
		3:10 PM 2021/10/6, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.zkoss.util.ArraysX;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.event.ZulEvents;

/**
 * Code generator for {@link ActionType}
 *
 * @author jumperchen
 */
/*package*/ class ActionTypeCodeGen {
	public static void main(String[] args)
			throws IOException, IllegalAccessException {
		final String tempalteHeader = IOUtils.toString(
				ActionTypeCodeGen.class.getResourceAsStream("/org/zkoss/stateless/action/action_type_header.template"));
		final String tempalte = IOUtils.toString(
				ActionTypeCodeGen.class.getResourceAsStream("/org/zkoss/stateless/action/action_type.template"));
		StringBuffer sb = new StringBuffer(1024 * 8);

		// Class Head
		sb.append(tempalteHeader);
		Field[] zkDeclaredFields = Events.class.getDeclaredFields();
		Field[] zulDeclaredFields = ZulEvents.class.getDeclaredFields();

		Field[] allFields = ArraysX.concat(zkDeclaredFields, zulDeclaredFields);
		Arrays.sort(allFields, Comparator.comparing(Field::getName));

		for (Field field : allFields) {
			if (field.getType() == String.class && Modifier.isPublic(
					field.getModifiers()) && Modifier.isStatic(
					field.getModifiers()) && Modifier.isFinal(
					field.getModifiers())) {
				String event = field.getName();
				//				System.out.println("Event:" + field.getName());
				String method = ((String) field.get(null));
				//				System.out.println("Method:" + method);
				String interface0 =
						String.valueOf(method.charAt(0)).toUpperCase()
								+ (method.substring(1));
				//				System.out.println("Interface:" + interface0);

				String result = tempalte.replaceAll("\\$event\\$", field.getDeclaringClass().getSimpleName() + "." + event)
						.replaceAll("\\$interface\\$", interface0)
						.replaceAll("\\$method\\$", method);

				sb.append("\n");
				sb.append(result);
			}
		}

		// Class footer
		sb.append("\n");
		sb.append("}");

		String root = ActionTypeCodeGen.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("build/classes/java/main/", "");
		System.out.println("Working Directory = " + root);
		final String file =
				root + MODULE_SRC + (ActionType.class.getCanonicalName().replace(".", "/")) + ".java";
		System.out.println("Full File Path = " + file);
		if (!new File(file).exists()) {
			throw new IOException("Wrong file path: " + file);
		}
		FileUtils.writeStringToFile(new File(file), sb.toString());
	}
	static final String MODULE_SRC = "src/main/java/";
}
