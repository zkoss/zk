/* ObjectMappers.java

	Purpose:
		
	Description:
		
	History:
		4:59 PM 2021/10/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.zkoss.util.media.Media;

/**
 * Object Mappers utils.
 * @author jumperchen
 */
public class ObjectMappers {

	// a workaround for #convert() to ignore serializing Media type.
	private static class MediaSerializer extends JsonSerializer<Media> {

		public void serialize(Media value, JsonGenerator gen,
				SerializerProvider serializers) throws IOException {
			gen.writeEmbeddedObject(value);
		}

		public Class<Media> handledType() {
			return Media.class;
		}
	}

	public final static ObjectMapper SETTER_OBJECT_MAPPER = new ObjectMapper().configure(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(
			new SimpleModule().addSerializer(new MediaSerializer()));

	/**
	 * Converts any given data into the given type if possible.
	 * All unknown fields are ignored.
	 */
	public static <T> T convert(Object data, Class<? extends T> type) {
		return (T) SETTER_OBJECT_MAPPER.convertValue(data, TypeFactory.rawClass(type));
	}
}
