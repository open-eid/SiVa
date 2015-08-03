package ee.sk.testserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestServer {
    private static final int SERVER_PORT = 8080;
    private Server server;

    public void startServer() throws Exception {
        server = new Server(SERVER_PORT);
        server.setHandler(createDssWebApp());

        server.start();
        server.setStopAtShutdown(true);
    }

    public void continuesServer() throws Exception {
        startServer();
        server.join();
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Handler createDssWebApp() throws IOException {
        DeployProperties deployProperties = new DeployProperties();
//        String warFile = deployProperties.getProperty(DeployPropertyKey.DEPLOY_WAR);
//        if (warNotExists(Paths.get(warFile))) {
//            final String errorMessage = "Make sure You have built the project. No WAR file found at path: ";
//            throw new FileNotFoundException(errorMessage + warFile);
//        }

        WebAppContext dssApp = new WebAppContext();
        dssApp.setDescriptor(deployProperties.getProperty(DeployPropertyKey.DEPLOY_WEBAPP_XML));
        dssApp.setResourceBase(deployProperties.getProperty(DeployPropertyKey.DEPLOY_WEBAPP));
        dssApp.setContextPath(deployProperties.getProperty(DeployPropertyKey.CONTEXT));

        return dssApp;
    }

    private static boolean warNotExists(Path warPath) {
        return Files.notExists(warPath);
    }
}
