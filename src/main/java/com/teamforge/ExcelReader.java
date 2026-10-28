package com.teamforge;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class ExcelReader {

    public static ArrayList<String> readNames(String filePath)
            throws IOException {

        ArrayList<String> employees = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                if (row.getCell(0) != null) {
                    String name = row.getCell(0).toString().trim();

                    if (!name.isEmpty()) {
                        employees.add(name);
                    }
                }
            }
        }

        return employees;
    }
}
