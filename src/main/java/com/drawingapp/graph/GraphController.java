package com.drawingapp.graph;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur de la vue Graphe.
 * 
 * Modes d'interaction :
 *   - ADD_NODE    : clic gauche ajoute un nœud
 *   - ADD_EDGE    : clic sur nœud A puis nœud B crée une arête
 *   - SELECT_PATH : clic sur nœud source puis destination → calcul du chemin
 *   - DELETE      : clic gauche supprime le nœud pointé
 *   - DRAG        : clic-glisser pour déplacer un nœud
 */
public class GraphController {

    // ── Widgets injectés (ou construits programmatiquement) ─────────────────

    private Canvas  canvas;
    private VBox    controlPanel;

    // ── Contrôles UI ────────────────────────────────────────────────────────

    private ToggleGroup modeGroup;
    private RadioButton rbAddNode, rbAddEdge, rbSelectPath, rbDelete;
    private ComboBox<String> algoCombo;
    private Label statusLabel;
    private Button btnClear, btnClearPath;

    // ── Modèle ──────────────────────────────────────────────────────────────

    private final GraphModel model = new GraphModel();

    // État de l'interaction
    private GraphNode firstNode   = null;  // pour ADD_EDGE et SELECT_PATH
    private GraphNode draggedNode = null;

    // Algorithmes disponibles
    private final ShortestPathAlgorithm[] algorithms = {
        new ShortestPathAlgorithm.Dijkstra(),
        new ShortestPathAlgorithm.BFS()
    };

    // ── Construction ─────────────────────────────────────────────────────────

    /**
     * Construit le panneau graphe complet (canvas + contrôles).
     * À appeler depuis DrawingView ou tout autre contrôleur parent.
     */
    public VBox buildPane(double canvasWidth, double canvasHeight) {
        canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.setStyle("-fx-background-color: #f8f8fc;");

        buildControlPanel();

        VBox root = new VBox(8);
        root.setPadding(new Insets(8));
        root.getChildren().addAll(controlPanel, canvas);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        // ── Événements souris ──
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        redraw();
        return root;
    }

    private void buildControlPanel() {
        controlPanel = new VBox(6);
        controlPanel.setPadding(new Insets(4, 0, 4, 0));

        // ── Ligne 1 : modes ──────────────────────────────────────────────
        modeGroup = new ToggleGroup();

        rbAddNode    = radio("➕ Nœud",        "ADD_NODE");
        rbAddEdge    = radio("🔗 Arête",        "ADD_EDGE");
        rbSelectPath = radio("📍 Chemin",        "SELECT_PATH");
        rbDelete     = radio("🗑 Supprimer",    "DELETE");

        rbAddNode.setSelected(true);

        HBox modeBox = new HBox(6,
            new Label("Mode :"), rbAddNode, rbAddEdge, rbSelectPath, rbDelete);
        modeBox.setStyle("-fx-alignment: center-left;");

        // ── Ligne 2 : algo + boutons ─────────────────────────────────────
        algoCombo = new ComboBox<>();
        for (ShortestPathAlgorithm a : algorithms) algoCombo.getItems().add(a.getName());
        algoCombo.getSelectionModel().selectFirst();
        algoCombo.setPrefWidth(180);

        btnClear     = new Button("Effacer graphe");
        btnClearPath = new Button("Effacer chemin");
        btnClear.setOnAction(e -> { model.clear(); redraw(); });
        btnClearPath.setOnAction(e -> { model.clearPathHighlight(); redraw(); });

        HBox algoBox = new HBox(8,
            new Label("Algorithme :"), algoCombo,
            btnClear, btnClearPath);
        algoBox.setStyle("-fx-alignment: center-left;");

        // ── Ligne 3 : statut ─────────────────────────────────────────────
        statusLabel = new Label("Cliquez sur le canvas pour ajouter des nœuds.");
        statusLabel.setStyle("-fx-text-fill: #444; -fx-font-style: italic; -fx-font-size: 12;");

        // ── Légende ──────────────────────────────────────────────────────
        Label legend = new Label(
            "🔵 Nœud normal  |  🔴 Source  |  🟣 Destination  |  🟢 Chemin trouvé  |  "
          + "Clic droit → renommer");
        legend.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");

        controlPanel.getChildren().addAll(modeBox, algoBox, statusLabel, legend);
    }

