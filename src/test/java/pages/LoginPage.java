package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
	WebDriver driver;
	
	@FindBy(id="user-name") WebElement userName;
	@FindBy(id="password") WebElement passWord;
	@FindBy(id="login-button") WebElement loginBtn;
	@FindBy(xpath = "//div[@class='error-message-container error']/h3") WebElement msg_error;
	
	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void login(String user, String pass) {
		userName.sendKeys(user);
		passWord.sendKeys(pass);
		loginBtn.click();
	}
	
	public String getErrMsg() {
		return msg_error.getText();
	}	
}