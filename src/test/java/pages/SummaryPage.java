package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SummaryPage {
	
	WebDriver driver;
	
	@FindBy(className = "summary_subtotal_label") WebElement itemTotalAmt;
	@FindBy(className = "summary_tax_label") WebElement taxAmt;
	@FindBy(className = "summary_total_label") WebElement finalTotalAmt;
	@FindBy(id="finish") WebElement finishBtn;
	@FindBy(className = "complete-header") WebElement confirmationMsg;
	
	
	public SummaryPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public double getSubTotalAmt() {
		return Double.parseDouble(itemTotalAmt.getText().replace("Item total: $", ""));
	}
	public double getTaxAmt() {
		return Double.parseDouble(taxAmt.getText().replace("Tax: $", ""));
	}
	public double getFinalTotalAmt() {
		return Double.parseDouble(finalTotalAmt.getText().replace("Total: $", ""));
	}
	public void clickFinish() {
		finishBtn.click();
	}
	public String getConfirmationMsg() {
		return confirmationMsg.getText();
	}
	
}
