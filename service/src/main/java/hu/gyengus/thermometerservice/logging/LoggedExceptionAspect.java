package hu.gyengus.thermometerservice.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class LoggedExceptionAspect {
    private static final Logger LOG = LoggerFactory.getLogger(LoggedExceptionAspect.class);
    
    @Around("@annotation(LoggedException)")
    public Object exceptionLoggingAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            LOG.error("{} {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        }
    }
}
