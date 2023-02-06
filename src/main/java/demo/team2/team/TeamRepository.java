package demo.team2.team;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByTeamIdIgnoreCase(String teamId);

}
