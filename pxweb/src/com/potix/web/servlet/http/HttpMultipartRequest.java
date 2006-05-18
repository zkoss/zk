/* HttpMultipartRequest.java

{{IS_NOTE
	$Id: HttpMultipartRequest.java,v 1.7 2006/05/11 07:43:43 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Feb 11 19:16:26     2003, Created by andrewho@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.http;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.potix.lang.Exceptions;
import com.potix.lang.Strings;
import com.potix.util.ArraysX;
import com.potix.util.CollectionsX;
import com.potix.util.logging.Log;
import com.potix.util.prefs.Apps;

/**
 * HttpMultipartRequest Multipart HTTP request for enctye="multipart/form"
 *
 * @author <a href="mailto:andrewho@potix.com">andrewho@potix.com</a>
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/11 07:43:43 $
 */
public class HttpMultipartRequest extends HttpServletRequestWrapper {
	private static final Log log = Log.lookup(HttpMultipartRequest.class);
	
    /**Tempary directory to store the large upload files. */
	protected static File _updFileDir = null;	
	/** Specifies the maximal upload size. */
	private static int _maxUpdSize;

	/** the parameter map. */
	private Map _params;
	/** the uploads. */
	private Map _uploads;
	
	/** Returns an request representing by the giving content.
	 *  If it's a multipart/form it will wrapper one request on it, others it will
	 *  return the parameter named request directly.
	 */
	public static final HttpServletRequest
	getInstance(HttpServletRequest request)
	throws FileUploadException, UnsupportedEncodingException {
		assert(!(request instanceof HttpMultipartRequest));	

		//we only new HttpMultipartRequest instance for multipart enctype
		if (!FileUploadBase.isMultipartContent(request))
            return request;

		return new HttpMultipartRequest(request);
	}
	/**Constructor. */
	private HttpMultipartRequest(HttpServletRequest request)
	throws FileUploadException, UnsupportedEncodingException {
		super(request);
		initMultipartParamMap(request);
	}

	//-- static utilities --//
	/** Returns the uploaded content of the specified name if any, or null
	 * if not found.
	 */
	public final FileItem getUploadItem(String name) {
		return _uploads != null ? (FileItem)_uploads.get(name): null;
	}

	//-- HttpServletRequest --//
	public final Map getParameterMap() {
		return _params;
	}
	public final Enumeration getParameterNames() {
		return new CollectionsX.CollectionEnumeration( _params.keySet() );
	}
	public final String[] getParameterValues(String name) {
		return (String[])_params.get(name);
	}
	public final String getParameter(String name) {
		final String[] strs = getParameterValues(name);
		if (strs == null)
			return null;
		return strs[0];
	}

	//-- private utilities --//	
	/**Returns a java.util.Map of the parameters of this request. 
	 * Request parameters are extra information sent with the request. 
	 * For HTTP servlets, parameters are contained in the query string 
	 * or posted form data.
	 */
	private final void initMultipartParamMap(HttpServletRequest hreq)
	throws FileUploadException, UnsupportedEncodingException {
		assert _params == null: "init twice?";

		//1. parse multipart (into extra and uplds)
		final Map extra = new HashMap(37);
		final Map uplds = new HashMap(7);		//upload files
		final String charset = hreq.getCharacterEncoding();
		for (Iterator it = parseRequest(hreq, charset).iterator(); it.hasNext(); ) {
			final FileItem fitem = (FileItem)it.next();

			String fdnm = fitem.getFieldName();
			if (fitem.isFormField()) {	//normal form field
				//add to extra (which will be added to _params later
				final String val = fitem.getString( hreq.getCharacterEncoding() );

				List vals = (List)extra.get(fdnm);
				if (vals == null)
					extra.put(fdnm, vals = new LinkedList());
				vals.add(val);
			} else {	//upload files...
				if (fitem.getName() == null ||  fitem.getName().length() <= 0) 
					continue;	//don't add it into items map
				final Object o = uplds.put(fdnm, fitem);
				assert o == null: "replicate upload id: "+fdnm;
			}
		}

		//2. setup _uploads
		if (!uplds.isEmpty()) //with upload files
			_uploads = uplds;

		//3. Merge extras to the request's parameter map
		if (extra.isEmpty()) {
			_params = hreq.getParameterMap();
		} else {
			_params = new HashMap(extra.size() * 2 + 11);
			final Map reqParams = hreq.getParameterMap();
			if (reqParams != null)
				_params.putAll(reqParams);

			for (Iterator it = extra.entrySet().iterator(); it.hasNext(); ) {
				final Map.Entry e = (Map.Entry)it.next();
				final Object name = e.getKey();
				final List vals = (List)e.getValue();
				String[] strs = (String[])vals.toArray(new String[vals.size()]);

				final Object oldVals = _params.get(name);
				if (oldVals != null)
					strs = (String[])ArraysX.concat(oldVals, strs);
				_params.put(name, strs);
			}
		}
	}

	/** Parses the request.
	 * @param charset the charset used to parsing header, such as field name
	 * and filename.
	 */
	private static final
	List parseRequest(HttpServletRequest hreq, String charset)
	throws FileUploadException {
		//setup the param map by the multipart rfc
    	final DiskFileUpload fu = new DiskFileUpload();
    	fu.setSizeMax(getMaxUploadSize());	// maximum size before a FileUploadException will be thrown
    	fu.setSizeThreshold(1024*128);	// maximum size that will be stored in memory
    		// the location for saving data that is larger than getSizeThreshold()
    	fu.setRepositoryPath( getUploadTempDir().getPath() );
    	fu.setHeaderEncoding(charset);
    	return fu.parseRequest(hreq);
	}
	/** Get a temporary directory to store upload files.
	 */
	synchronized private static final File getUploadTempDir() {
		if (_updFileDir != null)
			return _updFileDir;

		//Try to create a tempary file and know it's path
		//todo: auto-delete the tmp directory before shutdown
		File t = null;
    	try {
    		t = File.createTempFile("web", "tmp");
    		_updFileDir = t.getParentFile();
    	} catch (IOException ex) {
    		log.warning("Unable to create a temporary directory: "+t, ex);
    	} finally {
    		if (t != null)
    			t.delete();
    	}
		return _updFileDir;
	}
	/** Returns the maximal upload size (unit: bytes).
	 */
	private static final int getMaxUploadSize() {
		//No synchronization is required because it is OK to be temporay 'wrong'
		if (_maxUpdSize == 0) {
			_maxUpdSize = 5*1024*1024;
			final String s = Apps.getProperty(
				"com.potix.web.servlet.http.MaxUploadSize", null);
			if (s != null) {
				try {
					_maxUpdSize = Integer.parseInt(s)*1024;
						//convert KB to bytes
				} catch (Throwable ex) {
					log.warning("Wrong setting, use default", ex);
				}
			}
		}
		return _maxUpdSize;
	}

	//depreacated//
	/**
	 * @deprecated
	 */
	public String getRealPath(String path) {
		return super.getRealPath(path);
	}
	/**
	 * @deprecated
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return super.isRequestedSessionIdFromUrl();
	}
}
