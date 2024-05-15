package src.ahmedjordypiia.Controle;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import src.ahmedjordypiia.Modele.Node;
import src.ahmedjordypiia.Vue.TreeOfLifeVisual;

import java.io.IOException;

import static javafx.scene.paint.Color.rgb;

public class Control {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button especeDetails;

    @FXML
    private StackPane leftBar;

    @FXML
    public Button zoomInButton;
    @FXML
    public Button zoomOutButton;
    @FXML
    public TextField searchField;
    @FXML
    public Button searchButton;
    @FXML
    public Text speciesName;
    @FXML
    private Text speciesDescription;
    @FXML
    private Text speciesNameMenu;
    @FXML
    private BorderPane speciesMenu;
    @FXML
    private HBox topBar;
    @FXML
    private Hyperlink tolorgLink;
    @FXML
    private Rectangle espece;
    @FXML
    private Pane treePane;
    @FXML
    private Button reloadButton;
    private TreeOfLifeVisual tree;
    private ImageView speciesImage;

    private Node currentNode;

    private Application application;

    public Control() throws IOException {
    }

    public void initialize() throws IOException {
        tree = new TreeOfLifeVisual();
        Group treeGroup = tree.getTreeGroup();
        treePane.getChildren().add(treeGroup);
        speciesMenu.setVisible(false);

        reloadButton = new Button();
        reloadButton.setOnAction(this::reloadZoom);

        // Ajouter un gestionnaire d'événements de zoom à l'arbre
        treePane.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0){
                zoomFactor = 0.95;
            }
            treeGroup.setScaleX(treeGroup.getScaleX() * zoomFactor);
            treeGroup.setScaleY(treeGroup.getScaleY() * zoomFactor);
            event.consume();
        });

        tolorgLink = new Hyperlink();
        tolorgLink.setOnMouseClicked(event -> {
            if (currentNode != null) {
                // Ouvrir le lien dans le navigateur par défaut de l'utilisateur
                HostServices hostServices = (HostServices) this.getApplication().getHostServices();
                hostServices.showDocument(currentNode.getTolorgLink());
            }
        });
        setCurrentNode(tree.getTree().searchNode("Life on Earth"));
    }

    @FXML
    public void search(MouseEvent event) throws IOException {
        String name = searchField.getText();
        Node node = tree.getTree().searchNode(name);
        if (node != null) {
            setCurrentNode(node);
            openEspecePanel(null);
        }else {
            searchField.setText("");
            searchField.setPromptText("Espèce non trouvée");
            //prompt text color
            searchField.setStyle("-fx-prompt-text-fill: red;");
            searchField.setStyle("-fx-background-color: transparent;");
        }
    }

    @FXML
    void openEspecePanel(ActionEvent event) {
        speciesNameMenu.setText(speciesName.getText());
        speciesMenu.setVisible(true);
    }

    @FXML
    void reloadZoom(ActionEvent event) {
        tree.resetTree();
    }

    @FXML
    void closeEspecePanel(ActionEvent event) {
        speciesNameMenu.setText("");
        speciesMenu.setVisible(false);
    }

    @FXML
    public void zoomIn(ActionEvent event) {
        tree.zoomIn();
    }

    @FXML
    public void zoomOut(ActionEvent event) {
        tree.zoomOut();
    }

    @FXML
    void setEnteredTopBar(MouseEvent event) {
        topBar.setOpacity(1);
    }

    @FXML
    void setExitedTopBar(MouseEvent event) {
        topBar.setOpacity(0.5);
    }

    @FXML
    void setEnteredEspece(MouseEvent event) {
        espece.setOpacity(1);
    }

    @FXML
    void setExitedEspece(MouseEvent event) {
        espece.setOpacity(0.5);
    }

    @FXML
    public void openLink(ActionEvent event) {
        if (currentNode != null) {
            // Ouvrir le lien dans le navigateur par défaut de l'utilisateur
            HostServices hostServices = (HostServices) this.getApplication().getHostServices();
            hostServices.showDocument(currentNode.getTolorgLink());
        }
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setCurrentNode(Node node) {
        this.currentNode = node;
        this.speciesName.setText(this.currentNode.getName());
        this.speciesNameMenu.setText(this.currentNode.getName());
        this.speciesDescription.setText(this.currentNode.toStringNodeInfo());
        this.tolorgLink.setText(this.currentNode.getTolorgLink());
        this.tolorgLink.setVisited(false);
    }

    public TreeOfLifeVisual getTree() {
        return tree;
    }

}
