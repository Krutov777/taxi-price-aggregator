package ru.suai.diplom.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableCaching
@EnableScheduling
class SpringConfig {
}