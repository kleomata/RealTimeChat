package com.chat.SunScript.service.custom;

import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.entity.Role;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.repository.AdminRepository;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.service.custom.details.CustomAdminDetails;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomCombinedDetailService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public CustomCombinedDetailService(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to find user with username: " + username);
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            return new CustomAdminDetails(admin, getAuthorities(admin.getRole()));
        } else {
            System.out.println("No admin found for username: " + username);
        }


        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new CustomUserDetails(user, getAuthorities(user.getRole()));
        } else {
            System.out.println("No user found for username: " + username);
        }

        throw new UsernameNotFoundException("User with username "+username+" not found");
    }

    //@Override
    public UserDetails loadUserByObjectId(String id) throws UsernameNotFoundException {

        if (!ObjectId.isValid(id)) {
            throw new UsernameNotFoundException("Invalid ObjectId format");
        }

        ObjectId objectId = new ObjectId(id);

        Admin admin = adminRepository.findById(objectId).orElse(null);
        if (admin != null) {
            return new CustomAdminDetails(admin, getAuthorities(admin.getRole()));
        }

        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new CustomUserDetails(user, getAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

}
