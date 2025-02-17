package com.chat.SunScript.service.custom.details;

import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomAdminDetails implements UserDetails {

    private final Admin admin;
    private final Collection<? extends  GrantedAuthority> authorities;
    private final Role role;

    public CustomAdminDetails(Admin admin, Collection<? extends GrantedAuthority> authorities) {
        this.admin = admin;
        this.authorities = authorities;
        this.role = admin.getRole();
    }

    public Admin getAdmin() {
        return admin;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
