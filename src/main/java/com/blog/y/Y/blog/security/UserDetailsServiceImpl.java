package com.blog.y.Y.blog.security;


import com.blog.y.Y.blog.model.User;
import com.blog.y.Y.blog.repository.UserRepository;
import com.blog.y.Y.blog.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.logging.Logger;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Logger logger = Logger.getLogger(getClass().toString());
        logger.info("LOOKING FOR USER:" + username);
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            logger.info("USER NOT FOUND");
            throw new UsernameNotFoundException("Could not find user");
        } else {
            logger.info("USER FOUND");
        }

        return new MyUserDetails(user);
    }

}