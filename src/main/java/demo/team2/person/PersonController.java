package demo.team2.person;

import demo.team2.model.SimplePage;
import demo.team2.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(final PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String list(
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<PersonDTO> persons = personService.findAll(pageable);
        model.addAttribute("persons", persons);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(persons));
        return "person/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("person") final PersonDTO personDTO) {
        return "person/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("person") @Valid final PersonDTO personDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("personId") && personService.personIdExists(personDTO.getPersonId())) {
            bindingResult.rejectValue("personId", "Exists.person.personId");
        }
        if (bindingResult.hasErrors()) {
            return "person/add";
        }
        personService.create(personDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("person.create.success"));
        return "redirect:/persons";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("person", personService.get(id));
        return "person/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("person") @Valid final PersonDTO personDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("personId") &&
                !personService.get(id).getPersonId().equalsIgnoreCase(personDTO.getPersonId()) &&
                personService.personIdExists(personDTO.getPersonId())) {
            bindingResult.rejectValue("personId", "Exists.person.personId");
        }
        if (bindingResult.hasErrors()) {
            return "person/edit";
        }
        personService.update(id, personDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("person.update.success"));
        return "redirect:/persons";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = personService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            personService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("person.delete.success"));
        }
        return "redirect:/persons";
    }

}
