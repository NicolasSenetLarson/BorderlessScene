package borderless;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Create an undecorated JavaFX Scene with move, resize, minimise, maximise, close and Windows Aero Snap controls.
 * 
 * Usage:
 * <pre>
 * {@code
 * // Constructor using your primary stage and the root Parent of your content.
 * BorderlessScene scene = new BorderlessScene(yourPrimaryStage, yourParent);
 * yourPrimaryStage.setScene(scene); // Set the scene to your stage and you're done!
 * 
 * // To give window controls to your buttons:
 * scene.setCloseButton(button);
 * scene.setMaximiseButton(button);
 * scene.setMinimiseButton(button);
 * 
 * // To move the window around by pressing a node:
 * scene.setMoveControl(yourNode);
 * 
 * // To disable resize:
 * scene.setResize(false);
 * 
 * // To switch the content during runtime:
 * scene.setContent(yourNewParent);
 * }
 * </pre>
 * 
 * @author Nicolas Senet-Larson
 * @version 1.0
 */
public class BorderlessScene extends Scene {
	private BorderlessController controller;
	private AnchorPane root;
	
	/**
	 * The constructor.
	 * @param primaryStage your stage.
	 * @param root the root Parent of your content.
	 */
	public BorderlessScene(Stage primaryStage, Parent root) {
		super(new Pane());
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Borderless.fxml"));
			this.root = ((AnchorPane) loader.load());

			setRoot(this.root);
			setContent(root);

			this.controller = ((BorderlessController) loader.getController());
			this.controller.setMainApp(primaryStage);

			primaryStage.initStyle(StageStyle.UNDECORATED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change the content of the scene.
	 * @param content the root Parent of your new content.
	 */
	public void setContent(Parent content) {
		this.root.getChildren().remove(0);
		this.root.getChildren().add(0, content);
		AnchorPane.setLeftAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setTopAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setRightAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setBottomAnchor(content, Double.valueOf(0.0D));
	}

	/**
	 * Set a node that can be pressed and dragged to move the application around.
	 * @param node the node.
	 */
	public void setMoveControl(Node node) {
		this.controller.setMoveControl(node);
	}

	/**
	 * Set the default close button of your application.
	 * @param button the button that closes you application.
	 */
	public void setCloseButton(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				controller.closeApp();
			}
		});
	}

	/**
	 *  Set the maximise window button.
	 * @param button the button that maximises your application.
	 */
	public void setMaximiseButton(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				controller.maximise();
			}
		});
	}

	/**
	 * Set the minimise window button.
	 * @param button the button that minimises your application.
	 */
	public void setMinimiseButton(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				controller.minimise();
			}
		});
	}
	
	/**
	 * Disable/enable the resizing of your application. Enabled by default.
	 * @param bool false to disable, true to enable.
	 */
	public void setResize(Boolean bool) {
		this.controller.setResizable(bool);
	}
}
