package com.drawingapp.view;

import com.drawingapp.adapter.Shape3DAdapter;
import com.drawingapp.database.DrawingRepository;
import com.drawingapp.database.ShapeRecreator;
import com.drawingapp.factory.Shape2DFactory;
import com.drawingapp.factory.Shape3DFactory;
import com.drawingapp.factory.ShapeFactory;
import com.drawingapp.graph.GraphController;
import com.drawingapp.logger.LoggerManager;
import com.drawingapp.logger.LoggingStrategy;
import com.drawingapp.model.Dessin;
import com.drawingapp.model.Palette;
import com.drawingapp.model.Shape;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DrawingView {

    // ── Injections FXML ─────────────────────────────────────────────────
    @FXML private Canvas canvas;
    @FXML private ComboBox<LoggingStrategy> loggingStrategyCombo;
    @FXML private RadioButton radio2D;
    @FXML private RadioButton radio3D;
    @FXML private ToggleGroup dimensionGroup;
    @FXML private ComboBox<String> shapeComboBox;
    @FXML private Label infoLabel;
    @FXML private TabPane mainTabPane;
    @FXML private Tab     graphTab;

    // ── État interne ─────────────────────────────────────────────────────
    private Palette palette;
    private Dessin  dessin;
    private ShapeFactory currentFactory;
    private String       currentShapeType;

    // ── Initialisation ───────────────────────────────────────────────────

    @FXML
    public void initialize() {
        palette = new Palette();
        dessin  = new Dessin(canvas);
        palette.addObserver(dessin);

        dimensionGroup = new ToggleGroup();
        radio2D.setToggleGroup(dimensionGroup);
        radio3D.setToggleGroup(dimensionGroup);
        radio2D.setSelected(true);

        currentFactory = new Shape2DFactory();
        updateShapeComboBox();
        shapeComboBox.setOnAction(e -> selectCurrentShape());

        loggingStrategyCombo.getItems().addAll(LoggingStrategy.values());
        loggingStrategyCombo.setValue(LoggingStrategy.CONSOLE);

        if (!shapeComboBox.getItems().isEmpty()) {
            shapeComboBox.setValue(shapeComboBox.getItems().get(0));
            selectCurrentShape();
        }

        updateInfoLabel();

        if (mainTabPane != null && graphTab != null) {
            buildGraphTab();
        }

        LoggerManager.log("Application prête");
    }

    private void buildGraphTab() {
        GraphController graphCtrl = new GraphController();
        double w = canvas != null ? canvas.getWidth()  : 800;
        double h = canvas != null ? canvas.getHeight() : 600;
        VBox graphPane = graphCtrl.buildPane(w, h);
        graphTab.setContent(graphPane);
        LoggerManager.log("Module Graphe initialisé");
    }

    // ── Actions ──────────────────────────────────────────────────────────

    @FXML private void select2D() {
        currentFactory = new Shape2DFactory();
        updateShapeComboBox();
        palette.notifyObservers("DIMENSION_CHANGED", "2D");
        updateInfoLabel();
        if (!shapeComboBox.getItems().isEmpty()) {
            shapeComboBox.setValue(shapeComboBox.getItems().get(0));
            selectCurrentShape();
        }
    }

    @FXML private void select3D() {
        currentFactory = new Shape3DFactory();
        updateShapeComboBox();
        palette.notifyObservers("DIMENSION_CHANGED", "3D");
        updateInfoLabel();
        if (!shapeComboBox.getItems().isEmpty()) {
            shapeComboBox.setValue(shapeComboBox.getItems().get(0));
            selectCurrentShape();
        }
    }

    private void updateShapeComboBox() {
        shapeComboBox.getItems().clear();
        shapeComboBox.getItems().addAll(currentFactory.getAvailableShapes());
    }

    private void selectCurrentShape() {
        currentShapeType = shapeComboBox.getValue();
        if (currentShapeType != null) {
            palette.selectShape(currentFactory, currentShapeType);
            updateInfoLabel();
        }
    }

    private void updateInfoLabel() {
        if (infoLabel != null && currentFactory != null && currentShapeType != null) {
            infoLabel.setText("Mode: " + currentFactory.getFactoryType()
                    + " | Forme: " + currentShapeType);
        }
    }

    @FXML private void undo()     { dessin.undo();     updateInfoLabel(); }
    @FXML private void redo()     { dessin.redo();     updateInfoLabel(); }
    @FXML private void clearAll() { dessin.clearAll(); updateInfoLabel(); }

    @FXML private void changeLoggingStrategy() {
        LoggingStrategy strategy = loggingStrategyCombo.getValue();
        if (strategy != null) LoggerManager.getInstance().setStrategy(strategy);
    }

    // ── Sauvegarde ───────────────────────────────────────────────────────

    @FXML
    private void saveDrawing() {
        List<Shape> shapes = dessin.getShapes();
        if (shapes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune forme à sauvegarder",
                    "Dessinez au moins une forme avant de sauvegarder.");
            return;
        }
        TextInputDialog dlg = new TextInputDialog("Mon Dessin");
        dlg.setTitle("Sauvegarder le dessin");
        dlg.setHeaderText("Donnez un nom à votre dessin :");
        dlg.setContentText("Nom :");
        dlg.showAndWait().ifPresent(name -> {
            boolean ok = DrawingRepository.getInstance().saveDrawing(name, shapes);
            if (ok) {
                LoggerManager.log("Dessin sauvegardé : " + name);
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Dessin \"" + name + "\" sauvegardé (" + shapes.size() + " formes).");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur de sauvegarde",
                        "Vérifiez que MySQL est démarré (XAMPP).");
            }
        });
    }

    // ── Chargement ★★★ CORRIGÉ ★★★ ──────────────────────────────────────

    @FXML
    private void loadDrawing() {
        Map<Integer, String> drawings = DrawingRepository.getInstance().getAllDrawingNames();
        if (drawings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun dessin",
                    "Sauvegardez d'abord un dessin avant de le charger.");
            return;
        }
        ChoiceDialog<String> dlg = new ChoiceDialog<>();
        dlg.setTitle("Charger un dessin");
        dlg.setHeaderText("Sélectionnez un dessin :");
        dlg.getItems().addAll(drawings.values());
        dlg.showAndWait().ifPresent(selected -> {
            int id = drawings.entrySet().stream()
                    .filter(en -> en.getValue().equals(selected))
                    .findFirst().map(Map.Entry::getKey).orElse(-1);
            if (id > 0) {
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Le dessin actuel sera remplacé.\n" + selected)
                        .showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.OK) {
                                String json = DrawingRepository.getInstance().loadDrawingJson(id);
                                if (json != null) {
                                    List<Shape> loaded = ShapeRecreator.getInstance().recreateShapes(json);

                                    // ★★★ RÉ-APPLIQUER L'ADAPTER 3D ★★★
                                    List<Shape> adaptedShapes = new ArrayList<>();
                                    for (Shape shape : loaded) {
                                        if (shape.getType().equals("3D")) {
                                            shape = new Shape3DAdapter(shape);
                                        }
                                        adaptedShapes.add(shape);
                                    }

                                    dessin.setShapes(adaptedShapes);
                                    LoggerManager.log("Dessin chargé : " + selected
                                            + " (" + adaptedShapes.size() + " formes)");
                                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                                            adaptedShapes.size() + " forme(s) restaurée(s).");
                                }
                            }
                        });
            }
        });
    }

    // ── Suppression ──────────────────────────────────────────────────────

    @FXML
    private void deleteDrawing() {
        Map<Integer, String> drawings = DrawingRepository.getInstance().getAllDrawingNames();
        if (drawings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun dessin", "Aucun dessin à supprimer.");
            return;
        }
        ChoiceDialog<String> dlg = new ChoiceDialog<>();
        dlg.setTitle("Supprimer un dessin");
        dlg.setHeaderText("Sélectionnez un dessin à supprimer :");
        dlg.getItems().addAll(drawings.values());
        dlg.showAndWait().ifPresent(selected -> {
            int id = drawings.entrySet().stream()
                    .filter(en -> en.getValue().equals(selected))
                    .findFirst().map(Map.Entry::getKey).orElse(-1);
            if (id > 0) {
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Supprimer définitivement : " + selected + " ?")
                        .showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.OK) {
                                DrawingRepository.getInstance().deleteDrawing(id);
                                LoggerManager.log("Dessin supprimé : " + selected);
                            }
                        });
            }
        });
    }

    // ── Utilitaire ───────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(header);
        a.setContentText(content);
        a.show();
    }
}