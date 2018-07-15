package main;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainPage
{
    public void newFile()
    {
        System.out.println("new");
    }

    public void open()
    {
        System.out.println("open");
    }

    public void save()
    {
        System.out.println("save");
    }

    public void saveAs()
    {
        System.out.println("save as");
    }

    public void exit()
    {
        Platform.exit();
    }

    @FXML
    private void openAboutPage()
    {
        try
        {
            AboutPage.generate();
        }
        catch (Exception e)
        {
            System.out.println(
                "Error in opening about page: " +
                e.getMessage());
        }
    }

    @FXML
    private void openLicensePage()
    {
        try
        {
            LicensePage.generate();
        }
        catch (Exception e)
        {
            System.out.println(
                "Error in opening license page: " +
                e.getMessage());
        }
    }
}
