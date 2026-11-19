package com._61119report.global.aspect;

import com._61119report.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * RsData 를 반환하는 GET/POST/PUT/DELETE API 의 HTTP 상태코드를 rsData.statusCode() 로 지정한다.
 * 컨트롤러는 ResponseEntity 없이 RsData 를 그대로 반환하면 된다.
 */
@Aspect
@Component
public class ResponseAspect {

    @Around("""
            (
                @annotation(org.springframework.web.bind.annotation.GetMapping)
                || @annotation(org.springframework.web.bind.annotation.PostMapping)
                || @annotation(org.springframework.web.bind.annotation.PutMapping)
                || @annotation(org.springframework.web.bind.annotation.DeleteMapping)
            )
            && execution(com._61119report.global.rsData.RsData *(..))
            """)
    public Object setStatusFromRsData(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof RsData<?> rsData
                && RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes
                && attributes.getResponse() != null) {
            HttpServletResponse response = attributes.getResponse();
            response.setStatus(rsData.statusCode());
        }

        return result;
    }
}
