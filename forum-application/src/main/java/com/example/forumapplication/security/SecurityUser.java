//package com.example.forumapplication.security;
//import com.example.forumapplication.models.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class SecurityUser implements UserDetails {
//
//    /**
//     *
//     */
//    private static final long serialVersionUID = -6690946490872875352L;
//
//    private final User user;
//
//    public SecurityCustomer(Customer customer) {
//        this.customer = customer;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(customer.getRole()));
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return customer.getPwd();
//    }
//
//    @Override
//    public String getUsername() {
//        return customer.getEmail();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//}