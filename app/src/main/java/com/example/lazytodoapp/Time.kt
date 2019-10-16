package com.example.lazytodoapp

import android.os.Build
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*


class Time {
    lateinit var calendar: Calendar

    /**
     * Converter
     *
     * Calendar : Calendar 객체
     * timeMillis : milliseconds로 환산된 값
     * timestamp : yyyy-MM-dd:(HH-mm-ss) format으로 변환된 값
     *
     */

    companion object {
        val MIN = Time("2018-12-01")
        val MAX = Time("2100-01-01")
    }

    val TAG = this.javaClass.simpleName


    fun clone(): Time {

        return Time(calendar.clone() as Calendar)
    }

    constructor() {

        this.calendar = Calendar.getInstance()
    }

    constructor(calendar: Calendar) {
        this.calendar = calendar
    }

    constructor(date: Date) {
        this.calendar = Calendar.getInstance()
        calendar.time = date

    }

    constructor(timeMillis: Long) {
        calendar = Calendar.getInstance()
        calendar.time = java.util.Date(timeMillis)
    }

    constructor(timestamp: String) {
        if (Regex("[0-2][0-9][0-5][0-9]").matches(timestamp)) {
            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timestamp.substring(0, 2).toInt())
            calendar.set(Calendar.MINUTE, timestamp.substring(2, 4).toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return
        }

        val year = Integer.parseInt(timestamp.substring(0, 4))
        val month = Integer.parseInt(timestamp.substring(5, 7)) - 1
        val day = Integer.parseInt(timestamp.substring(8, 10))

        var hourOfDay = 0
        var minute = 0
        var second = 0
        if (timestamp.length > 10) {
            hourOfDay = Integer.parseInt(timestamp.substring(11, 13))
            minute = Integer.parseInt(timestamp.substring(14, 16))
            second = Integer.parseInt(timestamp.substring(17, 19))
        }

        calendar = Calendar.getInstance()
        calendar.set(year, month, day, hourOfDay, minute, second)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun toCalendar(): Calendar = calendar
    fun toTimeMillis(): Long = calendar.timeInMillis
    fun toDate(): Date = calendar.time

    fun toTimestamp(): String {
        return String.format(Locale.getDefault(), "%4d-%02d-%02dT%02d:%02d:%02d",
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))
    }

    fun toAlarmTime(): String {
        return String.format("%02d%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    fun toTimestamp(dateOnly: Boolean): String {
        return if (dateOnly) {
            String.format(Locale.getDefault(), "%4d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE))
        } else {
            String.format(Locale.getDefault(), "%4d-%02d-%02dT%02d:%02d:%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))
        }
    }

    fun diffInMonth(to: Time): Int {

        val diffYear = to.get(Calendar.YEAR) - this.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + to.get(Calendar.MONTH) - this.get(Calendar.MONTH)

        return diffMonth
    }

    /**
     * Calendar Wrapper Methods
     */


    fun get(type: Int): Int {
        return calendar.get(type)
    }


    fun set(type: Int, value: Int): Time {
        calendar.set(type, value)
        return this
    }

    fun add(type: Int, value: Int): Time {
        calendar.add(type, value)
        return this
    }

    /**
     * Custom Methods
     */

    fun getStart(): Time {
        val newCal = calendar.clone() as Calendar

        newCal.set(Calendar.HOUR_OF_DAY, 0)
        newCal.set(Calendar.MINUTE, 0)
        newCal.set(Calendar.SECOND, 0)
        newCal.set(Calendar.MILLISECOND, 0)
        return Time(newCal)
    }

    fun getEnd(): Time {
        return getStart().add(Calendar.DATE, 1)
    }

    fun getStartMonth(): Time {
        val newTime = getStart()
        newTime.set(Calendar.DATE, 1)
        return newTime
    }

    fun getStartWeek(startDayOfWeek: Int): Time {
        val dayDiff = (14 + (this.calendar.get(Calendar.DAY_OF_WEEK) - startDayOfWeek)) % 7
        val startDayOfWeek = this.clone() - dayDiff


        return startDayOfWeek
    }

    fun getWeeks(): Int {
        var startDay = this.getStartMonth().getStartWeek(Calendar.MONDAY)
        var endDay = this.getStartMonth().add(Calendar.MONTH, 1).add(Calendar.DATE, -1).getStartWeek(Calendar.MONDAY)
        var weeks = 4
        startDay += 7 * (weeks - 1)
        while (startDay < endDay) {
            startDay += 7
            weeks++
        }
        return weeks
    }


    fun isSameDate(other: Time): Boolean {
        return (this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                && this.get(Calendar.MONTH) == other.get(Calendar.MONTH)
                && this.get(Calendar.DATE) == other.get(Calendar.DATE))
    }

    fun getDayLength(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.of(get(Calendar.YEAR), get(Calendar.MONTH)).lengthOfMonth()
        } else {
            val mycal = GregorianCalendar(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DATE))
            mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }

    val DAY_OF_WEEK = arrayOf("토", "일", "월", "화", "수", "목", "금")

    fun getDayOfWeek(): String {
        return DAY_OF_WEEK[((7 + get(Calendar.DAY_OF_WEEK)) % 7)]
    }


    override fun equals(other: Any?): Boolean {
        return if (other is Time) {
            this.calendar.compareTo(other.calendar) == 0
        } else {
            false
        }
    }


    operator fun compareTo(time: Time): Int {
        return this.calendar.compareTo(time.calendar)
    }

    operator fun plus(day: Int): Time {
        return this.clone().add(Calendar.DATE, day)
    }

    operator fun plus(diff: Long): Time {
        val time = this.toTimeMillis() + diff
        return Time(time)
    }

    operator fun minus(day: Int): Time {
        return this.add(Calendar.DATE, -day)
    }

    operator fun minus(time: Time): Int {
        val diff = this.toTimeMillis() - time.toTimeMillis()
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }
    fun minusMillis(time: Time): Long {
        val diff = this.toTimeMillis() - time.toTimeMillis()
        return diff
    }


    fun rangeTo(other: Time): MutableList<Time> {
        val ranges = mutableListOf<Time>()
        var temp = this.clone().getStart()

        if (temp >= other) {
            return ranges
        }
        while (temp < other) {
            ranges.add(temp.clone())
            temp += 1
        }
        ranges.add(other)
        return ranges
    }

    fun until(other: Time): MutableList<Time> {
        val ranges = mutableListOf<Time>()
        var temp = this.clone().getStart()

        if (temp >= other) {
            return ranges
        }
        while (temp < other) {
            ranges.add(temp.clone())
            temp += 1
        }
        return ranges
    }

    fun format(format: String, timeDiff: Boolean = false): String? {

        val dateFormat = SimpleDateFormat(format, Locale.US)
        if (timeDiff) {
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        }
        val date = calendar.time
        return dateFormat.format(date)
    }


}
