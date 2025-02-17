package com.chat.SunScript.configuration;

import com.chat.SunScript.Util.JwtUtil;
import com.chat.SunScript.service.custom.CustomAdminDetailsService;
import com.chat.SunScript.service.custom.CustomCombinedDetailService;
import com.chat.SunScript.service.custom.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomAdminDetailsService customAdminDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //@Autowired
    //private CustomCombinedDetailService customCombinedDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        //ObjectId id = null;
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            System.out.println("Extracted JWT: " + jwt);
            System.out.println("Extracted Username: " + username);
        }

        if (username != null) {
            System.out.println("Extracted Username: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            try {
                userDetails = loadUserDetails(username);
                        //customCombinedDetailService.loadUserByUsername(username);
            } catch (Exception e) {
                System.out.println("User not found for username: "+username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            if (jwtUtil.isTokenValid(jwt, username)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("Token is invalid!");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

   private UserDetails loadUserDetails(String username) throws Exception{
        UserDetails userDetails = null;

        try {
            userDetails = customAdminDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                return  userDetails;
            }
        } catch (Exception e1) {
            System.out.println("Admin not found with ID: "+username);
        }

        try {
            userDetails = customUserDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                return userDetails;
            }
        } catch (Exception e2) {
            System.out.println("User not found with ID: "+username);
        }

        throw new Exception("User with ID "+username+" not found");
    }
}
