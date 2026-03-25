package es.mjusticia;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Selenium {

    private WebDriver driver;

    public void iniciarNavegador(String navegador, String driverPath, String urlInicial) {
        cerrarNavegador(); // por si hubiera uno previo
        try {
            if ("Chrome".equalsIgnoreCase(navegador)) {
                System.setProperty("webdriver.chrome.driver", driverPath);
                driver = new ChromeDriver();
            } else if ("Firefox".equalsIgnoreCase(navegador)) {
                System.setProperty("webdriver.gecko.driver", driverPath);
                driver = new FirefoxDriver();
            } else {
                throw new IllegalArgumentException("Navegador no soportado: " + navegador);
            }
            driver.manage().window().maximize();
            driver.get(urlInicial);
        } catch (Exception e) {
            cerrarNavegador();
            throw new RuntimeException("Error iniciando navegador: " + e.getMessage(), e);
        }
    }

    public void cerrarNavegador() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }

    public void capturarPantalla(File destinoPng) {
        if (driver == null) throw new IllegalStateException("El navegador no está iniciado");
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(src.toPath(), destinoPng.toPath());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la captura: " + e.getMessage(), e);
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
