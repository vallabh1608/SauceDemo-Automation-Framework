package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryPage {
	
	WebDriver driver;
	WebDriverWait wait;
	
	public static final String SORT_LOW_HIGH = "Price (low to high)";
	public static final String SORT_Z_TO_A = "Name (Z to A)";
	
	@FindBy(xpath = "//span[@class='title']") WebElement title;
	@FindBy(className = "product_sort_container") WebElement dropDown;
	@FindBy(className = "inventory_item_price") List<WebElement> inventoryPrices;
	@FindBy(className = "inventory_item_name") List<WebElement> inventoryNames;
	@FindBy(className = "btn_inventory") List<WebElement> inventoryBtns;
	@FindBy(xpath = "//span[@class='shopping_cart_badge']") WebElement cartBadge;
	@FindBy(className = "shopping_cart_link") WebElement cartLink;
	@FindBy(id = "react-burger-menu-btn") WebElement hamBurgerMenuIcon;
	@FindBy(id = "logout_sidebar_link") WebElement logOutOption;
	@FindBy(css = ".inventory_item_img img") List<WebElement> productImages;
	
	public InventoryPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		PageFactory.initElements(driver, this);
	}
	
	public String getPageTitle() {
		return title.getText();
	}
	
	public void sortByVisibileText(String text) {
		Select select = new Select(dropDown);
		select.selectByVisibleText(text);
	}
	
	public List<WebElement> getPriceElements(){
		return inventoryPrices;
	}
	
	public List<WebElement> getNameElements(){
		return inventoryNames;
	}
	
	public List<WebElement> getInventoryBtns(){
		return inventoryBtns;
	}
	public int getCartBadgeCount() {
		return Integer.parseInt(cartBadge.getText());
	}
	public void goTocartPage() {
		cartLink.click();
	}
	public void clickProductNameByIndex(int index) {
	    inventoryNames.get(index).click();
	}
	public void clickOnMenuBtn() {
		hamBurgerMenuIcon.click();
	}
	public void clickOnLogOut() {
		//logOutOption.click();
		wait.until(ExpectedConditions.elementToBeClickable(logOutOption)).click();
	}
	public List<WebElement> getProductImages() {
	    return productImages;
	}
	public void refreshElements() {
	    PageFactory.initElements(driver, this);
	}
	
}