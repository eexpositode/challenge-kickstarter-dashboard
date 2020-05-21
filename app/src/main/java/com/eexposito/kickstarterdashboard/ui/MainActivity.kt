package com.eexposito.kickstarterdashboard.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.helpers.CustomTabsDelegate
import com.eexposito.kickstarterdashboard.helpers.createInfoDialog
import com.eexposito.kickstarterdashboard.helpers.setColorResId
import com.eexposito.kickstarterdashboard.ui.delegates.search.SearchHostMediatorActivity
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewModel
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_input_int_range.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity :
    SearchHostMediatorActivity(),
    ProjectListView.OnProjectItemInteractionListener,
    IntRangePickerView.OnFilterActionInteractionListener {

    private val projectListViewModel: ProjectListViewModel by viewModel()
    private lateinit var customTabsDelegate: CustomTabsDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)
        customTabsDelegate = CustomTabsDelegate(this).apply {
            configure(
                color = R.color.colorPrimary,
                backButtonDrawable = ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_arrow_back_black_24dp
                )?.apply { setColorResId(this@MainActivity, R.color.colorOnPrimary) }
            )
        }
        (projectListView as? ProjectListView)?.bind(this)
        (projectListInputFilter as? IntRangePickerView)?.bind(this)
        projectListViewModel.run {
            fetchProjectList()
            projectList.observe(this@MainActivity, Observer { renderProjectList(it) })
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) return false
        menuInflater.inflate(R.menu.menu_main_screen, menu)
        // Set overflow menu items icons visible
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        // Associate search_client configuration with the SearchView
        menu.findItem(R.id.actionSearch).let { actionSearchMenuItem ->
            registerSearchViewDelegate(
                actionSearchMenuItem.actionView as SearchView,
                componentName,
                R.string.search_project_hint
            )
            actionSearchMenuItem.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionSearch -> true
        R.id.actionSortAlphabetically -> {
            projectListViewModel.sortProjectList(ProjectListViewModel.SortMethod.BY_TITLE)
                .observe(this@MainActivity, Observer { renderProjectList(it) })
            true
        }
        R.id.actionSortByTime -> {
            projectListViewModel.sortProjectList(ProjectListViewModel.SortMethod.BY_TIME_LEFT)
                .observe(this@MainActivity, Observer { renderProjectList(it) })
            true
        }
        R.id.actionFilterByBackers -> {
            (projectListInputFilter as? IntRangePickerView)?.switchVisibility()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (shouldConsumeBackPressedEvent())
            return
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onBackPressed()
    }

    override fun onPause() {
        unregisterSearchViewDelegate()
        super.onPause()
    }

    private fun renderProjectList(viewState: ProjectListViewState) = when (viewState) {
        is ProjectListViewState.LoadingState -> renderLoadingState(viewState)
        is ProjectListViewState.DataState -> updateProjectList(viewState)
        is ProjectListViewState.ErrorState -> displayErrorDialog(viewState)
    }

    private fun renderLoadingState(state: ProjectListViewState.LoadingState) =
        updateViewsVisibility(state)

    private fun updateProjectList(state: ProjectListViewState.DataState) {
        updateViewsVisibility(state)
        (projectListView as? ProjectListView)?.updateList(state.projects)
    }

    private fun displayErrorDialog(state: ProjectListViewState.ErrorState) {
        createInfoDialog(
            context = this,
            titleId = state.error.titleResId,
            message = state.error.getErrorMessage(this),
            positiveAction = { updateViewsVisibility(state) }
        ).show()
    }

    private fun updateViewsVisibility(state: ProjectListViewState) {
        when (state) {
            is ProjectListViewState.LoadingState -> {
                projectListProgressBar.visibility = View.VISIBLE
            }
            is ProjectListViewState.DataState -> {
                projectListProgressBar.visibility = View.INVISIBLE
                projectListView.visibility = if (state.projects.isEmpty()) View.INVISIBLE
                else View.VISIBLE
            }
            is ProjectListViewState.ErrorState -> {
                projectListProgressBar.visibility = View.INVISIBLE
                projectListView.visibility = View.INVISIBLE
            }
        }
    }

    override fun onProjectItemInteraction(item: ProjectItem) {
        customTabsDelegate.openUrl(item.url)
    }

    override fun onFilterActionClick(intRange: IntRange) {
        projectListViewModel.filterProjectListByBackersRange(
            fromInputText.text.toString().toInt()..toInputText.text.toString().toInt()
        ).observe(this@MainActivity, Observer { renderProjectList(it) })
    }

    override fun onVisibilityChange(isVisible: Boolean) {
        if (!isVisible)
            projectListViewModel.filterProjectListByBackersRange().observe(
                this@MainActivity, Observer { renderProjectList(it) }
            )
    }
}
