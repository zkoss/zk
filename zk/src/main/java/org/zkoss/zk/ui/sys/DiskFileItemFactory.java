/* DiskFileItemFactory.java

	Purpose:
		
	Description:
		
	History:
		2:07 PM 2/16/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.File;

import org.apache.commons.fileupload2.core.FileItem;

import org.zkoss.util.media.Media;

/**
 * <p>A factory interface for creating {@link FileItem} and {@link Media} instances.
 * Factories can provide their own custom configuration, over and above that provided
 * by the default file upload implementation.
 * <br/>
 * Unlike {@link org.apache.commons.fileupload2.core.FileItemFactory}, this factory
 * needs two extra information, sizeThreshold and repository.
 * </p>
 * @author jumperchen
 * @since 8.0.2
 */
public interface DiskFileItemFactory {
	/**
	 * Create a new {@link FileItem} instance from the supplied parameters and
	 * any local factory configuration.
	 *
	 * @param fieldName   The name of the form field.
	 * @param contentType The content type of the form field.
	 * @param isFormField <code>true</code> if this is a plain form field;
	 *                    <code>false</code> otherwise.
	 * @param fileName    The name of the uploaded file, if any, as supplied
	 *                    by the browser or other client.
	 * @param sizeThreshold The threshold, in bytes, below which items will be
	 *                      retained in memory and above which they will be
	 *                      stored as a file, if any.
	 * @param repository    The data repository, which is the directory in
	 *                      which files will be created, should the item size
	 *                      exceed the threshold, if any.
	 *
	 * @return The newly created file item.
	 */
	public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName,
			int sizeThreshold, File repository);

	/**
	 * Create a new {@link Media} instance from from the supplied parameters and
	 * any local factory configuration.
	 * @param fileItem The instance of the upload file.
	 * @param contentType The content type of the form field.
	 * @param fileName The name of the uploaded file, if any, as supplied
	 *                    by the browser or other client.
	 * @param isNative whether the file item is a binary type.
	 * @return The newly created media.
	 */
	public Media createMedia(FileItem fileItem, String contentType, String fileName, boolean isNative);
}
