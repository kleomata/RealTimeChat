package com.chat.SunScript.component;

import com.chat.SunScript.Util.JwtUtil;
import com.chat.SunScript.service.custom.CustomAdminDetailsService;
import com.chat.SunScript.service.custom.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;


@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private CustomAdminDetailsService customAdminDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("Accessor: "+accessor);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            logger.info("Token in header: {}", token);

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                logger.error("Token is invalid or missing 'Bearer '");
                throw new IllegalArgumentException("Token is invalid or missing 'Bearer '");
            }

            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.isTokenValid(token, username)) {
                UserDetails userDetails = null;

                try {
                    userDetails = loadUserDetails(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    accessor.setUser(authenticationToken);
                    Principal principal = accessor.getUser();
                    if (principal != null) {
                        logger.info("Principal: {}", principal.getName());
                    } else {
                        logger.info("Missing principal!");
                    }

                    logger.info("The user has successfully authenticated: {}", username);
                } catch (Exception e) {
                    logger.error("\"The user with username {} was not found.", username);
                    throw new IllegalArgumentException("User bot found!");
                }
            } else {
                logger.error("Invalid token for username: {}", username);
                throw new IllegalArgumentException("Invalid token!");
            }
        }
        return message;
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
                System.out.println("User found with Username: "+username);
                return userDetails;
            }
        } catch (Exception e2) {
            System.out.println("User not found with ID: "+username);
        }

        throw new Exception("User with ID "+username+" not found");
    }

}
