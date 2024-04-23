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
import ma.enset.bookagentproject.agents.BuyerAgent;
import ma.enset.bookagentproject.agents.ConsumerAgent;

public class BuyerContainer extends Application {
    public BuyerAgent agentGui;


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

    public void startContainer() throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl=new ProfileImpl(false);
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        jade.wrapper.AgentContainer agentContainer=runtime.createAgentContainer(profileImpl);
        AgentController agentController=agentContainer.createNewAgent("Buyer",
                BuyerAgent.class.getName(), new Object[]{this});
        agentController.start();
    }


    @Override
    public void start(Stage stage) throws Exception {

        startContainer();

        stage.setTitle("Buyer Agent");
        BorderPane borderPane=new BorderPane();

        VBox hboxL=new VBox();

        hboxL.getChildren().add(listViewMessages);
        hboxL.setPadding(new Insets(10));

        borderPane.setCenter(hboxL);

        Scene scene=new Scene(borderPane,600,200);
        stage.setScene(scene);
        stage.show();

    }
}
