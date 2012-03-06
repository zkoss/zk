/* Path.java

	Purpose:
		
	Description:
		
	History:
		Aug 12, 2011 2:11:16 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that holding a dot series path and process Form field name.
 * @author henrichen
 * @since 6.0.0
 */
public class Path {
    private List<PathNode> path;
    public Path() {
    	path = new ArrayList<PathNode>(); 
    }
    public void add(String origin, String evaled) {
    	path.add(new PathNode(origin, evaled));
    }
    public int size() {
    	return path.size();
    }
    /**
     * Returns the path as a String for Form access purpose. This implementation 
     * evaluate indirect bracket to final field name.
     * @return the path as a String for Form access purpose.
     */
    public String getAccessFieldName() {
    	final StringBuilder sb = new StringBuilder();
    	final Iterator<PathNode> it = path.iterator();
    	it.next(); //skip the 1st form property
    	while(it.hasNext()) {
    		sb.append('.').append(it.next().evaled);
    	}
    	return sb.substring(1);
    }
    
    /**
     * Returns the path as a String for tracking purpose.
     * @return the path as a String for tracking purpose.
     */
    public String getTrackFieldName() {
    	final StringBuilder sb = new StringBuilder();
    	final Iterator<PathNode> it = path.iterator();
    	it.next(); //skip the 1st form property
    	while(it.hasNext()) {
    		sb.append(it.next().origin);
    	}
    	return sb.charAt(0) == '.' ? sb.substring(1) : sb.toString();
    }
    
    /**
     * Returns the path as a List of field names.
     * @return the path as a List of field names.
     */
    public List<String> getTrackFieldsList() {
    	final List<String> fields = new ArrayList<String>(path.size());
    	for(PathNode node : path) {
    		fields.add(node.origin);
    	}
    	return fields;
    }
    
	/** Returns the base path of this dot series path for tracking purpose. 
	 * ex, base path of 'vm.person.address.fullstreet' will be 'vm.person.address'
	 * so, a 'fullstreet' depends-on 'city' in address will add that 
	 * 'vm.person.address.fullstreet' depends on 'vm.person.address'.'city'
	 * 
	 * @return the path before the last dot
	 * @see #getTrackProperty
	 */
	public String getTrackBasePath() {
    	final StringBuffer sb = new StringBuffer();
		for(PathNode prop : path.subList(0, path.size()-1)) { //remove the last one
			sb.append(prop.origin);
		}
    	return sb.charAt(0) == '.' ? sb.substring(1) : sb.toString();
	}
    
	public String toString() {
		return path.toString();
	}
	
    /**
     * Returns the last field name of this dot series path for tracking purpose.
     * @return the last field name of this dot series path for tracking purpose.
     */
    public String getTrackProperty() {
    	final String script = path.listIterator(path.size()).previous().origin;
    	return script.charAt(0) == '.' ? script.substring(1) : script;
    }
    
	private static class PathNode {
		final private String origin;
		final private String evaled;
		
		public PathNode(String origin, String evaled) {
			this.origin = origin;
			this.evaled = evaled;
		}
		
		public String toString() {
			return "["+ origin+ ", " + evaled+ "]";
		}
	}
}
