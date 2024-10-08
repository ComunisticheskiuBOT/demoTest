package org.example.demotest;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.demotest.managers.LoginManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.demotest.repository")
public class DemoTestApplication extends Application {
	public ApplicationContext applicationContext;
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
		this.applicationContext = springContext;
		stage.setTitle("Авторизация");
		LoginManager loginManager = new LoginManager(stage, applicationContext);
		loginManager.showLoginScreen();
	}

	@Override
	public void stop() throws Exception {
		springContext.close();
	}
}
