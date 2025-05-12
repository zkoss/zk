/* F102_ZK_5640Test.java

        Purpose:
                
        Description:
                
        History:
                Mon May 12 17:54:16 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Assertions;

import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.http.Wpds;

public class F102_ZK_5640Test extends WebDriverTestCase {

    protected static final String tempFileName = "F102-ZK-5640-temp-tzdb-version.json";

    /**
     * Create temp tzdb version json file,
     * and set library property "org.zkoss.zk.moment.timezone.path".
     * @param version the target file version
     */
    protected static void beforeAll0(String version) {
        String tempJsonContent = "{\"version\":\"" + version + "\"}";
        File tempFile = new File("src/main/webapp/test2/data/" + tempFileName);
        try {
            Files.write(tempFile.toPath(), tempJsonContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Assertions.fail("Failed to write temp tzdb version json file.", e);
        }
        Library.setProperty("org.zkoss.zk.moment.timezone.path", "/test2/data/" + tempFileName);
    }

    /**
     * Delete temp tzdb version json file,
     * and set library property "org.zkoss.zk.moment.timezone.path" to null.
     */
    protected static void afterAll0() {
        try {
            Files.delete(new File("src/main/webapp/test2/data/" + tempFileName).toPath());
        } catch (IOException e) {
            Assertions.fail("Failed to delete temp tzdb version json.", e);
        }
        Library.setProperty("org.zkoss.zk.moment.timezone.path", null);
    }

    protected static class TestLogHandler extends Handler {

        private final List<String> logs = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            logs.add(record.getMessage());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        public List<String> getLogs() {
            return logs;
        }
    }

    protected String getDifferentVersionExpectResult() {
        return "Time zone data version mismatch detected:\n" +
                " - Client (moment.js) tzdb version: " + getJsTZDBVersion() + "\n" +
                " - Server (JDK) tzdb version: " + getJavaTZDBVersion() + "\n" +
                "Date and time values may be incorrect if time zone rules differ.\n" +
                "To resolve, update the moment-timezone data on the client and/or the JDK time zone data (TZUpdater or Java update) on the server so both use the same version.";
    }

    protected static String getJavaTZDBVersion() {
        return ZoneRulesProvider.getVersions("UTC").lastEntry().getKey();
    }

    protected static String getJsTZDBVersion() {
        return Wpds.getClientTimeZoneDataBaseVersion();
    }
}
