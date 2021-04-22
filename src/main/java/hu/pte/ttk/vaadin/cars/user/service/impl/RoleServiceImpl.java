package hu.pte.ttk.vaadin.cars.user.service.impl;

import hu.pte.ttk.vaadin.cars.core.service.impl.CoreCRUDServiceImpl;
import hu.pte.ttk.vaadin.cars.user.entity.RoleEntity;
import hu.pte.ttk.vaadin.cars.user.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends CoreCRUDServiceImpl<RoleEntity> implements RoleService {

    @Override
    protected void updateCore(RoleEntity persistedEntity, RoleEntity entity) {
        persistedEntity.setAuthority(entity.getAuthority());
    }

    @Override
    protected Class<RoleEntity> getManagedClass() {
        return RoleEntity.class;
    }
}
