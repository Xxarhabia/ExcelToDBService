package com.msara.ExcelToDb.utils.manage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ManageSqlFiles {

    public static File generateSqlFile(String filename, String scripts) {
        File file = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(scripts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


}
