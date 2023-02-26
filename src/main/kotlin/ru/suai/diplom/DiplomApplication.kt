package ru.suai.diplom

import freemarker.template.Configuration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class DiplomApplication {
	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	@Bean(name = ["freemarkerConfiguration"])
	fun getFreeMarkerConfiguration(): Configuration? {
		val config = Configuration(Configuration.getVersion())
		config.setClassForTemplateLoading(this.javaClass, "/templates/")
		return config
	}
}

fun main(args: Array<String>) {
	runApplication<DiplomApplication>(*args)
}