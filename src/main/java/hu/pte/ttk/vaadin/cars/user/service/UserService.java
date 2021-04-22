package hu.pte.ttk.vaadin.cars.user.service;

import hu.pte.ttk.vaadin.cars.core.service.CoreCRUDService;
import hu.pte.ttk.vaadin.cars.user.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends CoreCRUDService<UserEntity>, UserDetailsService {

}
