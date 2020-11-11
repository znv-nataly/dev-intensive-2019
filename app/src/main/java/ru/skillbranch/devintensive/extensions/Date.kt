package ru.skillbranch.devintensive.extensions

import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy", locale: Locale = Locale("ru")): String {
    val dateFormat = java.text.SimpleDateFormat(pattern, locale)
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits): Date {
    var time = this.time

    time += when(units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val time = this.time
    val timeNow = date.time
    val diff = time - timeNow
    val diffAbs = abs(diff)
    val prefix = if (diff > 0) "через " else ""
    val postfix = if (diff < 0) " назад" else ""

    var result = ""
    when {
        diffAbs in 0 until SECOND -> {
            result = "только что"
        }
        diffAbs in SECOND until 45 * SECOND -> {
            result = "${prefix}несколько секунд$postfix"
        }
        diffAbs in 45 * SECOND until 75 * SECOND -> {
            result = "${prefix}минуту$postfix"
        }
        diffAbs in 75 * SECOND until 45 * MINUTE -> {
            val count = (diffAbs / MINUTE).toInt()
            result = "$prefix$count ${TimeUnits.MINUTE.plural(count)}$postfix"
        }
        diffAbs in 45 * MINUTE until 75 * MINUTE -> {
            result = "${prefix}час$postfix"
        }
        diffAbs in 75 * MINUTE until 22 * HOUR -> {
            val count = (diffAbs / HOUR).toInt()
            result = "$prefix$count ${TimeUnits.HOUR.plural(count)}$postfix"
        }
        diffAbs in 22 * HOUR until 26 * HOUR -> {
            result = "${prefix}день$postfix"
        }
        diffAbs in 26 * HOUR until 360 * DAY -> {
            val count = (diffAbs / DAY).toInt()
            result = "$prefix$count ${TimeUnits.DAY.plural(count)}$postfix"
        }
        diffAbs >= 360 * DAY -> {
            result = if (diff > 0) "более чем через год" else "более года назад"
        }
    }

    return result
}

enum class TimeUnits {

    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int): String {
        val pluralList: Map<Int, Map<TimeUnits, String>> = mapOf(
                0 to mapOf(SECOND to "секунд",  MINUTE to "минут",  HOUR to "часов", DAY to "дней"),
                1 to mapOf(SECOND to "секунду", MINUTE to "минуту", HOUR to "час",   DAY to "день"),
                2 to mapOf(SECOND to "секунды", MINUTE to "минуты", HOUR to "часа",  DAY to "дня"))
        val v = if (value % 10 == 0 || (value % 10 in 5..9)) 0 else if (value % 10 in 2..4) 2 else 1
        return pluralList[v]?.get(this).toString()
    }
}