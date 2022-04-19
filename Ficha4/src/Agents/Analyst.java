package Agents;

import Classes.Report;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

public class Analyst extends Agent {
    protected ArrayList<Report> reports = new ArrayList<>();

    protected void setup() {
        super.setup();

        this.addBehaviour(new Analyze(this, 10000));
    }

    private class Receive extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage reply = receive();
            try {
                Report r = (Report) reply.getContentObject();
                reports.add(r);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }
    }

    private class Analyze extends TickerBehaviour {

        public Analyze(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            String[] containers = {"Container1", "Container2", "Container3"};
            for(String c : containers) {
                ContainerID destination = new ContainerID();
                destination.setName(c);

                myAgent.doMove(destination);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                AID provider = new AID("Seller" + c.charAt(c.length() - 1), AID.ISLOCALNAME);
                msg.addReceiver(provider);
                myAgent.send(msg);
            }

            ContainerID destination = new ContainerID();
            destination.setName("Container0");
            myAgent.doMove(destination);

            for(int i = 0; i < reports.size(); i++) {
                System.out.println("Container" + i + 1 + "\n" + reports.get(i).toString());
            }
            reports.clear();
        }
    }
}
