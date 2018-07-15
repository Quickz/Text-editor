package sample;

import javafx.application.Platform;

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
}
