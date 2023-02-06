package demo.team2.team;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeamDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String teamId;

    @NotNull
    @Size(max = 255)
    private String name;

    private List<Long> teamPersons;

}
