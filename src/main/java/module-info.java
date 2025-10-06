module com.ursineenterprises.utilities.htmltopdf {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.slf4j;
    requires org.jsoup;
    requires openhtmltopdf.pdfbox;

    opens com.ursineenterprises.utilities.htmltopdf to javafx.fxml;
    exports com.ursineenterprises.utilities.htmltopdf;
}