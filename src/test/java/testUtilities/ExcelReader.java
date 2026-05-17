package testUtilities;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	public static Object[][] getTestData(String filePath, String sheetName){
		
		Object[][] data = null;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			DataFormatter formatter = new DataFormatter();
			
			int rowCount = sheet.getPhysicalNumberOfRows();
			int colCount = sheet.getRow(0).getLastCellNum();
			
			data = new Object[rowCount - 1][colCount];
			
			for(int i=1;i<rowCount;i++) {
				for(int j=0;j<colCount;j++) {
					data[i-1][j] = formatter.formatCellValue(sheet.getRow(i).getCell(j));
				}	
			}
			workbook.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
