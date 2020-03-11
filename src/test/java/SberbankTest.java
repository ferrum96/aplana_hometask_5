import org.assertj.core.api.SoftAssertions;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SberbankTest {

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {

        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://www.sberbank.ru/ru/person/credits/home/buying_complete_house";
        driver.get(baseUrl);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
    }

    @Test
    public void Test() throws Exception {

        String ipotekaBtnXpath = "//button[@aria-label='Меню Ипотека']";
        String ipotekaLinkXpath = "//a[contains(text(),'Ипотека на готовое жильё')]";
        String frameXpath = "//iframe[@src='https://ipoteka.domclick.ru/calc-reg/calculator.html?prod=3']";

        waitClickable(By.xpath("//a[@class='cookie-warning__close']"),10);
        WebElement closeCookie = driver.findElement(By.xpath("//a[@class='cookie-warning__close']"));
        closeCookie.click();

        Actions ipotekaBtn = new Actions(driver);
        ipotekaBtn.moveToElement(driver.findElement(By.xpath(ipotekaBtnXpath))).build().perform();

        Actions ipotekaLink = new Actions(driver);
        ipotekaLink.moveToElement(driver.findElement(By.xpath(ipotekaLinkXpath))).click().perform();

        waitPresence(By.xpath("//div[contains(@data-pid, 'Iframe')]/parent::div"),3);
        scrollToElement(By.xpath("//div[contains(@data-pid, 'Iframe')]/parent::div"),160);

        waitPresence(By.xpath(frameXpath),5);
        WebElement frame = driver.findElement(By.xpath(frameXpath));
        driver.switchTo().frame(frame);

        driver.manage().timeouts().pageLoadTimeout(5,SECONDS);

        enterTextInput(driver.findElement(By.id("estateCost")), "5180000");
        enterTextInput(driver.findElement(By.id("initialFee")), "3058000");
        enterTextInput(driver.findElement(By.id("creditTerm")), "30");

        clickOnInvisibleElement(By.xpath("//input[@data-test-id=\"youngFamilyDiscount\"]"));
        clickOnInvisibleElement(By.xpath("//input[@data-test-id=\"paidToCard\"]"));
        clickOnInvisibleElement(By.xpath("//input[@data-test-id=\"canConfirmIncome\"]"));

        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"amountOfCredit\"]")).getText()).isEqualTo("2 122 000 \u20BD");
                    softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"monthlyPayment\"]")).getText()).isEqualTo("17 998 \u20BD");
                    softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"requiredIncome\"]")).getText()).isEqualTo("29 997 \u20BD");
                    softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"rate\"]")).getText()).isEqualTo("9,6 %");
                });

        clickOnInvisibleElement(By.xpath("//input[@data-test-id=\"canConfirmIncome\"]"));

        SoftAssertions.assertSoftly(softly ->{
            softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"amountOfCredit\"]")).getText()).isEqualTo("2 122 000 \u20BD");
            softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"monthlyPayment\"]")).getText()).isEqualTo("17 535 \u20BD");
            softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"requiredIncome\"]")).getText()).isEqualTo("29 224 \u20BD");
            softly.assertThat(driver.findElement(By.xpath("//span[@data-test-id=\"rate\"]")).getText()).isEqualTo("9,4 %");
        });

    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    private void waitPresence(By locator, long timeout){
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void waitClickable(By locator, long timeout){
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void scrollToElement(By locator) {
        WebElement el = driver.findElement(locator);
        int y = el.getLocation().getY();
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollTo(0,"+y+")");
    }

    private void scrollToElement(By locator, int offset) {
        WebElement el = driver.findElement(locator);
        int y = el.getLocation().getY() + offset;
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollTo(0,"+y+")");
    }

    private static void enterTextInput(WebElement element, String value) throws InterruptedException{
        String val = value;
        element.clear();
        Thread.sleep(1500);

        for (int i = 0; i < val.length(); i++){
            char c = val.charAt(i);
            String str = new StringBuilder().append(c).toString();
            element.sendKeys(str);
        }
        Thread.sleep(1000);
    }

    private void clickOnInvisibleElement(By location) throws InterruptedException {

        WebElement element = driver.findElement(location);
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
        Thread.sleep(1000);

    }
}
