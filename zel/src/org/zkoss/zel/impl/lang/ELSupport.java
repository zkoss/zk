/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zkoss.zel.impl.lang;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javassist.util.proxy.ProxyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zel.ELException;
import org.zkoss.zel.impl.util.ClassUtil;
import org.zkoss.zel.impl.util.MessageFactory;


/**
 * A helper class that implements the EL Specification
 *
 * @author Jacob Hookom [jacob@hookom.net]
 */
public class ELSupport {
	//20120418, henrichen: moved from org.zkoss.zel.impl.parser.AstValue (ZK-1062)
    private static final boolean IS_SECURITY_ENABLED =
        (System.getSecurityManager() != null);
    
    private static final Logger log = LoggerFactory.getLogger(ELSupport.class);

    //20120418, henrichen: ZK-1062
    /**EL 2.2 by spec. should coerce "null" value to 0 number, 0 character, "" String, false boolean.
     *This spec. makes it impossible to store null state into java lang object when doing setValue() 
     *and can set unexpected coerced value into java lang object. So we provide a system property 
     *"org.zkoss.zel.impl.parser.COERCE_NULL_TO_NULL" to switch this behavior when doing setValue(). 
     *By default we will set org.zkoss.zel.impl.parser.COERCE_NULL_TO_NULL to true to allow set null value 
     *though this is not compliant to the EL 2.2 spec. Shall you need to make ZEL to follow EL 2.2 
     *spec. when doing setValue, please set "org.zkoss.zel.impl.parser.COERCE_NULL_TO_NULL" system property
     *to false.
     *Ref. https://services.brics.dk/java/courseadmin/SWP2011/documents/getDocument/expression_language-2_2-mrel-spec.pdf?d=41976
     */
    protected static final boolean COERCE_NULL_TO_NULL;
    
