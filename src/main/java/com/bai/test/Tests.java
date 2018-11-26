package com.bai.test;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.UUID;


public class Tests {
    public static void main(String[] args) throws IOException {

        String path = System.getProperty("user.dir");
        System.out.println(path);
        System.setProperty("webdriver.chrome.driver", path+"./src/main/java/com/bai/test/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:8080");

       // WebElement usernameElement     = driver.findElement(By.name("username"));
       // WebElement passwordElement     = driver.findElement(By.name("password"));
       // WebElement formElement        = driver.findElement(By.id("loginForm"));
       // usernameElement.sendKeys("user1");
       // passwordElement.sendKeys("user1");
       // formElement.submit();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/messages/add?messageText=testkodu");
        WebElement messageElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[3]"))
        );

        String message                 = messageElement.getText();
        String successMsg             = "Log in to add messages";
        Assert.assertEquals (message, successMsg);

        String uuid = UUID.randomUUID().toString();
        driver.get("http://localhost:8080/login/signin?username=user1&password=user1");
        driver.get("http://localhost:8080/messages/add?messageText="+uuid);

         messageElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/main/div[1]/table/tbody/tr[last()]/td[2]"))
        );

         message                 = messageElement.getText();
         successMsg             = uuid;
        Assert.assertEquals (message, successMsg);


        driver.get("http://localhost:8080/messages/add?messageText="+uuid);
        messageElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/main/div[1]/table/tbody/tr[last()]/td[2]"))
        );

        driver.quit();
    }
}
