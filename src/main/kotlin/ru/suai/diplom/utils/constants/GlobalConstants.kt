package ru.suai.diplom.utils.constants

object GlobalConstants {
    const val BASE_URL_YANDEX = "https://taxi-routeinfo.taxi.yandex.net/taxi_info"
    const val BASE_URL_TAXINF = "https://api.taxinf.ru/get_prices/?" +
            "city_name=&from_address_1st_line=from&from_address_2st_line=" +
            "&to_address_1st_line=to&to_address_2st_line=" +
            "&user_id=75442486-0878-440c-9db1-a7006c25a39f&key=AxdZsa2daDa2d&hash=eaa0ba3421b0336a524232d051158f75"
    const val USER_NOT_FOUND = "Аккаунт не найден"
    const val OCCUPIED_EMAIL = "Пользователь с таким email уже существует"
    const val OCCUPIED_LOGIN = "Пользователь с таким login-ом уже существует"
    const val PASSWORDS_DONT_MATCH = "Пароли не совпадают"
    const val UNAUTHORIZED = "Вы не авторизированы"
}