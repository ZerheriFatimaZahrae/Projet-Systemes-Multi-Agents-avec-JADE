package ma.enset.bookagentproject.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MainContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime= Runtime.instance();
        ProfileImpl profile=new ProfileImpl();

        profile.setParameter(profile.GUI,"true");
        AgentContainer mainContainer=runtime.createMainContainer(profile);


        mainContainer.start();

    }
}
