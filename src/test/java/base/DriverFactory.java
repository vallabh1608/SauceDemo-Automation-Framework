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
            ChromeOptions chromeOptions = new ChromeOptions();
            if (isHeadless) {
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--disable-gpu");
            }

            EdgeOptions edgeOptions = new EdgeOptions();
            if (isHeadless) {
                edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--disable-gpu");
            }

            FirefoxOptions firefoxOptions = new FirefoxOptions();
            if (isHeadless) {
                firefoxOptions.addArguments("--headless");
            }

            if (mode.equalsIgnoreCase("grid")) {
                System.out.println("Routing thread sandbox safely to Selenium Grid Hub...");
                if (browser.equalsIgnoreCase("chrome")) {
                    driver = new RemoteWebDriver(new URL(gridUrl), chromeOptions);
                } else if (browser.equalsIgnoreCase("edge")) {
                    driver = new RemoteWebDriver(new URL(gridUrl), edgeOptions);
                } else {
                    driver = new RemoteWebDriver(new URL(gridUrl), firefoxOptions);
                }
            } 
            else {
                System.out.println("Direct local framework runtime execution initialized...");
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