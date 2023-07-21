package demo.team2.person;

import com.github.javafaker.*;
import demo.team2.model.SimplePage;
import demo.team2.util.NotFoundException;
import java.util.*;
import demo.team2.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public SimplePage<PersonDTO> findAll(final Pageable pageable) {
        final Page<Person> page = personRepository.findAll(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map((person) -> mapToDTO(person, new PersonDTO()))
                .collect(Collectors.toList()),
                page.getTotalElements(), pageable);
    }

    public PersonDTO get(final Long id) {
        return personRepository.findById(id)
                .map(person -> mapToDTO(person, new PersonDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PersonDTO personDTO) {
        final Person person = new Person();
        mapToEntity(personDTO, person);
        return personRepository.save(person).getId();
    }

    public void update(final Long id, final PersonDTO personDTO) {
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(personDTO, person);
        personRepository.save(person);
    }

    public void delete(final Long id) {
        personRepository.deleteById(id);
    }

    private PersonDTO mapToDTO(final Person person, final PersonDTO personDTO) {
        personDTO.setId(person.getId());
        personDTO.setPersonId(person.getPersonId());
        personDTO.setName(person.getName());
        return personDTO;
    }

    private Person mapToEntity(final PersonDTO personDTO, final Person person) {
        person.setPersonId(personDTO.getPersonId());
        person.setName(personDTO.getName());
        return person;
    }

    public boolean personIdExists(final String personId) {
        return personRepository.existsByPersonIdIgnoreCase(personId);
    }

    @Transactional
    public String getReferencedWarning(final Long id) {
        final Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        if (!person.getTeamPersonTeams().isEmpty()) {
            return WebUtils.getMessage("person.team.manyToMany.referenced", person.getTeamPersonTeams().iterator().next().getId());
        }
        return null;
    }
    public PersonDTO createFakePerson() {
        Faker faker = new Faker();
        String name = faker.name().fullName(); // Miss Samanta Schmidt
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName(name);
        personDTO.setPersonId(UUID.randomUUID().toString());
        // personDTO.setPersonId(faker.idNumber().validSSN());
        Long id = create(personDTO);
        personDTO.setId(id);
        return personDTO;
    }
}
