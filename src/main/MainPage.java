package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class MainPage
{
    @FXML
    public TextArea content;

    public void newFile()
    {
        System.out.println("new");
    }

    public void open()
    {
        Stage fileDialog = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");


        File file = fileChooser.showOpenDialog(fileDialog);

        if (file != null)
        {
            loadContent(file.getPath());
        }
    }

    /**
     * loads the text inside
     * the text editor from the specified path
     **/
    private void loadContent(String path)
    {
        FileReader reader = null;
        BufferedReader bufferedReader = null;

        try
        {
            reader = new FileReader(path);
            bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.appendText(line + System.lineSeparator());
            }
        }
        catch (IOException e)
        {
            System.out.println(
                "Error in reading a file: " +
                e.getMessage());
        }
        finally
        {
            try
            {
                if (bufferedReader != null)
                {
                    bufferedReader.close();
                }
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                System.out.println(
                    "Error in closing file reader: " +
                    e.getMessage());
            }
        }
    }

    public void save()
    {
        // TODO:
        // add ability to save a file without
        // repeatedly having to open a file dialog
        // to specify the path
        saveAs();
    }

    public void saveAs()
    {
        Stage fileDialog = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Text Documents (*.txt)", ".txt"));

        File file = fileChooser.showSaveDialog(fileDialog);

        if (file != null)
        {
            saveContent(file.getPath());
        }
    }

    /**
     * saves the text inside
     * the text editor to the specified path
     **/
    private void saveContent(String path)
    {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;

        try
        {
            writer = new FileWriter(path);
            bufferedWriter = new BufferedWriter(writer);

            String contentText = content
                .getText()
                .replaceAll("\n", System.lineSeparator());

            bufferedWriter.write(contentText);
        }
        catch (IOException e)
        {
            System.out.println(
                "Error in writing to a file: " +
                e.getMessage());
        }
        finally
        {
            try
            {
                if (bufferedWriter != null)
                {
                    bufferedWriter.close();
                }
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException e)
            {
                System.out.println(
                    "Error in closing file writer: " +
                    e.getMessage());
            }
        }
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

    private Boolean controlKeyDown = false;

    @FXML
    private void onContentKeyPress(KeyEvent e)
    {
        if (controlKeyDown && e.getCode() == KeyCode.X)
        {
            /*
             * TODO:
             * refactor or move this dirty piece
             * of code on to a separate function
             * ---------------------------------
             * consider writing some tests
             **/
            String contentText = content.getText();

            if (contentText.length() == 0)
            {
                return;
            }

            int position = content.getCaretPosition();
            int lineNumber = 0;
            int previousLineEnding = 0;
            int lastLineEnding = contentText.length() - 1;

            // finding out the line that has to be deleted
            for (int i = 0; i < contentText.length(); i++)
            {
                if (contentText.charAt(i) == '\n')
                {
                    lineNumber++;
                    if (i >= position)
                    {
                        lastLineEnding = i;
                        break;
                    }
                    previousLineEnding = i;
                }
            }

            previousLineEnding = lineNumber < 2 ? 0 : previousLineEnding + 1;
            lastLineEnding++;

            // removing the line
            String lineToCut = "";
            for (int z = previousLineEnding; z < lastLineEnding; z++)
            {
                lineToCut += contentText.charAt(z);
            }

            System.out.println("cutting: " + lineToCut);
            System.out.println("line number: " + lineNumber);
            System.out.println("previous: " + previousLineEnding + ", last: " + lastLineEnding);

            String start = contentText.substring(0, previousLineEnding);
            String end = contentText.substring(lastLineEnding);
            content.setText(start + end);

            // moving the | thingy position
            content.positionCaret(previousLineEnding);
        }
        else if (e.getCode() == KeyCode.CONTROL)
        {
            controlKeyDown = true;
        }
    }

    @FXML
    private void onContentKeyRelease(KeyEvent e)
    {
        if (e.getCode() == KeyCode.CONTROL)
        {
            controlKeyDown = false;
        }
    }
}
