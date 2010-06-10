/*
 * $Id: JSONValue.java,v 1.1 2006/04/15 14:37:04 platform Exp $
 * Created on 2006-4-15
 */
package org.zkoss.json;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.zkoss.json.parser.JSONParser;
import org.zkoss.json.parser.ParseException;


/**
 * Utilities to encode and decode data in JSON format.
 * To decode, use {@link #parse(String)}.
 * To encode, use {@link #toJSONString}.
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONValue {
	/**
	 * Parse (aka., decode) JSON text into java object from the input source. 
	 * 
	 * @param in the input to parse.
	 * @return Instance of the following:
	 *	{@link JSONObject} (also java.util.Map),
	 * 	{@link JSONArray} (also java.util.List),
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean,
	 * 	null.
	 * If in is null, null is returned.
	 * 
	 */
	public static Object parse(Reader in) throws java.io.IOException {
		if (in == null) return null;
		JSONParser parser=new JSONParser();
		return parser.parse(in);
	}
	
	/**
	 * Parse (aka., decode) JSON text into java object from the string.
	 * @return the decoded object.
	 * If s is null, null is returned.
	 */
	public static Object parse(String s){
		if (s == null) return null;
		try {
			StringReader in=new StringReader(s);
			return parse(in);
		} catch(java.io.IOException e){
			throw new RuntimeException(e); //not possible
		}
	}
		
	/**
	 * Convert (aka., encode) an object to JSON text.
	 * <p>
	 * If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
	 * <p>
	 * DO NOT call this method from toJSONString() of a class that implements both JSONAware and Map or List with 
	 * "this" as the parameter, use JSONObject.toJSONString(Map) or JSONArray.toJSONString(List) instead. 
	 * 
	 * @param value
	 * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
	 */
	public static String toJSONString(Object value){
		if(value == null)
			return "null";
		
		if(value instanceof String)
			return "\""+escape((String)value)+"\"";
		
		if(value instanceof Double){
			if(((Double)value).isInfinite() || ((Double)value).isNaN())
				return "null";
			else
				return value.toString();
		}
		
		if(value instanceof Float){
			if(((Float)value).isInfinite() || ((Float)value).isNaN())
				return "null";
			else
				return value.toString();
		}		
		
		if(value instanceof Number)
			return value.toString();
		
		if(value instanceof Boolean)
			return value.toString();
		
		if((value instanceof JSONAware))
			return ((JSONAware)value).toJSONString();
		
		if(value instanceof Map)
			return JSONObject.toJSONString((Map)value);
		
		if(value instanceof List)
			return JSONArray.toJSONString((List)value);

		if (value.getClass().isArray()) {
			if (value instanceof Object[])
				return JSONArray.toJSONString((Object[])value);
			if (value instanceof int[])
				return JSONArray.toJSONString((int[])value);
			if (value instanceof double[])
				return JSONArray.toJSONString((double[])value);
			if (value instanceof long[])
				return JSONArray.toJSONString((long[])value);
			if (value instanceof float[])
				return JSONArray.toJSONString((float[])value);
			if (value instanceof short[])
				return JSONArray.toJSONString((short[])value);
			if (value instanceof byte[])
				return JSONArray.toJSONString((byte[])value);
			if (value instanceof boolean[])
				return JSONArray.toJSONString((boolean[])value);
			if (value instanceof char[])
				return JSONArray.toJSONString((char[])value);
		}
		return value.toString();
	}
	/** Converts an integer to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(int value) {
		return Integer.toString(value);
	}
	/** Converts a long to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(long value) {
		return Long.toString(value);
	}
	/** Converts a short to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(short value) {
		return Long.toString(value);
	}
	/** Converts a double to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(double value) {
		return Double.toString(value);
	}
	/** Converts a float to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(float value) {
		return Float.toString(value);
	}
	/** Converts a byte to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(byte value) {
		return Byte.toString(value);
	}
	/** Converts a boolean to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(boolean value) {
		return Boolean.toString(value);
	}
	/** Converts a char to JSON text
	 * <p>patched by tomyeh
	 */
	public static String toJSONString(char value) {
		return toJSONString("" + value);
	}

	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * @param s
	 * @return
	 */
	static String escape(String s){
		if(s==null)
			return null;
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void escape(String s, StringBuffer sb) {
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
                //Reference: http://www.unicode.org/versions/Unicode5.5.0/
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					sb.append("\\u");
					for(int k=0;k<4-ss.length();k++){
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		}//for
	}

}
