package com.brokage.infrastructure.security.aspect;

import com.brokage.infrastructure.security.UserPrincipal;
import com.brokage.infrastructure.security.annotation.ValidateCustomerAccess;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class CustomerAccessAspect {

    @Before("@annotation(validateCustomerAccess)")
    public void validateCustomerAccess(JoinPoint joinPoint, ValidateCustomerAccess validateCustomerAccess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new AccessDeniedException("Authentication required");
        }

        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();

        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }

        Long customerId = extractCustomerId(joinPoint, validateCustomerAccess.customerIdParam());

        if (!customerId.equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied: Cannot access other customer's data");
        }
    }

    private Long extractCustomerId(JoinPoint joinPoint, String parameterName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                String paramName = requestParam.value().isEmpty() ? parameter.getName() : requestParam.value();
                if (paramName.equals(parameterName) && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }

            if (parameter.getName().equals(parameterName) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }

        throw new IllegalArgumentException("Customer ID parameter not found: " + parameterName);
    }
}
