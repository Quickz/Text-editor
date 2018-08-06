package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ContentTab
{
    public StringProperty textProperty;

    public ContentTab(TextArea textArea)
    {
        textProperty = new SimpleStringProperty("");
        textProperty.bindBidirectional(textArea.textProperty());
    }

    public ContentTab()
    {
        textProperty = new SimpleStringProperty("");
    }

    /**
     * removes line of text
     * at the specified position
     * -------------------------
     * returns a new position
     * after the changes
     **/
    public int cutLine(int position)
    {
        String text = getText();
        if (text.length() == 0)
        {
            return position;
        }

        int lineNumber = 0;
        int previousLineEnding = 0;
        int lastLineEnding = text.length() - 1;

        // finding out the line that has to be deleted
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
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
            lineToCut += text.charAt(z);
        }

        copy(lineToCut);

        // assigning all of the text except the removed
        // line back to the text area
        String start = text.substring(0, previousLineEnding);
        String end = text.substring(lastLineEnding);
        setText(start + end);

        return previousLineEnding;
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

    public String getText()
    {
        return textProperty.get();
    }

    public void setText(String value)
    {
        textProperty.set(value);
    }
}
