# BorderlessScene
JavaFX "Undecorated" Scene with all basic window controls including Windows Aero Snap.

Create an undecorated JavaFX Scene with move, resize, minimise, maximise, close and Windows Aero Snap controls.

Usage:
```Java
// Constructor using your primary stage and the root Parent of your content.
BorderlessScene scene = new BorderlessScene(yourPrimaryStage, yourParent);
yourPrimaryStage.setScene(scene); // Set the scene to your stage and you're done!

// To give window controls to your buttons:
scene.setCloseButton(button);
scene.setMaximiseButton(button);
scene.setMinimiseButton(button);

// To move the window around by pressing a node:
scene.setMoveControl(yourNode);

// To disable resize:
scene.setResize(false);

// To switch the content during runtime:
scene.setContent(yourNewParent);
```
