import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;

public class SellerAgent extends Agent {
    HashMap<String,Integer> products = new HashMap();
    int profit = 0;

    @Override
    protected void setup() {
        super.setup();
        this.products.put("Maçã", 10);
        this.products.put("Banana", 8);
        this.products.put("Pera", 12);
        this.products.put("Melancia", 15);
        this.products.put("Cereja", 5);
        this.products.put("Limão", 9);

        this.addBehaviour(new TotalProfit(this, 1000));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("seller");
        dfd.addServices(sd);
    }

    private class TotalProfit extends TickerBehaviour {

        private SellerAgent agent;

        public TotalProfit(Agent a, long period) {
            super(a, period);
            this.agent = (SellerAgent) a;
        }

        @Override
        protected void onTick() {
            System.out.println(String.format("Total profit: %d", this.agent.profit));
        }
    }

    private class ProcessPurchase extends CyclicBehaviour {

        @Override
        public void action() {

            ACLMessage msg = myAgent.receive();
            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                String[] details = msg.getContent().split(";");
                String fruit = details[0];
                int qt = Integer.parseInt(details[1]);

                Integer price = products.get(fruit);
                ACLMessage reply;
                if (price == null) {
                    reply = new ACLMessage(ACLMessage.REFUSE);
                } else {
                    profit += products.get(fruit) * qt;

                    reply = new ACLMessage(ACLMessage.CONFIRM);
                }

            }

        }
    }
}
