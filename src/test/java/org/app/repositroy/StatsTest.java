 package org.app.repositroy;

 import org.app.AppConfig;
 import org.app.CustomWebSecurity;
 import org.app.entity.AppUser;
 import org.app.entity.RegisteredURL;
 import org.app.entity.Stats;
 import org.app.repository.AppUserRepository;
 import org.app.repository.RegisteredURLRepository;
 import org.app.repository.StatsRepository;
 import org.app.service.RedirectService;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.orm.ObjectOptimisticLockingFailureException;
 import org.springframework.security.test.context.support.WithMockUser;
 import org.springframework.test.context.junit4.SpringRunner;
 import org.springframework.test.context.web.WebAppConfiguration;

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

     @Autowired
     private RedirectService redirectService;

    private final String accountName1 = "aname1";
    private final String accountName2 = "aname2";
    private final String longUrl1 = "long url 1";
    private final String longUrl2 = "long url 2";
    private final String SHORT_URL_1 = "short url 1";
    private final String SHORT_URL_2 = "short url 2";


     @Before
     public void initData() {
         AppUser acnt1 = createAccount(accountName1, "pass1");
         appUserRepository.save(acnt1);
         AppUser acnt2 = createAccount(accountName2, "pass2");
         appUserRepository.save(acnt2);

        RegisteredURL rurl = createRegisteredURL(acnt2, longUrl1, SHORT_URL_1);
        registeredURLRepository.save(rurl);
        RegisteredURL rurl2 = rurl = createRegisteredURL(acnt2, longUrl2, SHORT_URL_2);
        registeredURLRepository.save(rurl);

        Stats stats = new Stats();
        stats.setUrl(rurl2);
        stats.setUser(acnt2);
        statsRepository.save(stats);

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
        List<Stats> alls = statsRepository.findAll();
        assertNotNull(alls);
        assertTrue(alls.size() > 0);
        logger.info(alls.toString());
    }



    @Test
    public void findByUserAndUrl() {
        AppUser user = appUserRepository.findByAccountName(accountName2);
        RegisteredURL url = registeredURLRepository.findByLongUrlAndAccountId(longUrl2, user.getId());
        Stats stat = statsRepository.findByUserAndUrl(user, url);
        assertNotNull(stat);
    }



     @Test
     public void findByUser() {
         List<AppUser> usrs = appUserRepository.findAll();
         assertTrue(usrs.size() > 0);
         statsRepository.findByUser(usrs.get(0));
         assertTrue(true);
     }


     @Test
     @WithMockUser(username = accountName2, roles = "USER")
     public void checkRedirectsCounter() {
         RegisteredURL redirectUrl1;
         redirectService.getRedirectUrl(SHORT_URL_1);
         redirectService.getRedirectUrl(SHORT_URL_1);
         redirectService.getRedirectUrl(SHORT_URL_1);
         redirectService.getRedirectUrl(SHORT_URL_1);
         redirectUrl1 = redirectService.getRedirectUrl(SHORT_URL_1);
         assertTrue(true);
         Stats stat1 = statsRepository.findByUserAndUrl(redirectUrl1.getAccount(), redirectUrl1);
         assertNotNull(stat1);
         assertEquals(5,stat1.getCounter().intValue());
         redirectService.getRedirectUrl(SHORT_URL_2);
         redirectService.getRedirectUrl(SHORT_URL_2);
         redirectService.getRedirectUrl(SHORT_URL_2);
         assertTrue(true);
         RegisteredURL redirectUrl2 = redirectService.getRedirectUrl(SHORT_URL_2);
         Stats stat2 = statsRepository.findByUserAndUrl(redirectUrl2.getAccount(), redirectUrl2);
         assertNotNull(stat2);
         assertEquals(4,stat2.getCounter().intValue());
     }

     @Test
     @WithMockUser(username = accountName2, roles = "USER")
     public void redirectNotExistingUrl() {
         RegisteredURL redirectUrl = redirectService.getRedirectUrl("");
         assertNull(redirectUrl);
     }

     @Test
     public void redirectNotAuthorized() {
         try {
             RegisteredURL redirectUrl = redirectService.getRedirectUrl(SHORT_URL_1);
             fail();
         } catch (Exception e) {
             assertTrue(true);
         }
     }


     @Test(expected = ObjectOptimisticLockingFailureException.class)
     public void getOptimisticLockingException() {
         AppUser user = appUserRepository.findByAccountName(accountName2);
         RegisteredURL url = registeredURLRepository.findByLongUrlAndAccountId(longUrl2, user.getId());
         Stats stat1 = statsRepository.findByUserAndUrl(user, url);
         Stats stat2 = statsRepository.findByUserAndUrl(user, url);
         stat1.setCounter(10);
         stat2.setCounter(20);
         statsRepository.save(stat1);
         statsRepository.save(stat2);
         fail();
     }

 }
