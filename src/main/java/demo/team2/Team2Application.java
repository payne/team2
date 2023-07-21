package demo.team2;

import static demo.team2.Utils.log;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import demo.team2.person.PersonService;

import org.springframework.beans.factory.annotation.*;


@SpringBootApplication
public class Team2Application implements CommandLineRunner {

  @Autowired PersonService personService;

    public static void main(final String[] args) {
        SpringApplication.run(Team2Application.class, args);
    }

    public void run(String... args) {
      log("Hello Command Line Runner!");
      for (int i=0; i < 3; i++) {
        personService.createFakePerson();
      }
    }

}
