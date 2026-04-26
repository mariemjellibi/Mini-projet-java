
import Service.QuizService;
import Service.QuizServiceImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) throws Exception {
        QuizService service = new QuizServiceImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("QuizService", service);
        System.out.println("✅ RMI Server running on port 1099");
    }
}
