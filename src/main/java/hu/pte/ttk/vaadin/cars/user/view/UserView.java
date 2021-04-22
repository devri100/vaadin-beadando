package hu.pte.ttk.vaadin.cars.user.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import hu.pte.ttk.vaadin.cars.menu.MenuComponent;
import hu.pte.ttk.vaadin.cars.user.entity.RoleEntity;
import hu.pte.ttk.vaadin.cars.user.entity.UserEntity;
import hu.pte.ttk.vaadin.cars.user.service.RoleService;
import hu.pte.ttk.vaadin.cars.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;

// http://localhost:8080/book
@Route
public class UserView extends VerticalLayout {
    private VerticalLayout form;
    private UserEntity selectedUser;
    private Binder<UserEntity> binder;
    private TextField username;
    private TextField firstName;
    private TextField lastName;
    //    private PasswordField password;
    private CheckboxGroup<RoleEntity> authorities;
    private Button deleteBtn = new Button("Törlés", VaadinIcon.TRASH.create());

    @Autowired
    private UserService service;
    @Autowired
    private RoleService roleService;

    @PostConstruct
    public void init() {
        add(new MenuComponent());
        add(new Text("Felhasználók"));
        Grid<UserEntity> grid = new Grid<>();
        grid.setItems(service.getAll());
        grid.addColumn(UserEntity::getId).setHeader("Id").setSortable(true);
        grid.addColumn(UserEntity::getUsername).setHeader("Felhasználónév").setSortable(true);
        grid.addColumn(UserEntity::getFirstName).setHeader("Keresztnév").setSortable(true);
        grid.addColumn(UserEntity::getLastName).setHeader("Vezetéknév").setSortable(true);
        grid.addColumn(userEntity -> {
                    if (userEntity.getAuthorities() != null) {
                        StringBuilder builder = new StringBuilder();
                        userEntity.getAuthorities().forEach(roleEntity -> {
                            builder.append(roleEntity.getAuthority()).append(",");
                        });
                        return builder.toString();
                    }
                    return "";
                }
        ).setHeader("Szerepkör");
        grid.setMultiSort(true);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if(event.getValue() != null) {
                selectedUser = new UserEntity();
                selectedUser.setId(event.getValue().getId());
                selectedUser.setFirstName(event.getValue().getFirstName());
                selectedUser.setLastName(event.getValue().getLastName());
                selectedUser.setUsername(event.getValue().getUsername());
                selectedUser.setAuthorities(event.getValue().getAuthorities());
                binder.setBean(selectedUser);
                form.setVisible(selectedUser != null);
                deleteBtn.setEnabled(selectedUser != null);
            }
        });



        addButtonBar(grid);
        add(grid);
        addForm(grid);
    }

    private void addForm(Grid<UserEntity> grid) {
        form = new VerticalLayout();
        binder = new Binder<>(UserEntity.class);

        HorizontalLayout nameField = new HorizontalLayout();
        username = new TextField();
        nameField.add(new Text("Felhasználónév"), username);

        HorizontalLayout firstNameField = new HorizontalLayout();
        firstName = new TextField();
        firstNameField.add(new Text("Keresztnév"), firstName);

        HorizontalLayout lastNameField = new HorizontalLayout();
        lastName = new TextField();
        lastNameField.add(new Text("Vezetéknév"), lastName);

        /*HorizontalLayout authorField = new HorizontalLayout();
        comboBox = new ComboBox<>();
        comboBox.setItems(roleService.getAll());
        comboBox.setItemLabelGenerator(authorEntity -> authorEntity.getAuthority());
        authorField.add(new Text("Szerepkör"), comboBox);*/

        HorizontalLayout checkBoxGroupField = new HorizontalLayout();
        authorities = new CheckboxGroup<>();
        authorities.setItems(roleService.getAll());
        authorities.setItemLabelGenerator(RoleEntity::getAuthority);
        checkBoxGroupField.add(new Text("Szerepkörök"), authorities);


        binder.forField(username)
                .asRequired("Kötelező mező")
                .bind(UserEntity::getUsername, UserEntity::setUsername);

        binder.forField(firstName)
                .asRequired("Kötelező mező")
                .bind(UserEntity::getFirstName, UserEntity::setFirstName);

        binder.forField(lastName)
                .asRequired("Kötelező mező")
                .bind(UserEntity::getLastName, UserEntity::setLastName);

        //TODO módosításnál checkbox nem pipálódik be, ha van adott szerepkör adott felhasználóhoz
        binder.forField(authorities)
                .withValidator(value -> !value.isEmpty(), "Kötelező mező")
                .bind(UserEntity::getAuthorities, UserEntity::setAuthorities);

        /*binder.forField(comboBox)
                .withValidator(Objects::nonNull, "Kötelező mező")
                .bind(UserEntity::getAuthorities, UserEntity::setAuthorities);*/

        form.add(nameField, firstNameField, lastNameField, checkBoxGroupField, addSaveBtn(grid));
        add(form);
        form.setVisible(false);
        binder.bindInstanceFields(this);
    }

    private Button addSaveBtn(Grid<UserEntity> grid) {
        Button saveBtn = new Button("Mentés", VaadinIcon.SAFE.create());
        saveBtn.addClickListener(buttonClickEvent -> {

            try {
                binder.writeBean(selectedUser);
                if (selectedUser.getId() == null) {
                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                    String pwd = RandomStringUtils.random(15, characters);
                    selectedUser.setPassword(new BCryptPasswordEncoder().encode(pwd));

                    Dialog dialog = new Dialog();
                    dialog.add(new Text("A generált jelszó:\n" + pwd));
                    dialog.setWidth("400px");
                    dialog.setHeight("150px");
                    dialog.open();

                    service.add(selectedUser);
                    selectedUser = null;
                    Notification.show("Sikeres mentés");
                } else {
                    //TODO javítani: módosításnál már meglévő szerekört nem lehet eltávólítani
                    service.update(selectedUser);
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

    private void addButtonBar(Grid<UserEntity> grid) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        deleteBtn.addClickListener(buttonClickEvent -> {
            service.remove(selectedUser);
            Notification.show("Sikeres törlés");
            selectedUser = null;
            grid.setItems(service.getAll());
            form.setVisible(false);

        });
        deleteBtn.setEnabled(false);

        Button addBtn = new Button("Új felhasználó rögzítése", VaadinIcon.PLUS.create());
        addBtn.addClickListener(buttonClickEvent -> {
            selectedUser = new UserEntity();
            binder.setBean(selectedUser);
            form.setVisible(true);

        });
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(addBtn);
        add(horizontalLayout);
    }
}
