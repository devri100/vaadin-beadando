package hu.pte.ttk.vaadin.cars.user.config;

import hu.pte.ttk.vaadin.cars.user.entity.RoleEntity;
import hu.pte.ttk.vaadin.cars.user.entity.UserEntity;
import hu.pte.ttk.vaadin.cars.user.service.RoleService;
import hu.pte.ttk.vaadin.cars.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;

@Configuration
public class UserAppInitConfig {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        List<RoleEntity> roleEntities = roleService.getAll();
        RoleEntity admin = new RoleEntity();
        RoleEntity user = new RoleEntity();
        if (roleEntities.isEmpty()) {
            admin.setAuthority("ROLE_ADMIN");
            roleService.add(admin);

            user.setAuthority("ROLE_USER");
            roleService.add(user);
        }

        List<UserEntity> userEntities = userService.getAll();
        if (userEntities.isEmpty()) {
            UserEntity entity = new UserEntity();
            entity.setPassword(new BCryptPasswordEncoder().encode("admin"));
            entity.setUsername("admin");
            entity.setAuthorities(new HashSet<>());
            entity.getAuthorities().add(admin);
            userService.add(entity);

        }
    }
}
