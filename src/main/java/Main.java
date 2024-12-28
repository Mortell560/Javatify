/**
 * Main class wrapper
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.exit(404);
        }
        App app = new App(args[0]);
        app.run();
    }
}
