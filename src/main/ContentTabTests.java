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
}
