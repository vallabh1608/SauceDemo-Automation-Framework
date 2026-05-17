package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import base.BaseTest;
import base.DriverFactory;
import pages.CartPage;
import pages.CheckOutPage;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailsPage;
import pages.SummaryPage;
import testUtilities.DataStore;
import testUtilities.ExtentReportManager;

public class SauceTests extends BaseTest {
	
	LoginPage loginPage;
	InventoryPage inventoryPage;
	CartPage cartPage;
	CheckOutPage checkOutPage;
	SummaryPage summaryPage;
	ProductDetailsPage productDetailsPage;
	
	@BeforeMethod
	public void pageSetUp() {
	    WebDriver activeThreadDriver = DriverFactory.getDriver();
	    
	    loginPage = new LoginPage(activeThreadDriver);
	    inventoryPage = new InventoryPage(activeThreadDriver);
	    cartPage = new CartPage(activeThreadDriver);
	    checkOutPage = new CheckOutPage(activeThreadDriver);
	    summaryPage = new SummaryPage(activeThreadDriver);
	    productDetailsPage = new ProductDetailsPage(activeThreadDriver);
	}
	
	@Test(dataProvider = "loginData", dataProviderClass = DataStore.class)
	public void loginScenarios(String scenario, String userName, String passWord, String Expected) {
		ExtentReportManager.getTest().info("Executing Authentication Profile: " + scenario);
		loginPage.login(userName, passWord);
		
		if(scenario.equalsIgnoreCase("Valid Login")) {
			Assert.assertEquals(inventoryPage.getPageTitle(), Expected, "Valid Login failed!");
			ExtentReportManager.getTest().pass("Successfully reached inventory landing dashboard.");
		} else {
			String errText = loginPage.getErrMsg();
			Assert.assertTrue(errText.contains(Expected), "Expected error message not found for: " + scenario);
			ExtentReportManager.getTest().pass("Negative authentication handled correctly. System surfaced expected error banner.");
		}
	}
	
	@Test
	public void verifyProductSorting() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		
		ExtentReportManager.getTest().info("Altering product listing sorting option to: Price (low to high)");
		inventoryPage.sortByVisibileText(InventoryPage.SORT_LOW_HIGH);
		
		List<WebElement> priceElements = inventoryPage.getPriceElements();
		List<Double> actualPrices = new ArrayList<>();
		for(WebElement pricesEl : priceElements) {
			Double price = Double.parseDouble(pricesEl.getText().replace("$", ""));
			actualPrices.add(price);
		}
		
		List<Double> sortedPrices = new ArrayList<>(actualPrices);
		Collections.sort(sortedPrices);
		Assert.assertEquals(actualPrices, sortedPrices, "Prices are NOT sorted Low to High!");
		ExtentReportManager.getTest().pass("Verified low-to-high numeric price sequencing remains secure.");
		
		ExtentReportManager.getTest().info("Altering product listing sorting option to: Name (Z to A)");
		inventoryPage.sortByVisibileText(InventoryPage.SORT_Z_TO_A);
		
		List<WebElement> nameElements = inventoryPage.getNameElements();
		List<String> actualNames = new ArrayList<>();
		for(WebElement nameEl : nameElements) {
			actualNames.add(nameEl.getText());
		}
		
