package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ContentTab
{
    public StringProperty textProperty;
    public Tab entry;

    public ContentTab(TabPane tabPane, TextArea textArea)
    {
        this(textArea);
        entry = new Tab("untitled");
        tabPane.getTabs().add(entry);
    }

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
    public int cutLine(int position, boolean copyTheLine)
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

        if (copyTheLine)
        {
            copy(lineToCut);
        }

        // assigning all of the text except the removed
        // line back to the text area
        String start = text.substring(0, previousLineEnding);
        String end = text.substring(lastLineEnding);
        setText(start + end);

        return previousLineEnding;
    }

    public int cutLine(int position)
    {
        return cutLine(position, true);
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

    /**
     * returns the number of lines
     * the content tab contains
     **/
    public int getLineCount()
    {
        String text = getText();
        int count = 1;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
            {
                count++;
            }
        }
        return count;
    }

    /**
     * returns an integer which
     * represents the length of the
     * currently selected line
     **/
    public int getLineLength(int line)
    {
        String text = getText();

        // empty content
        if (text.length() == 0)
        {
            return 0;
        }

        if (line == 1)
        {
            for (int i = 0; i < text.length(); i++)
            {
                if (text.charAt(i) == '\n')
                {
                    return i;
                }
            }
            return text.length();
        }

        int length = 0;
        int currentLine = 0;

        line--;
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
     * returns an integer (starting from 1)
     * which tells the column in a line
     * based on the position in the text
     **/
    public int getColumn(int positionInText)
    {
        String text = getText();
        int columnNumber = 0;
        for (int i = 0; i < text.length(); i++)
        {
            columnNumber++;
            if (i >= positionInText)
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
     * returns an integer (starting from 1)
     * which tells which line is in
     * the specified position
     **/
    public int getLine(int positionInText)
    {
        String text = getText();
        int lineNumber = 0;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
            {
                lineNumber++;
                if (i >= positionInText)
                {
                    return lineNumber;
                }
            }
        }
        return lineNumber + 1;
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
