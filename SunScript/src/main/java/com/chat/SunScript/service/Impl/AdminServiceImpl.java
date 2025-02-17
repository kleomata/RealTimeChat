package com.chat.SunScript.service.Impl;

import com.chat.SunScript.Util.JwtUtil;
import com.chat.SunScript.dto.admindto.GetAdminResponse;
import com.chat.SunScript.dto.admindto.LoginAdminRequest;
import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.entity.Role;
import com.chat.SunScript.repository.AdminRepository;
import com.chat.SunScript.service.AdminService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void createAdmin(Admin admin) {
        try {
            admin.setPassword(new BCryptPasswordEncoder().encode(admin.getPassword()));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setCreatedDate(LocalDateTime.now());
            adminRepository.save(admin);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Username already exists!");
        }
    }


    @Override
    public GetAdminResponse login(LoginAdminRequest request) {
      try {
            Admin admin = findByUsernameWithCollation(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid password!");
            }

            String token = jwtUtil.generationToken(admin.getUsername());

            return mapAdminToGetAdminResponse(admin,token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

    private Optional<Admin> findByUsernameWithCollation(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.collation(Collation.of("en"));

        Admin admin = mongoTemplate.findOne(query, Admin.class);
        return Optional.ofNullable(admin);
    }

    private GetAdminResponse mapAdminToGetAdminResponse(Admin admin, String token) {
        GetAdminResponse adminResponse = new GetAdminResponse();

        adminResponse.setId(admin.getId());
        adminResponse.setUsername(admin.getUsername());
        adminResponse.setName(admin.getName());
        adminResponse.setLastName(admin.getLastName());
        adminResponse.setCreatedDate(admin.getCreatedDate());

        adminResponse.setToken(token);

        return adminResponse;
    }

}
