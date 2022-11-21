package com.excelreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
        // csv fájl megnyitása
        // kiolvasás
        
        
        // excel fájlbaírás
        try {
            ExcelReport(
                LocalDateTime.now(),
                "AL",
                "20220601",
                "2022",
                "Quarterly",
                "3",
                61262,
                31.695650,
                33.721518,
                387.032666
            );
            System.out.println("Ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ExcelReport(LocalDateTime date,
        String region, 
        String mapversion, 
        String year, 
        String periodicity, 
        String period,
        int count,
        double mape,
        double smape,
        double rmse // aren't this float?
        ) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("AL");
        sheet.setColumnWidth(0, 7000);

        Object[][] data = {
            {"Volume Estimation Quality Report"},
            {},
            { "Metadata" },
            {},
            { "Run date", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) },
            { "Region", region },
            { "Map Version", mapversion },
            { "Year", year },
            { "Periodicity", periodicity },
            { "Period", period },
            {},
            { "GTD Correlation summary" },
            {},
            { "Count", count },
            { "MAPE", mape },
            { "SMAPE", smape },
            { "RMSE", rmse }
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
