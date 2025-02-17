package com.chat.SunScript.service.custom;

import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.entity.Role;
import com.chat.SunScript.repository.AdminRepository;
import com.chat.SunScript.service.custom.details.CustomAdminDetails;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
public class CustomAdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public CustomAdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return new CustomAdminDetails(admin, getAuthorities(admin.getRole()));
    }

    public UserDetails loadUserByObjectId(ObjectId id) throws UsernameNotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));


        return new CustomAdminDetails(admin, getAuthorities(admin.getRole()));

    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
