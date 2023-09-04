package com.leucine.streem.util;

import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WorkbookUtils {
  public static XSSFFont getXSSFFont(XSSFWorkbook wb, String fontName, short fontHeight) {
    XSSFFont font = wb.createFont();
    font.setFontName(fontName);
    font.setFontHeightInPoints(fontHeight);
    font.setBold(true);
    return font;
  }
}
