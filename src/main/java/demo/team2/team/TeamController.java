package demo.team2.team;

import demo.team2.model.SimplePage;
import demo.team2.person.Person;
import demo.team2.person.PersonRepository;
import demo.team2.util.WebUtils;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final PersonRepository personRepository;

    public TeamController(final TeamService teamService, final PersonRepository personRepository) {
        this.teamService = teamService;
        this.personRepository = personRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("teamPersonsValues", personRepository.findAll(Sort.by("id"))
                .stream()
                .collect(Collectors.toMap(Person::getId, Person::getPersonId)));
    }

    @GetMapping
    public String list(
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<TeamDTO> teams = teamService.findAll(pageable);
        model.addAttribute("teams", teams);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(teams));
        return "team/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("team") final TeamDTO teamDTO) {
        return "team/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("team") @Valid final TeamDTO teamDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("teamId") && teamService.teamIdExists(teamDTO.getTeamId())) {
            bindingResult.rejectValue("teamId", "Exists.team.teamId");
        }
        if (bindingResult.hasErrors()) {
            return "team/add";
        }
        teamService.create(teamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("team.create.success"));
        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("team", teamService.get(id));
        return "team/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("team") @Valid final TeamDTO teamDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("teamId") &&
                !teamService.get(id).getTeamId().equalsIgnoreCase(teamDTO.getTeamId()) &&
                teamService.teamIdExists(teamDTO.getTeamId())) {
            bindingResult.rejectValue("teamId", "Exists.team.teamId");
        }
        if (bindingResult.hasErrors()) {
            return "team/edit";
        }
        teamService.update(id, teamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("team.update.success"));
        return "redirect:/teams";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        teamService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("team.delete.success"));
        return "redirect:/teams";
    }

}
