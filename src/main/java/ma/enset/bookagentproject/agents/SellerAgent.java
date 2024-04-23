package ma.enset.bookagentproject.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import ma.enset.bookagentproject.containers.SellerContainer;

import java.util.Random;

public class SellerAgent extends GuiAgent   {
    public SellerContainer sellerContainer;
    @Override
    protected void setup() {
        System.out.println("Hello ! my name is :"+this.getAID().getName());
        if (this.getArguments().length==1){
            sellerContainer = (SellerContainer) getArguments()[0];
            sellerContainer.agentGui=this;
        }

        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        //Publier un Service :

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription=new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("Book-Sales");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e1) {
                    e1.printStackTrace();
                }
            }
        });




        //afficher les msgs recu dans l'interface
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage aclMsgRecu=receive();
                if(aclMsgRecu!=null){
                    //envoyer le msg recu pour l afficher a obsrbale list
                    sellerContainer.logMessage(aclMsgRecu);

                    switch (aclMsgRecu.getPerformative()){
                        case ACLMessage.CFP:
                            ACLMessage reply=aclMsgRecu.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage reply2=aclMsgRecu.createReply();

                            reply2.setPerformative(ACLMessage.AGREE);
                            reply2.setContent(aclMsgRecu.getContent());
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            send(reply2);
                            break;
                        default:
                            System.out.println(aclMsgRecu.getContent());
                            break;
                    }


                }else {
                    System.out.println("Seller bloquer ...");
                    block();
                }


            }
        });
    }

    //supprimer le service avant takeDown
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onGuiEvent(GuiEvent guiEvent) {


    }
}