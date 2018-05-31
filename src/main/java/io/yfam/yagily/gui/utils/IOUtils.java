package io.yfam.yagily.gui.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class IOUtils {
    public static String[] extractFileBaseNameAndExtension(String fullFileName) {
        String[] results = new String[2];
        results[0] = FilenameUtils.getBaseName(fullFileName);
        results[1] = FilenameUtils.getExtension(fullFileName);
        return results;
    }

    public static String generateUniqueFileName(String originalFullFileName) {
        String[] filenameParts = extractFileBaseNameAndExtension(originalFullFileName);
        return String.format("%s-%s.%s", filenameParts[0],
                UUID.randomUUID().toString(), filenameParts[1]);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFile(String sourceFilename, String destinationFilename) {
        File sourceFile = new File(sourceFilename);
        if (!sourceFile.exists()) throw new RuntimeException(String.format("File %s does not exist.", sourceFilename));

        try {
            FileUtils.copyFile(sourceFile, new File(destinationFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
