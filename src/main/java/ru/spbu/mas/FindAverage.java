package ru.spbu.mas;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FindAverage extends Behaviour {

    private int gainedAllNodes = 1;
    private int gainedNodes = 0;
    int step = 0;

    @Override
    public void action() {
        if (step == 0) {
            if (((DefaultAgent) myAgent).childs.size() == gainedNodes) {
                System.out.println(myAgent.getAID().getLocalName() + " got all child values");
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(((DefaultAgent) myAgent).parent);
                msg.setContent((((DefaultAgent) myAgent).number) + ":" + (gainedAllNodes));
                msg.setConversationId("number");
                myAgent.send(msg);
                step = 1;
            } else {
                ACLMessage msg2 = myAgent.receive(MessageTemplate.and(
                        MessageTemplate.MatchConversationId("number"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
                if (msg2 != null) {
                    String[] arr = msg2.getContent().split(":");
                    ((DefaultAgent) myAgent).number += Float.parseFloat(arr[0]);
                    gainedAllNodes += Integer.parseInt(arr[1]);
                    gainedNodes++;
                    System.out.println(myAgent.getAID().getLocalName() + " got child value :" + msg2.getContent() +
                            " from " + msg2.getSender().getLocalName());
                } else {
                    block();
                }
            }
        }
    }

    @Override
    public boolean done() {
        return step == 1;
    }
}
