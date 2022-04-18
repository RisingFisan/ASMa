import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.ArrayList;
import java.util.HashMap;

public class MainContainer {
    Runtime rt;
    ContainerController container;

    public ContainerController initContainerInPlatform(String host, String port, String containerName) {
        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        // create a non-main agent container
        ContainerController container = rt.createAgentContainer(profile);
        return container;
    }

    public void initMainContainerInPlatform(String host, String port, String containerName) {

        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile prof = new ProfileImpl();
        prof.setParameter(Profile.CONTAINER_NAME, containerName);
        prof.setParameter(Profile.MAIN_HOST, host);
        prof.setParameter(Profile.MAIN_PORT, port);
        prof.setParameter(Profile.MAIN, "true");
        prof.setParameter(Profile.GUI, "true");

        // create a main agent container
        this.container = rt.createMainContainer(prof);
        rt.setCloseVM(true);

    }

    public void startAgentInPlatform(String name, String classpath, Object[] args) {
        try {
            AgentController ac = container.createNewAgent(name, classpath, args);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAgentInPlatformContainer(ContainerController input_container, String name, String classpath, Object[] args) {
        try {
            AgentController ac = input_container.createNewAgent(name, classpath, args);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainContainer a = new MainContainer();

        try {

            a.initMainContainerInPlatform("localhost", "9888", "MainContainer");

            ContainerController c0 = a.initContainerInPlatform("localhost","9889", "Container0");
            ArrayList<ContainerController> containers = new ArrayList<>();
            for(int i = 1; i < 4; i++) {
                containers.add(a.initContainerInPlatform("localhost", Integer.toString(9889 + i), "Container" + i));
            }

            HashMap<String, Integer> products = new HashMap<>();
            products.put("Pera", 4);
            products.put("Banana", 3);
            products.put("Melancia", 8);
            products.put("Cereja", 1);
            products.put("Laranja", 5);

            for (int i = 1; i < 4; i++) {
                ContainerController c = containers.get(i - 1);
                a.startAgentInPlatformContainer(c, "Seller_" + i, "Agents.Seller", new Object[] {products, 5});
                Thread.sleep(500);
                for(int j = 0; j < 5; j++) {
                    a.startAgentInPlatformContainer(c, "Buyer" + j + "_" + i, "Agents.Buyer", new Object[]{products.keySet()});
                    Thread.sleep(100);
                }
            }

            //a.startAgentInPlatformContainer(c0, "Analyst", "Agents.Analyst", new Object[] {});

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
