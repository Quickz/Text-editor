package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaveFileWarning
{
    private Stage stage;

    private enum Status
    {
        save,
        dontSave,
        cancel
    }

    private static Status status = Status.cancel;

    /**
     * basically runs after the page has loaded
     * it runs after initialize method
     **/
    public void onStageLoad(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    private void save()
    {
        status = Status.save;
        stage.close();
    }

    @FXML
    private void dontSave()
    {
        status = Status.dontSave;
        stage.close();
    }

    @FXML
    private void cancel()
    {
        status = Status.cancel;
        stage.close();
    }

    public static String generate() throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("SaveFileWarning.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Save changes?");
        stage.setResizable(false);
        stage.setScene(scene);

        SaveFileWarning controller = loader.getController();
        controller.onStageLoad(stage);

        stage.showAndWait();

        return status.toString();
    }
}
