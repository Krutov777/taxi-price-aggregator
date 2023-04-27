package ru.suai.diplom.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import ru.suai.diplom.services.SignUpService


@Controller
class MvcController(private val signUpService: SignUpService) {
    @GetMapping("/user/change_password")
    fun showChangePasswordPage(
        model: ModelMap,
        @RequestParam("token") token: String
    ): ModelAndView {
        val result: String? = signUpService.validatePasswordResetToken(token)
        return if (result != null) {
            model.addAttribute("errorMessage", result)
            ModelAndView("errorValidatePasswordResetToken.html", model)
        } else {
            model.addAttribute("token", token)
            return ModelAndView("updatePassword.html", model)
        }
    }
}