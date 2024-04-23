package ma.enset.bookagentproject.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ma.enset.bookagentproject.containers.BuyerContainer;
import ma.enset.bookagentproject.containers.ConsumerContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyerAgent extends GuiAgent   {
    public BuyerContainer buyerContainer;
    private AID[] sellerAgents;
    List<ACLMessage> replies =new ArrayList<ACLMessage>();
    int counter=0;
    @Override
    protected void setup() {
        System.out.println("Hello ! my name is :"+this.getAID().getName());
        if (this.getArguments().length==1){
            buyerContainer = (BuyerContainer)  getArguments()[0];
            buyerContainer.agentGui=this;
        }

        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        //chercher les services :
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,5000) {
            @Override
            protected void onTick() {
                // Update the list of seller agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("transaction");
                sd.setName("Book-Sales");
                template.addServices(sd);
                try {
                    // chercher ts les services dans le type est transaction et name book sales
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    //
                    sellerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        sellerAgents[i] = result[i].getName();
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                //faire un filtre de msgs hors de liste mentionee
                MessageTemplate messageTemplate=MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                        MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
                                ,MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                        ,MessageTemplate.MatchPerformative(ACLMessage.AGREE))));
                ACLMessage aclMsgRecu=receive(messageTemplate);

                if(aclMsgRecu!=null){
                    String livre=aclMsgRecu.getContent();
                    switch (aclMsgRecu.getPerformative()){
                        //envoyer msg CFP a tous les agents vendeurs qui ont publier un service
                        case ACLMessage.REQUEST:
                            //envoyer request of consumer to seller
                            ACLMessage aclMessage=new ACLMessage(ACLMessage.CFP);
                            aclMessage.setContent(livre);
                            for(AID seller:sellerAgents){
                                aclMessage.addReceiver(seller);
                            }
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            send(aclMessage);

                            break;
                        case ACLMessage.PROPOSE:
                            ++counter;
                            System.out.println("counter"+counter);
                            replies.add(aclMsgRecu);
                            ACLMessage bestOffre=replies.get(0);
                            System.out.println("code de propose");
                            System.out.println("replies.size() :"+replies.size());
                            System.out.println("sellerAgents.length :"+sellerAgents.length);
                            if(replies.size()==sellerAgents.length){

                                double min=Double.parseDouble(bestOffre.getContent());
                                for (ACLMessage offre:replies){
                                    double price= Double.parseDouble(offre.getContent());
                                    if(price<min){
                                        bestOffre=offre;
                                        min=price;
                                    }
                                }

                                ACLMessage aclMsgAccept= new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                aclMsgAccept.setContent(bestOffre.getContent());
                                aclMsgAccept.addReceiver(bestOffre.getSender());
                                System.out.println(bestOffre.getSender()+" , "+bestOffre.getContent());
                                send(aclMsgAccept);
                            }

                            break;

                        case ACLMessage.AGREE:
                            ACLMessage aclMessage1=new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage1.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            aclMessage1.setContent(aclMsgRecu.getContent());
                            send(aclMessage1);

                            break;

                        case ACLMessage.REFUSE:

                            break;
                        default:
                    }

                    //afficher le msg recu dans l'interface
                    buyerContainer.logMessage(aclMsgRecu);




                }else {
                    System.out.println("Buyer bloquer ...");
                    block();
                }


            }
        });
    }



    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
    }
}