    private RadioButton radio(String text, String userData) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(modeGroup);
        rb.setUserData(userData);
        return rb;
    }

    // ── Gestion des événements souris ─────────────────────────────────────

    private void onMousePressed(MouseEvent e) {
        double x = e.getX(), y = e.getY();
        String mode = selectedMode();

        if (e.getButton() == MouseButton.SECONDARY) {
            // Clic droit : renommer le nœud
            GraphNode n = model.findNodeAt(x, y);
            if (n != null) promptRenameNode(n);
            return;
        }

        switch (mode) {
            case "ADD_NODE" -> {
                // Ajouter un nœud seulement si l'on ne clique pas sur un existant
                if (model.findNodeAt(x, y) == null) {
                    GraphNode n = model.addNode(x, y);
                    setStatus("Nœud " + n.getId() + " ajouté.");
                } else {
                    // Commencer un drag
                    draggedNode = model.findNodeAt(x, y);
                }
            }
            case "ADD_EDGE" -> {
                GraphNode clicked = model.findNodeAt(x, y);
                if (clicked == null) return;
                if (firstNode == null) {
                    firstNode = clicked;
                    firstNode.setSelected(true);
                    setStatus("Nœud " + firstNode.getId() + " sélectionné — cliquez sur le nœud destination.");
                } else if (clicked != firstNode) {
                    double weight = promptWeight();
                    GraphEdge edge = model.addEdge(firstNode, clicked, weight);
                    if (edge != null) setStatus("Arête ajoutée : " + edge);
                    else              setStatus("Arête déjà existante.");
                    firstNode.setSelected(false);
                    firstNode = null;
                }
            }
            case "SELECT_PATH" -> {
                GraphNode clicked = model.findNodeAt(x, y);
                if (clicked == null) return;
                if (firstNode == null) {
                    firstNode = clicked;
                    firstNode.setSource(true);
                    setStatus("Source : " + firstNode.getId() + " — cliquez sur la destination.");
                } else if (clicked != firstNode) {
                    String algoName = algoCombo.getValue();
                    ShortestPathAlgorithm algo = algorithmByName(algoName);
                    PathResult result = model.computeShortestPath(
                        firstNode.getId(), clicked.getId(), algo);
                    setStatus(result.toString());
                    firstNode = null;
                }
            }
            case "DELETE" -> {
                GraphNode n = model.findNodeAt(x, y);
                if (n != null) {
                    model.removeNode(n);
                    setStatus("Nœud " + n.getId() + " supprimé.");
                }
            }
        }
        redraw();
    }

    private void onMouseDragged(MouseEvent e) {
        if (draggedNode != null) {
            draggedNode.setPosition(e.getX(), e.getY());
            redraw();
        }
    }

    private void onMouseReleased(MouseEvent e) {
        draggedNode = null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String selectedMode() {
        Toggle t = modeGroup.getSelectedToggle();
        return t == null ? "ADD_NODE" : (String) t.getUserData();
    }

    private double promptWeight() {
        TextInputDialog dlg = new TextInputDialog("1");
        dlg.setTitle("Poids de l'arête");
        dlg.setHeaderText("Entrez le poids (distance) de l'arête :");
        dlg.setContentText("Poids :");
        Optional<String> result = dlg.showAndWait();
        try {
            return result.map(Double::parseDouble).orElse(1.0);
        } catch (NumberFormatException ex) {
            return 1.0;
        }
    }

    private void promptRenameNode(GraphNode n) {
        // GraphNode.id est final → on ne peut pas le renommer ici sans refactorer,
        // donc on affiche juste les infos
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Informations nœud");
        a.setHeaderText("Nœud : " + n.getId());
        StringBuilder sb = new StringBuilder("Arêtes connectées :\n");
        for (GraphEdge e : model.getEdges()) {
            if (e.getSource() == n || e.getTarget() == n) sb.append("  ").append(e).append("\n");
        }
        a.setContentText(sb.toString());
        a.show();
    }

    private ShortestPathAlgorithm algorithmByName(String name) {
        for (ShortestPathAlgorithm a : algorithms) {
            if (a.getName().equals(name)) return a;
        }
        return algorithms[0];
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
        model.setStatusMessage(msg);
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Fond léger
        gc.setFill(Color.rgb(248, 248, 252));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Quadrillage discret
        gc.setStroke(Color.rgb(200, 200, 210, 0.5));
        gc.setLineWidth(0.5);
        for (double xi = 0; xi < canvas.getWidth(); xi += 40)
            gc.strokeLine(xi, 0, xi, canvas.getHeight());
        for (double yi = 0; yi < canvas.getHeight(); yi += 40)
            gc.strokeLine(0, yi, canvas.getWidth(), yi);

        model.draw(gc);
    }

    public Canvas getCanvas() { return canvas; }
}
