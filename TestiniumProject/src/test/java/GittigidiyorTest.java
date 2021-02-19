import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import java.util.Random;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GittigidiyorTest {

    public WebDriver driver; //Url gittigidiyor anasayfasına göre ayarlanır.
    final static Logger logger = Logger.getLogger(GittigidiyorTest.class);
    protected static String url = "https://www.gittigidiyor.com/";
    protected static String loginUrl = "https://www.gittigidiyor.com/uye-girisi";

    @Before
    public void setUp(){

        logger.info("Test başlatıldı.");  //Test başlama uyarısı
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe"); //chromedriver.exe uygun dosya konumuna yerleştirilir.
        driver = new ChromeDriver();
        driver.manage().window().maximize();  //Browser tam ekran moduna alınır.


    }

    @Test
    public void correctOpen() throws InterruptedException{   //Girilen URL title'ı ile istenilen Anasayfa title'ı karşılaştırılıp, doğruluğu kontrol edilir.
        Thread.sleep(10);
        driver.get(url);
        Assert.assertEquals(driver.getTitle(), "GittiGidiyor - Türkiye'nin Öncü Alışveriş Sitesi");
        logger.info("Anasayfaya giriş başarılı"); //

        driver.navigate().to(loginUrl);
        driver.findElement(By.id("L-UserNameField")).sendKeys("username");
        driver.findElement(By.id("L-PasswordField")).sendKeys("pw");
        driver.findElement(By.id("gg-login-enter")).click();
        Assert.assertEquals(driver.getCurrentUrl(),"https://www.gittigidiyor.com/");
        logger.info("Login işlemi başarılı");

        driver.findElement(By.cssSelector("#main-header > " +
                "div:nth-child(3) > div > div > div > div.sc-1nx8ums-0.fXQfgp > for" +
                "m > div > div.sc-1yew439-3.dkiUfE > div.sc-4995aq-4.dNPmGY > input")).sendKeys("bilgisayar");
        logger.info("Arama yerine istenilen kelime girildi"); //

        driver.findElement(By.xpath("//*[@id=\"main-header\"]/div[3]/div/div/div/div[2]/form/div/div[2]/button")).click();
        logger.info("Arama başarılı"); //Arama butonuna basıldı

        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)"); //Sayfanın en altına inildi.

        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"best-match-right\"]/div[5]/ul/li[2]/a")).click();
        Assert.assertEquals(driver.getCurrentUrl(),"https://www.gittigidiyor.com/arama/?k=bilgisayar&sf=2");
        logger.info("2.Sayfanın Açıldığı kontrol edildi."); //

        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)"); //Sayfanın en altına inildi.

        int count=1;
        Random rand = new Random();
        int int_random = rand.nextInt(48); // Ürün seçmek için Rastgele sayı 1 - 48(dahil) oluşturuldu.
        int_random+=1;

        WebElement productUL = driver.findElement(By.xpath("//*[@id=\"best-match-right\"]/div[3]/div[2]/ul"));
        List<WebElement> productList= productUL.findElements(By.tagName("a")); // Sayfadaki tüm ürünler liste olarak tutuldu.


        for (WebElement li : productList) {
            if (count==int_random){ //
                li.click();
                logger.info("Rastgele olarak "+int_random+". sıradaki ürün seçildi ve ürün linkine gidildi.");
                break;

            }
            count+=1;
        }

        Thread.sleep(10);


        js.executeScript("window.scrollTo(0, document.body.scrollHeight)"); // Uzun isimler yüzünden click işlevi çalışmayabiliyor.

        String productName = driver.findElement(By.xpath("//*[@id=\"sp-title\"]")).getText();
        logger.info("Ürünün adı : "+productName);

        WebElement ProductPrice = driver.findElement(By.id("sp-price-highPrice")); // İndirimsiz Fiyat Default olarak ayarlandı.
        WebElement ProductPriceDiscount=driver.findElement(By.id("sp-price-lowPrice")); //İndirimli Fiyat Tutuluyor.
        String ProductPriceString;

        String indirimsiz= ProductPrice.getText();         //indirimsiz fiyatı tutuyor
        String indirimli= ProductPriceDiscount.getText();  //indirimli fiyatı tutuyor

        if (indirimli.isEmpty()){    // İndirimli bir tutar valid ise Ürünün fiyatını bu değere eşitliyor
            logger.info("Ürünün Fiyatı : " + indirimsiz + " olarak tutuluyor.");
            ProductPriceString=indirimsiz;
        }else{                       // İndirim yok ise Ürünün fiyatını indirimsiz tutara eşitliyor.
            logger.info("Ürünün Fiyatı : " + indirimli+ " olarak tutuluyor.");
            ProductPriceString=indirimli;
        }

        driver.findElement(By.id("add-to-basket")).click(); // Ürün sepete atılır.
        logger.info("Ürün sepete eklendi.");

        driver.findElement(By.xpath("//*[@id=\"header_wrapper\"]/div[4]/div[3]/a/div[1]")).click(); //Sepete girilir.
        logger.info("Sepete gidildi.");

        WebElement BasketPrice = driver.findElement(By.xpath("//*[@id=\"cart-price-container\"]/div[3]/p")); //Ürünün sepetteki fiyatı ayarlanır.
        String BasketPriceString= BasketPrice.getText();
        logger.info("Sepetteki Tutar : " + BasketPriceString);


        Assert.assertEquals(BasketPriceString, ProductPriceString); //Tutulan ürün fiyatı ile sepetteki tutar karşılaştırılır.
        logger.info("Ürün fiyatı ve Sepetteki Tutar Karşılaştırılması Başarılı.");


        Actions action = new Actions(driver);
        WebElement elem = driver.findElement(By.xpath("//*[@class='amount']"));
        action.moveToElement(elem).build().perform();
        action.contextClick(elem).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
        logger.info("Ürün adeti arttırılarak 2 olarak seçildi");

        Thread.sleep(500);

        String numberOfProduct= (driver.findElement(By.xpath("//*[@id=\"submit-cart\"]/div/div[2]/div[3]/div/div[1]/div/div[5]/div[1]/div/ul/li[1]/div[1]")).getText());

        Assert.assertEquals(numberOfProduct.charAt(14),'2');
        logger.info("Ürün Adedinin " + numberOfProduct.charAt(14) +" olduğu kontrol edildi.");

        Thread.sleep(500);

        driver.findElement(By.cssSelector(".btn-delete.btn-update-item.hidden-m")).click();
        logger.info("Ürün(ler) sepetten silindi.");   //Ürünler silinir.


        String actual = driver.findElement(By.xpath("//*[@id=\"empty-cart-container\"]/div[1]/div[1]/div/div[2]")).getText();
        String expected = "Sepetinizde ürün bulunmamaktadır.";



        if (actual.equals(expected)){
            logger.info("Sepetin boş olduğu doğrulandı.");
        }
        logger.info("Test Başarıyla sonuçlandı.");
    }



    @After
    public void quit() {
        driver.quit();
        logger.info("Driver Kapatıldı.");

    }
}
