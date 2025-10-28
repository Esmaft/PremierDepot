module sidTp1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens sidTp1 to javafx.fxml;
    exports sidTp1;
}
