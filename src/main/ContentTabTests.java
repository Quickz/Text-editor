package main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ContentTabTests
{
    @Test
    @DisplayName("╯°□°）╯")
    public void emptyContentLineLength()
    {
        ContentTab contentTab = new ContentTab();
        int lineLength = contentTab.getLineLength(1);
        Assertions.assertEquals(0, lineLength);
    }

    @Test
    public void singleLineContentLineLength()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText("a short sentence");
        int lineLength = contentTab.getLineLength(1);
        Assertions.assertEquals(16, lineLength);
    }

    @Test
    public void multiLineContentLineLength()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText(
            "several lines of text\n" +
            "that are written for a test\n" +
            "to make sure the app doesn't accidentally\n" +
            "get turned in to a toaster");
        int lineLength = contentTab.getLineLength(3);
        Assertions.assertEquals(41, lineLength);
    }

    @Test
    public void lineCountOfEmptyContent()
    {
        ContentTab contentTab = new ContentTab();
        int lineCount = contentTab.getLineCount();
        Assertions.assertEquals(1, lineCount);
    }

    @Test
    public void lineCountOfContent()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText(
            "Some fabulous\n" +
            "text for testing\n" +
            "line count method");
        int lineCount = contentTab.getLineCount();
        Assertions.assertEquals(3, lineCount);
    }

    @Test
    public void cutLinesFromContent()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText(
            "Several lines\n" +
            "of text\n" +
            "for testing cut line method\n" +
            "and make sure it works\n" +
            "properly");

        contentTab.cutLine(15, false);
        contentTab.cutLine(50, false);

        String expected =
            "Several lines\n" +
            "for testing cut line method\n" +
            "properly";

        Assertions.assertEquals(expected, contentTab.getText());
    }

    @Test
    public void getColumnInEmptyContent()
    {
        ContentTab contentTab = new ContentTab();
        int column = contentTab.getColumn(0);
        Assertions.assertEquals(1, column);
    }

    @Test
    public void getColumnInContent()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText(
            "Some juicy\n" +
            "text\n" +
            "for testing\n" +
            "the get clumn\n" +
            "method");
        int column = contentTab.getColumn(12);
        Assertions.assertEquals(2, column);
    }

    @Test
    public void getLineInEmptyContent()
    {
        ContentTab contentTab = new ContentTab();
        int line = contentTab.getLine(0);
        Assertions.assertEquals(1, line);
    }

    @Test
    public void getLineInContent()
    {
        ContentTab contentTab = new ContentTab();
        contentTab.setText(
            "Bunch of words\n" +
            "For testing get line method");
        int line = contentTab.getLine(15);
        Assertions.assertEquals(2, line);
    }
}
