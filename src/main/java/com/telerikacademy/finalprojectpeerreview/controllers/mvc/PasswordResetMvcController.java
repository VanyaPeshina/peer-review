package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.email.CustomerAccountService;
import com.telerikacademy.finalprojectpeerreview.email.ResetPasswordData;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
public class PasswordResetMvcController {

    private final MessageSource messageSource;
    private final CustomerAccountService customerAccountService;

    @Autowired
    public PasswordResetMvcController(MessageSource messageSource, CustomerAccountService customerAccountService) {
        this.messageSource = messageSource;

        this.customerAccountService = customerAccountService;
    }

    @GetMapping("/request")
    public String showPage(Model model) {
        setResetPasswordForm(model, new ResetPasswordData());
        return "forgot_password";
    }

    @PostMapping("/request")
    public String resetPassword(ResetPasswordData forgotPasswordData, RedirectAttributes redirectAttributes) {
        try {
            customerAccountService.forgottenPassword(forgotPasswordData.getEmail());
        } catch (EntityNotFoundException | DuplicateEntityException e) {
            e.printStackTrace();
        }
       /* redirectAttributes.addFlashAttribute("resetPasswordMsg",
                messageSource.getMessage("user.forgotpwd.msg", null, LocaleContextHolder.getLocale()));*/
        return "redirect:/login";
    }

    @GetMapping("/change")
    public String changePassword(@RequestParam(required = false) String token,
                                 final RedirectAttributes redirectAttributes,
                                 final Model model) {
        if (StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute("tokenError",
                    messageSource.getMessage("missing.token", null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        ResetPasswordData data = new ResetPasswordData();
        data.setToken(token);
        setResetPasswordForm(model, data);
        return "reset_password";
    }

    @PostMapping("/change")
    public String changePassword(final ResetPasswordData data, final Model model) {
        try {
        customerAccountService.updatePassword(data.getPassword(), data.getToken());
        } catch (EntityNotFoundException | DuplicateEntityException | UnauthorizedOperationException e) {
            model.addAttribute("tokenError",
                    messageSource.getMessage("invalid.token", null, LocaleContextHolder.getLocale()));
            return "reset_password";
        }
  /*      model.addAttribute("passwordUpdateMsg",
                messageSource.getMessage("user.password.updated.msg", null, LocaleContextHolder.getLocale()));*/
        setResetPasswordForm(model, new ResetPasswordData());
        return "redirect:/login";
    }

    private void setResetPasswordForm(Model model, ResetPasswordData data) {
        model.addAttribute("forgotPassword", data);
    }
}
