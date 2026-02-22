package org.zkoss.zktest.zats.test2;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class B104_ZK_5686Test {
    @Test
    public void test() throws Exception {
        File projectRoot = new File(System.getProperty("user.dir"));

        File gitRoot = projectRoot.getParentFile();
        if (projectRoot.getName().equals("zktest")) {
            gitRoot = projectRoot.getParentFile().getParentFile();
        } else if (projectRoot.getName().equals("zk")) {
            gitRoot = projectRoot.getParentFile();
        }

        File zklinterDir = new File(gitRoot, "zkcml/zklinter");
        if (!zklinterDir.exists()) {
            zklinterDir = new File(projectRoot.getParentFile(), "zkcml/zklinter");
        }
        Assertions.assertTrue(zklinterDir.exists());

        ProcessBuilder pb = new ProcessBuilder("./gradlew", "runLinter");
        pb.directory(zklinterDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        String out = output.toString();
        String[] lines = out.split("\n");
        for (String lineContent : lines) {
            if (lineContent.contains("B104-ZK-5686") && (lineContent.contains("SEVERE")
                    || lineContent.contains("ERROR") || lineContent.contains("WARNING"))) {
                Assertions.fail("Found B104-ZK-5686 error in zklinter output:\n" + lineContent);
            }
        }
    }
}
