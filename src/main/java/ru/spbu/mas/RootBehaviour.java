package ru.spbu.mas;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

public class RootBehaviour extends Behaviour {

    private int step = 0;
    private MessageTemplate mt;
    private int replies_count =0;
    @Override
    public void action() {
        switch (step) {
            case 0 -> {
                ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                for (int i = 0; i < ((DefaultAgent) myAgent).linkedAgents.size(); i++) {
                    msg.addReceiver(((DefaultAgent) myAgent).linkedAgents.get(i));
                }
                msg.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(msg);
                mt = MessageTemplate.MatchReplyWith(msg.getReplyWith());
                step = 1;
            }
            case 1 -> {
                ACLMessage reply = myAgent.receive(mt);
                if (reply != null && reply.getPerformative()!= ACLMessage.FAILURE) {
                    if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        ((DefaultAgent) myAgent).childs.add(reply.getSender());
                        System.out.println("ROOT added new child : " + reply.getSender().getLocalName());
                    } else {
                        System.out.println("Root node couldn't construct tree : " + reply.getPerformative());
                        step = 3;
                    }
                    replies_count++;
                    if (replies_count == ((DefaultAgent) myAgent).linkedAgents.size()) {
                        step = 2;
                        System.out.println(" ROOT Node " + myAgent.getAID().getLocalName() +
                        " found all childs : " + Arrays.toString( ((DefaultAgent) myAgent).childs.stream().map(AID::getLocalName).toArray()) );
                        myAgent.addBehaviour(new CountFinalAverage());
                    }
                } else {
                    block();
                }
            }
        }
    }

    @Override
    public boolean done() {
        return (step == 2 | step == 3);
    }
}
