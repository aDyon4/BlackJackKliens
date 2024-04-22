module com.example.bjkliens {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bjkliens to javafx.fxml;
    exports com.example.bjkliens;
}