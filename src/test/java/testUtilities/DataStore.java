package testUtilities;

import org.testng.annotations.DataProvider;

public class DataStore {

	@DataProvider(name="loginData")
	public Object[][] feeddata(){
		return ExcelReader.getTestData("src/test/resources/LoginTestData.xlsx", "Sheet1");
	}
}
