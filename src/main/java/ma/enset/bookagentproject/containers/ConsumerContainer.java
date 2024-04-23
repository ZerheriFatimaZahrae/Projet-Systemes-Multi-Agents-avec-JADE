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

public class ConsumerContainer extends Application {
    public ConsumerAgent agentGui;


    ObservableList<String> observableList= FXCollections.observableArrayList();
    ListView<String> listViewMessages= new ListView<String>(observableList);

    public static void main(String[] args) {
        launch();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(
                    aclMessage.getContent() +" from : "+
                    aclMessage.getSender().getName() +" with "+ ACLMessage.getPerformative(aclMessage.getPerformative())

            );
        });
    }

    public void startContainer() throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl=new ProfileImpl(false);
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        jade.wrapper.AgentContainer agentContainer=runtime.createAgentContainer(profileImpl);
        AgentController agentController=agentContainer.createNewAgent("Consumer",
                "ma.enset.bookagentproject.agents.ConsumerAgent", new Object[]{this});



        agentController.start();


    }
    @Override
    public void start(Stage stage) throws Exception {

        startContainer();

        stage.setTitle("Consumer Agent");
        BorderPane borderPane=new BorderPane();

        HBox vBox=new HBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        Label label=new Label("Book Name :");
        label.setStyle("-fx-font-size: 20px; " +          /* Font size */
                "-fx-text-fill: black; "       /* Text color */
        );

        TextField textFieldBookName=new TextField();

        textFieldBookName.setStyle("-fx-pref-height: 10px; " +          /* Set the height */
                "-fx-pref-width: 200px; " +          /* Set the width */
                "-fx-font-size: 15px; " +

                "-fx-background-radius: 20px;");
        Button button=new Button("OK");
        button.setStyle("-fx-background-color: #2196F3; " + /* Background color bleu */
                "-fx-text-fill: white; " +          /* Text color */
                "-fx-font-size: 12px; " +           /* Font size */
                "-fx-font-weight: bold; " +         /* Font weight */
                "-fx-padding: 8px 16px;"         /* Padding */
        );
        vBox.getChildren().addAll(label,textFieldBookName,button);

        borderPane.setTop(vBox);



        VBox vboxL=new VBox();
        vboxL.getChildren().add(listViewMessages);
        vboxL.setPadding(new Insets(10));
        vboxL.setSpacing(10);

        borderPane.setCenter(vboxL);

        button.setOnAction(event -> {
            String bookName=textFieldBookName.getText();


            textFieldBookName.setText("");

            GuiEvent eventAgent=new GuiEvent(this,1);
            eventAgent.addParameter(bookName);
            agentGui.onGuiEvent(eventAgent);

        });

        Scene scene=new Scene(borderPane,600,200);
        stage.setScene(scene);
        stage.show();

    }
}
