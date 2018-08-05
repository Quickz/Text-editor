package main;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

public class MainPage
{
    @FXML
    public TextArea content;

    @FXML
    private BorderPane mainContainer;

    @FXML
    private VBox lineNumberContainer;

    @FXML
    private ScrollPane lineNumberScrollPane;

    @FXML
    private Label bottomLineNumber;

    @FXML
    private Label bottomColumnNumber;

    @FXML
    private Label bottomLineLengthNumber;

    private Stage stage;

    // true if content was modified
    // since the last time it was modified
    // or set to a new one
    private String currentSaveDirectory;

    private boolean contentWasModified = false;

    // number of the line that is currently selected
    private int selectedLine = 1;

    @FXML
    private void initialize()
    {
        //DeveloperCommands.FillTextArea(content);

        content.scrollTopProperty().addListener(
            (obs, oldVal, newVal) -> onContentScroll());
        lineNumberScrollPane.vvalueProperty().addListener(
            (obs, oldVal, newVal) -> onLineNumberScroll());

        content
            .caretPositionProperty()
            .addListener((obs, oldVal, newVal) ->
                onContentCaretPositionChange(newVal));

        content
                .textProperty()
                .addListener(e -> onContentTextChange());

        lineNumberScrollPane
            .focusedProperty()
            .addListener(e -> onLineNumberContainerFocus());

        // made the hbox element stretch based
        // on the content text field
        HBox.setHgrow(content, Priority.ALWAYS);
    }

    /**
     * basically runs after the page has loaded
     * it runs after initialize method
     **/
    public void onStageLoad(Stage stage)
    {
        this.stage = stage;

        // called when closing app using X button
        stage.setOnCloseRequest(e -> onCloseButtonClick(e));

        stage.getScene().addEventFilter(
            KeyEvent.KEY_PRESSED, e -> onKeyPress(e));
        stage.getScene().addEventFilter(
            KeyEvent.KEY_RELEASED, e -> onKeyRelease(e));

        updateLineNumberCount();
        updateBottomColumnNumber();
        updateBottomLengthNumber();
    }

    /**
     * changes focus to content
     *
     **/
    private void onLineNumberContainerFocus()
    {
        content.requestFocus();
    }

    /**
     * called when the caret (| thingy)
     * position in the content changes
     **/
    private void onContentCaretPositionChange(Number position)
    {
        int lineNumber = getCurrentLine(content, (int)position);

        updateBottomColumnNumber();
        updateBottomLengthNumber();

        // if false, a new line was created, so setting line
        // number active at updateLineNumberCount() method instead
        if (lineNumberContainer.getChildren().size() >= lineNumber)
        {
            setLineNumberLabelActive(lineNumber);
        }
    }

    /**
     * highlights the specified line number label
     * and removes the highlight from the previous one
     * -----------------------------------------------
     * used to clearly show which line is currently selected
     **/
    private void setLineNumberLabelActive(int number)
    {
        var lineNumberLabels = lineNumberContainer.getChildren();

        // de-highlighting previous active line
        lineNumberLabels
            .get(selectedLine - 1)
            .getStyleClass()
            .remove("lineNumberActive");

        // highlighting the newly selected line
        lineNumberLabels
            .get(number - 1)
            .getStyleClass()
            .add("lineNumberActive");

        // saving the active line number
        selectedLine = number;

        updateBottomLineNumber();
    }

    /**
     * updates the line
     * number at the bottom
     **/
    private void updateBottomLineNumber()
    {
        bottomLineNumber.setText("Line: " + selectedLine);
    }

    /**
     * updates the column
     * number at the bottom
     **/
    private void updateBottomColumnNumber()
    {
        int position = content.getCaretPosition();
        bottomColumnNumber.setText(
            ", Column: " + getCurrentColumn(content, position));
    }

    /**
     * updates the length value at the bottom
     * of the currently selected line
     **/
    private void updateBottomLengthNumber()
    {
        int position = getCurrentLine(content, content.getCaretPosition());
        bottomLineLengthNumber.setText(
            ", Length: " +
            getCurrentLineLength(content, position));
    }

