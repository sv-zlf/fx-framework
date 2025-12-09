package com.fxly.demo.system.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class HttpError implements ErrorController {

    private final static String ERROR_PATH = "/error";

    @RequestMapping(path  = ERROR_PATH )
    public HttpResult error(HttpServletRequest request, HttpServletResponse response) {
        return HttpResult.setResult(HttpResultEnum.BAD_REQUEST);
    }

}
