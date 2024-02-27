package com.webstaurant;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class StainlessTables {
    public static void main(String[] args) {
        WebDriver driver = WebDriverManager.chromedriver().create();
        driver.get("https://www.webstaurantstore.com/");
        System.out.println(driver.getTitle());

        driver.manage().window().maximize();

        WebElement searchBar = driver.findElement(By.id("searchval"));

        // Search for the tables
        searchBar.sendKeys("stainless work table");
        searchBar.submit();

        //Check all the pages for results missing the stainless work table text
        System.out.println(checkPage(driver) + " products are missing the 'table' text.");

        //adds the last item to the cart
        addLastItem(driver);

        //navigates to the cart page and clears the cart
        clearCart(driver);

        // Wait just to see the the page a bit before the script ends
         try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();
    }


    public static void clearCart(WebDriver driver) {
        // Use JS to scroll to the top of the page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0)");

        WebElement viewCartButton = driver.findElement(By.xpath("//a[text()='View Cart']"));
        viewCartButton.click();

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe("https://www.webstaurantstore.com/cart/"));
        
        WebElement emptyButton = driver.findElement(By.xpath("//button[text()='Empty Cart']"));
        emptyButton.click();

        WebElement confirmButton = driver.findElement(By.xpath("//button[text()='Empty']"));
        confirmButton.click();
    }


    public static void addLastItem(WebDriver driver) {
        List<WebElement> buttons = driver.findElements(By.xpath("//input[@data-testid='itemAddCart']"));

        // Get and click the last button from the list
        WebElement lastButton = buttons.get(buttons.size() - 1);
        lastButton.click();
    }


    public static int checkPage(WebDriver driver) {
        int pageNum = 2; //will contain the result page number
        int timesTextMissing = 0; //counter for the number of times the 'tables' text is missing

        while (pageNum > 1) {
        
            timesTextMissing += checkTables(driver);
            String resultNum;
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Scroll to the bottom of the page
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

            List<WebElement> searchResultPages = driver.findElements(By.xpath("//div[@data-testid='paging']"));
            // Iterate through each page number
            for (WebElement pResult : searchResultPages) {
                try {
                    WebElement childElement = pResult.findElement(By.cssSelector("a[aria-label*='page " + pageNum + "']"));
                     resultNum = pResult.getText();
                    if (resultNum.contains(String.valueOf(pageNum))) {
                        if (childElement.isDisplayed()){
                        childElement.click();
                        }
                        else {
                            pageNum = -1;
                            break;
                        }
                    }
                }
                catch (NoSuchElementException e) {
                    //System.out.println("Element does not exist.");
                    pageNum = 0;
                    break;
                }
            }
            pageNum++;
        }
        return timesTextMissing;
    }



    public static int checkTables(WebDriver driver) {
        //will store instances of missed 'table' text
        int tablesNotFound = 0;

        // Get all the search result itemDescriptions
        List<WebElement> searchResults = driver.findElements(By.xpath("//span[@data-testid='itemDescription']"));

        // Iterate through each result
            for (WebElement result : searchResults) {

            // Parse if the result contains "table"
            String resultText = result.getText().toLowerCase();
            if (resultText.contains("table")) {
        //         System.out.println("Found 'table' in result: " + resultText);
            } else {
                System.out.println("Did not find 'table' in the product: " + resultText);
                tablesNotFound++;
            }
        } 
        return tablesNotFound;
    }
}
