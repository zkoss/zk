/* F103_ZK_5908_MultipleFileDownloadComposer.java

	Purpose:
		Demonstrate multiple file download feature
	Description:

	History:
		Dec 17, 2024, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Filedownload.DownloadItem;

/**
 * Test composer for multiple file download functionality
 *
 * @author jumperchen
 */
public class F103_ZK_5908_MultipleFileDownloadComposer extends SelectorComposer<Component> {

	/**
	 * Test 1: Download multiple files using varargs (original filenames)
	 */
	@Listen("onClick = #btnDownloadMultipleSimple")
	public void downloadMultipleSimple() {
		Media pdf = createSamplePDF("Report1.pdf", "This is Report 1");
		Media txt = createSampleText("Notes.txt", "These are my notes");
		Media csv = createSampleCSV("Data.csv", "ID,Name\n1,Alice\n2,Bob");

		// Simple usage: download 3 files with original names
		Filedownload.saveMultiple(pdf, txt, csv);
	}

	/**
	 * Test 2: Download multiple files with custom filenames using Map
	 */
	@Listen("onClick = #btnDownloadMultipleMap")
	public void downloadMultipleWithMap() {
		Media report = createSamplePDF("original.pdf", "Annual Report Content");
		Media chart = createSampleText("original.txt", "Chart Data");
		Media summary = createSampleText("original.txt", "Executive Summary");

		// Using Map to specify custom filenames
		Map<Media, String> files = new LinkedHashMap<>();
		files.put(report, "Annual-Report-2024.pdf");
		files.put(chart, "Sales-Chart-Q4.txt");
		files.put(summary, "Executive-Summary.txt");

		Filedownload.saveMultiple(files);
	}

	/**
	 * Test 3: Download multiple files using DownloadItem
	 */
	@Listen("onClick = #btnDownloadMultipleItems")
	public void downloadMultipleWithItems() {
		Media pdf1 = createSamplePDF("doc1.pdf", "Document 1");
		Media pdf2 = createSamplePDF("doc2.pdf", "Document 2");
		Media pdf3 = createSamplePDF("doc3.pdf", "Document 3");

		// Using DownloadItem for mixed usage (some custom, some original)
		Filedownload.saveMultiple(
			new DownloadItem(pdf1, "Custom-Doc-1.pdf"),
			new DownloadItem(pdf2), // uses original name
			DownloadItem.of(pdf3, "Custom-Doc-3.pdf") // using static factory
		);
	}

	/**
	 * Test 4: Download multiple files as a single ZIP archive
	 */
	@Listen("onClick = #btnDownloadAsZip")
	public void downloadAsZip() {
		Media pdf = createSamplePDF("Report.pdf", "Annual Report");
		Media txt = createSampleText("Notes.txt", "Meeting Notes");
		Media csv = createSampleCSV("Data.csv", "Sales Data");

		// Download all files as a single ZIP
		Filedownload.saveAsZip(
			new Media[] { pdf, txt, csv },
			"AllFiles-2024.zip"
		);
	}

	/**
	 * Test 5: Download multiple files as ZIP with custom names
	 */
	@Listen("onClick = #btnDownloadAsZipMap")
	public void downloadAsZipWithCustomNames() {
		Media report = createSamplePDF("temp1.pdf", "Report Content");
		Media chart = createSampleText("temp2.txt", "Chart Data");
		Media summary = createSampleCSV("temp3.csv", "Summary Data");

		Map<Media, String> files = new LinkedHashMap<>();
		files.put(report, "2024-Annual-Report.pdf");
		files.put(chart, "Q4-Sales-Chart.txt");
		files.put(summary, "Executive-Summary.csv");

		Filedownload.saveAsZip(files, "Annual-Package-2024.zip");
	}

	/**
	 * Test 6: Download many files (stress test)
	 */
	@Listen("onClick = #btnDownloadMany")
	public void downloadManyFiles() {
		Media[] medias = new Media[10];
		for (int i = 0; i < 10; i++) {
			medias[i] = createSampleText("file" + i + ".txt", "Content of file " + i);
		}

		// Test queue with many files
		Filedownload.saveMultiple(medias);
	}

	/**
	 * Test 7: Large batch as ZIP (recommended for 10+ files)
	 */
	@Listen("onClick = #btnDownloadManyAsZip")
	public void downloadManyAsZip() {
		Media[] medias = new Media[10];
		for (int i = 0; i < 10; i++) {
			medias[i] = createSampleText("report" + i + ".txt", "Report content " + i);
		}

		// Better user experience: single ZIP for many files
		Filedownload.saveAsZip(medias, "all-reports.zip");
	}

	// Helper methods to create sample media

	private Media createSamplePDF(String filename, String content) {
		byte[] data = content.getBytes();
		return new AMedia(filename, "pdf", "application/pdf", data);
	}

	private Media createSampleText(String filename, String content) {
		return new AMedia(filename, "txt", "text/plain", content);
	}

	private Media createSampleCSV(String filename, String content) {
		return new AMedia(filename, "csv", "text/csv", content);
	}
}

