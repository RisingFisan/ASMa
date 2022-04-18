package Agents;

import Classes.Report;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

public class Analyst extends Agent {
    protected void setup() {
        super.setup();

        this.addBehaviour(new Analyze(this, 10000));
    }

    private class Analyze extends TickerBehaviour {

        public Analyze(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            String[] containers = {"Container1", "Container2", "Container3"};
            ArrayList<Report> reports = new ArrayList<>();
            for(String c : containers) {
                ContainerID destination = new ContainerID();
                destination.setName(c);

                myAgent.doMove(destination);

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("seller");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if(result.length > 0) {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(result[0].getName());
                        myAgent.send(msg);

                        ACLMessage reply = receive();
                        Report r = (Report) reply.getContentObject();
                        reports.add(r);
                    }
                } catch (FIPAException | UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }

            ContainerID destination = new ContainerID();
            destination.setName("Container0");
            myAgent.doMove(destination);

            for(int i = 0; i < reports.size(); i++) {
                System.out.println("Container" + i + 1 + "\n" + reports.get(i).toString());
            }
        }
    }
}
