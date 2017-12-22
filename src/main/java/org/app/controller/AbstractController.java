package org.app.controller;

import org.app.controller.exeption.ExceptionJSONInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {

    protected static Logger logger = LoggerFactory.getLogger(AbstractController.class);


    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    ExceptionJSONInfo handleEntityNotFoundException(HttpServletRequest request, Exception ex){
        logger.info("ex:", ex);
        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setUrl(request.getRequestURL().toString());
        response.setMessage(ex.getMessage() == null ? "error on making request" : ex.getMessage());
        return response;
    }

}
