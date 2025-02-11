package com.msara.ExcelToDb.utils;

public class ScriptsUtils {

    public static String evaluateDataType(String value) {

        if (value.contains("id")){
            return "SERIAL PRIMARY KEY";
        } else {
            return "VARCHAR (250) NOT NULL";
        }
    }

    public static String chooseDatabaseEngine(int engineIndex) {
        if (engineIndex == 1){
            return "postgres";
        } else {
            throw new RuntimeException("No se puede seleccionar database");
        }
    }
}
