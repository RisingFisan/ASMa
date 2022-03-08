import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiverAgent extends Agent {

    public void setup() {
        super.setup();

        this.addBehaviour(new ReceiveBehavior());
    }

    private class ReceiveBehavior extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();
            if(msg != null) {
                System.out.println("Recebi uma mensagem de " + msg.getSender() + ". Conteúdo: " + msg.getContent());
            }
            block();
        }
    }
}
