package srmsocieties;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    private final StudentDAO students = new StudentDAO();
    private final ClubDAO clubs = new ClubDAO();

    // Any registration number that looks like this counts as a student (RA + digits).
    // Change the pattern here if your registration numbers have a different length.
    private static final String STUDENT_PATTERN = "^RA\\d{8,16}$";

    private boolean loggedIn(HttpSession s) { return s.getAttribute("regNo") != null; }

    @GetMapping("/")
    public String loginPage(HttpSession s) {
        return loggedIn(s) ? "redirect:/categories" : "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String role, @RequestParam String regNo, @RequestParam String password,
                        HttpSession s, Model m) {
        regNo = regNo.trim();
        password = password.trim();
        boolean asAdmin = role.equals("admin");
        try {
            String name;
            if (asAdmin) {
                name = students.checkAdmin(regNo, password);                      // admin box: checked in MySQL
            } else {
                regNo = regNo.toUpperCase();                                      // student box: RA + digits,
                boolean ok = regNo.matches(STUDENT_PATTERN) && regNo.equals(password.toUpperCase()); // password = reg no
                name = ok ? regNo : null;
            }
            if (name != null) {
                s.setAttribute("regNo", regNo);
                s.setAttribute("name", name);
                s.setAttribute("admin", asAdmin);
                return "redirect:/categories";
            }
            m.addAttribute("error", "Invalid registration number or password.");
        } catch (Exception e) {
            m.addAttribute("error", "Database error: " + e.getMessage());
        }
        m.addAttribute("role", asAdmin ? "admin" : "user");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession s) { s.invalidate(); return "redirect:/"; }

    @GetMapping("/categories")
    public String categories(HttpSession s) {
        return loggedIn(s) ? "categories" : "redirect:/";
    }

    @GetMapping("/category")
    public String category(@RequestParam String name, HttpSession s, Model m) throws Exception {
        if (!loggedIn(s)) return "redirect:/";
        List<Club> list = clubs.findByCategory(name);
        m.addAttribute("category", name);
        m.addAttribute("clubs", list);
        return "clubs";
    }

    @GetMapping("/club")
    public String club(@RequestParam int id, HttpSession s, Model m) throws Exception {
        if (!loggedIn(s)) return "redirect:/";
        Club c = clubs.findClub(id);
        if (c == null) return "redirect:/categories";
        m.addAttribute("club", c);
        m.addAttribute("teams", clubs.findTeams(id));
        return "club";
    }

    @GetMapping("/team")
    public String team(@RequestParam int id, HttpSession s, Model m) throws Exception {
        if (!loggedIn(s)) return "redirect:/";
        Team t = clubs.findTeam(id);
        if (t == null) return "redirect:/categories";
        m.addAttribute("team", t);
        m.addAttribute("club", clubs.findClub(t.getClubId()));
        return "team";
    }

    // ================= ADMIN =================
    private boolean admin(HttpSession s) { return Boolean.TRUE.equals(s.getAttribute("admin")); }

    private void fillClub(Model m, int id, String category, String name, String intro) {
        m.addAttribute("id", id); m.addAttribute("category", category);
        m.addAttribute("name", name); m.addAttribute("intro", intro);
    }

    @GetMapping("/admin/club/new")
    public String newClub(@RequestParam String category, HttpSession s, Model m) {
        if (!admin(s)) return "redirect:/";
        fillClub(m, 0, category, "", "");
        return "club-form";
    }

    @GetMapping("/admin/club/edit")
    public String editClub(@RequestParam int id, HttpSession s, Model m) throws Exception {
        if (!admin(s)) return "redirect:/";
        Club c = clubs.findClub(id);
        fillClub(m, id, c.getCategory(), c.getName(), c.getIntro());
        return "club-form";
    }

    @PostMapping("/admin/club/save")
    public String saveClub(@RequestParam int id, @RequestParam String category, @RequestParam String name,
                           @RequestParam String intro, HttpSession s) throws Exception {
        if (!admin(s)) return "redirect:/";
        if (id == 0) clubs.addClub(category, name.trim(), intro.trim());
        else clubs.updateClub(id, name.trim(), intro.trim());
        return "redirect:/category?name=" + category;
    }

    @PostMapping("/admin/club/delete")
    public String deleteClub(@RequestParam int id, HttpSession s) throws Exception {
        if (!admin(s)) return "redirect:/";
        Club c = clubs.findClub(id);
        clubs.deleteClub(id);
        return "redirect:/category?name=" + c.getCategory();
    }

    private void fillTeam(Model m, int id, int clubId, String name, String intro, String heads, String apply) {
        m.addAttribute("id", id); m.addAttribute("clubId", clubId); m.addAttribute("name", name);
        m.addAttribute("intro", intro); m.addAttribute("heads", heads); m.addAttribute("apply", apply);
    }

    @GetMapping("/admin/team/new")
    public String newTeam(@RequestParam int clubId, HttpSession s, Model m) {
        if (!admin(s)) return "redirect:/";
        fillTeam(m, 0, clubId, "", "", "", "");
        return "team-form";
    }

    @GetMapping("/admin/team/edit")
    public String editTeam(@RequestParam int id, HttpSession s, Model m) throws Exception {
        if (!admin(s)) return "redirect:/";
        Team t = clubs.findTeam(id);
        fillTeam(m, id, t.getClubId(), t.getName(), t.getIntro(), t.getHeads(), t.getApplyInfo());
        return "team-form";
    }

    @PostMapping("/admin/team/save")
    public String saveTeam(@RequestParam int id, @RequestParam int clubId, @RequestParam String name,
                           @RequestParam String intro, @RequestParam String heads,
                           @RequestParam String apply, HttpSession s) throws Exception {
        if (!admin(s)) return "redirect:/";
        if (id == 0) clubs.addTeam(clubId, name.trim(), intro.trim(), heads.trim(), apply.trim());
        else clubs.updateTeam(id, name.trim(), intro.trim(), heads.trim(), apply.trim());
        return "redirect:/club?id=" + clubId;
    }

    @PostMapping("/admin/team/delete")
    public String deleteTeam(@RequestParam int id, HttpSession s) throws Exception {
        if (!admin(s)) return "redirect:/";
        Team t = clubs.findTeam(id);
        clubs.deleteTeam(id);
        return "redirect:/club?id=" + t.getClubId();
    }
}
