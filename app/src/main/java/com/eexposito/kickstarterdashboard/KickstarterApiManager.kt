package com.eexposito.kickstarterdashboard

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Observable
import org.json.JSONArray

class KickstarterApiManager(context: Context) {

    companion object {
        const val API_URL = "http://starlord.hackerearth.com/kickstarter"
    }

    //TODO This should be a singleton right?
    private var requestQueue = Volley.newRequestQueue(context)

    fun fetchProjectList() = Observable
        .create<JSONArray> { emitter ->
            requestQueue.add(JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                Response.Listener { emitter.onNext(it) },
                Response.ErrorListener { emitter.onError(it) }
            ))
        }
        .flatMap { Observable.just(parseJsonToPOJO(it)) }!!

    private fun parseJsonToPOJO(jsonResponse: JSONArray) = Moshi
        .Builder()
        .build()
        .adapter<List<Project>>(Types.newParameterizedType(List::class.java, Project::class.java))
        //TODO Use nullSafe() or notNull() to avoid nullable results?
        .fromJson(jsonResponse.toString())
        ?: throw AppException.ProjectListIsNullException
}
