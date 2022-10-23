/* ExtraListValueSerializer.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 9 11:42:16 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyrex.benchmark;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import org.zkoss.zephyrex.bind.ClientBindComposer;

import java.io.IOException;
import java.util.Collection;

/**
 * An extra list serializer to add extra value on air.
 * @author jameschu
 */
/*package*/ class ExtraCollectionValueSerializer extends CollectionSerializer {
	ClientBindComposer owner;

	public ExtraCollectionValueSerializer(CollectionSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
		super(src, property, vts, valueSerializer, unwrapSingle);
		if (src instanceof ExtraCollectionValueSerializer)
			this.owner = ((ExtraCollectionValueSerializer) src).owner;
	}

	ExtraCollectionValueSerializer (CollectionSerializer serializer) {
		super(serializer, null, null, null, false);
	}

	public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
		return new ExtraCollectionValueSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
	}

	public CollectionSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
		return new ExtraCollectionValueSerializer(this, property, vts, elementSerializer, unwrapSingle);
	}

	@Override
	public void serializeContents(Collection<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
		super.serializeContents(value, g, provider);
		int i = System.identityHashCode(value);
		String encoded = Integer.toUnsignedString(i, 36);
//		owner.idMappings.put(encoded, value);
		g.writeStartObject();
		g.writeStringField("$uid", encoded);
		g.writeEndObject();
	}
}
