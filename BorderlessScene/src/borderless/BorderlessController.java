package borderless;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller implements window controls: close, maximise, minimise, move, and Windows Aero Snap.
 * 
 * @author Nicolas Senet-Larson
 * @version 1.0
 */
public class BorderlessController {
	private Stage primaryStage;
	private Delta prevSize;
	private Delta prevPos;
	private boolean maximised;
	private boolean snapped;
	@FXML
	private Pane leftPane;
	@FXML
	private Pane rightPane;
	@FXML
	private Pane topPane;
	@FXML
	private Pane bottomPane;
	@FXML
	private Pane topLeftPane;
	@FXML
	private Pane topRightPane;
	@FXML
	private Pane bottomLeftPane;
	@FXML
	private Pane bottomRightPane;

	/**
	 * The constructor.
	 */
	public BorderlessController() {
		prevSize = new Delta();
		prevPos = new Delta();
		maximised = false;
		snapped = false;
	}

	/**
	 * Called after the FXML layout is loaded.
	 */
	@FXML
	private void initialize() {
		setResizeControl(leftPane, "left");
		setResizeControl(rightPane, "right");
		setResizeControl(topPane, "top");
		setResizeControl(bottomPane, "bottom");
		setResizeControl(topLeftPane, "top-left");
		setResizeControl(topRightPane, "top-right");
		setResizeControl(bottomLeftPane, "bottom-left");
		setResizeControl(bottomRightPane, "bottom-right");
	}

	/**
	 * Reference to main application.
	 * @param primaryStage the main application stage.
	 */
	protected void setMainApp(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * Close the application.
	 */
	protected void closeApp() {
		primaryStage.close();
	}

	/**
	 * Maximise on/off the application.
	 */
	protected void maximise() {
		Rectangle2D screen = ((Screen) Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
				primaryStage.getWidth() / 2, primaryStage.getHeight() / 2).get(0)).getVisualBounds();
		
		if (maximised) {
			if (screen.contains(prevPos.x, prevPos.y)) {	// Un-maximise if maximise was in same screen.
				primaryStage.setWidth(prevSize.x);
				primaryStage.setHeight(prevSize.y);
				primaryStage.setX(prevPos.x);
				primaryStage.setY(prevPos.y);
			} else {										// Else un-maximise and fit to new screen size.
				if (prevSize.x > screen.getWidth())
					if (primaryStage.getMinWidth() < screen.getWidth() - 20)
						primaryStage.setWidth(screen.getWidth() - 20);
					else
						primaryStage.setWidth(primaryStage.getMinWidth());
				else
					primaryStage.setWidth(prevSize.x);
				
				if (prevSize.y > screen.getHeight())
					if (primaryStage.getMinHeight() < screen.getHeight() - 20)
						primaryStage.setHeight(screen.getHeight() - 20);
					else
						primaryStage.setHeight(primaryStage.getMinHeight());
				else
					primaryStage.setHeight(prevSize.y);
				
				primaryStage.setX(screen.getMinX() + (screen.getWidth() - primaryStage.getWidth()) / 2);
				primaryStage.setY(screen.getMinY() + (screen.getHeight() - primaryStage.getHeight()) / 2);
			}
			isMaximised(false);
		} else {
			// Record position and size, and maximise.
			if (!snapped) {
				prevSize.x = primaryStage.getWidth();
				prevSize.y = primaryStage.getHeight();
				prevPos.x = primaryStage.getX();
				prevPos.y = primaryStage.getY();
			}
			primaryStage.setX(screen.getMinX());
			primaryStage.setY(screen.getMinY());
			primaryStage.setWidth(screen.getWidth());
			primaryStage.setHeight(screen.getHeight());

			isMaximised(true);
		}
	}

	/**
	 * Minimise the application.
	 */
	protected void minimise() {
		primaryStage.setIconified(true);
	}

