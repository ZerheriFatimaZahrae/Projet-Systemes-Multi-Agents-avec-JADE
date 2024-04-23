package ma.enset.bookagentproject.agents;
import jade.core.AID;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import ma.enset.bookagentproject.containers.ConsumerContainer;

public class ConsumerAgent extends GuiAgent   {
    public ConsumerContainer consumerAgent;
    @Override
    protected void setup() {
        System.out.println("Hello ! my name is :"+this.getAID().getName());
        if (this.getArguments().length==1){
            consumerAgent = (ConsumerContainer) getArguments()[0];
            consumerAgent.agentGui=this;
        }

        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMsgRecu=receive();
                if(aclMsgRecu!=null){
                    //envoyer le msg recu pour l afficher a obsrbale list
                    switch (aclMsgRecu.getPerformative()){

                        case ACLMessage.CONFIRM:
                            consumerAgent.logMessage(aclMsgRecu);
                            break;
                        default:
                            System.out.println("erreur");

                    }

                }else {
                    System.out.println("Consumer bloquer ...");
                    block();
                }


            }
        });
    }



    @Override
    public void onGuiEvent(GuiEvent guiEvent) {

        if(guiEvent.getType()==1){
            String bookName= (String) guiEvent.getParameter(0);

            ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(bookName);
            aclMessage.addReceiver(new AID("Buyer",AID.ISLOCALNAME));

            send(aclMessage);

        }

    }
}