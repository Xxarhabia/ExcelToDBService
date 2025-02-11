package com.msara.ExcelToDb.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.msara.ExcelToDb.utils.ExcelUtils.getCellValueAsString;
import static com.msara.ExcelToDb.repository.ScriptsRepository.*;
import static com.msara.ExcelToDb.utils.manage.ManageCompressedFile.compressFile;

@Service
public class ExcelService {

    public File processExcel(MultipartFile file, String schemaName) {
        try {
            StringBuilder firstScript = new StringBuilder();
            StringBuilder secondScript = new StringBuilder();

            List<String> queries = new ArrayList<>();
            List<String> columnNames = new ArrayList<>();
            List<List<String>> rowData = new ArrayList<>();
            List<String> cellData = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();

            String fileName = file.getOriginalFilename();
            firstScript.append(createDatabaseScript(fileName)).append("\n");
            firstScript.append(createSchemaScript(schemaName)).append("\n");

            Workbook workbook = WorkbookFactory.create(file.getInputStream());

            //Obtenemos los nombres de las hojas
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }

            //Estructura para almacenar los datos de cada hoja
            Map<String, List<List<String>>> excelData = new LinkedHashMap<>();
            //Estructura para almacenar el nombre de la tabla y el nombre de las columnas
            Map<String, List<String>> createTableMap = new HashMap<>();

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = workbook.getSheetName(sheetIndex);

                //Obtener nombres de las columnas
                Row headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    for (Cell cell : headerRow) {
                        columnNames.add(getCellValueAsString(cell));
                    }
                }

                for (String tableName : sheetNames) {
                    createTableMap.put(tableName, columnNames);
                }

                //Leer los datos de las filas
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        for (Cell cell : row) {
                            cellData.add(getCellValueAsString(cell));
                        }
                        rowData.add(cellData);
                    }
                }

                //Guardar los datos de la hoja en le mapa
                excelData.put(sheetName, rowData);
            }

            workbook.close();

            firstScript.append(createTableScript(createTableMap));
            secondScript.append(insertIntoTableScript(excelData));

            queries.add(firstScript.toString());
            queries.add(secondScript.toString());
            fileNames.add("1_crete_setup.sql");
            fileNames.add("2_insert_in_tables.sql");

            Map<List<String>, List<String>> filesQueries = new LinkedHashMap<>();
            filesQueries.put(fileNames, queries);

            return compressFile(filesQueries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
