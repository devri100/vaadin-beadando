package hu.pte.ttk.vaadin.cars.car.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import hu.pte.ttk.vaadin.cars.manufacturer.entity.ManufacturerEntity;
import hu.pte.ttk.vaadin.cars.manufacturer.service.ManufacturerService;
import hu.pte.ttk.vaadin.cars.car.entity.CarEntity;
import hu.pte.ttk.vaadin.cars.car.service.CarService;
import hu.pte.ttk.vaadin.cars.menu.MenuComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

// http://localhost:8080/book
@Route
public class CarView extends VerticalLayout {
    private VerticalLayout form;
    private CarEntity selectedCar;
    private Binder<CarEntity> binder;
    private ComboBox<ManufacturerEntity> manufacturer;
    private TextField type;
    private TextField numberOfDoors;
    private TextField year;
    private Button deleteBtn = new Button("Törlés", VaadinIcon.TRASH.create());

    @Autowired
    private CarService service;
    @Autowired
    private ManufacturerService manufacturerService;

    @PostConstruct
    public void init() {
        add(new MenuComponent());
        add(new Text("Gépjárművek"));
        Grid<CarEntity> grid = new Grid<>();
        grid.setItems(service.getAll());
        grid.addColumn(CarEntity::getId).setHeader("Id").setSortable(true);
        grid.addColumn(carEntity -> {
                    if (carEntity.getManufacturer() != null) {
                        return carEntity.getManufacturer().getName();
                    }
                    return "";
                }
        ).setHeader("Author").setSortable(true);
        grid.addColumn(CarEntity::getType).setHeader("Típus").setSortable(true);
        grid.addColumn(CarEntity::getNumberOfDoors).setHeader("Ajtók száma").setSortable(true);
        grid.addColumn(CarEntity::getYear).setHeader("Gyártási év").setSortable(true);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if(event.getValue() != null) {
                selectedCar = new CarEntity();
                selectedCar.setId(event.getValue().getId());
                selectedCar.setManufacturer(event.getValue().getManufacturer());
                selectedCar.setNumberOfDoors(event.getValue().getNumberOfDoors());
                selectedCar.setYear(event.getValue().getYear());
                selectedCar.setType(event.getValue().getType());
                selectedCar.setManufacturer(event.getValue().getManufacturer());
                binder.setBean(selectedCar);
                form.setVisible(selectedCar != null);
                deleteBtn.setEnabled(selectedCar != null);
            }
        });
        grid.setMultiSort(true);
        addButtonBar(grid);
        add(grid);
        addForm(grid);
    }

    private void addForm(Grid<CarEntity> grid) {
        form = new VerticalLayout();
        binder = new Binder<>(CarEntity.class);

        HorizontalLayout manufacturerField = new HorizontalLayout();
        manufacturer = new ComboBox<>();
        manufacturer.setItems(manufacturerService.getAll());
        manufacturer.setItemLabelGenerator(ManufacturerEntity::getName);
        manufacturerField.add(new Text("Gyártó"), manufacturer);

        HorizontalLayout typeField = new HorizontalLayout();
        type = new TextField();
        typeField.add(new Text("Típus"), type);

        HorizontalLayout numberOfDoorsField = new HorizontalLayout();
        numberOfDoors = new TextField();
        numberOfDoors.setMaxLength(2);
        numberOfDoorsField.add(new Text("Ajtók száma"), numberOfDoors);

        HorizontalLayout yearField = new HorizontalLayout();
        year = new TextField();
        year.setMaxLength(4);

        yearField.add(new Text("Gyártási év"), year);

        binder.forField(manufacturer)
                .withValidator(Objects::nonNull,"Kötelező mező")
                .bind(CarEntity::getManufacturer, CarEntity::setManufacturer);

        binder.forField(type)
                .asRequired("Kötelező mező")
                .bind(CarEntity::getType, CarEntity::setType);

        StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter("Csak számot lehet megadni") {
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat format = super.getFormat(locale);
                format.setGroupingUsed(false);
                return format;
            };
        };

        binder.forField(numberOfDoors)
                .withConverter(plainIntegerConverter)
                .bind(CarEntity::getNumberOfDoors, CarEntity::setNumberOfDoors);

        binder.forField(year)
                .withConverter(plainIntegerConverter)
                .bind(CarEntity::getYear, CarEntity::setYear);

        form.add(manufacturerField, typeField, numberOfDoorsField, yearField, addSaveBtn(grid));
        add(form);
        form.setVisible(false);
        binder.bindInstanceFields(this);
    }

    private Button addSaveBtn(Grid<CarEntity> grid) {
        Button saveBtn = new Button("Mentés", VaadinIcon.SAFE.create());
        saveBtn.addClickListener(buttonClickEvent -> {
            //mentés
            try {
                binder.writeBean(selectedCar);
                if (selectedCar.getId() == null) {
                    service.add(selectedCar);
                    selectedCar = null;
                    Notification.show("Sikeres mentés");
                } else {
                    service.update(selectedCar);
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

    private void addButtonBar(Grid<CarEntity> grid) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        deleteBtn.addClickListener(buttonClickEvent -> {
            service.remove(selectedCar);
            Notification.show("Sikeres törlés");
            selectedCar = null;
            grid.setItems(service.getAll());
            form.setVisible(false);

        });
        deleteBtn.setEnabled(false);

        Button addBtn = new Button("Új gépjármű rögzítése", VaadinIcon.PLUS.create());
        addBtn.addClickListener(buttonClickEvent -> {
            selectedCar = new CarEntity();
            binder.setBean(selectedCar);
            form.setVisible(true);
        });
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(addBtn);
        add(horizontalLayout);
    }
}