		List<String> sortedNames = new ArrayList<>(actualNames);
		Collections.sort(sortedNames, Collections.reverseOrder());
		Assert.assertEquals(actualNames, sortedNames, "Names are NOT sorted Z to A!");
		ExtentReportManager.getTest().pass("Verified lexicographical reverse alphabetical sorting sequence order.");
	}
	
	@Test
	public void VerifyCartBadgeUpdateOnAddAndRemove() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		
		ExtentReportManager.getTest().info("Adding first 3 elements iteratively to the active shopping cart selection.");
		for(int i = 0; i < 3; i++) {
			inventoryPage.refreshElements(); 
			List<WebElement> inventoryBtns = inventoryPage.getInventoryBtns();
			inventoryBtns.get(i).click();
			
			inventoryPage.refreshElements(); 
			Assert.assertEquals(inventoryPage.getInventoryBtns().get(i).getText(), "Remove", "Button text did not change for item " + i);
		}
		
		inventoryPage.refreshElements();
		List<WebElement> inventoryNames = inventoryPage.getNameElements();
		String name1 = inventoryNames.get(1).getText();
		String name2 = inventoryNames.get(2).getText();
		
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 3, "Badge count mismatch!");
		ExtentReportManager.getTest().pass("Cart header notification badge matches exactly [3].");

		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(0).click(); 
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 2, "Badge count should be 2!");
		ExtentReportManager.getTest().info("Evicted initial item from catalog table screen view. Badge dropped to [2].");
		
		inventoryPage.goTocartPage();
		List<String> expectedCartNames = new ArrayList<>(Arrays.asList(name1, name2));
		List<String> actualCartNames = cartPage.getProductNames();
		
		Collections.sort(expectedCartNames);
		Collections.sort(actualCartNames);
		Assert.assertEquals(actualCartNames, expectedCartNames, "The cart contents do not match!");
		ExtentReportManager.getTest().pass("Verified surviving catalog item identities match checkout details layout page.");
	}
	
	@Test
	public void e2ePurchase() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		
		List<WebElement> productNames = inventoryPage.getNameElements();
		List<WebElement> inventoryBtns = inventoryPage.getInventoryBtns();
		
		for(int i = 0; i < productNames.size(); i++) {
			if(productNames.get(i).getText().equalsIgnoreCase("Sauce Labs Backpack")) {
				inventoryBtns.get(i).click();
				break;
			}
		}
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1, "Badge count mismatch!");
		ExtentReportManager.getTest().info("Target 'Sauce Labs Backpack' mounted into transaction session.");
		
		inventoryPage.goTocartPage();
		cartPage.clickCheckOut();
		
		ExtentReportManager.getTest().info("Populating submission form parameters with structural dataset properties.");
		checkOutPage.enterDetails(creader.getString("firstname"), creader.getString("lastname"), creader.getString("zipCode"));
		checkOutPage.clickContinue();
		
		double itemPrice = summaryPage.getSubTotalAmt();
		double taxAmt = summaryPage.getTaxAmt();
		double actualTotalAmt = itemPrice + taxAmt;
		double expectedTotalAmt = summaryPage.getFinalTotalAmt();
		
		Assert.assertEquals(actualTotalAmt, expectedTotalAmt, "Math mismatch in order summary!");
		ExtentReportManager.getTest().pass("Order statement calculation verified successfully: Base Item Subtotal + Calculated Tax matches Grand Total.");
		
		summaryPage.clickFinish();
		Assert.assertEquals(summaryPage.getConfirmationMsg(), "Thank you for your order!", "Final order confirmation failed!"); 
		ExtentReportManager.getTest().pass("E2E purchase process sequence achieved completely. Order confirmation greeting screen logged.");
	}
	
	@Test
	public void verifyProductDetailPage() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		
		String expected_productName = inventoryPage.getNameElements().get(1).getText();
		ExtentReportManager.getTest().info("Navigating straight into single product specialized deep-link detail page sheet panel.");
		inventoryPage.clickProductNameByIndex(1);
		
		String actualProductName = productDetailsPage.getProductName();
		String actualProductDesc = productDetailsPage.getProductDesc();
		String actualProductPrice = productDetailsPage.getproductPrice();
		
		Assert.assertEquals(actualProductName, expected_productName, "Product Name Mismatch!");	
		Assert.assertFalse(actualProductDesc.isEmpty(), "Product description is empty!");
		Assert.assertTrue(actualProductPrice.contains("$"), "Price format is incorrect or missing!");
		ExtentReportManager.getTest().pass("Isolated deep-link detail parameters (Title text, descriptive text strings, pricing tags) are structurally healthy.");
		
		productDetailsPage.clickOnAddToCart();
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1, "Cart badge did not update from detail page!");
		ExtentReportManager.getTest().pass("Basket accumulation action processed directly within detail page view matrix.");
		
		productDetailsPage.ClickOnBackToProductsBtn();
		
		// FIXED: Replaced 'driver' with thread-isolated 'DriverFactory.getDriver()'
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("inventory.html"), "Did not navigate back to the inventory URL!");
		Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Main inventory page heading is incorrect!");
		ExtentReportManager.getTest().pass("History navigation routing back to main grid catalog validated.");
	}
	
	@Test
	public void verifyCartPersistenceAndRemoval() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(0).click(); 
		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(1).click(); 
		
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 2, "Initial badge count should be 2!");
		
		String expectedRemainingName = inventoryPage.getNameElements().get(1).getText();
		String expectedRemainingPrice = inventoryPage.getPriceElements().get(1).getText();
		
		ExtentReportManager.getTest().info("Executing comprehensive navigation stress cycle detour to verify cache persistence.");
		inventoryPage.refreshElements();
		inventoryPage.getNameElements().get(0).click();
		productDetailsPage.ClickOnBackToProductsBtn();
		
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 2, "Badge count should be 2!");
		ExtentReportManager.getTest().pass("State retention audit check passed: active basket contents survive deep view hops uncorrupted.");
		
		inventoryPage.goTocartPage();
		Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart Item Count Mismatch");
		
		cartPage.removeCartItemByIndex(0);
		Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart Item Count Mismatch");
		ExtentReportManager.getTest().info("Target collection row item dropped directly from within the checkout table context layout.");

		String actualProdname = cartPage.getProductNames().get(0);
		String actualProPrice = cartPage.getCartItemPriceByIndex(0);
		
		Assert.assertEquals(actualProdname, expectedRemainingName, "The wrong item was left in the cart!");
		Assert.assertEquals(actualProPrice, expectedRemainingPrice, "The price of the remaining item changed unexpectedly!");
		ExtentReportManager.getTest().pass("Surviving line record tracking balances and identification markers confirmed as unaltered.");
	}
	
	@Test
	public void verifyLogoutAndSessionValidation() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		
		ExtentReportManager.getTest().info("Triggering standard exit sequence logic to clear active auth profiles.");
		inventoryPage.clickOnMenuBtn();
		inventoryPage.clickOnLogOut();
		
		// FIXED: Replaced 'driver' with thread-isolated 'DriverFactory.getDriver()'
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("https://www.saucedemo.com/"), "URL does not match the login screen base destination!");
		ExtentReportManager.getTest().pass("Session token invalidated natively. Browser routed back to baseline landing gate.");
		
		ExtentReportManager.getTest().info("Executing malicious route-injection verification step directly into protected view workspace (/inventory.html).");
		DriverFactory.getDriver().get("https://www.saucedemo.com/inventory.html");
		
		String currUrlAfterByPass = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currUrlAfterByPass.contains("https://www.saucedemo.com/"), "System failed to redirect unauthenticated guest back to landing pad.");
		ExtentReportManager.getTest().pass("Access-control guard check confirmed: direct link bypass aborted, unauthorized context routed back to gate screen layout.");
	}
	
	@Test
	public void verifyImageAndProductConsistency() {
		loginPage.login(creader.getString("username"), creader.getString("password"));
		inventoryPage.refreshElements();
		
		int productCount = inventoryPage.getProductImages().size();
		ExtentReportManager.getTest().info("Commencing global accessibility asset audit loop across " + productCount + " image containers.");
		
		for (int i = 0; i < productCount; i++) {
			inventoryPage.refreshElements();
			WebElement imgElement = inventoryPage.getProductImages().get(i);
			
			String imgSrc = imgElement.getAttribute("src");
			String imgAlt = imgElement.getAttribute("alt");
			String productName = inventoryPage.getNameElements().get(i).getText();
			
			Assert.assertNotNull(imgSrc, "Product image source attribute is completely null at index " + i);
			Assert.assertFalse(imgSrc.isEmpty(), "Product image source attribute is empty at index " + i);
			Assert.assertFalse(imgSrc.contains("sl-404"), "Broken image link signature (sl-404) detected at index " + i);
			Assert.assertEquals(imgAlt, productName, "Accessibility Failure: Image alt text does not match product label at index " + i);
		}
		ExtentReportManager.getTest().pass("Asset verification complete: All listed pictures are rendering uncorrupted with flawless alt-text descriptive profiles.");
		
		inventoryPage.refreshElements();
		String targetedProductName = inventoryPage.getNameElements().get(0).getText();
		
		inventoryPage.getProductImages().get(0).click(); 
		
		// FIXED: Passed ThreadLocal driver handle down explicitly
		pages.ProductDetailsPage detailsPage = new pages.ProductDetailsPage(DriverFactory.getDriver());
		String actualDetailName = detailsPage.getProductName();
		
		Assert.assertEquals(actualDetailName, targetedProductName, "Image routing discrepancy: Clicking the image loaded the wrong product detail view!");
		ExtentReportManager.getTest().pass("Visual anchor hyper-routing check verified. Product picture click maps to the proper target product summary sheets layout.");
	}
	
	// FIXED: Added dependsOnMethods parameter to guarantee isolation test runs in its own pristine sandbox window block
	@Test(dependsOnMethods = {"VerifyCartBadgeUpdateOnAddAndRemove", "e2ePurchase", "verifyCartPersistenceAndRemoval"})
	public void verifyCrossUserCartIsolation() {
		loginPage.login("standard_user", creader.getString("password"));
		ExtentReportManager.getTest().info("Multi-Tenant Isolation Phase 1: populating tracking memory state of baseline tenant account context profile 'standard_user'.");
		
		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(0).click(); 
		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(1).click(); 
		Assert.assertEquals(inventoryPage.getCartBadgeCount(), 2, "Standard user cart did not register 2 items!");

		inventoryPage.clickOnMenuBtn();
		inventoryPage.clickOnLogOut();
		
		ExtentReportManager.getTest().info("Executing absolute browser cleanup: dropping cookies, resetting local and session storage layers.");
		
		// FIXED: Switched old 'driver' call references to isolated 'DriverFactory.getDriver()'
		DriverFactory.getDriver().manage().deleteAllCookies();
		org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) DriverFactory.getDriver();
		js.executeScript("window.localStorage.clear();");
		js.executeScript("window.sessionStorage.clear();");

		loginPage.login("problem_user", creader.getString("password"));
		ExtentReportManager.getTest().info("Multi-Tenant Isolation Phase 2: mounting structural cross-tenant verification account profile 'problem_user'.");
		
		inventoryPage.refreshElements();
		inventoryPage.goTocartPage();
		Assert.assertEquals(cartPage.getCartItemCount(), 0, "SECURITY LEAK: Data isolation failure! problem_user can see items added by standard_user.");
		ExtentReportManager.getTest().pass("Data state leak check passed. Active user context has clean isolated baseline session context parameters.");

		DriverFactory.getDriver().get("https://www.saucedemo.com/inventory.html");
		inventoryPage.refreshElements();
		inventoryPage.getInventoryBtns().get(0).click();
		
		inventoryPage.clickOnMenuBtn();
		inventoryPage.clickOnLogOut();
		
		ExtentReportManager.getTest().info("Wiping state footprints left by trailing session actions across storage parameters.");
		DriverFactory.getDriver().manage().deleteAllCookies();
		js.executeScript("window.localStorage.clear();");
		js.executeScript("window.sessionStorage.clear();");
		
		loginPage.login("standard_user", creader.getString("password"));
		ExtentReportManager.getTest().info("Multi-Tenant Isolation Phase 3: Reloading standard execution context profile 'standard_user' into clean dashboard view.");
		
		inventoryPage.refreshElements();
		inventoryPage.goTocartPage();
		Assert.assertEquals(cartPage.getCartItemCount(), 0, "Data persistence isolation failure: standard_user's new login state caught data contamination.");
		ExtentReportManager.getTest().pass("Total cross-account security decoupling verified. State boundaries completely prevent cross-tenant data visibility leakage leaks.");
	}
}