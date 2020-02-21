module ru.vinokurov {
    requires javafx.controls;
    requires javafx.fxml;

    opens ru.vinokurov to javafx.fxml;
    exports ru.vinokurov;
}