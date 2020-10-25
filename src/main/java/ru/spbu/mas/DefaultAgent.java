package ru.spbu.mas;

import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;

public class DefaultAgent extends Agent {
    protected ArrayList<AID> linkedAgents = new ArrayList<AID>();
    protected float number;
    protected AID parent = null;
    protected ArrayList<AID> childs = new ArrayList<>();
    private boolean isRoot;

    @Override
    protected void setup() {
        //parse args
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String[] neighbors = args[0].toString().split(", ");
            number = Float.parseFloat(args[1].toString());
            isRoot = Boolean.parseBoolean(args[2].toString());
            for (String neighbor : neighbors) {
                AID uid = new AID(neighbor, AID.ISLOCALNAME);
                linkedAgents.add(uid);
            }
        }

        int id = Integer.parseInt(getAID().getLocalName());
        System.out.println("Agent #" + id + "Name : " + getLocalName() + "Number : " + number);

        if (isRoot){
            addBehaviour(new RootBehaviour());
        }
        else {
            addBehaviour(new NodeBehaviour());
        }
    }
}
