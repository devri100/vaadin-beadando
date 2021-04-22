package hu.pte.ttk.vaadin.cars;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import hu.pte.ttk.vaadin.cars.menu.MenuComponent;

@Route
public class MainView extends VerticalLayout {

    public MainView() {
        add(new MenuComponent());
        Text text = new Text("Fő oldal");
        add(text);
    }
}
