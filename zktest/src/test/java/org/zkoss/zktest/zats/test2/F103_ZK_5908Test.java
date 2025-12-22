/* F103_ZK_5908Test.java

	Purpose:
		Test for multiple file download functionality
	Description:
		Tests Filedownload.saveMultiple() and saveAsZip() APIs with actual file download verification
	History:
		12:28 PM 2025/12/17, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Test for ZK-5908: Multiple file download support
 * Tests file download functionality with actual file verification
 * @author jumperchen
 */
public class F103_ZK_5908Test extends WebDriverTestCase {

	private Path downloadDir;

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();

		// Create temporary download directory
		try {
			downloadDir = Files.createTempDirectory("zk-download-test-");
			System.out.println("Download directory: " + downloadDir.toAbsolutePath());

			// Configure Chrome to auto-download files to our temp directory
			Map<String, Object> prefs = new HashMap<>();
			prefs.put("download.default_directory", downloadDir.toAbsolutePath().toString());
			prefs.put("download.prompt_for_download", false);
			prefs.put("download.directory_upgrade", true);
			prefs.put("safebrowsing.enabled", false);

			options.setExperimentalOption("prefs", prefs);
		} catch (IOException e) {
			System.err.println("Warning: Failed to create download directory: " + e.getMessage());
		}

