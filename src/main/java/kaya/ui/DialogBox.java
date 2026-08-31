package kaya.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message together with a label identifying its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label avatar;

    /**
     * Loads the reusable dialog layout and fills it with one message.
     *
     * @param text the message to display
     * @param speaker the short speaker label
     */
    private DialogBox(String text, String speaker) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout", exception);
        }
        dialog.setText(text);
        avatar.setText(speaker);
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text the user's message
     * @return the user dialog
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You");
    }

    /**
     * Creates a left-aligned dialog for Kaya.
     *
     * @param text Kaya's response
     * @return Kaya's dialog
     */
    public static DialogBox getKayaDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "K");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Places the speaker label on the left for Kaya's messages.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