    /**
     * returns an integer (starting from 1)
     * which tells which line is currently selected
     **/
    private int getCurrentLine(TextArea textArea, int position)
    {
        String text = textArea.getText();
        int lineNumber = 0;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
            {
                lineNumber++;
                if (i >= position)
                {
                    return lineNumber;
                }
            }
        }
        return lineNumber + 1;
    }

    /**
     * returns an integer (starting from 1)
     * which tells which column in a line
     * is currently selected
     **/
    private int getCurrentColumn(TextArea textArea, int position)
    {
        String text = textArea.getText();
        int columnNumber = 0;
        for (int i = 0; i < text.length(); i++)
        {
            columnNumber++;
            if (i >= position)
            {
                return columnNumber;
            }

            if (text.charAt(i) == '\n')
            {
                columnNumber = 0;
            }
        }
        return columnNumber + 1;
    }

    /**
     * returns an integer which
     * represents the length of the
     * currently selected line
     **/
    private int getCurrentLineLength(TextArea textArea, int line)
    {
        String text = textArea.getText();

        // empty content
        if (text.length() == 0)
        {
            return 0;
        }

        int length = 0;
        int currentLine = 0;

        if (line != 1)
        {
            line--;
        }

        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
            {
                if (line == currentLine)
                {
                    return length - 1;
                }
                length = 0;
                currentLine++;
            }
            length++;
        }
        return length - 1;
    }

    /**
     * when scrolling line number container
     * the content element mirrors the scroll position
     **/
    private void onLineNumberScroll()
    {
        ScrollPane pane = ((ScrollPane)content.getChildrenUnmodifiable().get(0));
        pane.setVvalue(lineNumberScrollPane.getVvalue());
    }

    /**
     * when scrolling content text area
     * the line number container element
     * mirrors the scroll position
     **/
    private void onContentScroll()
    {
        ScrollPane pane = ((ScrollPane)content.getChildrenUnmodifiable().get(0));
        lineNumberScrollPane.setVvalue(pane.getVvalue());
    }

    /**
     * adds or removes line numbers
     * until their count matches the
     * line count of the content
     **/
    private void updateLineNumberCount()
    {
        int lineCount = getContentLineCount();
        int numberCount = lineNumberContainer.getChildren().size();

        // removing if too many
        // (the user deleted some)
        if (lineCount < numberCount)
        {
            lineNumberContainer.getChildren().remove(lineCount, numberCount);
            return;
        }

        // adding if not enough
        // (the user added new ones)
        for (int i = numberCount; i < lineCount; i++)
        {
            addLineNumberEntry();
        }


        int lineNumber = getCurrentLine(
            content,
            (int)content.getCaretPosition());

        setLineNumberLabelActive(lineNumber);
    }

    /**
     * creates a label on the side
     * which displays the number of the
     * text line
     **/
    private void addLineNumberEntry()
    {
        int number = lineNumberContainer.getChildren().size() + 1;
        Label label = new Label(Integer.toString(number));
        label.setMinWidth(40);
        label.setMinHeight(16);

        label.setOnMouseClicked(e -> lineNumberLabelOnClick(number));

        label.setAlignment(Pos.BASELINE_RIGHT);
        lineNumberContainer.getChildren().add(label);
    }

    /**
     * selects the whole line of the
     * content text that belongs to the label
     **/
    private void lineNumberLabelOnClick(int number)
    {
        // first line selected
        if (number == 1)
        {
            content.positionCaret(0);
            return;
        }

        // used to select the previous line
        // will then modify position forward to select
        // the beginning of the line instead of the end
        number--;

        String contentText = content.getText();
        int currentLine = 0;
        int position = 0;

        while (position < contentText.length())
        {
            if (contentText.charAt(position) == '\n')
            {
                currentLine++;
                if (currentLine >= number)
                {
                    break;
                }
            }
            position++;
        }

        // position of the start of the line
        position++;

        // moving the car position
        content.positionCaret(position);

        int endPosition = position;

        while (endPosition < contentText.length() &&
               contentText.charAt(endPosition) != '\n')
        {
            endPosition++;
        }

        // selecting content from current caret
        // position to the specified position
        content.selectPositionCaret(endPosition + 1);
    }

    public void newFile()
    {
        try
        {
            if (!contentWasModified)
            {
                currentSaveDirectory = null;
                content.clear();
                contentWasModified = false;
                return;
            }

            String response = SaveFileWarning.generate();

            if (response.equals("save"))
            {
                boolean savedSuccessfully = save();
                if (savedSuccessfully)
                {
                    currentSaveDirectory = null;
                    content.clear();
                    contentWasModified = false;
                }
            }
            else if (response.equals("dontSave"))
            {
                currentSaveDirectory = null;
                content.clear();
                contentWasModified = false;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }

    public void open()
    {
        Stage fileDialog = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");

        File file = fileChooser.showOpenDialog(fileDialog);

        if (file != null)
        {
            currentSaveDirectory = file.getPath();
            loadContent(file.getPath());
            contentWasModified = false;
        }
    }

    /**
     * returns the number of lines
     * the content text area contains
     **/
    private int getContentLineCount()
    {
        String contentText = content.getText();
        int count = 1;
        for (int i = 0; i < contentText.length(); i++)
        {
            if (contentText.charAt(i) == '\n')
            {
                count++;
            }
        }
        return count;
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

    /**
     * saves current text content
     * returns true if it was successfully saved
     **/
    public boolean save()
    {
        if (currentSaveDirectory == null)
        {
            return saveAs();
        }
        else
        {
            saveContent(currentSaveDirectory);
            contentWasModified = false;
            return true;
        }
    }

    /**
     * saves current text content
     * returns true if it was successfully saved
     **/
    public boolean saveAs()
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
            currentSaveDirectory = file.getPath();
            contentWasModified = false;
            saveContent(file.getPath());
            return true;
        }
        return false;
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

    /**
     * called when closing
     * the app using the X button
     * at the top right corner
     **/
    private void onCloseButtonClick(WindowEvent e)
    {
        // canceling the default functionality
        // of the button
        e.consume();
        // exiting as usual using a written method
        exit();
    }

    public void exit()
    {
        try
        {
            if (!contentWasModified)
            {
                Platform.exit();
                return;
            }

            String response = SaveFileWarning.generate();

            if (response.equals("save"))
            {
                boolean savedSuccessfully = save();
                if (savedSuccessfully)
                {
                    Platform.exit();
                }
            }
            else if (response.equals("dontSave"))
            {
                Platform.exit();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
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
        updateLineNumberCount();
    }

    @FXML
    private void onKeyPress(KeyEvent e)
    {
        if (controlKeyDown &&
            e.getCode() == KeyCode.X &&
            content.getSelectedText().isEmpty())
        {
            cutSelectedTextAreaLine(content);
        }
        else if (e.getCode() == KeyCode.CONTROL)
        {
            controlKeyDown = true;
        }
    }

    /**
     * called when the text inside
     * the content changes
     **/
    private void onContentTextChange()
    {
        contentWasModified = true;
    }

    @FXML
    private void onKeyRelease(KeyEvent e)
    {
        if (e.getCode() == KeyCode.CONTROL)
        {
            controlKeyDown = false;
        }
    }

    /**
     * removes selected line inside the
     * specified text area
     **/
    private void cutSelectedTextAreaLine(TextArea textArea)
    {
        String contentText = textArea.getText();

        if (contentText.length() == 0)
        {
            return;
        }

        int position = textArea.getCaretPosition();
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

        String lineToCut = "";
        for (int z = previousLineEnding; z < lastLineEnding; z++)
        {
            lineToCut += contentText.charAt(z);
        }

        copy(lineToCut);

        // assigning all of the text except the removed
        // line back to the text area
        String start = contentText.substring(0, previousLineEnding);
        String end = contentText.substring(lastLineEnding);
        textArea.setText(start + end);

        // moving the | thingy position
        textArea.positionCaret(previousLineEnding);
    }

    /**
     * assigns the specified text to the
     * copy clipboard
     * ---------------------------------
     * user can paste whatever has been copied
     **/
    private void copy(String text)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);

        clipboard.setContent(content);
    }
}
