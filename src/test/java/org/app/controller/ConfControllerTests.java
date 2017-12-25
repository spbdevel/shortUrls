package org.app.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.AppConfig;
import org.app.CustomWebSecurity;
import org.app.dto.AccountReq;
import org.app.dto.UrlReq;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AppConfig.class, CustomWebSecurity.class} )
@WebAppConfiguration
public class ConfControllerTests {

    private static Logger logger = LoggerFactory.getLogger(ConfControllerTests.class);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RegisteredURLRepository registeredURLRepository;

    @Autowired
    private StatsRepository statsRepository;

    private MockMvc mvc;

    private final String accountName1 = "aname1";
    private final String accountName2 = "aname2";
    private final String url = "http://some.com";


    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/").with(testSecurityContext()))
                .build();
        AppUser acnt = createAccount(accountName1, "pass1");
        appUserRepository.save(acnt);

        acnt = createAccount(accountName2, "pass2");
        appUserRepository.save(acnt);

        AppUser acc1 = appUserRepository.findByAccountName(accountName1);

        RegisteredURL registeredURL = new RegisteredURL();
        registeredURL.setAccount(acc1);
        registeredURL.setLongUrl(url);
        registeredURL.setShortUrl("http://asdf");
        registeredURLRepository.save(registeredURL);

        Stats stats = new Stats();
        stats.setUrl(registeredURL);
        stats.setUser(acc1);
        statsRepository.save(stats);
    }


    @After
    public void after() {
        statsRepository.deleteAll();
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
    public void accountCreate() throws Exception {
        AccountReq acnt = new AccountReq();
        acnt.setAccId("new_account");
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(acnt);
        logger.info(s);
        ResultActions perform = mvc.perform(post("/account/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isCreated());

        MockHttpServletResponse response  = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());

    }


    @Test
    public void accountAlreadyExist() throws Exception {
        AccountReq acnt = new AccountReq();
        acnt.setAccId(accountName1);
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(acnt);
        ResultActions perform = mvc.perform(post("/account/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().is(HttpStatus.CONFLICT.value()));

        MockHttpServletResponse response  = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }


    @Test
    public void accountNullId() throws Exception {
        AccountReq acnt = new AccountReq();
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(acnt);
        ResultActions perform = mvc.perform(post("/account/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isBadRequest());

        MockHttpServletResponse response = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }

    @Test
    public void accountEmptyId() throws Exception {
        AccountReq acnt = new AccountReq();
        acnt.setAccId(" ");
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(acnt);
        ResultActions perform = mvc.perform(post("/account/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isBadRequest());

        MockHttpServletResponse response  = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }




    @Test
    public void regNullUrl() throws Exception {
        UrlReq rUrl = new UrlReq();
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(rUrl);
        ResultActions perform = mvc.perform(post("/register/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isBadRequest());

        MockHttpServletResponse response = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }


    @Test
    @WithMockUser(username = accountName1, roles = "USER")
    public void regExistedValidUrl() throws Exception {
        UrlReq rUrl = new UrlReq();
        rUrl.setUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(rUrl);
        ResultActions perform = mvc.perform(post("/register/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isFound());

        MockHttpServletResponse response = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }


    @Test
    @WithMockUser(username = accountName1, roles = "USER")
    public void regNewValidUrl() throws Exception {
        UrlReq rUrl = new UrlReq();
        rUrl.setUrl("http://new.url.com");
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(rUrl);
        ResultActions perform = mvc.perform(post("/register/")
                .contentType(MediaType.APPLICATION_JSON).content(s));
        perform.andExpect(status().isCreated());

        MockHttpServletResponse response = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }


    @Test
    @WithMockUser(username = accountName1, roles = "USER")
    public void accountStats() throws Exception {
        ResultActions perform = mvc.perform(
                get("/statistic")
                .header("AccountId", accountName1))
                .andExpect(status().isOk());
        MockHttpServletResponse response = perform.andReturn().getResponse();
        logger.info("resp: " + response.getContentAsString());
    }



}