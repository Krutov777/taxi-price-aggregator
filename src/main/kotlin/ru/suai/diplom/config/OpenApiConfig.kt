package ru.suai.diplom.config

import io.swagger.v3.oas.models.*
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.suai.diplom.security.filters.JwtTokenAuthenticationFilter.Companion.AUTHENTICATION_URL
import ru.suai.diplom.security.filters.JwtTokenAuthenticationFilter.Companion.USERNAME_PARAMETER

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(buildSecurity())
            .paths(buildAuthenticationPath())
            .components(buildComponents())
            .info(buildInfo())
    }

    private fun buildAuthenticationPath(): Paths {
        return Paths()
            .addPathItem(AUTHENTICATION_URL, buildAuthenticationPathItem())
    }

    private fun buildAuthenticationPathItem(): PathItem {
        return PathItem().post(
            Operation()
                .addTagsItem("Authentication")
                .requestBody(buildAuthenticationRequestBody())
                .responses(buildAuthenticationResponses())
        )
    }

    private fun buildAuthenticationRequestBody(): RequestBody {
        return RequestBody().content(
            Content()
                .addMediaType(
                    "application/x-www-form-urlencoded",
                    MediaType()
                        .schema(
                            Schema<Any>()
                                .`$ref`("EmailAndPassword")
                        )
                )
        )
    }

    private fun buildAuthenticationResponses(): ApiResponses {
        return ApiResponses()
            .addApiResponse(
                "200",
                ApiResponse()
                    .content(
                        Content()
                            .addMediaType(
                                "application/json",
                                MediaType()
                                    .schema(
                                        Schema<Any>()
                                            .`$ref`("Tokens")
                                    )
                            )
                    )
            )
    }

    private fun buildSecurity(): SecurityRequirement {
        return SecurityRequirement().addList(BEARER_AUTH_SCHEMA_NAME)
    }

    private fun buildInfo(): Info {
        return Info().title("taxi aggregator service API by Alexander Krutov").version("0.1")
    }

    private fun buildComponents(): Components {
        val emailAndPassword = Schema<Any>()
            .type("object")
            .description("Email и пароль пользователя")
            .addProperties(USERNAME_PARAMETER, Schema<Any>().type("string"))
            .addProperties("password", Schema<Any>().type("string"))
        val tokens = Schema<Any>()
            .type("object")
            .description("Access и Refresh токены")
            .addProperties("accessToken", Schema<Any>().type("string").description("Токен доступа"))
            .addProperties("refreshToken", Schema<Any>().type("string").description("Токен для обновления"))
        val securityScheme = SecurityScheme()
            .name("bearerAuth")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
        return Components()
            .addSchemas("EmailAndPassword", emailAndPassword)
            .addSchemas("Tokens", tokens)
            .addSecuritySchemes("bearerAuth", securityScheme)
    }

    companion object {
        const val BEARER_AUTH_SCHEMA_NAME = "bearerAuth"
    }
}