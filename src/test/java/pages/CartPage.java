package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CartPage {
	
	WebDriver driver;

	@FindBy(xpath = "//div[@class='inventory_item_name']") List<WebElement> productNames;
	@FindBy(xpath = "//button[@id='checkout' and @name='checkout']") WebElement checkOutBtn;
	@FindBy(className = "cart_item") List<WebElement> cartItems;
	@FindBy(className = "inventory_item_price") List<WebElement> cartItemPrices;
	@FindBy(id="remove-sauce-labs-backpack")  List<WebElement> cartRemoveBtns;
	
	public CartPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public List<String> getProductNames(){
		List<String> names = new ArrayList<String>();
		for(WebElement el : productNames) {
			names.add(el.getText());
		}
		return names;
	}
	
	public void clickCheckOut() {
		checkOutBtn.click();
	}
	
	public int getCartItemCount() {
		return cartItems.size();
	}
	public void removeCartItemByIndex(int index) {
		cartRemoveBtns.get(index).click();
	}

	public String getCartItemPriceByIndex(int index) {
	    return cartItemPrices.get(index).getText();
	}
	
}
