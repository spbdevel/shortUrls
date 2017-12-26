package org.app.controller;

import org.app.entity.RegisteredURL;
import org.app.service.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class RedirectController extends  AbstractController  {


    @Autowired
    private RedirectService redirectService;


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/{shortUrl}", method=RequestMethod.GET)
    public ResponseEntity<String> redirect(@PathVariable("shortUrl") String shortUrl) {
        HttpHeaders headers = new HttpHeaders();
        RegisteredURL redirectUrl = redirectService.getRedirectUrl(shortUrl);
        HttpStatus status = HttpStatus.FOUND;
        if (redirectUrl != null) {
            headers.add("Location", redirectUrl.getLongUrl());
            if(redirectUrl.getRedirectType() == 301) {
                    status = HttpStatus.MOVED_PERMANENTLY;
            }
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(null, headers, status);
    }

}