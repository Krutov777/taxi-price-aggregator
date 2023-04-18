package ru.suai.diplom.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.suai.diplom.dto.request.addOrderHistoryRequest
import ru.suai.diplom.dto.response.HistoryPriceResponse
import ru.suai.diplom.dto.response.TaxiPricesResponse
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Schema(name = "Price Controller")
@RequestMapping("api/prices")
interface PriceApi {
    @Operation(summary = "Получение цен на такси")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Цены на такси успешно получены",
            content = [Content(schema = Schema(implementation = TaxiPricesResponse::class))]
        ), ApiResponse(responseCode = "400", description = "Ошибка при получении цен на такси")]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPricesTaxi(
        @Valid @NotBlank @Length(
            min = 3,
            max = 256
        ) @Parameter(description = "Адрес 'Откуда'") @RequestParam("fromAddress") fromAddress: String,
        @Valid @NotBlank @Length(
            min = 3,
            max = 256
        ) @Parameter(description = "Адрес 'Куда'") @RequestParam("toAddress") toAddress: String,
        @Valid @NotNull @Parameter(description = "Долгота 'Откуда'") @RequestParam("longitudeFrom") longitudeFrom: Double,
        @Valid @NotNull @Parameter(description = "Широта 'Откуда'") @RequestParam("latitudeFrom") latitudeFrom: Double,
        @Valid @NotNull @Parameter(description = "Долгота 'Куда'") @RequestParam("longitudeTo") longitudeTo: Double,
        @Valid @NotNull @Parameter(description = "Широта 'Куда'") @RequestParam("latitudeTo") latitudeTo: Double,
    ): ResponseEntity<TaxiPricesResponse>

    @Operation(summary = "Добавление заказа истории цен")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Успешно заказана история цен на такси",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]
        ),
            ApiResponse(responseCode = "400", description = "Ошибка заказе истории цен на такси"),
            ApiResponse(responseCode = "401", description = "Не авторизованы"),
            ApiResponse(responseCode = "403", description = "Нет прав доступа"),
            ApiResponse(responseCode = "404", description = "Не найдено")
        ]
    )
    @PostMapping
    fun addOrderHistoryPrice(@Valid @RequestBody addOrderHistoryRequest: addOrderHistoryRequest, authentication: Authentication?): ResponseEntity<HttpStatus>

    @Operation(summary = "Получение истории цен на такси")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "История цен на такси успешно получена",
            content = [Content(schema = Schema(implementation = HistoryPriceResponse::class))]
        ), ApiResponse(responseCode = "400", description = "Ошибка при получении истории цен на такси")]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE], value = ["/history"])
    fun getHistoryPrice(
        @Valid @NotNull @Parameter(description = "Долгота 'Откуда'") @RequestParam("longitudeFrom") longitudeFrom: Double,
        @Valid @NotNull @Parameter(description = "Широта 'Откуда'") @RequestParam("latitudeFrom") latitudeFrom: Double,
        @Valid @NotNull @Parameter(description = "Долгота 'Куда'") @RequestParam("longitudeTo") longitudeTo: Double,
        @Valid @NotNull @Parameter(description = "Широта 'Куда'") @RequestParam("latitudeTo") latitudeTo: Double,
    ): ResponseEntity<HistoryPriceResponse>

    @Operation(summary = "Получение истории цен на такси для пользователя")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "История цен на такси успешно получена",
            content = [Content(schema = Schema(implementation = HistoryPriceResponse::class))]
        ), ApiResponse(responseCode = "400", description = "Ошибка при получении истории цен на такси")]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE], value = ["/history_user"])
    fun getHistoryPriceByAuthentication(
        authentication: Authentication?
    ): ResponseEntity<HistoryPriceResponse>
}