package ru.live.kamaz_cs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.live.kamaz_cs.domain.Role;
import ru.live.kamaz_cs.domain.User;
import ru.live.kamaz_cs.service.UserService;

import java.util.Map;

@Controller
@RequestMapping("/user")
//@PreAuthorize("hasAuthority('ADMIN')")
// проверка на наличие прав ADMIN-а у пользователей ко всем методам в этом классе, чтобы это заработало - нужно в классе WebSecurityConfig указать @EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", service.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {
        service.saveUser(user, username, form);
        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
    ) {
        service.updateProfile(user, password, email);
        return "redirect:/user/profile";
    }

    @GetMapping("subscriber/{user}")
    public String subscriber(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user
    ) {
        service.subscriber(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("unsubscriber/{user}")
    public String unsubscriber(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user
    ) {
        service.unsubscriber(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("{type}/{user}/list")
    public String userList(
            Model model,
            @PathVariable String type,
            @PathVariable User user
    ) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);

        if ("subscriptions".equals(type)) {
            model.addAttribute("users", user.getSubscriptions());
        } else {
            model.addAttribute("users", user.getSubscribers());
        }
        return "subscriptions";
    }
}
