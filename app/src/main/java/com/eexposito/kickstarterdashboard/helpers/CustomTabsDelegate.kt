package com.eexposito.kickstarterdashboard.helpers

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.eexposito.kickstarterdashboard.R
import org.jetbrains.anko.browse

/**
 * Implementation and connection to the Chrome Custom Tabs.
 * It provides a session to speed up opening the url, certain customizations on the opened url and an
 * alternative if no Custom Tabs are installed in the phone
 *
 * From:
 * https://developer.chrome.com/multidevice/android/customtabs
 * https://medium.com/@pancodemakes/tutorial-chrome-custom-tabs-c727cf3f4ddd
 */
class CustomTabsDelegate(private val context: Context) : CustomTabsServiceConnection() {

    companion object {
        const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
    }

    private var isCustomTabsAvailable = true
    private var customTabsSession: CustomTabsSession? = null

    @ColorInt
    private var toolBarColor: Int? = null
    private var backButton: Bitmap? = null
    private var urlToOpen: String? = null

    init {
        CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, this)
    }

    fun configure(@ColorRes color: Int, backButtonDrawable: Drawable? = null) {
        toolBarColor = ContextCompat.getColor(context, color)
        backButton = backButtonDrawable?.toBitmap()
    }

    fun openUrl(urlToOpen: String) {
        this.urlToOpen = urlToOpen
        if (isCustomTabsAvailable) {
            if (customTabsSession == null) CustomTabsIntent.Builder()
            else CustomTabsIntent.Builder(customTabsSession).apply {
                if (toolBarColor != null) setToolbarColor(toolBarColor!!)
                if (backButton != null) setCloseButtonIcon(backButton!!)
            }
                .setStartAnimations(context, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .setExitAnimations(context, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                .build()
                .launchUrl(context, Uri.parse(urlToOpen))
        } else {
            context.browse(urlToOpen)
        }
    }

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        isCustomTabsAvailable = client.warmup(0L)
        customTabsSession = client.newSession(null)
        isCustomTabsAvailable = isCustomTabsAvailable || urlToOpen?.let {
            customTabsSession?.mayLaunchUrl(Uri.parse(it), null, null) ?: false
        } ?: false
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        customTabsSession = null
    }
}
