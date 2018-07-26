package main;

import javafx.scene.control.TextArea;

public class DeveloperCommands
{
    /**
     * fills specified text area with
     * some sample text for testing
     **/
    public static void FillTextArea(TextArea textArea)
    {
        String text = "";
        for (int i = 1; i <= 100; i++)
        {
            text += i + ". line\n";
        }
        textArea.setText(text);
    }
}
