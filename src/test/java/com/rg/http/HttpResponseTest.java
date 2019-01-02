package com.rg.http;

import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class HttpResponseTest{

    public static WebDriver driver;
    public static ServerSocket serverSocket;

    public Socket socket;
    public HttpRequest request;
    public HttpResponse response;

    public static boolean ready;

    public static final String PROXY = "localhost:8080";

    public static final String HTML = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width\">\n" +
            "  <title>JS Bin</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "HELLO\n";

    @BeforeClass
    public static void initClass() throws IOException {
        serverSocket = new ServerSocket(8080);

        new Thread(()-> {
            System.setProperty("webdriver.chrome.driver", new File("src/test/resources/chromedriver.exe")
                    .getAbsolutePath());
            ChromeOptions option = new ChromeOptions();
            option.addArguments("--proxy-server=com.rg.config.http://" + PROXY);
            driver = new ChromeDriver(option);
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

            while(true){
                if(ready) {
                    try{
                        driver.get("com.rg.config.http://www.SocialNetwork-1.0.com");
                    }catch (TimeoutException e){
                        System.out.println("TIMEOUT");
                    }
                }
            }
        }).start();
    }

    @Before
    public void init() throws IOException, HttpParser.WrongRequestException {
        if(socket != null) {
            close();
        }

        ready = true;

        while(ready) {
            try {
                socket = serverSocket.accept();
                request = HttpParser.parse(socket.getInputStream());
                response = new HttpResponse(socket.getOutputStream(), request.getRequestLine());
                ready = false;
            } catch (NullPointerException e) {
                System.out.println("Selenium can't create correct request");
                close();
            }
        }
    }

    @Test
    public void sendResponseTest() throws IOException, HttpParser.WrongRequestException, InterruptedException {
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", "text/html");
        response.getContentOutputStream().write(HTML.getBytes());
        response.flush();
    }

    @After
    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
