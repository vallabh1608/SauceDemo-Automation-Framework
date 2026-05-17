//package base;
//
//import org.openqa.selenium.WebDriver;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//
//import utilities.ConfigReader;
//
//
//public class BaseTest {
//    protected WebDriver driver;
//    protected ConfigReader creader;
//    
//    @BeforeMethod
//    public void setUp() {
//    	    creader = new ConfigReader("src/test/resources/config.properties");
//        String Browser = creader.getString("browser");
//        driver = DriverFactory.initDriver(Browser);
//        String url = creader.getString("url");
//        driver.get(url);
//    }
//
//   @AfterMethod
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//}

//package base;
//
//import java.lang.reflect.Method;
//import org.openqa.selenium.WebDriver;
//import org.testng.ITestResult;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.AfterSuite;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.BeforeSuite;
//import utilities.ConfigReader;
//import utilities.ExtentReportManager; 
//
//public class BaseTest {
//    protected WebDriver driver;
//    protected ConfigReader creader;
//    
//    @BeforeSuite
//    public void setupReporting() {
//        
//        ExtentReportManager.getInstance();
//    }
//
//    @BeforeMethod
//    public void setUp(Method method) { 
//        creader = new ConfigReader("src/test/resources/config.properties");
//        String Browser = creader.getString("browser");
//        driver = DriverFactory.initDriver(Browser);
//        String url = creader.getString("url");
//        driver.get(url);
//        
//        // Dynamically launches a clean test tracking card inside the HTML report
//        ExtentReportManager.startTest(method.getName());
//    }
//
//    @AfterMethod
//    public void tearDown(ITestResult result) { 
//        
//        if (result.getStatus() == ITestResult.FAILURE) {
//            ExtentReportManager.getTest().fail("Test Case FAILED: " + result.getThrowable());
//        } else if (result.getStatus() == ITestResult.SUCCESS) {
//            ExtentReportManager.getTest().pass("Test Case PASSED successfully.");
//        } else if (result.getStatus() == ITestResult.SKIP) {
//            ExtentReportManager.getTest().skip("Test Case SKIPPED.");
//        }
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//    
//    @AfterSuite
//    public void wrapUpReporting() {
//        ExtentReportManager.flushReports();
//    }
//}
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
    // FIX 1: REMOVED "protected WebDriver driver;" to prevent different threads from overwriting it!
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
        
        // Read the headless property text and convert it to a true/false boolean parameter
        boolean headlessFlag = Boolean.parseBoolean(creader.getString("browser.headless"));
        
        // Initialize factory engine using all three switches
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
        
        // FIX 4: Quit the unique browser instance belonging to this thread
        if (DriverFactory.getDriver() != null) {
            DriverFactory.getDriver().quit();
        }
        
        // FIX 5: Clean up the ThreadLocal memory slot to prevent memory leak issues
        DriverFactory.unloadDriver();
    }
    
    @AfterSuite
    public void wrapUpReporting() {
        ExtentReportManager.flushReports();
    }
}