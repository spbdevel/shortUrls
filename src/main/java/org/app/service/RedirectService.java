package org.app.service;

import org.app.entity.AppUser;
import org.app.entity.RegisteredURL;
import org.app.entity.Stats;
import org.app.repository.AppUserRepository;
import org.app.repository.RegisteredURLRepository;
import org.app.repository.StatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {

    private static Logger logger = LoggerFactory.getLogger(RedirectService.class);

    @Autowired
    AppUserRepository appUserRepository;


    @Autowired
    RegisteredURLRepository registeredURLRepository;


    @Autowired
    private StatsRepository statsRepository;


    public RegisteredURL getRedirectUrl(String shortUrl) {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        AppUser user = appUserRepository.findByAccountName(auth.getName());
        RegisteredURL url = registeredURLRepository.findByShortUrlAndAccountId(shortUrl, user.getId());
        if(url == null)
            return null;

        Stats stat = statsRepository.findByUserAndUrl(user, url);
        int counter;
        if(stat != null) {
            counter = stat.getCounter();
        } else {
            stat = new Stats();
            stat.setUser(user);
            stat.setUrl(url);
            counter = stat.getCounter();
        }
        stat.setCounter(++counter);
        logger.debug(url.getLongUrl() +", counter: " + counter);
        try {
            //TODO  put this to optiomistic lock transaction
            statsRepository.save(stat);
        } catch (Throwable e) {
            logger.error("error on updating stats: " + e.getMessage());
            logger.error("the stats: " + stat, e);
        }
        return  url;
    }
}