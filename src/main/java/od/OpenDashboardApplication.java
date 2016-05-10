package od;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by roland@edia.nl on 10-05-16.
 */
@SpringBootApplication
public class OpenDashboardApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OpenDashboard.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(OpenDashboard.class, args);
	}
}
