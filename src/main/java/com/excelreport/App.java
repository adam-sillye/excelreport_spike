package com.excelreport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // kiolvasás
        // ...
        
        // excel fájlbaírás
        try {
            ExcelReport(
                new Overall(
                    LocalDateTime.now(),
                    "AL",
                    "20220601",
                    "2022",
                    "Quarterly",
                    "3",
                    61262,
                    31.695650,
                    33.721518,
                    387.03266
                )
            );
            System.out.println("Ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ExcelReport(Overall overall) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(overall.getRegion());
        sheet.setColumnWidth(0, 7000);

        Object[][] data = {
            {"Volume Estimation Quality Report"},
            {},
            { "Metadata" },
            {},
            { "Run date", overall.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) },
            { "Region", overall.getRegion() },
            { "Map Version", overall.getMapversion() },
            { "Year", overall.getYear() },
            { "Periodicity", overall.getPeriodicity() },
            { "Period", overall.getPeriod() },
            {},
            { "GTD Correlation summary" },
            {},
            { "Count", overall.getCount() },
            { "MAPE", overall.getMape() },
            { "SMAPE", overall.getSmape() },
            { "RMSE", overall.getRmse() }
        };
 
        int rowCount = 0;
         
        for (Object[] aBook : data) {
            Row row = sheet.createRow(rowCount++);
             
            int columnCount = 0;
             
            for (Object field : aBook) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
            }
        }
         
        try (FileOutputStream outputStream = new FileOutputStream("ExcelReport.xlsx")) {
            workbook.write(outputStream);
        }
    }
}
