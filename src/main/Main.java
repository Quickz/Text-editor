package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        initializeApp(primaryStage);
    }

    private void initializeApp(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(new Scene(root, 300, 275));

        MainPage controller = loader.getController();
        controller.onStageLoad(primaryStage);

        primaryStage.show();
    }
}
