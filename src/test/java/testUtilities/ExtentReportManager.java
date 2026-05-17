package testUtilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testTracker = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
            
            File reportDir = new File(System.getProperty("user.dir") + "/reports");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            // FIX: Change ExtentHtmlReporter to ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure the look and feel using the Spark configuration engine
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("SauceDemo Automation Project");
            sparkReporter.config().setReportName("E2E Regression Execution Status");
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter); // Attached the Spark reporter
            
            extent.setSystemInfo("Automation Engineer", "Vallabh");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extent;
    }

    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        testTracker.set(test);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        return testTracker.get();
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}