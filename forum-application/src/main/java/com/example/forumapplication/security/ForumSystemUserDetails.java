//package com.example.forumapplication.security;
//
//import com.example.forumapplication.models.User;
//import com.example.forumapplication.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ForumSystemUserDetails implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        List<User> customer = userRepository.findByEmail(username);
//        if (customer.size() == 0) {
//            throw new UsernameNotFoundException("User details not found for the user : " + username);
//        }
//        return new SecurityUser(username.get(0));
//    }
//
//}
