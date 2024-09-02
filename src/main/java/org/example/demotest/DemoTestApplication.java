package org.example.demotest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.LoginManager;
import org.example.demotest.app_controllers.MainViewManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.demotest.repository")
public class DemoTestApplication extends Application {

	@Autowired
	private org.example.demotest.app_controllers.welcomeController welcomeController;

	private ConfigurableApplicationContext springContext;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void init() {
		springContext = SpringApplication.run(getClass());
		springContext.getAutowireCapableBeanFactory().autowireBean(this);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(new StackPane());

		LoginManager loginManager = new LoginManager(scene);
		loginManager.showLoginScreen();

		stage.setScene(scene);
		stage.setHeight(800);
		stage.setWidth(600);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		springContext.close();
	}
}
