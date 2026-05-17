package base;

import java.lang.reflect.Method;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import testUtilities.ConfigReader;
import testUtilities.ExtentReportManager; 

public class BaseTest {
    protected ConfigReader creader;
    
    @BeforeSuite
    public void setupReporting() {
        ExtentReportManager.getInstance();
    }

    @BeforeMethod
    public void setUp(Method method) { 
        creader = new ConfigReader("src/test/resources/config.properties");
        String browser = creader.getString("browser");
        String url = creader.getString("url");
        String mode = creader.getString("execution.mode");
        
        boolean headlessFlag = Boolean.parseBoolean(creader.getString("browser.headless"));
        
        DriverFactory.initDriver(browser, mode, headlessFlag);
        
        DriverFactory.getDriver().get(url);
        ExtentReportManager.startTest(method.getName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) { 
        if (result.getStatus() == ITestResult.FAILURE) {
            ExtentReportManager.getTest().fail("Test Case FAILED: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReportManager.getTest().pass("Test Case PASSED successfully.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportManager.getTest().skip("Test Case SKIPPED.");
        }
        
        if (DriverFactory.getDriver() != null) {
            DriverFactory.getDriver().quit();
        }
        
        DriverFactory.unloadDriver();
    }
    
    @AfterSuite
    public void wrapUpReporting() {
        ExtentReportManager.flushReports();
    }
}