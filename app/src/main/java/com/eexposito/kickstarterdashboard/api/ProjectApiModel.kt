package com.eexposito.kickstarterdashboard.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectApiModel(
    @Json(name = "s.no")
    var sNo: Int,
    @Json(name = "amt.pledged")
    var amtPledged: Int,
    @Json(name = "blurb")
    var blurb: String,
    @Json(name = "by")
    var by: String,
    @Json(name = "country")
    var country: String,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "end.time")
    var endTime: String,
    @Json(name = "location")
    var location: String,
    @Json(name = "percentage.funded")
    var percentageFunded: Int,
    @Json(name = "num.backers")
    var numBackers: String, //TODO This has to be an Int. Use custom adapter with moshi
    @Json(name = "state")
    var state: String,
    @Json(name = "title")
    var title: String,
    @Json(name = "type")
    var type: String,
    @Json(name = "url")
    var url: String
)
