package org.zkoss.json;

/**
 * Beans that support customized output of JSON text shall implement this interface. 
 * It is called when encoding an object.
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONAware {
	/**
	 * @return JSON text
	 */
	String toJSONString();
}
