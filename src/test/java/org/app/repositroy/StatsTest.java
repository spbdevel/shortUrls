 package org.app.repositroy;

 import org.app.AppConfig;
 import org.app.CustomWebSecurity;
 import org.app.entity.AppUser;
 import org.app.entity.RegisteredURL;
 import org.app.entity.Stats;
 import org.app.repository.AppUserRepository;
 import org.app.repository.RegisteredURLRepository;
 import org.app.repository.StatsRepository;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Ignore;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.dao.DataIntegrityViolationException;
 import org.springframework.test.context.junit4.SpringRunner;
 import org.springframework.test.context.web.WebAppConfiguration;

 import javax.validation.ConstraintViolationException;
 import java.util.Date;
 import java.util.List;

 import static org.junit.Assert.*;

 @RunWith(SpringRunner.class)
 @SpringBootTest(classes = {AppConfig.class, CustomWebSecurity.class} )
 @WebAppConfiguration
 public class StatsTest {
     private static Logger logger = LoggerFactory.getLogger(StatsTest.class);



     @Autowired
     private AppUserRepository appUserRepository;

     @Autowired
     private RegisteredURLRepository registeredURLRepository;

     @Autowired
     private StatsRepository statsRepository;

     private final String accountName1 = "aname1";
     private final String accountName2 = "aname2";


     @Before
     public void initData() {
         AppUser acnt = createAccount(accountName1, "pass1");
         appUserRepository.save(acnt);
         acnt = createAccount(accountName2, "pass2");
         appUserRepository.save(acnt);

         RegisteredURL rurl = createRegisteredURL(acnt, "long url 1", "short url 1");
         registeredURLRepository.save(rurl);
         rurl = createRegisteredURL(acnt, "long url 2", "short url 2");
         registeredURLRepository.save(rurl);
     }

     @After
     public void clean() {
         statsRepository.deleteAll();
         registeredURLRepository.deleteAll();
         appUserRepository.deleteAll();
     }

     private RegisteredURL createRegisteredURL(AppUser account, String longUrl, String shorUrl) {
         RegisteredURL rurl = new RegisteredURL();
         rurl.setAccount(account);
         rurl.setLongUrl(longUrl);
         rurl.setShortUrl(shorUrl);
         return rurl;
     }



     private AppUser createAccount(String name, String pass) {
         AppUser acnt = new AppUser();
         acnt.setAccountName(name);
         acnt.setPassword(pass);
         return acnt;
     }


     @Test
     public void saveAndFind() {
         //List<AppUser> all = appUserRepository.saveAndFind();
         List<RegisteredURL> all = registeredURLRepository.findAll();
         Stats stats = new Stats();
         RegisteredURL url = all.get(0);
         stats.setUrl(url);
         stats.setUser(url.getAccount());
         statsRepository.save(stats);
         List<Stats> alls = statsRepository.findAll();
         assertNotNull(alls);
         assertTrue(all.size() > 0);
         logger.info(alls.toString());
     }



     @Test
     public void findByUserAndUrl() {
         List<RegisteredURL> urls = registeredURLRepository.findAll();
         List<AppUser> usrs = appUserRepository.findAll();
         statsRepository.findByUserAndUrl(usrs.get(0), urls.get(0));
     }



     @Test
     public void findByUser() {
         List<AppUser> usrs = appUserRepository.findAll();
         statsRepository.findByUser(usrs.get(0));
     }



 }
