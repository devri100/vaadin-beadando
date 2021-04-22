package hu.pte.ttk.vaadin.cars.menu;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import hu.pte.ttk.vaadin.cars.security.SecurityUtils;

public class MenuComponent extends HorizontalLayout {

    public MenuComponent() {
        Anchor main = new Anchor();
        main.setText("Főoldal");
        main.setHref("/");
        add(main);

        Anchor car = new Anchor();
        car.setText("Gépjárművek");
        car.setHref("/car");
        add(car);

        Anchor manufacturer = new Anchor();
        manufacturer.setText("Gyártók");
        manufacturer.setHref("/manufacturer");
        add(manufacturer);

        if(SecurityUtils.isAdmin()){
            Anchor user = new Anchor();
            user.setText("Felhasználók");
            user.setHref("/user");
            add(user);
        }
    }
}
