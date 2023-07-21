package demo.team2;

import static demo.team2.Utils.log;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Team2Application implements CommandLineRunner {

    public static void main(final String[] args) {
        SpringApplication.run(Team2Application.class, args);
    }

    public void run(String... args) {
      log("Hello Command Line Runner!");
    }

}
