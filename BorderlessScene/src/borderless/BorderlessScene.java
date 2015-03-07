package borderless;

import java.io.IOException;

import borderless.BorderlessController.Delta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Undecorated JavaFX Scene with implemented move, resize, minimise, maximise and Windows Aero Snap controls.
 * 
 * Usage:
 * <pre>
 * {@code
 * // Constructor using your primary stage and the root Parent of your content.
 * BorderlessScene scene = new BorderlessScene(yourPrimaryStage, yourParent);
 * yourPrimaryStage.setScene(scene); // Set the scene to your stage and you're done!
 * 
 * // Maximise (on/off) and minimise the application:
 * scene.maximise();
 * scene.minimise();
 * 
 * // To move the window around by pressing a node:
 * scene.setMoveControl(yourNode);
 * 
 * // To disable resize:
 * scene.setResizable(false);
 * 
 * // To switch the content during runtime:
 * scene.setContent(yourNewParent);
 * 
 * // Check if maximised:
 * Boolean bool = scene.isMaximised();
 * 
 * // Get windowed size and position:
 * scene.getWindowedSize();
 * scene.getWindowedPosition();
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
	 *  Toggle to maximise the application.
	 */
	public void maximise() {
		controller.maximise();
	}

	/**
	 * Minimise the application to the taskbar.
	 */
	public void minimise() {
		controller.minimise();
	}
	
	/**
	 * Disable/enable the resizing of your application. Enabled by default.
	 * @param bool false to disable, true to enable.
	 */
	public void setResizable(Boolean bool) {
		controller.setResizable(bool);
	}
	
	/**
	 * Check the maximised state of the application.
	 * @return true if the window is maximised.
	 */
	public Boolean isMaximised() {
		return controller.maximised;
	}
	
	/**
	 * Returns the width and height of the application when windowed.
	 * @return instance of Delta class. Delta.x = width, Delta.y = height.
	 */
	public Delta getWindowedSize() {
		return controller.prevSize;
	}
	
	/**
	 * Returns the x and y position of the application when windowed.
	 * @return instance of Delta class. Use Delta.x and Delta.y.
	 */
	public Delta getWindowedPositon() {
		return controller.prevPos;
	}
}
