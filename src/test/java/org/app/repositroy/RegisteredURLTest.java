 package org.app.repositroy;

 import org.app.AppConfig;
 import org.app.CustomWebSecurity;
 import org.app.entity.AppUser;
 import org.app.entity.RegisteredURL;
 import org.app.repository.AppUserRepository;
 import org.app.repository.RegisteredURLRepository;
 import org.junit.After;
 import org.junit.Before;
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
 import java.util.List;

 import static org.junit.Assert.*;

 @RunWith(SpringRunner.class)
 @SpringBootTest(classes = {AppConfig.class, CustomWebSecurity.class} )
 @WebAppConfiguration
 public class RegisteredURLTest {
     private static Logger logger = LoggerFactory.getLogger(RegisteredURLTest.class);


     @Autowired
     private AppUserRepository appUserRepository;

     @Autowired
     private RegisteredURLRepository registeredURLRepository;

     private final String accountName1 = "aname1";
     private final String accountName2 = "aname2";


     @Before
     public void initData() {
         AppUser acnt = createAccount(accountName1, "pass1");
         appUserRepository.save(acnt);

         acnt = createAccount(accountName2, "pass2");
         appUserRepository.save(acnt);

     }


     @After
     public void dropData() {
         registeredURLRepository.deleteAll();
         appUserRepository.deleteAll();
     }


     private AppUser createAccount(String name, String pass) {
         AppUser acnt = new AppUser();
         acnt.setAccountName(name);
         acnt.setPassword(pass);
         return acnt;
     }


     @Test
     public void findAll() {
         AppUser account = appUserRepository.findByAccountName(accountName1);
         RegisteredURL rurl = createRegisteredURL(account, "long url 1", "short url 1");
         registeredURLRepository.save(rurl);
         rurl = createRegisteredURL(account, "long url 2", "short url 2");
         registeredURLRepository.save(rurl);
         List<RegisteredURL> all = registeredURLRepository.findAll();
         assertNotNull(all);
         assertEquals(all.size(), 2);
     }


     @Test
     public void saveValid() {
         AppUser account = appUserRepository.findByAccountName(accountName1);
         RegisteredURL rurl = createRegisteredURL(account, "long url", "short url");
         try {
             RegisteredURL save = registeredURLRepository.save(rurl);
             assertTrue(true);
         } catch (DataIntegrityViolationException e) {
             fail();
         }
     }

     @Test
     public void saveDuplicate() {
         AppUser account = appUserRepository.findByAccountName(accountName1);
         RegisteredURL rurl = createRegisteredURL(account, "long url", "short url");
         try {
             RegisteredURL save = registeredURLRepository.save(rurl);
             assertTrue(true);
         } catch (DataIntegrityViolationException e) {
             fail();
         }
         rurl = createRegisteredURL(account, "long url 2", "short url");
         try {
             registeredURLRepository.save(rurl);
             fail();
         } catch (DataIntegrityViolationException e) {
             assertTrue(true);
         }
     }

     @Test
     public void saveNotValid() {
         AppUser account = appUserRepository.findByAccountName(accountName1);
         RegisteredURL rurl = createRegisteredURL(account, null, "short url");
         try {
             registeredURLRepository.save(rurl);
             fail();
         } catch (ConstraintViolationException e) {
             assertTrue(true);
             logger.info(e.getMessage());
         }
     }

     private RegisteredURL createRegisteredURL(AppUser account, String longUrl, String shorUrl) {
         RegisteredURL rurl = new RegisteredURL();
         rurl.setAccount(account);
         rurl.setLongUrl(longUrl);
         rurl.setShortUrl(shorUrl);
         return rurl;
     }

     @Test
     public void findByShourtUrlAndAccount(){
         AppUser appUser = appUserRepository.findAll().get(0);
         String shorUrl = "short url 1";
         RegisteredURL rurl = createRegisteredURL(appUser, "long url 1", shorUrl);
         registeredURLRepository.save(rurl);
         rurl = createRegisteredURL(appUser, "long url 2", "short url 2");
         registeredURLRepository.save(rurl);

         RegisteredURL found = registeredURLRepository
                 .findByShortUrlAndAccountId(shorUrl, appUser.getId());
         assertNotNull(found);
         assertEquals(found.getShortUrl(), shorUrl);

         logger.info(found.toString());
     }

 }
