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


    private val confirmMailTemplate = "confirm_mail.ftlh"

    fun sendMail(user: User, subject: String?) {
        val preparator = MimeMessagePreparator { mimeMessage: MimeMessage? ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setSubject(subject.toString())
            messageHelper.setText(getEmailContent(user), true)
            messageHelper.setTo(user.email.toString())
            messageHelper.setFrom(from.toString())
        }
        mailSender.send(preparator)
    }

    @Throws(IOException::class, TemplateException::class)
    fun getEmailContent(user: User): String {
        val stringWriter = StringWriter()
        val model: MutableMap<String, Any> = HashMap()
        model["user"] = user
        configuration.getTemplate(confirmMailTemplate).process(model, stringWriter)
        return stringWriter.buffer.toString()
    }
}

