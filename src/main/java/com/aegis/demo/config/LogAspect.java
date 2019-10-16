package com.aegis.demo.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * controller的接口日志类
 * Created by zhouyi on 2019/6/9.
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

  /**
   * 定义一个切入点.
   * 解释下：
   * <p>
   * ~ 第一个 * 代表任意修饰符及任意返回值.
   * ~ 第二个 * 任意包名
   * ~ 第三个 * 定义在controller包或者子包
   * ~ 第四个 * 任意方法
   * ~ .. 匹配任意数量的参数.
   */
  @Pointcut(value = "execution(public * com.aegis..*.controller..*.*(..))")
  public void webLog() {
  }

  @Before(value = "webLog()")
  public void doBefore(JoinPoint joinPoint) {
    StringBuffer sb = new StringBuffer(getUrl(joinPoint));
    String[] params = ((CodeSignature) joinPoint.getStaticPart().getSignature()).getParameterNames();
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    int i = 0;
    for (Object arg : joinPoint.getArgs()) {
      //过滤掉参数为request和response的
      if (attr == null || (arg != null && arg != attr.getRequest() && arg != attr.getResponse())) {
        sb.append("&")
          .append(params[i])
          .append("=")
          .append(new Gson().toJson(arg));
      }
      i += 1;
    }
    log.info(sb.toString());
  }

  @AfterReturning(returning = "ret", pointcut = "webLog()")
  public void doAfterReturning(JoinPoint joinPoint, Object ret) {
    String retData = getUrl(joinPoint) + new Gson().toJson(ret);
    //Todo 获取response里面的内容
//        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
//        if (requestAttributes != null){
//            log.info(requestAttributes.getResponse().getHeaderNames().toString());
//            String jessionId = requestAttributes.getResponse().getHeader("Set-Cookie");
//            if (!StringUtils.isEmpty(jessionId)){
//                retData += " " + jessionId;
//            }
//        }
    log.info(retData);
  }

  public String getUrl(JoinPoint joinPoint) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String s = "";
    if (requestAttributes != null) {
      s += requestAttributes.getRequest().getRemoteAddr() + " "
        + requestAttributes.getRequest().getRequestURI() + " "
        + requestAttributes.getRequest().getMethod() + " "
        + requestAttributes.getRequest().getHeader("User-Agent") + " ";
    }
    s += joinPoint.getSignature().getDeclaringTypeName()
      + "." + joinPoint.getSignature().getName() + " ";
    return s;
  }
}
