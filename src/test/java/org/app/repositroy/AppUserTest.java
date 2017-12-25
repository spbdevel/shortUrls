 package org.app.repositroy;

 import org.app.AppConfig;
 import org.app.CustomWebSecurity;
 import org.app.entity.AppUser;
 import org.app.repository.AppUserRepository;
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
 public class AppUserTest {
     private static Logger logger = LoggerFactory.getLogger(AppUserTest.class);



     @Autowired
     private AppUserRepository appUserRepository;

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
         List<AppUser> accounts = appUserRepository.findAll();
         assertNotNull(accounts);
         assertTrue(accounts.size() > 0);
     }

     @Test
     public void findByName() {
         AppUser account = appUserRepository.findByAccountName(accountName1);
         assertNotNull(account);
     }

     @Test
     public void uniqueNotNullName() {
         AppUser account = new AppUser();
         account.setAccountName(accountName1);
         account.setPassword("pass");
         try {
             appUserRepository.save(account);
             fail();
         } catch (DataIntegrityViolationException e) {
             assertTrue(true);
         }
         account.setAccountName(null);
         account.setPassword("pass");
         try {
             appUserRepository.save(account);
             fail();
         } catch (ConstraintViolationException e) {
             assertTrue(true);
         }
     }

     @Test
     public void passNotNull() {
         AppUser account = new AppUser();
         account.setAccountName(String.valueOf(new Date().getTime()));
         try {
             appUserRepository.save(account);
             fail();
         } catch (ConstraintViolationException e) {
             logger.info(e.getMessage());
             assertTrue(true);
         }
     }


 }
