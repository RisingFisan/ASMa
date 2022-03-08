import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SenderAgent extends Agent {

    public void setup() {
        super.setup();

        this.addBehaviour(new SendBehavior());
    }

    private class SendBehavior extends OneShotBehaviour {

        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("receiver_agent", AID.ISLOCALNAME));
            msg.setContent("Olá receiver agent!");
            send(msg);
            block();
        }
    }
}
