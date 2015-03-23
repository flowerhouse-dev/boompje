/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom;


import domain.Administratie;
import domain.Gezin;
import domain.Persoon;
import static java.time.Instant.now;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import domain.Geslacht;

/**
 *
 * @author Jesse
 */
public class Stamboom extends Application {
    
//    private final Node rootIcon = 
//        new ImageView(new Image(getClass().getResourceAsStream("root.png")));
//    private final Image depIcon = 
//        new Image(getClass().getResourceAsStream("department.png"));
    List<Persoon> employees;
    Administratie admin;
    TreeItem<String> rootNode = new TreeItem<String>("Root Node");
    
    public static void main(String[] args) {
        Application.launch(args);
    }

    public Stamboom() {
        admin = new Administratie();
        Persoon piet = admin.addPersoon(Geslacht.MAN, new String[]{"Piet", "Franciscus"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede", null);
        Persoon teuntje = admin.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1949, Calendar.MAY, 5), "Amersfoort", null);
        Gezin pietEnTeuntje = admin.addHuwelijk(piet, teuntje, new GregorianCalendar(1970, Calendar.MAY, 23));
        admin.addPersoon(domain.Geslacht.MAN, new String[]{"karel", "henkie"}, "ebole","van", Calendar.getInstance(), "afrika", pietEnTeuntje);
        this.employees = admin.getPersonen();
    }
 
    @Override
    public void start(Stage stage) {
        rootNode.setExpanded(true);
        for (Persoon persoon : employees) {
            TreeItem<String> empLeaf = new TreeItem<>(persoon.getNaam());
            boolean found = false;
            for (TreeItem<String> depNode : rootNode.getChildren()) {
                if (depNode.getValue().contentEquals(persoon.getNaam())){
                    depNode.getChildren().add(empLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem depNode = new TreeItem<>(persoon.getNaam(),
                        new ImageView()
                );
                depNode = getTree(depNode,persoon);
                rootNode.getChildren().add(depNode);
                //depNode.getChildren().add(empLeaf);
            }
        }
 
        stage.setTitle("Tree View Sample");
        VBox box = new VBox();
        final Scene scene = new Scene(box, 400, 300);
        scene.setFill(Color.LIGHTGRAY);
 
        TreeView<String> treeView = new TreeView<String>(rootNode);
        treeView.setEditable(true);
        treeView.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new TextFieldTreeCellImpl();
            }
        });
 
        box.getChildren().add(treeView);
        stage.setScene(scene);
        stage.show();
    }
    
    public TreeItem getTree(TreeItem tree, Persoon persoon) {
        if(persoon.getOuderlijkGezin() == null) {
            return tree;
        }
        if(persoon.getOuderlijkGezin().getOuder1() != null) {
            TreeItem depNode = new TreeItem<>(persoon.getOuderlijkGezin().getOuder1().getNaam(),
                        new ImageView()
            );
            depNode = getTree(depNode, persoon.getOuderlijkGezin().getOuder1());
            tree.getChildren().add(depNode);
        }
        if(persoon.getOuderlijkGezin().getOuder2() != null) {
            TreeItem depNode = new TreeItem<>(persoon.getOuderlijkGezin().getOuder2().getNaam(),
                        new ImageView()
            );
                        depNode = getTree(depNode, persoon.getOuderlijkGezin().getOuder1());

            tree.getChildren().add(depNode);
        }
        return tree;
       
    }
    
    private final class TextFieldTreeCellImpl extends TreeCell<String> {
 
        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();
 
        public TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("Add Employee");
            addMenu.getItems().add(addMenuItem);
            addMenuItem.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    TreeItem newEmployee = 
                        new TreeItem<String>("New Employee");
                            getTreeItem().getChildren().add(newEmployee);
                }
            });
        }
 
        @Override
        public void startEdit() {
            super.startEdit();
 
            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
 
            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        }
 
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (
                        !getTreeItem().isLeaf()&&getTreeItem().getParent()!= null
                    ){
                        setContextMenu(addMenu);
                    }
                }
            }
        }
        
        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
 
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });  
            
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