    static {
        if (IS_SECURITY_ENABLED) {
            COERCE_NULL_TO_NULL = AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>(){
                        public Boolean run() {
                            return Boolean.valueOf(System.getProperty(
                                    "org.zkoss.zel.impl.parser.COERCE_NULL_TO_NULL",
                                    "true"));
                        }

                    }
            ).booleanValue();
        } else {
            COERCE_NULL_TO_NULL = Boolean.valueOf(System.getProperty(
                    "org.zkoss.zel.impl.parser.COERCE_NULL_TO_NULL",
                    "true")).booleanValue();
        }
    }
    //^^

    private static final Long ZERO = Long.valueOf(0L);

    protected static final boolean COERCE_TO_ZERO;

    static {
        if (IS_SECURITY_ENABLED) {
            COERCE_TO_ZERO = AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>(){
                        
                        public Boolean run() {
                            return Boolean.valueOf(System.getProperty(
                                    "org.apache.el.parser.COERCE_TO_ZERO",
                                    "false"));
                        }

                    }
            ).booleanValue();
        } else {
            COERCE_TO_ZERO = Boolean.valueOf(System.getProperty(
                    "org.apache.el.parser.COERCE_TO_ZERO",
                    "false")).booleanValue();
        }
    }


    /**
     * Compare two objects, after coercing to the same type if appropriate.
     *
     * If the objects are identical, or they are equal according to
     * {@link #equals(Object, Object)} then return 0.
     *
     * If either object is a BigDecimal, then coerce both to BigDecimal first.
     * Similarly for Double(Float), BigInteger, and Long(Integer, Char, Short, Byte).
     *
     * Otherwise, check that the first object is an instance of Comparable, and compare
     * against the second object. If that is null, return 1, otherwise
     * return the result of comparing against the second object.
     *
     * Similarly, if the second object is Comparable, if the first is null, return -1,
     * else return the result of comparing against the first object.
     *
     * A null object is considered as:
     * <ul>
     * <li>ZERO when compared with Numbers</li>
     * <li>the empty string for String compares</li>
     * <li>Otherwise null is considered to be lower than anything else.</li>
     * </ul>
     *
     * @param obj0 first object
     * @param obj1 second object
     * @return -1, 0, or 1 if this object is less than, equal to, or greater than val.
     * @throws ELException if neither object is Comparable
     * @throws ClassCastException if the objects are not mutually comparable
     */
    public static final int compare(final Object obj0, final Object obj1)
            throws ELException {
        if (obj0 == obj1 || equals(obj0, obj1)) {
            return 0;
        }
        if (isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal) coerceToNumber(obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal) coerceToNumber(obj1, BigDecimal.class);
            return bd0.compareTo(bd1);
        }
        if (isDoubleOp(obj0, obj1)) {
            Double d0 = (Double) coerceToNumber(obj0, Double.class);
            Double d1 = (Double) coerceToNumber(obj1, Double.class);
            return d0.compareTo(d1);
        }
        if (isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger) coerceToNumber(obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger) coerceToNumber(obj1, BigInteger.class);
            return bi0.compareTo(bi1);
        }
        if (isLongOp(obj0, obj1)) {
            Long l0 = (Long) coerceToNumber(obj0, Long.class);
            Long l1 = (Long) coerceToNumber(obj1, Long.class);
            return l0.compareTo(l1);
        }
        if (obj0 instanceof String || obj1 instanceof String) {
            return coerceToString(obj0).compareTo(coerceToString(obj1));
        }
        if (obj0 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked") // checked above
            final Comparable<Object> comparable = (Comparable<Object>) obj0;
            return (obj1 != null) ? comparable.compareTo(obj1) : 1;
        }
        if (obj1 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked") // checked above
            final Comparable<Object> comparable = (Comparable<Object>) obj1;
            return (obj0 != null) ? -comparable.compareTo(obj0) : -1;
        }
        throw new ELException(MessageFactory.get("error.compare", obj0, obj1));
    }

    /**
     * Compare two objects for equality, after coercing to the same type if appropriate.
     *
     * If the objects are identical (including both null) return true.
     * If either object is null, return false.
     * If either object is Boolean, coerce both to Boolean and check equality.
     * Similarly for Enum, String, BigDecimal, Double(Float), Long(Integer, Short, Byte, Character)
     * Otherwise default to using Object.equals().
     *
     * @param obj0 the first object
     * @param obj1 the second object
     * @return true if the objects are equal
     * @throws ELException if one of the coercion fails
     */
    public static final boolean equals(final Object obj0, final Object obj1)
            throws ELException {
        if (obj0 == obj1) {
            return true;
        } else if (obj0 == null || obj1 == null) {
            return false;
        } else if (isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal) coerceToNumber(obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal) coerceToNumber(obj1, BigDecimal.class);
            return bd0.equals(bd1);
        } else if (isDoubleOp(obj0, obj1)) {
            Double d0 = (Double) coerceToNumber(obj0, Double.class);
            Double d1 = (Double) coerceToNumber(obj1, Double.class);
            return d0.equals(d1);
        } else if (isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger) coerceToNumber(obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger) coerceToNumber(obj1, BigInteger.class);
            return bi0.equals(bi1);
        } else         if (isLongOp(obj0, obj1)) {
            Long l0 = (Long) coerceToNumber(obj0, Long.class);
            Long l1 = (Long) coerceToNumber(obj1, Long.class);
            return l0.equals(l1);
        } else if (obj0 instanceof Boolean || obj1 instanceof Boolean) {
            return coerceToBoolean(obj0, false).equals(coerceToBoolean(obj1, false));
        } else if (obj0.getClass().isEnum()) {
            return obj0.equals(coerceToEnum(obj1, obj0.getClass()));
        } else if (obj1.getClass().isEnum()) {
            return obj1.equals(coerceToEnum(obj0, obj1.getClass()));
        } else if (obj0 instanceof String || obj1 instanceof String) {
            int lexCompare = coerceToString(obj0).compareTo(coerceToString(obj1));
            return (lexCompare == 0) ? true : false;
        } else {
            return obj0.equals(obj1);
        }
    }

    // Going to have to have some casts /raw types somewhere so doing it here
    // keeps them all in one place. There might be a neater / better solution
    // but I couldn't find it
    @SuppressWarnings("unchecked")
    public static final Enum<?> coerceToEnum(final Object obj,
            @SuppressWarnings("rawtypes") Class type) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        if (type.isAssignableFrom(obj.getClass())) {
            return (Enum<?>) obj;
        }

        if (!(obj instanceof String)) {
            throw new ELException(MessageFactory.get("error.convert",
                    obj, obj.getClass(), type));
        }

        Enum<?> result;
        try {
             result = Enum.valueOf(type, (String) obj);
        } catch (IllegalArgumentException iae) {
            throw new ELException(MessageFactory.get("error.convert",
                    obj, obj.getClass(), type));
        }
        return result;
    }

    /**
     * Convert an object to Boolean.
     * Null and empty string are false.
     * @param obj the object to convert
     * @param primitive is the target a primitive in which case coercion to null
     *                  is not permitted
     * @return the Boolean value of the object
     * @throws ELException if object is not Boolean or String
     */
    public static final Boolean coerceToBoolean(final Object obj,
            boolean primitive) throws ELException {

        if (!COERCE_TO_ZERO && !primitive) {
            if (obj == null) {
                return null;
            }
        }

        if (obj == null || "".equals(obj)) {
            return Boolean.FALSE;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            return Boolean.valueOf((String) obj);
        }

        throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), Boolean.class));
    }

    private static final Character coerceToCharacter(final Object obj)
            throws ELException {
        if (obj == null || "".equals(obj)) {
            return Character.valueOf((char) 0);
        }
        if (obj instanceof String) {
            return Character.valueOf(((String) obj).charAt(0));
        }
        if (ELArithmetic.isNumber(obj)) {
            return Character.valueOf((char) ((Number) obj).shortValue());
        }
        Class<?> objType = obj.getClass();
        if (obj instanceof Character) {
            return (Character) obj;
        }

        throw new ELException(MessageFactory.get("error.convert",
                obj, objType, Character.class));
    }

    protected static final Number coerceToNumber(final Number number,
            final Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            return Long.valueOf(number.longValue());
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            return new Double(number.doubleValue());
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            return Integer.valueOf(number.intValue());
        }
        if (BigInteger.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).toBigInteger();
            }
            if (number instanceof BigInteger) {
                return number;
            }
            return BigInteger.valueOf(number.longValue());
        }
        if (BigDecimal.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return number;
            }
            if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger) number);
            }
            return new BigDecimal(number.doubleValue());
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            return Byte.valueOf(number.byteValue());
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            return Short.valueOf(number.shortValue());
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            return new Float(number.floatValue());
        }
        if (Number.class.equals(type)) {
            return number;
        }

        throw new ELException(MessageFactory.get("error.convert",
                number, number.getClass(), type));
    }

    public static final Number coerceToNumber(final Object obj,
            final Class<?> type) throws ELException {

        if (!COERCE_TO_ZERO) {
            if (obj == null && !type.isPrimitive()) {
                return null;
            }
        }

        if (obj == null || "".equals(obj)) {
            return coerceToNumber(ZERO, type);
        }
        if (obj instanceof String) {
            return coerceToNumber((String) obj, type);
        }
        if (ELArithmetic.isNumber(obj)) {
            return coerceToNumber((Number) obj, type);
        }

        if (obj instanceof Character) {
            return coerceToNumber(Short.valueOf((short) ((Character) obj)
                    .charValue()), type);
        }

        throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), type));
    }

    protected static final Number coerceToNumber(final String val,
            final Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            try {
                return Long.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            try {
                return Integer.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            try {
                return Double.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (BigInteger.class.equals(type)) {
            try {
                return new BigInteger(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (BigDecimal.class.equals(type)) {
            try {
                return new BigDecimal(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            try {
                return Byte.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            try {
                return Short.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            try {
                return Float.valueOf(val);
            } catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert",
                        val, String.class, type));
            }
        }

        throw new ELException(MessageFactory.get("error.convert",
                val, String.class, type));
    }

    /**
     * Coerce an object to a string.
     * @param obj the object to convert
     * @return the String value of the object
     */
    public static final String coerceToString(final Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Enum<?>) {
            return ((Enum<?>) obj).name();
        } else {
            return obj.toString();
        }
    }
    
  //ZK-1062: null intbox give a 0 value to Long bean property (expect null)
    protected static final Object coerceToTypeForSetValue(final Object obj,
            Class<?> type) throws ELException {
        if (type == null || Object.class.equals(type) ||
                (obj != null && type.isAssignableFrom(obj.getClass()))) {
            return obj;
        }
        if (String.class.equals(type)) {
            return obj == null && COERCE_NULL_TO_NULL ? null : coerceToString(obj);
        }
        // since 8.0.0 support proxy form binding
        if (ProxyFactory.isProxyClass(type)) {
        	type = type.getSuperclass();
        	if (obj != null && type.isAssignableFrom(obj.getClass()))
        		return obj;
        }
        if (ELArithmetic.isNumberType(type)) {
            return (obj == null || "".equals(obj)) && !type.isPrimitive() && COERCE_NULL_TO_NULL ? null : coerceToNumber(obj, type);
        }
        if (Character.class.equals(type) || Character.TYPE == type) {
            return (obj == null || "".equals(obj)) && !type.isPrimitive() && COERCE_NULL_TO_NULL ? null : coerceToCharacter(obj);
        }
        if (Boolean.class.equals(type) || Boolean.TYPE == type) {
            return (obj == null || "".equals(obj)) && !type.isPrimitive() && COERCE_NULL_TO_NULL ? null : coerceToBoolean(obj, true);
        }
        if (type.isEnum()) {
            return coerceToEnum(obj, type);
        }
        //ZK-919: Cannot handle java.sql.Timestamp
        if (isDateTimeType(type)) {
        	return coerceToDateTime(obj, type);
        }
        
        //ZK-1595: Cannot coerce Set to List
        //Dennis, do the last effort to convert to List or Set
        //sometimes, if user's bean doesn't allow the List/Set we converted, it will still get error 
        //but, if we don't do this, user will still get error from last line of this method.
        if (isCollectionType(type)) {
        	return coerceToCollection(obj, type);
        }

        // new to spec
        if (obj == null)
            return null;
        if (obj instanceof String) {
            if ("".equals(obj))
                return null;
            PropertyEditor editor = PropertyEditorManager.findEditor(type);
            if (editor != null) {
                editor.setAsText((String) obj);
                return editor.getValue();
            }
        }
        
        throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), type));
    }

    public static final Object coerceToType(final Object obj,
            Class<?> type) throws ELException {

        if (type == null || Object.class.equals(type) ||
                (obj != null && type.isAssignableFrom(obj.getClass()))) {
            return obj;
        }

        if (!COERCE_TO_ZERO) {
            if (obj == null && !type.isPrimitive() &&
                    !String.class.isAssignableFrom(type)) {
                return null;
            }
        }

        if (String.class.equals(type)) {
            return coerceToString(obj);
        }
        // since 8.0.0 support proxy form binding
        if (ProxyFactory.isProxyClass(type)) {
        	type = type.getSuperclass();
        	if (obj != null && type.isAssignableFrom(obj.getClass()))
        		return obj;
        }
        if (ELArithmetic.isNumberType(type)) {
            return coerceToNumber(obj, type);
        }
        if (Character.class.equals(type) || Character.TYPE == type) {
            return coerceToCharacter(obj);
        }
        if (Boolean.class.equals(type) || Boolean.TYPE == type) {
            return coerceToBoolean(obj, Boolean.TYPE == type);
        }
        if (type.isEnum()) {
            return coerceToEnum(obj, type);
        }
        //ZK-919: Cannot handle java.sql.Timestamp
        if (isDateTimeType(type)) {
        	return coerceToDateTime(obj, type);
        }
        
        //ZK-1595: Cannot coerce Set to List
        if (isCollectionType(type)) {
        	return coerceToCollection(obj, type);
        }

        // new to spec
        if (obj == null)
            return null;
        if (obj instanceof String) {
            PropertyEditor editor = PropertyEditorManager.findEditor(type);
            if (editor == null) {
                if ("".equals(obj)) {
                    return null;
                }
                throw new ELException(MessageFactory.get("error.convert", obj,
                        obj.getClass(), type));
            } else {
                try {
                    editor.setAsText((String) obj);
                    return editor.getValue();
                } catch (RuntimeException e) {
                    if ("".equals(obj)) {
                        return null;
                    }
                    throw new ELException(MessageFactory.get("error.convert",
                            obj, obj.getClass(), type), e);
                }
            }
        }

        // Handle special case because the syntax for the empty set is the same
        // for an empty map. The parser will always parse {} as an empty set.
        if (obj instanceof Set && type == Map.class &&
                ((Set<?>) obj).isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        // Handle arrays
        if (type.isArray() && obj.getClass().isArray()) {
            return coerceToArray(obj, type);
        }

        throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), type));
    }

    private static Object coerceToArray(final Object obj,
            final Class<?> type) {
        // Note: Nested arrays will result in nested calls to this method.

        // Note: Calling method has checked the obj is an array.

        int size = Array.getLength(obj);
        // Cast the input object to an array (calling method has checked it is
        // an array)
        // Get the target type for the array elements
        Class<?> componentType = type.getComponentType();
        // Create a new array of the correct type
        Object result = Array.newInstance(componentType, size);
        // Coerce each element in turn.
        for (int i = 0; i < size; i++) {
            Array.set(result, i, coerceToType(Array.get(obj, i), componentType));
        }

        return result;
    }
    
    private static boolean isCollectionType(Class<?> type) {
		//support set and list only
		return Collection.class.isAssignableFrom(type);
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object coerceToCollection(Object obj, Class<?> type) {
		if(obj==null) return null;
		// since 8.0.0 support proxy form binding
        if (ProxyFactory.isProxyClass(type)) {
        	type = type.getSuperclass();
        	if (obj != null && type.isAssignableFrom(obj.getClass()))
        		return obj;
        }
		if(obj instanceof Collection){
			//try the construct first
			try {
				return ClassUtil.newInstance(type, new Object[]{obj});
			} catch (Throwable e) {
				//B70-ZK-2493 only throw those undefined exceptions
				if (!(e instanceof NoSuchMethodException) && !(e instanceof InstantiationException) && 
					!(e instanceof InvocationTargetException) && !(e instanceof IllegalAccessException)) {
					log.warn("ClassUtil.newInstance has unexpected error: ", e);
					throw new ELException("ClassUtil.newInstance has unexpected error: ", e);
				}
			}
			
			//try the common java.lang collections
			if(Set.class.isAssignableFrom(type)){
				if(Set.class.equals(type) || LinkedHashSet.class.isAssignableFrom(type)){
					return new LinkedHashSet((Collection)obj);
				}else if(HashSet.class.isAssignableFrom(type)){
					return new HashSet((Collection)obj);
				}else if(TreeSet.class.isAssignableFrom(type)){
					return new TreeSet((Collection)obj);
				}
				//give it a default one
				return new LinkedHashSet((Collection)obj);
			}else if(List.class.isAssignableFrom(type)){
				if(List.class.equals(type) || LinkedList.class.isAssignableFrom(type)){
					return new LinkedList((Collection)obj);
				}else if(ArrayList.class.isAssignableFrom(type)){
					return new ArrayList((Collection)obj);
				}else if(Vector.class.isAssignableFrom(type)){
					return new Vector((Collection)obj);
				}
				//give it a default
				return new LinkedList((Collection)obj);
			}
		}
		throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), type));
	}

    public static final boolean isBigDecimalOp(final Object obj0,
            final Object obj1) {
        return (obj0 instanceof BigDecimal || obj1 instanceof BigDecimal);
    }

    public static final boolean isBigIntegerOp(final Object obj0,
            final Object obj1) {
        return (obj0 instanceof BigInteger || obj1 instanceof BigInteger);
    }

    public static final boolean isDoubleOp(final Object obj0, final Object obj1) {
        return (obj0 instanceof Double
                || obj1 instanceof Double
                || obj0 instanceof Float
                || obj1 instanceof Float);
    }

    public static final boolean isLongOp(final Object obj0, final Object obj1) {
        return (obj0 instanceof Long
                || obj1 instanceof Long
                || obj0 instanceof Integer
                || obj1 instanceof Integer
                || obj0 instanceof Character
                || obj1 instanceof Character
                || obj0 instanceof Short
                || obj1 instanceof Short
                || obj0 instanceof Byte
                || obj1 instanceof Byte);
    }

    public static final boolean isStringFloat(final String str) {
        int len = str.length();
        if (len > 1) {
            for (int i = 0; i < len; i++) {
                switch (str.charAt(i)) {
                case 'E':
                    return true;
                case 'e':
                    return true;
                case '.':
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public ELSupport() {
        super();
    }
    
    //20120314, henrichen: ZK-919
    public static final boolean isDateTimeType(final Class<?> type) {
        return java.util.Date.class.isAssignableFrom(type);
    }
    
    /**
     * Coerce an object to an instance of java.util.Date
     * @param obj the object to be converted; must be an instance of java.util.Date or 
     * a number which represents the millisecond since "the epoch" base time, namely January 1, 1970, 00:00:00 GMT.
     * @param type the class type which shall be a kind of java.util.Date; e.g. java.sql.Date, java.sql.Time, java.sql.Timestamp
     * @return the instance as requested by the specified type
     */
	@SuppressWarnings("unchecked")
	public static final java.util.Date coerceToDateTime(final Object obj,
            @SuppressWarnings("rawtypes") Class type) throws ELException {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        // since 8.0.0 support proxy form binding
        if (ProxyFactory.isProxyClass(type)) {
        	type = type.getSuperclass();
        }
        
        if (type.isAssignableFrom(obj.getClass())) {
            return (java.util.Date) obj;
        }

        //convert among java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp
		try {
	        if (obj instanceof java.util.Date) {
	        	return newDateInstance(((java.util.Date) obj).getTime(), type);
	        } else if (obj instanceof Number) {
	        	return newDateInstance(((Number) obj).longValue(), type);
	        }
		} catch (Exception e) {
	        throw new ELException(MessageFactory.get("error.convert",
	                obj, obj.getClass(), type), e);
		}
        
        throw new ELException(MessageFactory.get("error.convert",
                obj, obj.getClass(), type));
    }
	
	@SuppressWarnings("rawtypes")
	private static java.util.Date newDateInstance(long time, Class type) throws Exception {
    	@SuppressWarnings("unchecked")
		final Constructor ctr = type.getConstructor(new Class[] {long.class});
    	return (java.util.Date) ctr.newInstance(time);
	}

}
