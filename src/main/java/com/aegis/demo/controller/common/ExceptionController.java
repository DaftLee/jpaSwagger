package com.aegis.demo.controller.common;

import com.aegis.demo.pojo.common.Msg;
import com.aegis.demo.pojo.common.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 李成超
 * @date 2019/10/16 15:56
 * @description TODO
 **/
@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    protected ResultData handleException(Exception e) {
        if (e instanceof AuthorizationException) {
            log.error(e.getMessage());
            return new ResultData(e instanceof UnauthorizedException ? Msg.NO_PERMISSION : Msg.AUTH_ERROR);
        } else if (e instanceof ValidationException
                || e instanceof BindException
                || e instanceof MissingServletRequestParameterException) {
            log.error(e.getMessage());
            return new ResultData(Msg.PARAM, e.getMessage());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            log.error(e.getMessage());
            return new ResultData(Msg.METHOD_NOT_ALLOWED, e.getMessage());
        } else if (e instanceof UnknownSessionException) {
            log.error(e.getMessage());
            return new ResultData(Msg.INVALID_SESSION, e.getMessage());
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            StringBuffer sb = new StringBuffer();
            if (bindingResult.hasErrors()) {
                List<ObjectError> errorList = bindingResult.getAllErrors();
                errorList.forEach(error -> {
                    FieldError fieldError = (FieldError) error;
                    sb.append(fieldError.getField() + ":" + fieldError.getDefaultMessage() + ";");
                });
            }
            log.error(sb.toString());
            return new ResultData<>(sb.toString(), Msg.ERROR_VALIDATION);
        }
        String err = Arrays.stream(e.getStackTrace())
                .filter(it -> it.getClassName().contains("dispute"))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        String msg = e.toString() + "\n" + err;
        log.error(msg);
        return new ResultData(Msg.ERROR, msg);
    }
}

