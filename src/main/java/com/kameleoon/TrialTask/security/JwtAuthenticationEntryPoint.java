package com.kameleoon.TrialTask.security;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kameleoon.TrialTask.dto.ApiErrorDto;
import com.kameleoon.TrialTask.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    @Autowired
    public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse httpServletResponse, AuthenticationException ex) throws IOException {

        Throwable throwable = null;
        if (request.getAttribute("jakarta.servlet.error.exception") != null) {
            throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
            resolver.resolveException(request, httpServletResponse, null, (Exception) throwable);
        }
        if (!httpServletResponse.isCommitted()) {
            if(throwable == null) {//User has no Auth in request header
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                JsonMapper jsonMapper = new JsonMapper();
                jsonMapper.registerModule(new JavaTimeModule());
                jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.UNAUTHORIZED,
                        request.getRequestURI().toString(),
                        new ErrorDto(ex.getClass().getName(), "Authentication is required to access this resource")
                );
                String json = jsonMapper.writeValueAsString(errorDto);
                httpServletResponse.getOutputStream().println(json);
            } else{//Not Handled Internal Server Error
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                JsonMapper jsonMapper = new JsonMapper();
                jsonMapper.registerModule(new JavaTimeModule());
                jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI().toString(),
                        new ErrorDto(throwable.getClass().getName(), throwable.getMessage())
                );
                String json = jsonMapper.writeValueAsString(errorDto);
                httpServletResponse.getOutputStream().println(json);
            }
        }
    }
}