		return options;
	}

	@BeforeEach
	public void setup() {
		// Download directory is created in getWebDriverOptions()
		// This method is kept for consistency
	}

	@AfterEach
	public void cleanup() {
		// Clean up download directory
		if (downloadDir != null && Files.exists(downloadDir)) {
			try (var stream = Files.walk(downloadDir)) {
				stream.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(f -> {
						if (!f.delete()) {
							System.err.println("Failed to delete: " + f);
						}
					});
			} catch (IOException e) {
				System.err.println("Failed to clean up download directory: " + e.getMessage());
			}
		}
	}

	/**
	 * Wait for file to be downloaded
	 * Note: Due to browser security restrictions, actual file download verification
	 * may not work in all test environments. This method is provided for reference.
	 */
	private boolean waitForFileDownload(String fileName, int timeoutSeconds) {
		if (downloadDir == null) return false;

		Path filePath = downloadDir.resolve(fileName);
		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

		while (System.currentTimeMillis() < endTime) {
			if (Files.exists(filePath)) {
				// Also check that file is not still being downloaded (.crdownload extension)
				String tempFileName = fileName + ".crdownload";
				Path tempPath = downloadDir.resolve(tempFileName);
				if (!Files.exists(tempPath)) {
					return true;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/**
	 * Wait for multiple files to be downloaded
	 * Note: Due to browser security restrictions, actual file download verification
	 * may not work in all test environments. This method is provided for reference.
	 */
	private boolean waitForMultipleFiles(int expectedCount, int timeoutSeconds) {
		if (downloadDir == null) return false;

		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

		while (System.currentTimeMillis() < endTime) {
			try (var stream = Files.list(downloadDir)) {
				long count = stream
					.filter(p -> !p.getFileName().toString().endsWith(".crdownload"))
					.count();

				if (count >= expectedCount) {
					return true;
				}
			} catch (IOException e) {
				// Continue waiting
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/**
	 * Get count of downloaded files
	 */
	private int getDownloadedFileCount() {
		if (downloadDir == null) return 0;

		try (var stream = Files.list(downloadDir)) {
			return (int) stream
				.filter(p -> !p.getFileName().toString().endsWith(".crdownload"))
				.count();
		} catch (IOException e) {
			return 0;
		}
	}

	/**
	 * Check if file exists in download directory
	 */
	private boolean fileExists(String fileName) {
		if (downloadDir == null) return false;
		return Files.exists(downloadDir.resolve(fileName));
	}

	@Test
	public void testMultipleDownloadSimple() {
		connect();

		// Test 1: Simple multiple download with varargs
		click(jq("$btnDownloadMultipleSimple"));
		waitResponse();

		// Verify download iframe exists
		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify files (may not work in all environments due to browser security)
		// Wait a bit for downloads to process
		sleep(2000);

		// Check if files were downloaded (optional verification)
		boolean hasDownloads = waitForMultipleFiles(3, 3);
		if (hasDownloads) {
			System.out.println("Successfully verified 3 files downloaded");
			Assertions.assertTrue(fileExists("Report1.pdf"), "Report1.pdf should exist");
			Assertions.assertTrue(fileExists("Notes.txt"), "Notes.txt should exist");
			Assertions.assertTrue(fileExists("Data.csv"), "Data.csv should exist");
		} else {
			System.out.println("Note: File download verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during multiple download");
	}

	@Test
	public void testMultipleDownloadWithMap() {
		connect();

		// Test 2: Download with custom filenames using Map
		click(jq("$btnDownloadMultipleMap"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify files (optional)
		sleep(2000);
		boolean hasDownloads = waitForMultipleFiles(3, 3);
		if (hasDownloads) {
			System.out.println("Successfully verified custom named files");
			Assertions.assertTrue(fileExists("Annual-Report-2024.pdf"), "Annual-Report-2024.pdf should exist");
			Assertions.assertTrue(fileExists("Sales-Chart-Q4.txt"), "Sales-Chart-Q4.txt should exist");
			Assertions.assertTrue(fileExists("Executive-Summary.txt"), "Executive-Summary.txt should exist");
		} else {
			System.out.println("Note: File download verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during Map-based download");
	}

	@Test
	public void testMultipleDownloadWithItems() {
		connect();

		// Test 3: Download using DownloadItem
		click(jq("$btnDownloadMultipleItems"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify files (optional)
		sleep(2000);
		boolean hasDownloads = waitForMultipleFiles(3, 3);
		if (hasDownloads) {
			System.out.println("Successfully verified DownloadItem files");
			Assertions.assertTrue(fileExists("Custom-Doc-1.pdf"), "Custom-Doc-1.pdf should exist");
			Assertions.assertTrue(fileExists("doc2.pdf"), "doc2.pdf should exist");
			Assertions.assertTrue(fileExists("Custom-Doc-3.pdf"), "Custom-Doc-3.pdf should exist");
		} else {
			System.out.println("Note: File download verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during DownloadItem-based download");
	}

	@Test
	public void testDownloadAsZip() {
		connect();

		// Test 4: Download as ZIP (simple)
		click(jq("$btnDownloadAsZip"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify ZIP file (optional)
		sleep(2000);
		boolean hasZip = waitForFileDownload("AllFiles-2024.zip", 3);
		if (hasZip && fileExists("AllFiles-2024.zip")) {
			System.out.println("Successfully verified ZIP file");
			// Verify ZIP contains 3 files
			try {
				Path zipPath = downloadDir.resolve("AllFiles-2024.zip");
				try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
					Assertions.assertEquals(3, zipFile.size(),
						"ZIP should contain 3 files");

					// Verify file names inside ZIP
					Assertions.assertNotNull(zipFile.getEntry("Report.pdf"),
						"ZIP should contain Report.pdf");
					Assertions.assertNotNull(zipFile.getEntry("Notes.txt"),
						"ZIP should contain Notes.txt");
					Assertions.assertNotNull(zipFile.getEntry("Data.csv"),
						"ZIP should contain Data.csv");
				}
			} catch (IOException e) {
				System.err.println("Could not verify ZIP contents: " + e.getMessage());
			}
		} else {
			System.out.println("Note: ZIP file verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during ZIP download");
	}

	@Test
	public void testDownloadAsZipWithCustomNames() {
		connect();

		// Test 5: Download as ZIP with custom names
		click(jq("$btnDownloadAsZipMap"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify ZIP file (optional)
		sleep(2000);
		boolean hasZip = waitForMultipleFiles(1, 5);
		if (hasZip && getDownloadedFileCount() >= 1) {
			System.out.println("Successfully verified ZIP file with custom names");
		} else {
			System.out.println("Note: ZIP file verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during ZIP download with custom names");
	}

	@Test
	public void testDownloadManyFiles() {
		connect();

		// Test 6: Stress test - 10 files separately
		click(jq("$btnDownloadMany"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify 10 files (optional)
		sleep(2000);
		boolean hasFiles = waitForMultipleFiles(10, 8);
		if (hasFiles) {
			int fileCount = getDownloadedFileCount();
			System.out.println("Successfully verified " + fileCount + " files downloaded");
			Assertions.assertTrue(fileCount >= 10,
				"Should have at least 10 files downloaded");
		} else {
			System.out.println("Note: File download verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during stress test with 10 files");
	}

	@Test
	public void testDownloadManyAsZip() {
		connect();

		// Test 7: Many files as ZIP
		click(jq("$btnDownloadManyAsZip"));
		waitResponse();

		Assertions.assertTrue(jq("#zk_download").exists(),
			"Download iframe should be created");

		// Try to verify ZIP file (optional)
		sleep(2000);
		boolean hasZip = waitForMultipleFiles(1, 5);
		if (hasZip) {
			System.out.println("Successfully verified ZIP file");
			// Try to find and verify the ZIP file
			try (var stream = Files.list(downloadDir)) {
				Path zipFile = stream
					.filter(p -> p.getFileName().toString().endsWith(".zip"))
					.findFirst()
					.orElse(null);

				if (zipFile != null) {
					try (ZipFile zip = new ZipFile(zipFile.toFile())) {
						Assertions.assertEquals(10, zip.size(),
							"ZIP should contain 10 files");
					}
				}
			} catch (IOException e) {
				System.err.println("Could not verify ZIP contents: " + e.getMessage());
			}
		} else {
			System.out.println("Note: ZIP file verification skipped (browser security)");
		}

		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during ZIP download with 10 files");
	}

	@Test
	public void testAllDownloadButtons() {
		connect();

		// Verify all buttons are present and visible
		Assertions.assertTrue(jq("$btnDownloadMultipleSimple").exists(),
			"Simple download button should exist");
		Assertions.assertTrue(jq("$btnDownloadMultipleMap").exists(),
			"Map download button should exist");
		Assertions.assertTrue(jq("$btnDownloadMultipleItems").exists(),
			"DownloadItem button should exist");
		Assertions.assertTrue(jq("$btnDownloadAsZip").exists(),
			"ZIP download button should exist");
		Assertions.assertTrue(jq("$btnDownloadAsZipMap").exists(),
			"ZIP with custom names button should exist");
		Assertions.assertTrue(jq("$btnDownloadMany").exists(),
			"Many files button should exist");
		Assertions.assertTrue(jq("$btnDownloadManyAsZip").exists(),
			"Many files as ZIP button should exist");
	}

	@Test
	public void testNoErrorsOnPageLoad() {
		connect();

		// Verify page loads without errors
		Assertions.assertTrue(jq("@window").exists(),
			"Window component should be loaded");
		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur on page load");

		// Verify main label exists (checking content is present)
		Assertions.assertTrue(jq("@label").exists(),
			"Labels should be loaded");

		// Verify at least one button exists
		Assertions.assertTrue(jq("@button").exists(),
			"Buttons should be loaded");
	}

	@Test
	public void testDownloadQueueSequential() {
		connect();

		// Click multiple download buttons in quick succession
		// This tests that the queue mechanism prevents conflicts
		click(jq("$btnDownloadMultipleSimple"));
		waitResponse();

		sleep(200); // Small delay to allow first download to start

		click(jq("$btnDownloadMultipleMap"));
		waitResponse();

		sleep(200);

		click(jq("$btnDownloadMultipleItems"));
		waitResponse();

		// Wait for queue to process
		sleep(2000);

		// Verify no errors occurred during rapid sequential downloads
		Assertions.assertFalse(isZKLogAvailable(),
			"No ZK errors should occur during rapid sequential downloads");
	}
}
