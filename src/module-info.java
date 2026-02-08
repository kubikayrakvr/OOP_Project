/**
 * 
 */
/**
 * 
 */
module NYP_Proje {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.graphics; 
    requires javafx.base;
	requires org.junit.jupiter.api;

    opens GUI to javafx.controls, javafx.fxml, javafx.media, javafx.web, javafx.swing, javafx.graphics, javafx.base;
    exports GUI;
}