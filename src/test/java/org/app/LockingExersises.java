package org.app;

import org.app.entity.AppUser;
import org.app.entity.RegisteredURL;
import org.app.entity.Stats;
import org.app.repository.AppUserRepository;
import org.app.repository.RegisteredURLRepository;
import org.app.repository.StatsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SpringBootTest(classes = {AppConfig.class})
@RunWith(SpringRunner.class)
public class LockingExersises {

    private static Logger logger = LoggerFactory.getLogger(LockingExersises.class);

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RegisteredURLRepository registeredURLRepository;

    @Autowired
    private StatsRepository statsRepository;


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



    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void getOptimisticLockingException() {
        AppUser user = appUserRepository.findByAccountName(accountName2);
        RegisteredURL url = registeredURLRepository.findByLongUrlAndAccountId(longUrl2, user.getId());
        Stats stat1 = statsRepository.findByUserAndUrl(user, url);
        Stats stat2 = statsRepository.findByUserAndUrl(user, url);
        stat1.setCounter(10);
        stat2.setCounter(20);
        statsRepository.save(stat1);

        //this save will happen in different transaction, so it throws exception
        statsRepository.save(stat2);
        fail();
    }

    @Test
    @Transactional(isolation = Isolation.SERIALIZABLE) //any level will work same way here
    public void serializableWorks() {
        AppUser user = appUserRepository.findByAccountName(accountName2);
        RegisteredURL url = registeredURLRepository.findByLongUrlAndAccountId(longUrl2, user.getId());
        Stats stat1 = statsRepository.findByUserAndUrl(user, url);
        Stats stat2 = statsRepository.findByUserAndUrl(user, url);
        stat1.setCounter(10);
        stat2.setCounter(20);
        statsRepository.save(stat1);

        //version will not be changed becasue transaction is still the same
        statsRepository.save(stat2);
        logger.info(stat1.toString());
        logger.info(stat2.toString());

        assertEquals(stat1, stat2);
    }


    @Test(expected = RollbackException.class)
    public void pessimisticCommitFailure() {
        try {
            twoConcurentTransactions(LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE, new HashMap());
        } catch (Exception e) {
            logger.info("err: ", e);
            throw e;
        }
        fail();
    }

    @Test(expected = PessimisticLockException.class)
    public void lockFailure() {
        try {
            twoConcurentTransactions(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_READ, new HashMap());
        } catch (Exception e) {
            logger.info("err: ", e);
            throw e;
        }
        fail();
    }


    @Test(expected = LockTimeoutException.class)
    public void lockTimeoutFailure() {
        try {
            Map params = new HashMap();
            params.put("javax.persistence.lock.timeout", 5000);
            //params.put("spring.jpa.properties.javax.persistence.query.timeout", 5000);
            twoConcurentTransactions(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_READ, params);
        } catch (Exception e) {
            logger.info("err: ", e);
            throw e;
        }
        fail();
    }


    @Test(expected = RollbackException.class)
    public void optimisticCommitFailure() {
        try {
            twoConcurentTransactions(LockModeType.OPTIMISTIC_FORCE_INCREMENT, LockModeType.NONE, new HashMap());
        } catch (Exception e) {
            logger.info("err: ", e);
            throw e;
        }
        fail();
    }


    private void twoConcurentTransactions(LockModeType modeType1, LockModeType modeType2, Map map) {
        EntityManager em1 = emf.createEntityManager(map);
        EntityManager em2 = emf.createEntityManager(map);
        EntityTransaction tx1 = em1.getTransaction();
        EntityTransaction tx2 = em2.getTransaction();

        try {
            tx1.begin();
            Stats stats = (Stats) em1.createQuery("from Stats").getResultList().get(0);
            logger.info("try to lock 1: " + stats);
            em1.lock(stats, modeType1);
            tx2.begin();
            Stats stats2 = (Stats) em2.createQuery("from Stats").getResultList().get(0);
            logger.info("try to lock 2: " + stats2);

            //on pessimistic/pessimistic this lock fails
            em2.lock(stats2, modeType2);
            stats2.setCounter(20);

            // on pessimistic/none locks this commit fails
            tx2.commit();

            logger.info("commited: " + stats2.toString());
            logger.info("to commit: " + stats.toString());

            // on optimistic/none lock this commit fails
            tx1.commit();
        } finally {
            if (tx2.isActive()) tx2.rollback();
            if (tx1.isActive()) tx1.rollback();
        }
    }

}
