package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckOutPage {
	
	WebDriver driver;
	
	@FindBy(id = "first-name") WebElement firstName;
	@FindBy(name = "lastName") WebElement lastName;
	@FindBy(xpath = "//input[@placeholder='Zip/Postal Code']") WebElement zipCode;
	@FindBy(id="continue") WebElement continueBtn;
	
	public CheckOutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterDetails(String firstName, String lastName, String ZipCode) {
		this.firstName.sendKeys(firstName);
		this.lastName.sendKeys(lastName);
		this.zipCode.sendKeys(ZipCode);
	}
	
	public void clickContinue() {
		continueBtn.click();
	}
}
