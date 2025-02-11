package com.msara.ExcelToDb.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.msara.ExcelToDb.utils.ScriptsUtils.evaluateDataType;

public class ScriptsRepository {

    public static String createDatabaseScript(String databaseName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE DATABASE ")
                .append(databaseName.substring(0, databaseName.lastIndexOf('.')))
                .append("\n\tWITH OWNER = postgres ")
                .append("\n\tENCODING = 'UTF8' ")
                .append("\n\tCONNECTION LIMIT = -1")
                .append(";\n");

        return sql.toString();
    }

    public static String createSchemaScript(String schemaName) {
        return schemaName != null ? "CREATE SCHEMA " + schemaName + ";\n" : "";
    }

    public static String createTableScript(Map<String, List<String>> tableMap) {
        StringBuilder sql = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : tableMap.entrySet()) {
            sql.append("CREATE TABLE ")
                    .append(entry.getKey())
                    .append(" (");

            List<String> values = entry.getValue()
                    .stream()
                    .filter(value -> value != null && !value.trim().isEmpty()) // Filtrar valores vacíos
                    .toList();

            int size = values.size();

            for (int i = 0; i < size; i++) {
                String value = values.get(i);
                sql.append("\n\t")
                        .append(value)
                        .append(" ")
                        .append(evaluateDataType(value));

                if (i < size - 1) { // Solo agrega la coma si no es el último elemento
                    sql.append(",");
                }
            }
            sql.append("\n);\n");
        }
        return sql.toString();
    }

    public static String insertIntoTableScript(Map<String, List<List<String>>> insertMap) {
        StringBuilder sql = new StringBuilder();

        for (Map.Entry<String, List<List<String>>> entry : insertMap.entrySet()) {
            sql.append("INSERT INTO ").append(entry.getKey()).append(" VALUES ");

            List<List<String>> filas = entry.getValue();
            int filasSize = filas.size();

            for (int i = 0; i < filasSize; i++) {
                List<String> valores = filas.get(i);
                sql.append("(");

                int valoresSize = valores.size();
                for (int j = 0; j < valoresSize; j++) {
                    sql.append("'").append(valores.get(j)).append("'");

                    // Agrega coma solo si NO es el último elemento de la fila
                    if (j < valoresSize - 1) {
                        sql.append(",");
                    }
                }
                sql.append(")");

                // Agrega coma solo si NO es la última fila de inserción
                if (i < filasSize - 1) {
                    sql.append(",");
                }
            }
            sql.append(";\n"); // Finaliza la sentencia INSERT
        }

        return sql.toString();
    }

}
