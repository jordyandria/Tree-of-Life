module src.ahmedjordypiia {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jsoup;

    exports src.ahmedjordypiia.Vue to javafx.graphics;

    exports src.ahmedjordypiia.Controle;
    exports src.ahmedjordypiia.Main;
    opens src.ahmedjordypiia.Main to javafx.fxml;
    opens src.ahmedjordypiia.Controle to javafx.fxml;
}