/* null.java

	Purpose:
		
	Description:
		
	History:
		3:13 PM 2021/9/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyrex.benchmark;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import org.zkoss.zephyrex.bind.ClientBindComposer;

import java.io.IOException;
import java.util.Set;

/**
 * An extra field serializer to add extra fields on air.
 * @author jumperchen
 */
/*package*/ class ExtraFieldSerializer  extends BeanSerializerBase {
	ClientBindComposer owner;

	ExtraFieldSerializer(ExtraFieldSerializer source,
			ObjectIdWriter objectIdWriter) {
		super(source, objectIdWriter);
	}

	ExtraFieldSerializer(ExtraFieldSerializer source, Set<String> toIgnore,
			Set<String> toInclude) {
		super(source, toIgnore, toInclude);
	}

	ExtraFieldSerializer(ExtraFieldSerializer src,
			ObjectIdWriter objectIdWriter, Object filterId) {
		super(src, objectIdWriter, filterId);
	}

	public BeanSerializerBase withObjectIdWriter(
			ObjectIdWriter objectIdWriter) {
		return new ExtraFieldSerializer(this, objectIdWriter);
	}

	protected BeanSerializerBase withByNameInclusion(Set<String> toIgnore,
			Set<String> toInclude) {
		return new ExtraFieldSerializer(this, toIgnore, toInclude);
	}

	protected BeanSerializerBase asArraySerializer() {
		return new ExtraFieldSerializer(this);
	}

	public BeanSerializerBase withFilterId(Object filterId) {
		return new ExtraFieldSerializer(this, _objectIdWriter, filterId);
	}

	protected BeanSerializerBase withProperties(
			BeanPropertyWriter[] properties,
			BeanPropertyWriter[] filteredProperties) {
		return null;
	}

	ExtraFieldSerializer(BeanSerializerBase source) {
		super(source);
	}

	public void serialize(Object bean, JsonGenerator jgen,
			SerializerProvider provider)
			throws IOException, JsonGenerationException {
		jgen.writeStartObject();
		serializeFields(bean, jgen, provider);
		int i = System.identityHashCode(bean);
		String encoded = Integer.toUnsignedString(i, 36);
//		owner.idMappings.put(encoded, bean);
		//ClientBindComposer.BEAN_UID
		jgen.writeStringField("$uid", encoded);
		jgen.writeEndObject();
	}
}
