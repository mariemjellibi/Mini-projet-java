
import Service.QuizService;
import Service.QuizServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            // Create the remote service object
            QuizService service = new QuizServiceImpl();

            // Create RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the stub to a name
            registry.rebind("QuizService", service);

            System.out.println("✅ RMI Server is running...");
            System.out.println("QuizService bound in registry");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}