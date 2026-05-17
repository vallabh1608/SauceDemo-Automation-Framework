//package base;
//
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.edge.EdgeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//
//public class DriverFactory 
//{	
//	static WebDriver driver ;
//	public static WebDriver initDriver(String browser)
//	{
//		if (browser.equalsIgnoreCase("chrome")) {
//            driver = new ChromeDriver();
//		}else if(browser.equalsIgnoreCase("edge")) {
//			driver = new EdgeDriver();
//		}else {
//			driver = new FirefoxDriver();
//		}
//		driver.manage().window().maximize();
//		return driver;
//	}
//	
//	
//without grid
//}
//package base;
//
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.edge.EdgeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//
//public class DriverFactory {	
//    // FIX: Wrap the driver in a ThreadLocal container for parallel execution safety
//    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
//
//    public static WebDriver initDriver(String browser) {
//        WebDriver driver;
//        
//        if (browser.equalsIgnoreCase("chrome")) {
//            driver = new ChromeDriver();
//        } else if(browser.equalsIgnoreCase("edge")) {
//            driver = new EdgeDriver();
//        } else {
//            driver = new FirefoxDriver();
//        }
//        
//        driver.manage().window().maximize();
//        tlDriver.set(driver); // Store the driver instance safely inside this specific thread
//        return getDriver();
//    }
//    
//    // Helper method to retrieve the correct driver instance for the active thread
//    public static synchronized WebDriver getDriver() {
//        return tlDriver.get();
//    }
//
//    // Helper method to remove the driver instance from memory after cleanup
//    public static void unloadDriver() {
//        tlDriver.remove();
//    }
//}
//with grid

//package base;
//
//import java.net.URL;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.edge.EdgeOptions;
//import org.openqa.selenium.firefox.FirefoxOptions;
//import org.openqa.selenium.remote.RemoteWebDriver;
//
//public class DriverFactory {	
//    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
//
//    public static WebDriver initDriver(String browser) {
//        WebDriver driver = null;
//        // The network address pointing directly to your active Selenium Grid Hub router
//        String gridUrl = "http://localhost:4444/wd/hub";
//        
//        try {
//            if (browser.equalsIgnoreCase("chrome")) {
//                ChromeOptions options = new ChromeOptions();
//                // You can add options here if needed, like options.addArguments("--headless");
//                driver = new RemoteWebDriver(new URL(gridUrl), options);
//                
//            } else if (browser.equalsIgnoreCase("edge")) {
//                EdgeOptions options = new EdgeOptions();
//                driver = new RemoteWebDriver(new URL(gridUrl), options);
//                
//            } else {
//                FirefoxOptions options = new FirefoxOptions();
//                driver = new RemoteWebDriver(new URL(gridUrl), options);
//            }
//        } catch (Exception e) {
//            System.out.println("❌ Critical Error: Failed to connect to the Selenium Grid Hub!");
//            e.printStackTrace();
//        }
//        
//        if (driver != null) {
//            driver.manage().window().maximize();
//            tlDriver.set(driver);
//        }
//        return getDriver();
//    }
//    
//    public static synchronized WebDriver getDriver() {
//        return tlDriver.get();
//    }
//
//    public static void unloadDriver() {
//        tlDriver.remove();
//    }
//}

//with 2 option grid or local
package base;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverFactory {	
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver initDriver(String browser, String mode, boolean isHeadless) {
        WebDriver driver = null;
        String gridUrl = "http://localhost:4444/wd/hub";
        
        try {
            // Configure Chrome Options
            ChromeOptions chromeOptions = new ChromeOptions();
            if (isHeadless) {
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--disable-gpu");
            }

            // Configure Edge Options
            EdgeOptions edgeOptions = new EdgeOptions();
            if (isHeadless) {
                edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--disable-gpu");
            }

            // Configure Firefox Options
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            if (isHeadless) {
                firefoxOptions.addArguments("--headless");
            }

            // CHOICE 1: RUN ON THE SELENIUM GRID
            if (mode.equalsIgnoreCase("grid")) {
                System.out.println("🌐 Routing thread sandbox safely to Selenium Grid Hub...");
                if (browser.equalsIgnoreCase("chrome")) {
                    driver = new RemoteWebDriver(new URL(gridUrl), chromeOptions);
                } else if (browser.equalsIgnoreCase("edge")) {
                    driver = new RemoteWebDriver(new URL(gridUrl), edgeOptions);
                } else {
                    driver = new RemoteWebDriver(new URL(gridUrl), firefoxOptions);
                }
            } 
            // CHOICE 2: RUN LOCALLY ON DESKTOP
            else {
                System.out.println("🖥️ Direct local framework runtime execution initialized...");
                if (browser.equalsIgnoreCase("chrome")) {
                    driver = new ChromeDriver(chromeOptions);
                } else if (browser.equalsIgnoreCase("edge")) {
                    driver = new EdgeDriver(edgeOptions);
                } else {
                    driver = new FirefoxDriver(firefoxOptions);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Factory Engine Error: Connection sequence refused!");
            e.printStackTrace();
        }
        
        if (driver != null) {
            driver.manage().window().maximize();
            tlDriver.set(driver);
        }
        return getDriver();
    }
    
    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void unloadDriver() {
        tlDriver.remove();
    }
}