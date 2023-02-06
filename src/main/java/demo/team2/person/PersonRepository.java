package demo.team2.person;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByPersonIdIgnoreCase(String personId);

}
