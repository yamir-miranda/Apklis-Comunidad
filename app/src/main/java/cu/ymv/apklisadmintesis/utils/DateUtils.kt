package cu.ymv.apklisadmintesis.utils

import android.content.Context
import android.util.Log
import cu.ymv.apklisadmintesis.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    fun isMoreOfDays(days: Int, date: Date): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val date1: Date = calendar.time
        return date.before(date1)
    }

    fun formatStringDate(
        format_in: String,
        format_out: String,
        string_date: String
    ): String {
        val date: Date = SimpleDateFormat(format_in, Locale.ENGLISH).parse(string_date)
        return SimpleDateFormat(format_out, Locale.ENGLISH).format(date)
    }

    fun esHora(): Boolean {
        val date: Date
        val hora_inicio_noche = "19:00"
        val hora_fin_noche = "23:59"
        val hora_inicio_madrugada = "00:00"
        val hora_fin_madrugada = "7:00"
        val now = Calendar.getInstance()
        val hour = now[Calendar.HOUR_OF_DAY]
        val minute = now[Calendar.MINUTE]
        date = parseDate("$hour:$minute")
        val date_hora_inicio_noche = parseDate(hora_inicio_noche)
        val date_hora_inicio_madrugada = parseDate(hora_inicio_madrugada)
        val date_hora_fin_noche = parseDate(hora_fin_noche)
        val date_hora_fin_madrugada = parseDate(hora_fin_madrugada)
        return date_hora_inicio_noche.before(date) && date_hora_fin_noche.after(date) || date_hora_inicio_madrugada.before(
            date
        ) && date_hora_fin_madrugada.after(date)
    }

    private fun parseDate(date: String): Date {
        return try {
            SimpleDateFormat("HH:mm", Locale.US).parse(date)
        } catch (e: Exception) {
            Log.e("DateUtil", "Exception: " + e.message)
            Date(0)
        }
    }

    fun formatStringeIODate(format_in: String, format_out: String, string_date: String): String {
        // format_in -> dd/MM/yyyy HH:mm:ss
        // format_out -> dd/MM/yyyy HH:mm:ss

        val date = SimpleDateFormat(format_in, Locale.ENGLISH).parse(string_date)
        val format = SimpleDateFormat(format_out, Locale.ENGLISH)
        return format.format(date)


    }

    fun StringToDate(format: String, string_date: String): Date? {
        return try {
            SimpleDateFormat(format, Locale.ENGLISH).parse(string_date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }


    fun getTimeAgo(date: Date?, ctx: Context): String? {
        if (date == null) {
            return null
        }
        val time = date.time
        val curDate = currentDate()
        val now = curDate.time
        if (time > now || time <= 0) {
            return null
        }
        val dim = getTimeDistanceInMinutes(time)
        val timeAgo: String
        timeAgo = if (dim == 0) {
            "hace menos de un minuto"
        } else if (dim == 1) {
            return ctx.resources.getString(R.string.date_util_suffix) + " " + "1 " + ctx.resources
                .getString(R.string.date_util_unit_minute)
        } else if (dim >= 2 && dim <= 44) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + dim + " " + ctx.resources
                .getString(R.string.date_util_unit_minutes)
        } else if (dim >= 45 && dim <= 89) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + ctx.resources
                .getString(R.string.date_util_term_an) + " " + ctx.resources
                .getString(R.string.date_util_unit_hour)
        } else if (dim >= 90 && dim <= 1439) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + Math.round(dim / 60.toFloat()) + " " + ctx.resources
                .getString(R.string.date_util_unit_hours)
        } else if (dim >= 1440 && dim <= 2519) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + "1 " + ctx.resources.getString(
                R.string.date_util_unit_day
            )
        } else if (dim >= 2520 && dim <= 43199) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + Math.round(dim / 1440.toFloat()) + " " + ctx.resources
                .getString(R.string.date_util_unit_days)
        } else if (dim >= 43200 && dim <= 86399) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + ctx.resources
                .getString(R.string.date_util_term_a) + " " + ctx.resources
                .getString(R.string.date_util_unit_month)
        } else if (dim >= 86400 && dim <= 525599) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + Math.round(dim / 43200.toFloat()) + " " + ctx.resources
                .getString(R.string.date_util_unit_months)
        } else if (dim >= 525600 && dim <= 655199) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + ctx.resources
                .getString(R.string.date_util_term_a) + " " + ctx.resources
                .getString(R.string.date_util_unit_year)
        } else if (dim >= 655200 && dim <= 914399) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + ctx.resources
                .getString(R.string.date_util_prefix_over) + " " + ctx.resources
                .getString(R.string.date_util_term_a) + " " + ctx.resources
                .getString(R.string.date_util_unit_year)
        } else if (dim >= 914400 && dim <= 1051199) {
            ctx.resources.getString(R.string.date_util_suffix) + " " + ctx.resources
                .getString(R.string.date_util_prefix_almost) + " 2 " + ctx.resources
                .getString(R.string.date_util_unit_years)
        } else {
            ctx.resources.getString(R.string.date_util_suffix) + " " + Math.round(dim / 525600.toFloat()) + " " + ctx.resources
                .getString(R.string.date_util_unit_years)
        }
        return timeAgo
    }

    private fun getTimeDistanceInMinutes(time: Long): Int {
        val timeDistance = currentDate().time - time
        return Math.round(Math.abs(timeDistance) / 1000 / 60.toFloat())
    }

    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun loadDateofString(format: String, string_date: String): Date {
        // format -> dd/MM/yyyy HH:mm:ss
        return SimpleDateFormat(format, Locale.ENGLISH).parse(string_date)
    }
}