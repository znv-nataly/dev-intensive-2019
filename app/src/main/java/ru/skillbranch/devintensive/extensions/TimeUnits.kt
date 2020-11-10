package ru.skillbranch.devintensive.extensions

fun TimeUnits.plural(value: Int): String {

    val pluralList: Map<Int, Map<TimeUnits, String>> = mapOf(
        0 to mapOf(TimeUnits.SECOND to "секунд", TimeUnits.MINUTE to "минут", TimeUnits.HOUR to "часов", TimeUnits.DAY to "дней"),
        1 to mapOf(TimeUnits.SECOND to "секунду", TimeUnits.MINUTE to "минуту", TimeUnits.HOUR to "час", TimeUnits.DAY to "день"),
        2 to mapOf(TimeUnits.SECOND to "секунды", TimeUnits.MINUTE to "минуты", TimeUnits.HOUR to "часа", TimeUnits.DAY to "дня")
    )
    val v = if (value % 10 == 0 || (value % 10 in 5..9)) 0 else if (value % 10 in 2..4) 2 else 1

    return pluralList[v]?.get(this).toString()
}