package base;

import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.MediaEntityBuilder;
import testUtilities.ConfigReader;
import testUtilities.ExtentReportManager; 

public class BaseTest {
    protected ConfigReader creader;
    public static final Logger log = LogManager.getLogger(BaseTest.class);
    
    @BeforeSuite
    public void setupReporting() {
        // Mutes the long, harmless Selenium CDP warnings from the console log output
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.SEVERE);
        
        log.info("Starting Test Suite execution.");
        ExtentReportManager.getInstance();
    }

    @BeforeMethod
    public void setUp(Method method) { 
        log.info("Setting up browser environment for test case: " + method.getName());
        
        creader = new ConfigReader("src/test/resources/config.properties");
        String browser = creader.getString("browser");
        String url = creader.getString("url");
        String mode = creader.getString("execution.mode");
        boolean headlessFlag = Boolean.parseBoolean(creader.getString("browser.headless"));
        
        log.info("Configurations -> Browser: " + browser + ", Mode: " + mode + ", Headless: " + headlessFlag);
        
        DriverFactory.initDriver(browser, mode, headlessFlag);
        
        log.info("Navigating to URL: " + url);
        DriverFactory.getDriver().get(url);
        
        ExtentReportManager.startTest(method.getName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) { 
        if (result.getStatus() == ITestResult.FAILURE) {
            
            // Check if TestNG is bypassing this failure for an upcoming RetryAnalyzer attempt
            if (result.wasRetried()) {
                log.warn("Test encountered a problem but is set to retry. Skipping screenshot capture for now.");
                ExtentReportManager.getTest().warning("Test case encountered an issue. Triggering retry logic...");
            } else {
                // This block runs ONLY when the test officially fails permanently
                log.error("Test Case FAILED permanently: " + result.getName());
                log.error("Exception thrown: ", result.getThrowable());
                
                // 1. Log failure text inside the report
                ExtentReportManager.getTest().fail("Test Case FAILED: " + result.getThrowable());
                
                // 2. Take a screenshot and add it to the report
                String base64Screenshot = captureScreenshotToBase64();
                if (base64Screenshot != null) {
                    ExtentReportManager.getTest().fail(
                        "Failure Screenshot:", 
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
                    );
                    log.info("Screenshot successfully attached to the Extent Report log entry.");
                }
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("Test Case PASSED: " + result.getName());
            ExtentReportManager.getTest().pass("Test Case PASSED successfully.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("Test Case SKIPPED: " + result.getName());
            ExtentReportManager.getTest().skip("Test Case SKIPPED.");
        }
        
        if (DriverFactory.getDriver() != null) {
            log.info("Closing browser driver instance.");
            DriverFactory.getDriver().quit();
        }
        
        DriverFactory.unloadDriver();
    }
    
    @AfterSuite
    public void wrapUpReporting() {
        log.info("Writing all final test logs and compiling the Extent Report HTML file.");
        ExtentReportManager.flushReport();
        log.info("Suite execution completed successfully.");
    }
    
    /**
     * Captures a screenshot of the browser window and converts it to a Base64 string.
     */
    public String captureScreenshotToBase64() {
        try {
            log.info("Capturing screenshot from the active browser thread.");
            TakesScreenshot ts = (TakesScreenshot) DriverFactory.getDriver();
            return ts.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture browser screenshot: " + e.getMessage());
            return null;
        }
    }
}