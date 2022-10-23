/* ExtraKeyValueSerializer.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 8 15:22:13 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyrex.benchmark;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import org.zkoss.zephyrex.bind.ClientBindComposer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * An extra map serializer to add extra key-value on air.
 * @author jameschu
 */
/*package*/ class ExtraMapEntrySerializer extends MapSerializer {
	ClientBindComposer owner;

	ExtraMapEntrySerializer(MapSerializer source) {
		super(source, null, null, null, null, null);
	}

	protected ExtraMapEntrySerializer(MapSerializer extraKeyValueSerializer, TypeSerializer vts, Object suppressableValue, boolean suppressNulls) {
		super(extraKeyValueSerializer, vts, suppressableValue, suppressNulls);
		if (extraKeyValueSerializer instanceof ExtraMapEntrySerializer)
			this.owner = ((ExtraMapEntrySerializer) extraKeyValueSerializer).owner;
	}

	protected ExtraMapEntrySerializer(MapSerializer extraKeyValueSerializer, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, Set<String> included) {
		super(extraKeyValueSerializer, property, keySerializer, valueSerializer, ignored, included);
		if (extraKeyValueSerializer instanceof ExtraMapEntrySerializer)
			this.owner = ((ExtraMapEntrySerializer) extraKeyValueSerializer).owner;
	}

	protected ExtraMapEntrySerializer(MapSerializer ser, Object filterId, boolean sortKeys) {
		super(ser, filterId, sortKeys);
		if (ser instanceof ExtraMapEntrySerializer)
			this.owner = ((ExtraMapEntrySerializer) ser).owner;
	}

	public MapSerializer _withValueTypeSerializer(TypeSerializer vts) {
		if (this._valueTypeSerializer == vts) {
			return this;
		} else {
			return new ExtraMapEntrySerializer(this, vts, this._suppressableValue, this._suppressNulls);
		}
	}

	public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, Set<String> included, boolean sortKeys) {
		ExtraMapEntrySerializer ser = new ExtraMapEntrySerializer(this, property, keySerializer, valueSerializer, ignored, included);
		if (sortKeys != ser._sortKeys) {
			ser = new ExtraMapEntrySerializer(ser, this._filterId, sortKeys);
		}
		return ser;
	}

	public MapSerializer withFilterId(Object filterId) {
		if (this._filterId == filterId) {
			return this;
		} else {
			return new ExtraMapEntrySerializer(this, filterId, this._sortKeys);
		}
	}

	public MapSerializer withContentInclusion(Object suppressableValue, boolean suppressNulls) {
		if (suppressableValue == this._suppressableValue && suppressNulls == this._suppressNulls) {
			return this;
		} else {
			return new ExtraMapEntrySerializer(this, this._valueTypeSerializer, suppressableValue, suppressNulls);
		}
	}

	@Override
	public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject(value);
		this.serializeWithoutTypeInfo(value, gen, provider);
		int i = System.identityHashCode(value);
		String encoded = Integer.toUnsignedString(i, 36);
//		owner.idMappings.put(encoded, value);
		gen.writeStringField("$uid", encoded);
		gen.writeEndObject();
	}
}
