package com.msara.ExcelToDb.controller;

import com.msara.ExcelToDb.service.ExcelService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/convert-excel")
    public ResponseEntity<?> convertExcelToDatabase(@RequestParam("file")MultipartFile file, @RequestParam String schemaName) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacio");
        }

        File compressedFile = excelService.processExcel(file, schemaName);
        return ResponseEntity.status(HttpStatus.OK).body(compressedFile);
    }
}
