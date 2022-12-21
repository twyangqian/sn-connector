package com.thoughtworks.otr.snconnector.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    public static File base64ToFile(String fileName, String base64) throws IOException {
        File file = new File(fileName);
        byte[] decode = Base64.getDecoder().decode(base64.split(",")[1]);
        FileUtils.writeByteArrayToFile(file, decode);
        return file;
    }
}