	/**
	 * Set a node that can be pressed and dragged to move the application around.
	 * @param node the node.
	 */
	protected void setMoveControl(final Node node) {
		final Delta delta = new Delta();
		final Delta eventSource = new Delta();

		// Record drag deltas on press.
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.isPrimaryButtonDown()) {
					delta.x = mouseEvent.getX();
					delta.y = mouseEvent.getY();
					
					if (maximised || snapped) {
						delta.x = (prevSize.x * (mouseEvent.getX() / primaryStage.getWidth()));
						delta.y = (prevSize.y * (mouseEvent.getY() / primaryStage.getHeight()));
					} else {
						prevSize.x = primaryStage.getWidth();
						prevSize.y = primaryStage.getHeight();
						prevPos.x = primaryStage.getX();
						prevPos.y = primaryStage.getY();
					}
					
					eventSource.x = mouseEvent.getScreenX();
					eventSource.y = node.prefHeight(primaryStage.getHeight());
					System.out.println(eventSource.y);
				}
			}
		});
		
		// Dragging moves the application around.
		node.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.isPrimaryButtonDown()) {
					// Move x axis.
					primaryStage.setX(mouseEvent.getScreenX() - delta.x);
					
					if (snapped) {
						// Aero Snap off.
						Rectangle2D screen = ((Screen) Screen.getScreensForRectangle(mouseEvent.getScreenX(),
								mouseEvent.getScreenY(), 1, 1).get(0)).getVisualBounds();
						
						primaryStage.setHeight(screen.getHeight());
						
						if (mouseEvent.getScreenY() > eventSource.y) {
							primaryStage.setWidth(prevSize.x);
							primaryStage.setHeight(prevSize.y);
							snapped = false;
						}
					} else {
						// Move y axis.
						primaryStage.setY(mouseEvent.getScreenY() - delta.y);
					}
					
					// Aero Snap off.
					if (maximised) {
						primaryStage.setWidth(prevSize.x);
						primaryStage.setHeight(prevSize.y);
						isMaximised(false);
					}
				}
			}
		});
		
		// Maximise on double click.
		node.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getClickCount() == 2)) {
					maximise();
				}
			}
		});
		
		// Aero Snap on release.
		node.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getScreenX() != eventSource.x)) {
					Rectangle2D screen = ((Screen) Screen.getScreensForRectangle(mouseEvent.getScreenX(),
							mouseEvent.getScreenY(), 1, 1).get(0)).getVisualBounds();
					
					// Aero Snap Left.
					if (mouseEvent.getScreenX() == screen.getMinX()) {
						primaryStage.setY(screen.getMinY());
						primaryStage.setHeight(screen.getHeight());

						primaryStage.setX(screen.getMinX());
						if (screen.getWidth() / 2 < primaryStage.getMinWidth()) {
							primaryStage.setWidth(primaryStage.getMinWidth());
						} else {
							primaryStage.setWidth(screen.getWidth() / 2);
						}
						
						snapped = true;
					}
					
					// Aero Snap Right.
					else if (mouseEvent.getScreenX() == screen.getMaxX() - 1) {
						primaryStage.setY(screen.getMinY());
						primaryStage.setHeight(screen.getHeight());
						
						if (screen.getWidth() / 2 < primaryStage.getMinWidth()) {
							primaryStage.setWidth(primaryStage.getMinWidth());
						} else {
							primaryStage.setWidth(screen.getWidth() / 2);
						}
						primaryStage.setX(screen.getMaxX() - primaryStage.getWidth());

						snapped = true;
					}
					
					// Aero Snap Top.
					else if (mouseEvent.getScreenY() == screen.getMinY()) {
						primaryStage.setX(screen.getMinX());
						primaryStage.setY(screen.getMinY());
						primaryStage.setWidth(screen.getWidth());
						primaryStage.setHeight(screen.getHeight());
						isMaximised(true);
					}
				}
			}
		});
	}

	/**
	 * Set pane to resize application when pressed and dragged.
	 * @param pane the pane the action is set to.
	 * @param direction the resize direction. Diagonal: 'top' or 'bottom' + 'right' or 'left'.
	 */
	private void setResizeControl(Pane pane, final String direction) {
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.isPrimaryButtonDown()) {
					double width = primaryStage.getWidth();
					double height = primaryStage.getHeight();
					
					// Horizontal resize.
					if (direction.endsWith("left")) {
						if ((width > primaryStage.getMinWidth()) || (mouseEvent.getX() < 0)) {
							primaryStage.setWidth(width - mouseEvent.getScreenX() + primaryStage.getX());
							primaryStage.setX(mouseEvent.getScreenX());
						}
					} else if ((direction.endsWith("right"))
							&& ((width > primaryStage.getMinWidth()) || (mouseEvent.getX() > 0))) {
						primaryStage.setWidth(width + mouseEvent.getX());
					}
					
					// Vertical resize.
					if (direction.startsWith("top")) {
						if (snapped) {
							primaryStage.setHeight(prevSize.y);
							snapped = false;
						} else if ((height > primaryStage.getMinHeight()) || (mouseEvent.getY() < 0)) {
							primaryStage.setHeight(height - mouseEvent.getScreenY() + primaryStage.getY());
							primaryStage.setY(mouseEvent.getScreenY());
						}
					} else if (direction.startsWith("bottom")) {
						if (snapped) {
							primaryStage.setY(prevPos.y);
							snapped = false;
						} else if ((height > primaryStage.getMinHeight()) || (mouseEvent.getY() > 0)) {
							primaryStage.setHeight(height + mouseEvent.getY());
						}
					}
				}
			}
		});
		
		// Record application height and y position.
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ((mouseEvent.isPrimaryButtonDown()) && (!snapped)) {
					prevSize.y = primaryStage.getHeight();
					prevPos.y = primaryStage.getY();
				}
			}
		});
		
		// Aero Snap Resize.
		pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (!snapped)) {
					Rectangle2D screen = ((Screen) Screen.getScreensForRectangle(mouseEvent.getScreenX(),
							mouseEvent.getScreenY(), 1, 1).get(0)).getVisualBounds();
					
					if ((primaryStage.getY() <= screen.getMinY()) && (direction.startsWith("top"))) {
						primaryStage.setHeight(screen.getHeight());
						primaryStage.setY(screen.getMinY());
						snapped = true;
					}
					
					if ((mouseEvent.getScreenY() >= screen.getMaxY()) && (direction.startsWith("bottom"))) {
						primaryStage.setHeight(screen.getHeight());
						primaryStage.setY(screen.getMinY());
						snapped = true;
					}
				}
			}
		});
		
		// Aero Snap resize on double click.
		pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getClickCount() == 2)
						&& ((direction.equals("top")) || (direction.equals("bottom")))) {
					Rectangle2D screen = ((Screen) Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
							primaryStage.getWidth() / 2, primaryStage.getHeight() / 2).get(0)).getVisualBounds();
					
					if (snapped) {
						primaryStage.setHeight(prevSize.y);
						primaryStage.setY(prevPos.y);
						snapped = false;
					} else {
						prevSize.y = primaryStage.getHeight();
						prevPos.y = primaryStage.getY();
						primaryStage.setHeight(screen.getHeight());
						primaryStage.setY(screen.getMinY());
						snapped = true;
					}
				}
			}
		});
	}

	private void isMaximised(Boolean maximised) {
		this.maximised = maximised;
		setResizable(!maximised);
	}

	protected void setResizable(Boolean bool){
		leftPane.setDisable(!bool);
		rightPane.setDisable(!bool);
		topPane.setDisable(!bool);
		bottomPane.setDisable(!bool);
		topLeftPane.setDisable(!bool);
		topRightPane.setDisable(!bool);
		bottomLeftPane.setDisable(!bool);
		bottomRightPane.setDisable(!bool);
	}
	
	class Delta {double x,y;}
}
