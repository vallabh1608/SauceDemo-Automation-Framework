package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProductDetailsPage {
	
	WebDriver driver;
	
	@FindBy(className = "inventory_details_name") WebElement productName;
	@FindBy(xpath = "//div[@class='inventory_details_desc large_size' and @data-test='inventory-item-desc']") WebElement productDesc;
	@FindBy(className = "inventory_details_price") WebElement productPrice;
	@FindBy(id="add-to-cart") WebElement addToCart;
	@FindBy(name = "back-to-products") WebElement backToProductsBtn;

	public ProductDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public String getProductName() {return productName.getText();};
	public String getProductDesc() {return productDesc.getText();};
	public String getproductPrice() {return productPrice.getText();};
	
	public void clickOnAddToCart() {
		addToCart.click();
	}
	public void ClickOnBackToProductsBtn() {
		backToProductsBtn.click();
	}
}
