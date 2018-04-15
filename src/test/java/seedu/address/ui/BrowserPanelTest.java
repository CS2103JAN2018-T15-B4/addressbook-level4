package seedu.address.ui;


import static junit.framework.TestCase.assertEquals;
import static seedu.address.ui.BrowserPanel.GOOGLE_URL;

import java.net.MalformedURLException;
import java.net.URL;

import guitests.guihandles.BrowserPanelHandle;
import guitests.guihandles.LinkedInWindowHandle;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;

//@@author x3tsunayh

public class BrowserPanelTest extends GuiUnitTest {

    private BrowserPanel browserPanel;
    private BrowserPanelHandle browserPanelHandle;

    @Before
    public void setUp() throws Exception {
        guiRobot.interact(() -> browserPanel = new BrowserPanel(null));
        Stage browserPanelStage = FxToolkit.setupStage((stage) -> stage.setScene(browserPanel.getRoot().getScene()));
        FxToolkit.showStage();
        browserPanelHandle = new BrowserPanelHandle(browserPanelStage);
    }

    @Test
    public void display() {
        URL expectedGooglePage = null;
        try {
            expectedGooglePage = new URL(GOOGLE_URL);
        } catch (MalformedURLException e) {
            throw new AssertionError("Google URL broken");
        }
        assertEquals(expectedGooglePage, browserPanelHandle.getLoadedUrl());
    }
}