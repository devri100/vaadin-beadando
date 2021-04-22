package hu.pte.ttk.vaadin.cars.car.service.impl;

import hu.pte.ttk.vaadin.cars.car.entity.CarEntity;
import hu.pte.ttk.vaadin.cars.car.service.CarService;
import hu.pte.ttk.vaadin.cars.core.service.impl.CoreCRUDServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl extends CoreCRUDServiceImpl<CarEntity> implements CarService {


    @Override
    protected void updateCore(CarEntity persistedEntity, CarEntity entity) {
        persistedEntity.setManufacturer(entity.getManufacturer());
        persistedEntity.setType(entity.getType());
        persistedEntity.setNumberOfDoors(entity.getNumberOfDoors());
        persistedEntity.setYear(entity.getYear());
    }

    @Override
    protected Class<CarEntity> getManagedClass() {
        return CarEntity.class;
    }
}
