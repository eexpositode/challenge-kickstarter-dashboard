package com.eexposito.kickstarterdashboard.viewmodels

import com.eexposito.kickstarterdashboard.api.Project

data class ProjectItem(private val project: Project) {
    val title = project.title
    //TODO Format currency
    val pledge = project.amtPledged.toString()
    val backers = project.numBackers
    //TODO Use Datetime to calculate time left
    val daysLeft = project.endTime
}
