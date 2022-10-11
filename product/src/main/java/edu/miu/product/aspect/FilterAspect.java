package edu.miu.product.aspect;

import edu.miu.product.exception.UnknownServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Aspect
@Component
public class FilterAspect {
    @Value("${mytoken}")
    private String token;

    @Autowired
    HttpServletRequest request;

    @Pointcut("execution(* edu.miu.product.controller.*.*(..))")
    public void controllerClassMethods() {}

    @Before("controllerClassMethods()")
    public void tokenChecker(JoinPoint joinPoint){
        if(request.getHeader("token") != null){
            if (request.getHeader("token").equals(token)){
                System.out.println("access granted");
            }else {
                System.out.println("access denied");
                throw new UnknownServiceException("Access denied ");
            }
        }else {
            System.out.println("no token");
            throw new UnknownServiceException("Access denied");
        }
    }
}
