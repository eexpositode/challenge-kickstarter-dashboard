package com.eexposito.kickstarterdashboard.viewmodels

import com.eexposito.kickstarterdashboard.helpers.PROJECT_URL_PREFIX
import com.eexposito.kickstarterdashboard.persistence.ProjectEntity
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import java.text.NumberFormat
import java.util.*

data class ProjectItem(private val project: ProjectEntity) {
    val title = project.title
    val pledge = NumberFormat.getCurrencyInstance()
        .apply {
            maximumFractionDigits = 2
            currency = Currency.getInstance("USD")
        }
        .format(project.amtPledged)!!
    val backers = project.numBackers
    val backersToDisplay = backers.toString()
    val daysLeft = DateTimeTz.nowLocal()
        .minus(DateTime.fromString(project.endTime))
        .days.toInt().toString()
    val url = PROJECT_URL_PREFIX.plus(project.url)
}
