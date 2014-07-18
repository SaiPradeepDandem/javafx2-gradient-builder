package com.javafx.gradientbuilder.application;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GradientBuilderApp extends Application {

	// Root Node of the application.
	private BorderPane root;
	
	// Shapes(Panes) to which the gradient is applied.
	private StackPane rectangle;
	private StackPane circle;
	
	// Instance Variables
	private enum GradientType { LINEAR, RADIAL };
	private LinearSettingsLayout linearSettingLayout;
	private RadialSettingsLayout radialSettingLayout;
	private StackPane settingsContainer;
	
	// Observable Property to determine the type of the current selected gradient.
	private SimpleObjectProperty<GradientType> gradientType = new SimpleObjectProperty<GradientType>();
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		configureSceneAndStage(stage);
		configureCenter();
		configureToolBar();
	}

	/**
	 * Configures the Scene and Stage for the application.
	 */
	private void configureSceneAndStage(Stage stage){
		// Initializing the root node
		root = new BorderPane();
		root.autosize();
		Scene scene = new Scene(root, Color.WHITE);
		scene.getStylesheets().add("styles/gradientbuilder.css");
		
		// Default settings for the stage.
		stage.setTitle("Gradient Builder");
		stage.setWidth(1200);
	    stage.setHeight(650);
	    stage.setScene(scene);
	    stage.show();
	}

	/**
	 * Configures the center part(body) of the application.
	 */
	private void configureCenter() {
		// Getting the left side top pane. (Rectangle's view)
		StackPane rectanglePane = configureRectanglePane();
		
		// Getting the left side bottom pane. (Circle's view)
		StackPane circlePane = configureCirclePane();
		
		// Getting the right side settings pane.
		ScrollPane rightPane = configureGradientSettings();
		
		SplitPane leftPane = new SplitPane();
		leftPane.setOrientation(Orientation.VERTICAL);
		leftPane.getItems().addAll(rectanglePane, circlePane);
		
		SplitPane mainPane = new SplitPane();
		mainPane.getItems().addAll(leftPane,rightPane);
		
		// Setting the entire layout as the center to the root(BorderPane) node.
		root.setCenter(mainPane);
	}
	
	/**
	 * Configures the tool bar of the application.
	 */
	private void configureToolBar() {
		// Getting the "Custom" radio buttons for the Gradient types.
		final CustomRadioButton linearButton = new CustomRadioButton("Linear");
		linearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				gradientType.set(GradientType.LINEAR);
			}
		});
		final CustomRadioButton radialButton = new CustomRadioButton("Radial");
		radialButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				gradientType.set(GradientType.RADIAL);
			}
		});
		
		// When the gradient type is changed, Listener to switch the layouts and apply the styles to the shapes.
		gradientType.addListener(new ChangeListener<GradientType>() {
			@Override
			public void changed(ObservableValue<? extends GradientType> arg0,	GradientType arg1, GradientType type) {
				settingsContainer.getChildren().clear();
				switch(type){
				case LINEAR:
					linearButton.setSelected(true);
					radialButton.setSelected(false);
					settingsContainer.getChildren().add(linearSettingLayout);
					linearSettingLayout.buildGradient();
					break;
				case RADIAL:
					linearButton.setSelected(false);
					radialButton.setSelected(true);
					settingsContainer.getChildren().add(radialSettingLayout);
					radialSettingLayout.buildGradient();
					break;
				}
			}
		});
		
		// Initializing the application tool bar and setting the radio buttons.
		ToolBar toolBar = ToolBarBuilder.create().prefHeight(35).build();
		toolBar.getItems().addAll(linearButton, radialButton);
		
		// Setting the ToolBar as the top to the root(BorderPane) node.
		root.setTop(toolBar);
		
		// By default selecting the "Linear" gradient.
		gradientType.set(GradientType.LINEAR);
	}

	/**
	 * Configures the layout for the "Rectangle" shape.
	 * @return StackPane
	 */
	private StackPane configureRectanglePane(){
		// Initializing the "Rectangle".
		rectangle = new StackPane();

		// Creating the width label and binding its value with the rectangle's "widthProperty".
		Label widthLbl = getValueLabel();
		widthLbl.textProperty().bind(new StringBinding() {
			{
				bind(rectangle.widthProperty());
			}
			@Override
			protected String computeValue() {
				return rectangle.getWidth()+"px";
			}
		});
		
		// Creating the height label and binding its value with the rectangle's "heightProperty".
		Label heightLbl = getValueLabel();
		heightLbl.textProperty().bind(new StringBinding() {
			{
				bind(rectangle.heightProperty());
			}
			@Override
			protected String computeValue() {
				return rectangle.getHeight()+"px";
			}
		});
		
		// Creating a HBox layout to place the Rectangle's width and height information.
		HBox hb = HBoxBuilder.create()
							 .alignment(Pos.CENTER_RIGHT)
							 .prefHeight(20).build();
		hb.getChildren().addAll(getBoldLabel("Width : "), widthLbl, getSpacer(), getBoldLabel("Height : "), heightLbl);
		
		// BorderPane to hold the Rectangle and Bounds information.
		BorderPane bp = new BorderPane();
		bp.setCenter(rectangle);
		bp.setBottom(hb);
		
		// StackPane to hold the BorderPane and to apply the padding to BorderPane.
		StackPane rectanglePane = StackPaneBuilder.create()
											.padding(new Insets(15,15,3,15))
											.children(bp)
											.build();
		return rectanglePane;
	}
	
	/**
	 * Configures the layout for the "Circle" shape.
	 * @return StackPane
	 */
	private StackPane configureCirclePane(){
		// Initializing the "Circle".
		circle = StackPaneBuilder.create().alignment(Pos.CENTER)
										  .styleClass("circle-shape")
										  .build();

		// Creating the x-radius label and binding its value with the circle's half "widthProperty".
		Label radiusX = getValueLabel();
		radiusX.textProperty().bind(new StringBinding() {
			{
				bind(circle.widthProperty());
			}
			@Override
			protected String computeValue() {
				return (circle.getWidth())/2+"px";
			}
		});
		
		// Creating the y-radius label and binding its value with the circle's half "heightProperty".
		Label radiusY = getValueLabel();
		radiusY.textProperty().bind(new StringBinding() {
			{
				bind(circle.heightProperty());
			}
			@Override
			protected String computeValue() {
				return ((circle.getHeight()/2))+"px";
			}
		});
		
		// Creating a HBox layout to place the Circle's x-radius and y-radius information.
		HBox hb = HBoxBuilder.create()
							 .alignment(Pos.CENTER_RIGHT)
							 .prefHeight(20).build();
		hb.getChildren().addAll(getBoldLabel("X-Radius : "), radiusX, getSpacer(), getBoldLabel("Y-Radius : "),radiusY);
		
		// BorderPane to hold the Circle and Bounds information.
		BorderPane bp = new BorderPane();
		bp.setCenter(circle);
		bp.setBottom(hb);
		
		// StackPane to hold the BorderPane and to apply the padding to BorderPane.
		StackPane circlePane = StackPaneBuilder.create()
											.padding(new Insets(15,15,3,15))
											.children(bp)
											.build();
		return circlePane;
	}
	
	/**
	 * Configures the Gradient setting pane.
	 * @return ScrollPane
	 */
	private ScrollPane configureGradientSettings(){
		// Initializing the RadialSettingsLayout.
		radialSettingLayout = new RadialSettingsLayout(this);
		
		// Initializing the LinearSettingsLayout.
		linearSettingLayout = new LinearSettingsLayout(this);
		
		// Initializing the container to hold RadialSettingsLayout or LinearSettingsLayout.
		settingsContainer = StackPaneBuilder.create().alignment(Pos.TOP_LEFT).build();
		
		// Wrapping the container with the ScrollPane.
		ScrollPane scroll = ScrollPaneBuilder.create()
				                             .styleClass("builder-scroll-pane")
				                             .fitToHeight(true)
				                             .fitToWidth(true)
				                             .content(settingsContainer)
				                             .build();
		return scroll;
	}

	/**
	 * Utility method to return a StackPane with some width to act like a spacer.
	 * @return StackPane
	 */
	private StackPane getSpacer(){
		return StackPaneBuilder.create().prefWidth(20).build();
	}
	
	/**
	 * Utility method to return a Label with bold font style.
	 * @return Label
	 */
	private Label getBoldLabel(String str){
		return LabelBuilder.create()
						   .text(str)
						   .style("-fx-font-weight:bold;").build();
	}
	
	/**
	 * Utility method to return a Label with normal font style.
	 * @return Label
	 */
	private Label getValueLabel(){
		return LabelBuilder.create()
						   .style("-fx-font-family:verdana;").build();
	}
	
	/**
	 * Method to apply the styles to the shapes.
	 * @param bg - CSS gradient string.
	 */
	public void applyStyles(String bg){
		rectangle.setStyle("-fx-background-color:"+bg);
		circle.setStyle("-fx-background-color:"+bg);
	}
}

