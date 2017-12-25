package org.app.service;

import org.app.entity.AppUser;
import org.app.entity.Role;
import org.app.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(CustomUserDetailService .class);

    @Autowired
    private AppUserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("try find " + username);
        AppUser user = userRepository.findByAccountNameWithRoles(username);
        Optional<AppUser> opt = Optional.ofNullable(user);
        if (!opt.isPresent()) {
            throw new UsernameNotFoundException("username not found");
        }
        Collection<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> auths =
                roles.parallelStream().map(e -> new SimpleGrantedAuthority(e.getName()))
                        .collect(Collectors.toList());
        String password = user.getPassword();
        logger.debug(user.toString());
        return new User(username, password, auths);
    }


}