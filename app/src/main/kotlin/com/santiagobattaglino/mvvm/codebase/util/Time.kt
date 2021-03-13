package com.santiagobattaglino.mvvm.codebase.util

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

fun getCalendar(timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Calendar {
    return Calendar.getInstance(timeZone)
}

fun getFormat(): SimpleDateFormat {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
}

fun getDateTimeFormat(): SimpleDateFormat {
    return SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault())
}

fun getDateFormat(): SimpleDateFormat {
    return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
}

fun getTimeFormat(): SimpleDateFormat {
    return SimpleDateFormat("HH:mm", Locale.getDefault())
}

fun String.toDate(
    formatter: SimpleDateFormat = getFormat(),
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date? {
    formatter.timeZone = timeZone
    return try {
        formatter.parse(this) ?: null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Date.formatTo(
    formatter: SimpleDateFormat = getFormat(),
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): String {
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun getAtLocalTime(dateString: String): String? =
    dateString.toDate()?.formatTo(getDateTimeFormat(), timeZone = TimeZone.getDefault())

fun getCurrentTime(formatter: SimpleDateFormat = getFormat()): String {
    return Date(System.currentTimeMillis()).formatTo(formatter)
}

fun getDatePicker(): MaterialDatePicker.Builder<Long> {
    val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
    val constraintsBuilder = CalendarConstraints.Builder()
    val validators: ArrayList<CalendarConstraints.DateValidator> = ArrayList()
    validators.add(DateValidatorPointBackward.before(getCalendar().timeInMillis))
    constraintsBuilder.setValidator(CompositeDateValidator.allOf(validators))
    datePickerBuilder.setCalendarConstraints(constraintsBuilder.build())
    return datePickerBuilder
}

fun getTimePicker(calendar: Calendar = getCalendar()): TimePickerDialog {
    val hourNow = calendar[Calendar.HOUR_OF_DAY]
    val minNow = calendar[Calendar.MINUTE]
    val timePickerDialog =
        TimePickerDialog.newInstance(
            null,
            hourNow,
            minNow,
            true
        )

    val calendarLocalTime = getCalendar(TimeZone.getDefault())
    timePickerDialog.setMaxTime(
        calendarLocalTime.get(Calendar.HOUR_OF_DAY),
        calendarLocalTime.get(Calendar.MINUTE),
        calendarLocalTime.get(Calendar.SECOND)
    )
    return timePickerDialog
}