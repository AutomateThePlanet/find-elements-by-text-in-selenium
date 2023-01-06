import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FindElementsByTextTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private ChromeDriver driver;
    private WebDriverWait webDriverWait;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
    }

    @Test
    public void findElementByCompleteTextMatch() {
        driver.get("https://www.lambdatest.com/selenium-playground/");

        WebElement checkBoxDemoPage = driver.findElement(By.xpath("//a[text()='Checkbox Demo']"));
        checkBoxDemoPage.click();

        WebElement header = driver.findElement(By.xpath("//h1"));
        Assertions.assertEquals("Checkbox Demo", header.getText());
    }

    @Test
    public void findElementsByCompleteTextMatch() {
        driver.get("https://www.lambdatest.com/selenium-playground/");

        List<WebElement> tableOptions = driver.findElements(By.xpath("//a[contains(text(),'Table')]"));
        for(var element: tableOptions){
            System.out.println("The different options with table in name are:" + element.getText());
        }
    }

    @Test
    public void findElementsByText_treeListCheckboxSection_nameIsChristianPalmer() {
        driver.get("https://demos.telerik.com/kendo-ui/treelist/checkbox-selection");

        driver.manage().addCookie(new Cookie("OptanonAlertBoxClosed", ""));
        driver.navigate().refresh();
        var tableRow = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='Christian Palmer']/parent::tr")));
        var checkbox = tableRow.findElement(By.xpath(".//td/input"));
        checkbox.click();

        Assertions.assertTrue(checkbox.isSelected());
    }

    @Test
    public void findElementsByText_treeListCheckboxSection_allQAs() {
        driver.get("https://demos.telerik.com/kendo-ui/treelist/checkbox-selection");

        driver.manage().addCookie(new Cookie("OptanonAlertBoxClosed", ""));
        driver.navigate().refresh();

        var checkboxes = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//td[contains(text(), 'QA')]/parent::tr/td/input")));
        checkboxes.stream().forEach(e -> e.click());

        checkboxes.stream().forEach(c -> Assertions.assertTrue(c.isSelected()));
    }

    @Test
    @SneakyThrows
    public void findElementsByText_grid() {
        driver.get("https://demos.telerik.com/kendo-ui/grid/index");

        driver.manage().addCookie(new Cookie("OptanonAlertBoxClosed", ""));
        driver.navigate().refresh();

        var categories = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//p[contains(text(), 'Category:')]")));

        // Integer.parse is not working
        NumberFormat.getNumberInstance(java.util.Locale.US);
        var categoriesPrices = categories.stream().filter(e -> {
            try {
                return NumberFormat.getNumberInstance(java.util.Locale.US).parse(e.getText().split(Pattern.quote("$"))[1]).intValue() > 27300;
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }).map(e -> e.getText()).collect(Collectors.toList());
        for (var currentCategoryPrice:categoriesPrices) {
            String currentCategoryExpand = String.format("//p[contains(text(), '%s')]/a", currentCategoryPrice);
            var expand = driver.findElement(By.xpath(currentCategoryExpand));
            expand.click();
        }

        var rowsToMark = driver.findElements(By.xpath("//div[@class='product-name' and starts-with(text(), 'T')]/parent::td/preceding-sibling::td[@class='checkbox-align']/input"));
        rowsToMark.stream().forEach(e -> {
            if (e.isDisplayed()) {
                e.click();
            }
        });

        rowsToMark.stream().forEach(c -> {
                    if (c.isDisplayed()) {
                        Assertions.assertTrue(c.isSelected());
                    }
                });
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}