package hu.pte.ttk.vaadin.cars.manufacturer.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import hu.pte.ttk.vaadin.cars.manufacturer.entity.ManufacturerEntity;
import hu.pte.ttk.vaadin.cars.manufacturer.service.ManufacturerService;
import hu.pte.ttk.vaadin.cars.menu.MenuComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

// http://localhost:8080/manufacturer
@Route
public class ManufacturerView extends VerticalLayout {
    private VerticalLayout form;
    private ManufacturerEntity selectedManufacturer;
    private Binder<ManufacturerEntity> binder;
    private TextField name;
    private Button deleteBtn = new Button("Törlés", VaadinIcon.TRASH.create());

    @Autowired
    private ManufacturerService service;

    @PostConstruct
    public void init() {
        add(new MenuComponent());
        add(new Text("Gyártók"));
        Grid<ManufacturerEntity> grid = new Grid<>();
        grid.setItems(service.getAll());
        grid.addColumn(ManufacturerEntity::getId).setHeader("Id").setSortable(true);
        grid.addColumn(ManufacturerEntity::getName).setHeader("Név").setSortable(true);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if(event.getValue() != null) {
                selectedManufacturer = new ManufacturerEntity();
                selectedManufacturer.setId(event.getValue().getId());
                selectedManufacturer.setName(event.getValue().getName());
                binder.setBean(selectedManufacturer);
                form.setVisible(selectedManufacturer != null);
                deleteBtn.setEnabled(selectedManufacturer != null);
            }
        });
        grid.setMultiSort(true);
        addButtonBar(grid);
        add(grid);
        addForm(grid);
    }

    private void addForm(Grid<ManufacturerEntity> grid) {
        form = new VerticalLayout();
        binder = new Binder<>(ManufacturerEntity.class);

        HorizontalLayout nameField = new HorizontalLayout();
        name = new TextField();
        nameField.add(new Text("Név"), name);

        binder.forField(name)
                .asRequired("Kötelező mező")
                .bind(ManufacturerEntity::getName, ManufacturerEntity::setName);

        form.add(nameField, addSaveBtn(grid));
        add(form);
        form.setVisible(false);
        binder.bindInstanceFields(this);
    }

    private Button addSaveBtn(Grid<ManufacturerEntity> grid) {
        Button saveBtn = new Button("Mentés", VaadinIcon.SAFE.create());
        saveBtn.addClickListener(buttonClickEvent -> {
            try {
                binder.writeBean(selectedManufacturer);
                if (selectedManufacturer.getId() == null) {
                    service.add(selectedManufacturer);
                    selectedManufacturer = null;
                    Notification.show("Sikeres mentés");
                } else {
                    service.update(selectedManufacturer);
                    Notification.show("Sikeres módosítás");
                }
                grid.setItems(service.getAll());
                form.setVisible(false);
            } catch (ValidationException e) {
                Notification.show("Érvénytelen adatok");
            }
        });
        return saveBtn;
    }

    private void addButtonBar(Grid<ManufacturerEntity> grid) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        deleteBtn.addClickListener(buttonClickEvent -> {
            service.remove(selectedManufacturer);
            Notification.show("Sikeres törlés");
            selectedManufacturer = null;
            grid.setItems(service.getAll());
            form.setVisible(false);

        });
        deleteBtn.setEnabled(false);

        Button addBtn = new Button("Új gyártó rögzítése", VaadinIcon.PLUS.create());
        addBtn.addClickListener(buttonClickEvent -> {
            selectedManufacturer = new ManufacturerEntity();
            binder.setBean(selectedManufacturer);
            form.setVisible(true);

        });
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(addBtn);
        add(horizontalLayout);
    }
}
