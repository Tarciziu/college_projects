import controller.LoginController;
import controller.MainWindowController2;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import services.IService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClient.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        // IService server = new ServicesRpcProxy(serverIP, serverPort);

        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
        IService server = (IService)factory.getBean("service");


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/gui/login.fxml"));
        Parent root = loader.load();


        LoginController login =
                loader.getController();
        login.setService(server);


        FXMLLoader cloader = new FXMLLoader(
                getClass().getResource("/gui/mainWindow2.fxml"));
        Scene croot = new Scene(cloader.load());


        MainWindowController2 mainWindowCtrl =
                cloader.getController();
        mainWindowCtrl.setService(server);

        login.setController(mainWindowCtrl);
        login.setParent(croot);

        primaryStage.setTitle("GCT Trans");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
