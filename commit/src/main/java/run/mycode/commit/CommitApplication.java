package run.mycode.commit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"run.mycode.commit","run.mycode.lti.launch"})
public class CommitApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommitApplication.class, args);
	}

}
