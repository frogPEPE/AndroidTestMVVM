package com.example.timesnewswire.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.timesNewsWireAPI.TNWAPI
import com.example.timesnewswire.models.timesNewsWireAPI.TNWURLBuilder
import com.example.timesnewswire.viewModels.ArticleViewModel
import com.example.timesnewswire.viewModels.ArticleViewModelFactory
import com.example.timesnewswire.volley.VolleySingleton
import com.example.timesnewswire.activities.articlesRecyclerView.RVAdapter
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity
import com.example.timesnewswire.models.timesNewsWireAPI.TNWFunctions
import com.example.timesnewswire.models.timesNewsWireAPI.TNWTools
import com.example.timesnewswire.R
import com.example.timesnewswire.volley.VolleyReturnCallback
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timesnewswire.activities.articlesRecyclerView.ArticleDiffUtilCallback
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter


class MainActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var swipeContainer: SwipeRefreshLayout
    private var appIsOnline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val repository = Repository(this)
        StartSectionsUpdate()
        TNWFunctions(this, repository).SetArticlesUpdateDBAlarm()

        //region Article View Model Initialize
        articleViewModel = ViewModelProviders.of(
            this,
            ArticleViewModelFactory(
                this.application,
                repository,
                TNWAPI(this.applicationContext, TNWURLBuilder(this)),
                TNWFunctions(this, repository)
            )
        ).get(ArticleViewModel::class.java)

        articleViewModel.connectivityLiveData.observe(this, Observer<Boolean> { applicationIsOnline ->
            appIsOnline = applicationIsOnline
        })
        //endregion

        //region Article Recycler View Initialize
        recyclerView = findViewById(R.id.mainRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val articleRVAdapter = RVAdapter(ArticleDiffUtilCallback(), object: RVAdapter.OnItemClickListener {
            override fun onItemClick(article: ArticleEntity) {
                if (appIsOnline)
                    startActivity(CoreSingleton.getInstance(this@MainActivity).GetWebViewIntent(article))
                else
                    MessageToUser(resources.getString(R.string.mainActivity_Application_Offline), Toast.LENGTH_SHORT)
            }
        })
        articleViewModel.pagedListLiveData.observe(this, Observer<PagedList<ArticleEntity>> { data ->
            articleRVAdapter.submitList(data)
        })
        recyclerView.adapter = AlphaInAnimationAdapter(articleRVAdapter)
        //endregion

        swipeContainer = findViewById(R.id.mainSwipeRefresh)
        swipeContainer.setOnRefreshListener {
            val tnwURLBuilder = TNWURLBuilder(this)
            articleViewModel.UpdateArticlesDB(
                tnwURLBuilder.allSources,
                tnwURLBuilder.allSections,
                tnwURLBuilder.defaultArticlesCount,
                tnwURLBuilder.defaultArticlesOffset,
                object : SwipeRefreshSuccess {
                    override fun onSuccess() {
                        swipeContainer.isRefreshing = false
                    }
                }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        //Close all responses in queue
        VolleySingleton.getInstance(this).requestQueue.cancelAll(object: RequestQueue.RequestFilter {
            override fun apply(request: Request<*>): Boolean {
                // do I have to cancel this response?
                return true // -> always yes
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        menu?.let {
            val searchView = it.findItem(R.id.searchArticlesMainActivityMenuItem).actionView as SearchView
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { it -> articleViewModel.filterConstraintLiveData.SetNewString(it.trim().toLowerCase()) }
                    return false
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return true
    }

    private fun MessageToUser(message: String, showDuration: Int) {
        Toast.makeText(this, message, showDuration).show()
    }

    private fun StartSectionsUpdate() {
        val tnwAPI = TNWAPI(this, TNWURLBuilder(this))
        val repository = Repository(this)
        CoreSingleton.getInstance(this).coroutineScope.launch {
            if (repository.SectionEntityIsEmpty()) {
                val volleyReturnCallback = object : VolleyReturnCallback {
                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, R.string.API_Sections_Response_Error, Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(result: String) {
                        val jElement: JsonElement = JsonParser().parse(result)
                        val jObject: JsonObject = jElement.asJsonObject
                        val sections: List<SectionEntity> = TNWTools().GetSectionsFromJson(jObject)
                        repository.ResetSectionsDB(sections)
                    }
                }

                TNWFunctions(this@MainActivity, repository).StartSectionsUpdate(tnwAPI, volleyReturnCallback)
            }
        }
    }
}