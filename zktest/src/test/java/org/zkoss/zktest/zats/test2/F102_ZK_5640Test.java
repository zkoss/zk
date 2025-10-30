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
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.Assertions;

import org.zkoss.lang.Library;
import org.zkoss.zk.au.http.TimeZoneDataBaseVersionChecker;

public class F102_ZK_5640Test {

    protected static Server server;
    protected static int port;
    protected static String getContextPath() {
        return "/zktest";
    }
    protected static String getHost() {
        return "127.0.0.1";
    }
    protected static Logger _logger = null;
    protected static TestLogHandler _handler = null;
    protected static final String tempFileName = "F102-ZK-5640-temp-tzdb-version.json";

    /**
     * Init server and all needed variables.
     */
    protected static void beforeAll0() throws Exception {
        _logger = Logger.getLogger("org.zkoss.zk.au.http.TimeZoneDataBaseVersionChecker");
        _handler = new TestLogHandler();
        _logger.addHandler(_handler);

        server = new Server(new InetSocketAddress(getHost(), 0));
        final WebAppContext context = new WebAppContext();
        context.setContextPath(getContextPath());

        Resource[] res = new Resource[] {Resource.newResource("./src/main/webapp/"), JarResource.newJarResource(Resource.newResource("./src/main/webapp/data/zk-3123-1.0.jar"))};
        context.setBaseResource(new ResourceCollection(res));
        server.setHandler(context);
        server.start();

        for (Connector c : server.getConnectors()) {
            if (c instanceof NetworkConnector) {
                if (((NetworkConnector)c).getLocalPort() > 0) {
                    port = ((NetworkConnector)c).getLocalPort();
                    break;
                }
            }
        }
    }
    
    /**
     * Create temp tzdb version json file,
     * and set library property "org.zkoss.zk.moment.timezone.path".
     * @param version the target file version
     */
    protected static void createTempTZDBFileAndApplyToLibraryProperty(String version) throws Exception {
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
    protected static void afterAll0() throws Exception {
        try {
            Files.delete(new File("src/main/webapp/test2/data/" + tempFileName).toPath());
        } catch (IOException e) {
            Assertions.fail("Failed to delete temp tzdb version json.", e);
        }
        Library.setProperty("org.zkoss.zk.moment.timezone.path", null);
        if (server != null) {
            server.stop();
        }
        _logger.removeHandler(_handler);
        _logger = null;
        _handler = null;
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
        return TimeZoneDataBaseVersionChecker.getClientTimeZoneDataBaseVersion();
    }
}
