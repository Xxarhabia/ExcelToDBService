package com.msara.ExcelToDb.utils.manage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.msara.ExcelToDb.utils.manage.ManageSqlFiles.generateSqlFile;
public class ManageCompressedFile {

    public static File compressFile(Map<List<String>, List<String>> filesQueries) throws IOException {
        int indexKey = 0;
        List<File> files = new ArrayList<>();
        for (Map.Entry<List<String>, List<String>> entry : filesQueries.entrySet()) {
             for (String values : entry.getValue()) {
                files.add(generateSqlFile(entry.getKey().get(indexKey), values));
                indexKey++;
             }
        }

        File currentDir = new File(System.getProperty("user.dir"));
        File compressedDir = new File(currentDir, "compressed_file");

        if (!compressedDir.exists()) {
            compressedDir.mkdir();
        }

        File zipFile = new File(compressedDir, "database.zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (File file : files) {
                if (!file.exists() || file.isDirectory()) {
                    System.out.println("saltando: " + file.getAbsolutePath());
                    continue;
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(bytes)) != -1) {
                        zipOut.write(bytes, 0, bytesRead);
                    }

                    zipOut.closeEntry();
                    System.out.println("Archivo agregado: " + file.getName());
                }
            }
        }
        return zipFile;
    }
}
