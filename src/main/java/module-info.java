module org.iesinfanta.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.scripting;


    opens org.iesinfanta.calculator to javafx.fxml;
    exports org.iesinfanta.calculator;
}