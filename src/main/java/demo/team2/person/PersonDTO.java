package demo.team2.person;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PersonDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String personId;

    @NotNull
    @Size(max = 255)
    private String name;

}
