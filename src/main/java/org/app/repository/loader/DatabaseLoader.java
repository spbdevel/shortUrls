package org.app.repository.loader;

import org.app.entity.AppUser;
import org.app.entity.Role;
import org.app.repository.AppUserRepository;
import org.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.BiConsumer;

@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository userRepository;



    //Create initial users
    @Override
    public void run(String... strings) throws Exception {
        AppUser admin = userRepository.save(new AppUser("admin", "", passwordEncoder.encode("12345"), null));
        AppUser user1 = userRepository.save(new AppUser("user1", "", passwordEncoder.encode("12345"), null));
        AppUser user2 = userRepository.save(new AppUser("user2", "", passwordEncoder.encode("12345"), null));

        Role admRole = new Role();
        admRole.setName("ADMIN");
        admRole = roleRepository.save(admRole);

        //create regular users
        Role userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);

        BiConsumer<AppUser, Role> biConsumer = (x, y) -> {
            x.setRoles(Arrays.asList(y));
            userRepository.save(x);
        };

        biConsumer.accept(admin, admRole);
        biConsumer.accept(user1, userRole);
        biConsumer.accept(user2, userRole);
    }

}