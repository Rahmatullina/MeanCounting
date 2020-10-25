package ru.spbu.mas;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import java.util.concurrent.ThreadLocalRandom;

import java.util.HashMap;

class MainController {
    private static final int numberOfAgents = 5;
    void initAgents() {
        // Retrieve the singleton instance of the JADE Runtime
        Runtime rt = Runtime.instance();
        //Create a container to host the Default Agent
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "false");
        ContainerController cc = rt.createMainContainer(p);
        try {
            int randomNum = ThreadLocalRandom.current().nextInt(1, numberOfAgents + 1);
            System.out.println("Random root id : " + randomNum);
            HashMap<Integer, String> neighbors = new HashMap<Integer, String>();
            neighbors.put(1, "2, 3, 4");
            neighbors.put(2, "1, 4");
            neighbors.put(3, "1, 4, 5");
            neighbors.put(4, "1, 2, 3, 5");
            neighbors.put(5, "3, 4");
            for(int i=1; i <= MainController.numberOfAgents; i++) {
                float number = i*10;
                AgentController agent;
                if (i != randomNum) {
                    agent = cc.createNewAgent(Integer.toString(i),
                            "ru.spbu.mas.DefaultAgent", new Object[]{neighbors.get(i), number, false});
                }
                else {
                    agent = cc.createNewAgent(Integer.toString(i),
                            "ru.spbu.mas.DefaultAgent", new Object[]{neighbors.get(i), number, true});
                }

                agent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
