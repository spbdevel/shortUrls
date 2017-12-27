package org.app.controller;

import org.app.dto.AccountReq;
import org.app.dto.AccountResp;
import org.app.dto.UrlReq;
import org.app.dto.UrlResponse;
import org.app.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController()
public class ConfController extends  AbstractController {

    @Autowired
    MainService confService;


    @RequestMapping(value = "/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResp> account(@RequestBody AccountReq req) {
        AccountResp accountResponse = new AccountResp();
        HttpStatus status = HttpStatus.CREATED;
        logger.debug("account: " + req.getAccId());
        if (req.getAccId() == null || req.getAccId().trim().length() == 0) { //todo validation check
            status = HttpStatus.BAD_REQUEST;
            accountResponse.setSuccess(false);
            accountResponse.setDescription("id should be valid");
            return new ResponseEntity<>(accountResponse, status);
        }
        AccountResp account = confService.account(req.getAccId());
        if(!account.getFound())
            status = HttpStatus.CONFLICT;

        return new ResponseEntity<>(account, status);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlResponse> registerUrl(@RequestBody UrlReq url) {
        HttpStatus status = HttpStatus.CREATED;
        UrlResponse urlResponse;
        try {
            urlResponse = confService.register(url);
            if(urlResponse.isFound())
                status = HttpStatus.FOUND;
            if(urlResponse.isBadRequest())
                status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            urlResponse = new UrlResponse();
            urlResponse.setDescription("internal error: " + e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(urlResponse, status);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> accountStats(
            @RequestHeader("AccountId") String accountName) {
        logger.debug("stats for: " + accountName);

        HttpStatus status = HttpStatus.OK;
        Map<String, Integer> stats = confService.stats(accountName);
        if(stats.isEmpty())
            status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(stats, status);
    }



}
