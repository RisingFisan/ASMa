package Agents;

import Classes.Purchase;
import Classes.Report;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Seller extends Agent {

    protected HashMap<String, Integer> products;
    protected HashMap<String, Integer> sold;
    protected int totalProfit;
    protected int buyers;

    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        this.products = new HashMap<>((HashMap) args[0]);
        this.buyers = (int) args[1];
        this.sold = new HashMap<>(products.keySet().stream().collect(Collectors.toMap(Function.identity(), dummy -> 0)));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("seller");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        this.addBehaviour(new DisplayProfit(this, 10000));
        this.addBehaviour(new Sell());
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        super.takeDown();
    }

    private class DisplayProfit extends TickerBehaviour {
        public DisplayProfit(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            System.out.printf("Total profit from Seller %s: %d\n",  myAgent.getAID().getLocalName(), totalProfit);
        }
    }

    private class Sell extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        Purchase p = (Purchase) msg.getContentObject();

                        if (products.containsKey(p.productName)) {
                            int profit = p.quantity * products.get(p.productName);
                            sold.compute(p.productName, (k, v) -> v + profit);
                            totalProfit += profit;

                            System.out.printf("%s sold %d %s to %s\n", myAgent.getAID().getLocalName(), p.quantity, p.productName, msg.getSender().getLocalName());

                            ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
                            reply.setContentObject(p);
                            reply.addReceiver(msg.getSender());
                            myAgent.send(reply);
                        } else {
                            ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
                            reply.addReceiver(msg.getSender());
                            myAgent.send(reply);
                        }
                    } catch (IOException | UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);

                    Map.Entry<String, Integer> e = sold.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).findFirst().get();
                    Report r = new Report(totalProfit, totalProfit / buyers, e.getKey(), e.getValue());

                    try {
                        reply.setContentObject(r);
                        reply.addReceiver(msg.getSender());
                        myAgent.send(reply);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                block();
            }
        }
    }
}
