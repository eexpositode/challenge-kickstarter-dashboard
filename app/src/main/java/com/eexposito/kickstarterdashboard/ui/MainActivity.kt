package com.eexposito.kickstarterdashboard.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.helpers.CustomTabsDelegate
import com.eexposito.kickstarterdashboard.helpers.createInfoDialog
import com.eexposito.kickstarterdashboard.helpers.forceSoftKeyboardHiding
import com.eexposito.kickstarterdashboard.helpers.setColorResId
import com.eexposito.kickstarterdashboard.ui.delegates.search.SearchViewDelegate
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import com.eexposito.kickstarterdashboard.viewmodels.ProjectsViewModel
import com.eexposito.kickstarterdashboard.viewmodels.ProjectsViewState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity :
    AppCompatActivity(),
    ProjectListView.OnProjectItemInteractionListener,
    IntRangePickerView.OnFilterActionInteractionListener {

    private lateinit var customTabsDelegate: CustomTabsDelegate
    private val projectsViewModel: ProjectsViewModel by viewModel()
    private var searchViewDelegate : SearchViewDelegate? = null

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
        projectsViewModel.run {
            fetchProjects()
            projects.observe(this@MainActivity, Observer { renderProjectList(it) })
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) return false
        menuInflater.inflate(R.menu.menu_main_screen, menu)
        // Set overflow menu items icons visible
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        menu.findItem(R.id.actionSearch).let { actionSearchMenuItem ->
            searchViewDelegate = SearchViewDelegate(
                componentName = componentName,
                hintResId = R.string.search_project_hint,
                onSearchRequested = { searchString ->
                    projectsViewModel.searchProjects(searchString).observe(
                        this@MainActivity, Observer { renderProjectList(it) }
                    )
                },
                searchView = actionSearchMenuItem.actionView as SearchView
            )
            actionSearchMenuItem.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionSearch -> true
        R.id.actionSortAlphabetically -> {
            projectsViewModel.sortProjects(ProjectsViewModel.SortMethod.BY_TITLE)
                .observe(this@MainActivity, Observer { renderProjectList(it) })
            true
        }
        R.id.actionSortByTime -> {
            projectsViewModel.sortProjects(ProjectsViewModel.SortMethod.BY_TIME_LEFT)
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
        if (searchViewDelegate?.shouldConsumeBackPressedEvent() == true)
            return
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onBackPressed()
    }

    override fun onPause() {
        searchViewDelegate?.onPause()
        super.onPause()
    }

    private fun renderProjectList(viewState: ProjectsViewState) = when (viewState) {
        is ProjectsViewState.LoadingState -> renderLoadingState(viewState)
        is ProjectsViewState.DataState -> updateProjectList(viewState)
        is ProjectsViewState.ErrorState -> displayErrorDialog(viewState)
    }

    private fun renderLoadingState(state: ProjectsViewState.LoadingState) =
        updateViewsVisibility(state)

    private fun updateProjectList(state: ProjectsViewState.DataState) {
        updateViewsVisibility(state)
        (projectListView as? ProjectListView)?.updateList(state.projects)
    }

    private fun displayErrorDialog(state: ProjectsViewState.ErrorState) {
        createInfoDialog(
            context = this,
            titleId = state.error.titleResId,
            message = state.error.getErrorMessage(this),
            positiveAction = { updateViewsVisibility(state) }
        ).show()
    }

    private fun updateViewsVisibility(state: ProjectsViewState) {
        when (state) {
            is ProjectsViewState.LoadingState -> {
                projectListProgressBar.visibility = View.VISIBLE
            }
            is ProjectsViewState.DataState -> {
                projectListProgressBar.visibility = View.INVISIBLE
                projectListView.visibility = if (state.projects.isEmpty()) View.INVISIBLE
                else View.VISIBLE
            }
            is ProjectsViewState.ErrorState -> {
                projectListProgressBar.visibility = View.INVISIBLE
                projectListView.visibility = View.INVISIBLE
            }
        }
    }

    override fun onProjectItemInteraction(item: ProjectItem) {
        customTabsDelegate.openUrl(item.url)
    }

    override fun onFilterActionClick(from: Int?, to: Int?) {
        projectsViewModel.filterProjectsByBackersRange(from, to).observe(
            this@MainActivity, Observer { renderProjectList(it) }
        )
    }

    override fun onVisibilityChange(isVisible: Boolean) {
        if (!isVisible) {
            projectsViewModel.filterProjectsByBackersRange().observe(
                this@MainActivity, Observer { renderProjectList(it) }
            )
            forceSoftKeyboardHiding()
        }
    }
}
