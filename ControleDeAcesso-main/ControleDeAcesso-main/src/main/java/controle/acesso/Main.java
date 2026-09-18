package controle.acesso;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/controle/acesso/view/login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Main.class.getResource("/controle/acesso/css/estilo.css").toExternalForm());
        stage.setTitle("Controle de Acesso - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
