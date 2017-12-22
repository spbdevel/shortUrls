package org.app.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.app.dto.AccountResp;
import org.app.dto.UrlReq;
import org.app.dto.UrlResponse;
import org.app.entity.AppUser;
import org.app.entity.RegisteredURL;
import org.app.entity.Role;
import org.app.entity.Stats;
import org.app.repository.AppUserRepository;
import org.app.repository.RegisteredURLRepository;
import org.app.repository.RoleRepository;
import org.app.repository.StatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {

    private static Logger logger = LoggerFactory.getLogger(MainService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RegisteredURLRepository registeredURLRepository;

    @Autowired
    StatsRepository statsRepository;

    private List<Role> userRoles;


    public AccountResp account(String accId) {
        AccountResp accountResponse = new AccountResp();
        AppUser found = appUserRepository.findByAccountName(accId);

        if (found != null) {
            accountResponse.setFound(false);
            accountResponse.setSuccess(false);
            accountResponse.setDescription("account already exist");
        } else {
            accountResponse.setFound(true);
            accountResponse.setSuccess(true);
            accountResponse.setDescription("Your account is opened");
            accountResponse.setPassword(RandomStringUtils.randomAlphabetic(10));
            AppUser account = new AppUser();
            account.setAccountName(accId);
            account.setPassword(passwordEncoder.encode(accountResponse.getPassword()));
            if (userRoles == null) {
                initUserRoles();
            }
            account.setRoles(userRoles);
            appUserRepository.save(account);
            logger.debug("created: " + account);
        }
        return accountResponse;
    }

    private void initUserRoles() {
        Role role = roleRepository.findByName("USER");
        userRoles = Collections.nCopies(1, role);
    }


    public UrlResponse register(UrlReq req) {
        logger.debug(req.toString());
        UrlResponse urlResponse = new UrlResponse();
        try {
            new URL(req.getUrl()).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            urlResponse.setDescription("not valid URL");
            urlResponse.setBadRequest(true);
            return urlResponse;
        }

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        String uName = auth.getName();

        List<RegisteredURL> urls = registeredURLRepository.findByLongUrlAndAccountAccountName(req.getUrl(), uName);
        logger.debug("urls.size: " + urls.size());
        if(urls.size() > 0) {
            urls.stream().forEach(e -> logger.debug("url: " + e.getShortUrl()));
            urlResponse.setShortcode(urls.get(0).getShortUrl());
            urlResponse.setDescription("was registered before");
            urlResponse.setFound(true);
            return urlResponse;
        }

        AppUser user = appUserRepository.findByAccountName(uName);

        RegisteredURL rUrl = new RegisteredURL();
        rUrl.setLongUrl(req.getUrl());
        rUrl.setRedirectType(req.getRedirectType());
        rUrl.setAccount(user);
        rUrl.setShortUrl(RandomStringUtils.randomAlphanumeric(7));
        registeredURLRepository.save(rUrl);
        urlResponse.setShortcode(rUrl.getShortUrl());
        urlResponse.setDescription("new url was registered");
        return urlResponse;
    }


    public Map<String, Integer> stats(String accountName) {
        Map<String, Integer> map = new LinkedHashMap<>();
        AppUser user = appUserRepository.findByAccountName(accountName);
        if(user == null)
            return map;
        List<Stats> lst = statsRepository.findByUser(user);
        for(Stats st: lst) {
            map.put(st.getUrl().getLongUrl(), st.getCounter());
        }
        return map;

    }


}