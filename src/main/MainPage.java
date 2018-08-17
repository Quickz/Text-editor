package main;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;

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

    @FXML
    private TabPane contentTabPane;

    private Stage stage;

    private ArrayList<ContentTab> contentTabs = new ArrayList<>();
    private int selectedTab = 0;

    private File currentFile;

    // true if content was modified
    // since the last time it was modified
    // or set to a new one
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

        addNewContentTab();

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

    private void addNewContentTab()
    {
        contentTabs.add(new ContentTab(contentTabPane, content));
        contentTabs
            .get(selectedTab)
            .entry
            .setOnCloseRequest(e ->
                onTabClose(e, contentTabs.get(selectedTab)));
    }

    /**
     * runs when a tab is closed
     * TODO: refactor this code
     **/
    private void onTabClose(Event x, ContentTab tab)
    {
        try
        {
            if (!contentWasModified)
            {
                contentTabs.remove(tab);
                if (contentTabs.size() == 0)
                {
                    addNewContentTab();
                }

                generateNewFile();
                return;
            }

            String response = SaveFileWarning.generate();

            if (response.equals("save"))
            {
                boolean savedSuccessfully = save();
                if (savedSuccessfully)
                {
                    contentTabs.remove(tab);
                    if (contentTabs.size() == 0)
                    {
                        addNewContentTab();
                    }

                    generateNewFile();
                }
                else
                {
                    x.consume();
                }
            }
            else if (response.equals("dontSave"))
            {
                contentTabs.remove(tab);
                if (contentTabs.size() == 0)
                {
                    addNewContentTab();
                }

                generateNewFile();
            }
            else
            {
                x.consume();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
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
        int lineNumber = contentTabs.get(selectedTab).getLine((int)position);

        updateBottomColumnNumber();
        updateBottomLengthNumber();

        // updating scroll position since there may be
        // new line entry from udating line number count
        setLineNumberScrollValueToContentValue();

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
            ", Column: " + contentTabs.get(selectedTab).getColumn(position));
    }

    /**
     * updates the length value at the bottom
     * of the currently selected line
     **/
    private void updateBottomLengthNumber()
    {
        int line = contentTabs.get(selectedTab).getLine(content.getCaretPosition());
        bottomLineLengthNumber.setText(
            ", Length: " +
            contentTabs.get(selectedTab).getLineLength(line));
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
        setLineNumberScrollValueToContentValue();
    }

    /**
     * assigns the content scroll position
     * to the line number container scroll
     * position
     **/
    private void setLineNumberScrollValueToContentValue()
    {
        ScrollPane pane = ((ScrollPane)content.getChildrenUnmodifiable().get(0));
        lineNumberScrollPane.setVvalue(pane.getVvalue());
    }

    /**
     * adds or removes line numbers (labels)
     * until their count matches the
     * line count of the content
     **/
    private void updateLineNumberCount()
    {
        int lineCount = contentTabs.get(selectedTab).getLineCount();
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

        int lineNumber = contentTabs.get(selectedTab).getLine(content.getCaretPosition());
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
        label.setMinWidth(45);
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

    @FXML
    private void newFile()
    {
        try
        {
            if (!contentWasModified)
            {
                generateNewFile();
                return;
            }

            String response = SaveFileWarning.generate();

            if (response.equals("save"))
            {
                boolean savedSuccessfully = save();
                if (savedSuccessfully)
                {
                    generateNewFile();
                }
            }
            else if (response.equals("dontSave"))
            {
                generateNewFile();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void generateNewFile()
    {
        currentFile = null;
        content.clear();
        contentWasModified = false;
        contentTabs.get(selectedTab).entry.setText("untitled");
    }

    @FXML
    private void open()
    {
        Stage fileDialog = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");

        File file = fileChooser.showOpenDialog(fileDialog);

        if (file != null)
        {
            currentFile = file;
            loadContent(file.getPath());
            contentWasModified = false;
            contentTabs.get(selectedTab).entry.setText(file.getName());
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

    /**
     * saves current text content
     * returns true if it was successfully saved
     **/
    @FXML
    private boolean save()
    {
        if (currentFile == null)
        {
            return saveAs();
        }
        else
        {
            saveContent(currentFile.getPath());
            contentWasModified = false;
            contentTabs.get(selectedTab).entry.setText(currentFile.getName());
            return true;
        }
    }

    /**
     * saves current text content
     * returns true if it was successfully saved
     **/
    @FXML
    private boolean saveAs()
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
            currentFile = file;
            contentWasModified = false;
            saveContent(file.getPath());
            contentTabs.get(selectedTab).entry.setText(file.getName());
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

    @FXML
    private void exit()
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
    private void onKeyPress(KeyEvent e)
    {
        if (controlKeyDown &&
            e.getCode() == KeyCode.X &&
            content.getSelectedText().isEmpty())
        {
            int newCaretPosition = contentTabs.get(selectedTab)
                .cutLine(content.getCaretPosition());

            // moving the | thingy position
            content.positionCaret(newCaretPosition);
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
    private void onContentKeyTyped()
    {
        updateLineNumberCount();
    }

    @FXML
    private void onKeyRelease(KeyEvent e)
    {
        if (e.getCode() == KeyCode.CONTROL)
        {
            controlKeyDown = false;
        }
    }
}
