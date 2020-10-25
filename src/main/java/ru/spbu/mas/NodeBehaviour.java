package ru.spbu.mas;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Arrays;

public class NodeBehaviour extends Behaviour {

    private int step = 0;
    private String mt2;
    private int replies_count = 0;
    @Override
    public void action() {
        switch (step) {
            case 0 -> {
                ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                if (reply != null) {
                    System.out.println(myAgent.getAID().getLocalName() + " received propose from : " + reply.getSender().getLocalName());
                    if (((DefaultAgent) myAgent).parent == null) {
                        ((DefaultAgent) myAgent).parent = reply.getSender();
                        ACLMessage msg2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        msg2.addReceiver(reply.getSender());
                        msg2.setReplyWith(reply.getReplyWith());
                        myAgent.send(msg2);
                        step = 1;
                    }
                } else {
                    block();
                }
            }
            case 1 -> {
                ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                for (int i = 0; i < ((DefaultAgent) myAgent).linkedAgents.size(); i++) {
                    if (!((DefaultAgent) myAgent).linkedAgents.get(i).getLocalName().equals(((DefaultAgent) myAgent).parent.getLocalName()))
                        msg.addReceiver(((DefaultAgent) myAgent).linkedAgents.get(i));
                }
                msg.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(msg);
                mt2 = msg.getReplyWith();
                step = 2;
            }
            case 2 -> {
                ACLMessage msg3 = myAgent.receive();
                if (msg3 != null && msg3.getPerformative()!= ACLMessage.FAILURE) {
                    if (msg3.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && msg3.getReplyWith().equals(mt2)) {
                        System.out.println(myAgent.getAID().getLocalName() + " added new child : " + msg3.getSender().getLocalName());
                        ((DefaultAgent) myAgent).childs.add(msg3.getSender());
                        replies_count++;
                    }
                    else if (msg3.getPerformative() == ACLMessage.REJECT_PROPOSAL && msg3.getReplyWith().equals(mt2)){
                        replies_count++;
                    }
                    else if (msg3.getPerformative() == ACLMessage.PROPOSE){
                        System.out.println( myAgent.getAID().getLocalName() + " rejected proposal from : " + msg3.getSender().getLocalName());
                        ACLMessage msg2 = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                        msg2.addReceiver(msg3.getSender());
                        msg2.setReplyWith(msg3.getReplyWith());
                        myAgent.send(msg2);
                    }

                    if (replies_count == ((DefaultAgent) myAgent).linkedAgents.size() - 1) {
                        step = 3;
                        System.out.println(" Node " + myAgent.getAID().getLocalName() +
                        " found all childs : " + Arrays.toString( ((DefaultAgent) myAgent).childs.stream().map(AID::getLocalName).toArray()) );
                        myAgent.addBehaviour(new FindAverage());
                    }
                } else {
                    block();
                }
            }
        }
    }

    @Override
    public boolean done() {
        return (step == 3);
    }
}
