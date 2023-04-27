package ru.suai.diplom.utils.emails

import freemarker.template.Configuration
import freemarker.template.TemplateException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Component
import ru.suai.diplom.models.User
import java.io.IOException
import java.io.StringWriter
import javax.mail.internet.MimeMessage


@Component
class EmailUtil(
    private val mailSender: JavaMailSender,
    @Qualifier("freemarkerConfiguration")
    val configuration: Configuration,
    @Value("\${spring.mail.username}")
    private val from: String,
) {
    private val resetPasswordMailTemplate = "resetPassword.ftlh"

    private val confirmMailTemplate = "confirmation.ftlh"

    fun sendLinkForConfirmUser(user: User, subject: String?) {
        val preparator = MimeMessagePreparator { mimeMessage: MimeMessage? ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setSubject(subject.toString())
            messageHelper.setText(getEmailContentForConfirmUser(user), true)
            messageHelper.setTo(user.email.toString())
            messageHelper.setFrom(from)
        }
        mailSender.send(preparator)
    }

    @Throws(IOException::class, TemplateException::class)
    fun getEmailContentForConfirmUser(user: User): String {
        val stringWriter = StringWriter()
        val model: MutableMap<String, Any> = HashMap()
        model["user"] = user
        configuration.getTemplate(confirmMailTemplate).process(model, stringWriter)
        return stringWriter.buffer.toString()
    }

    @Throws(IOException::class, TemplateException::class)
    fun getEmailContentForResetPassword(user: User, token: String): String {
        val stringWriter = StringWriter()
        val model: MutableMap<String, Any> = HashMap()
        model["token"] = token
        model["user"] = user
        configuration.getTemplate(resetPasswordMailTemplate).process(model, stringWriter)
        return stringWriter.buffer.toString()
    }

    fun sendLinkForResetPassword(user: User, token: String, subject: String?) {
        val preparator = MimeMessagePreparator { mimeMessage: MimeMessage? ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setSubject(subject.toString())
            messageHelper.setText(getEmailContentForResetPassword(user, token), true)
            messageHelper.setTo(user.email.toString())
            messageHelper.setFrom(from)
        }
        mailSender.send(preparator)
    }
}

