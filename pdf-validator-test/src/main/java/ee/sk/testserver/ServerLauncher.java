package ee.sk.testserver;

public class ServerLauncher {
    public static void main(String... args) throws Exception {
        new TestServer().continuesServer();
    }
}