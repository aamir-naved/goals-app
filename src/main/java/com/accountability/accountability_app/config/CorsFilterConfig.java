//package com.accountability.accountability_app.config;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import java.io.IOException;
//
//@Component
//public class CorsFilterConfig implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        HttpServletRequest request = (HttpServletRequest) req;
//
//        System.out.println("CORS Filter: Request received - " + request.getMethod() + " " + request.getRequestURI());
//
//        // Allow all origins (for testing) — change this to specific domains later
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//        // Allow all common HTTP methods
//        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//
//        // Allow all necessary headers
//        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
//
//        // Allow sending credentials (cookies, authorization headers)
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//
//        System.out.println("CORS Headers set for response.");
//
//        // Handle preflight requests (OPTIONS)
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//
//        chain.doFilter(req, res);
//        System.out.println("Request passed through CORS filter.");
//    }
//}
