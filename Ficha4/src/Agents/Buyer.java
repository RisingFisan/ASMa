package Agents;

import Classes.Purchase;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class Buyer extends Agent {

    protected HashSet<String> products;

    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        this.products = new HashSet<>((Set)args[0]);

        this.addBehaviour(new Buy(this, 1000));
        this.addBehaviour(new ConfirmPurchase());
    }

    protected void takeDown() { super.takeDown(); }

    private class Buy extends TickerBehaviour {

        public Buy(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("seller");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if(result.length > 0) {
                    var rand = new Random();
                    String product = products.stream().skip(rand.nextInt(products.size())).findFirst().get();
                    int quantity = rand.nextInt(1, 10);

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    Purchase p = new Purchase(product, quantity);
                    msg.setContentObject(p);
                    msg.addReceiver(result[0].getName());
                    myAgent.send(msg);
                    System.out.printf("%s attempted to buy %d %s\n", myAgent.getAID().getLocalName(), quantity, product);
                }
            } catch (IOException | FIPAException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ConfirmPurchase extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.CONFIRM) {
                    try {
                        Purchase p = (Purchase) msg.getContentObject();
                        System.out.printf("%s received confirmation of successful purchase of %d %s\n", myAgent.getAID().getLocalName(), p.quantity, p.productName);
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                block();
            }
        }
    }
}
