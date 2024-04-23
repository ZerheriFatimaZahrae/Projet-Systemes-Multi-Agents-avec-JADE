package ma.enset.bookagentproject.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ma.enset.bookagentproject.agents.ConsumerAgent;
import ma.enset.bookagentproject.agents.SellerAgent;

public class SellerContainer extends Application {
    public SellerAgent agentGui;
    jade.wrapper.AgentContainer agentContainer;


    ObservableList<String> observableList= FXCollections.observableArrayList();
    ListView<String> listViewMessages= new ListView<String>(observableList);

    public static void main(String[] args) {
        launch();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(
                    aclMessage.getContent() +" from : "+
                            aclMessage.getSender().getName()
                            +" with "+ ACLMessage.getPerformative(aclMessage.getPerformative())

            );
        });
    }

    public void startContainer() throws Exception {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl=new ProfileImpl(false);
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer=runtime.createAgentContainer(profileImpl);

        agentContainer.start();


    }
    @Override
    public void start(Stage stage) throws Exception {

        startContainer();

        stage.setTitle("Seller Agent");
        BorderPane borderPane=new BorderPane();

        HBox vBox=new HBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        Label label=new Label("Agent Name :");
        label.setStyle("-fx-font-size: 20px; " +          /* Font size */
                "-fx-text-fill: black; "       /* Text color */
                );
        TextField textField=new TextField();
        textField.setStyle("-fx-pref-height: 10px; " +          /* Set the height */
                "-fx-pref-width: 200px; " +          /* Set the width */
                "-fx-font-size: 15px; " +

                "-fx-background-radius: 20px;");
        Button button=new Button("Deploy");
        vBox.getChildren().addAll(label,textField,button);

        borderPane.setTop(vBox);



        VBox vboxL=new VBox();
        vboxL.getChildren().add(listViewMessages);
        vboxL.setPadding(new Insets(10));
        vboxL.setSpacing(10);

        borderPane.setCenter(vboxL);
        button.setStyle("-fx-background-color: #2196F3; " + /* Background color bleu */
                "-fx-text-fill: white; " +          /* Text color */
                "-fx-font-size: 12px; " +           /* Font size */
                "-fx-font-weight: bold; " +         /* Font weight */
                "-fx-padding: 8px 16px;"         /* Padding */
        );

        button.setOnAction(event -> {
            String nameAgent=textField.getText();



            try {
                AgentController agentController=agentContainer.createNewAgent(nameAgent,
                        SellerAgent.class.getName(), new Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
            textField.setText("");
        });

        Scene scene=new Scene(borderPane,600,200);
        stage.setScene(scene);


        stage.show();

    }
}
