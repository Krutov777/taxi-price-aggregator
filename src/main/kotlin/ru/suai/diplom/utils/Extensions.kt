package ru.suai.diplom.utils

//    https://stackoverflow.com/questions/11145681/how-to-convert-a-string-with-unicode-encoding-to-a-string-of-letters
fun String.unescapeUnicode() = replace("\\\\u([0-9A-Fa-f]{4})".toRegex()) {
    String(Character.toChars(it.groupValues[1].toInt(radix = 16)))
}