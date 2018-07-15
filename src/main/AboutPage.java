package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;

public class AboutPage
{
    public static void generate() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("AboutPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void openSourceCode()
    {
        String url = "https://github.com/Quickz/Text-editor";

        try
        {
            if (Desktop.isDesktopSupported())
            {
                Desktop.getDesktop().browse(new URI(url));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Error in browsing the source code web page");
        }
    }
}
