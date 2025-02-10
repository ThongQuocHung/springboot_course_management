package group_2.cursus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import group_2.cursus.entity.Admin;
import group_2.cursus.repository.AdminRepository;
import group_2.cursus.repository.UserRepository;

@Configuration
public class AppInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, AdminRepository adminRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                Admin a = new Admin("admin@gmail.com", passwordEncoder.encode("123456"), "ADMIN", false);
                a.setIsAdminSystem(true);

                adminRepository.save(a);
                System.out.println("admin user has been created with default password: admin, please change it");
            }
        };
    }
}