package com.mazenrashed.github.ui

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.mazenrashed.github.R
import com.mazenrashed.github.data.model.Repo
import com.mazenrashed.github.databinding.ActivityMainBinding
import com.mazenrashed.github.databinding.RepoItemBinding
import com.minimize.android.rxrecycleradapter.RxDataSource
import com.minimize.android.rxrecycleradapter.SimpleViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration


class MainActivity : AppCompatActivity() {

    private val viewModel: RepositoriesViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private val bag = CompositeDisposable()
    private val repositories: ArrayList<Repo> = ArrayList()
    private val dataSource = RxDataSource<RepoItemBinding, Repo>(R.layout.repo_item, repositories)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        initRecyclerView()
        initRefreshLayout()
        listenToRefreshState()
        listenToDataChanges()
        listenToMessages()
        initDataSource()
    }

    private fun initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener { viewModel.loadRepos() }
    }

    private fun initDataSource() {
        dataSource
            .asObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::bindRepoViewHolder) {
                it.printStackTrace()
            }
            .addTo(bag)
    }

    private fun bindRepoViewHolder(viewHolder : SimpleViewHolder<Repo, RepoItemBinding>){
        val repo = viewHolder.item
        val binding = viewHolder.viewDataBinding ?: return

        binding.forks.text = "${repo?.forksCount}"
        binding.starts.text = "${repo?.stargazersCount}"
        binding.watches.text = "${repo?.watchersCount}"
        binding.repoName.text = repo?.fullName
        binding.repoDesc.text = repo?.description
        binding.imageView.load(repo?.owner?.avatarUrl) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    private fun listenToMessages() {
        viewModel
            .toastMessages
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
            .addTo(bag)
    }

    private fun listenToDataChanges() {
        viewModel
            .repositories
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                repositories.apply {
                    clear()
                    addAll(it)
                }
                dataSource.updateDataSet(it)
                dataSource.updateAdapter()
            }, {
                it.printStackTrace()
            })
            .addTo(bag)
    }

    private fun listenToRefreshState() {
        viewModel
            .isLoading
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.refreshLayout.isRefreshing = it
            }
            .addTo(bag)

    }

    private fun initRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).build())
        dataSource.bindRecyclerView(binding.recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    dataSource.updateDataSet(repositories) //base items should remain the same
                        .filter { repo ->
                            if (newText.isNullOrEmpty()) true else repo.name.toLowerCase().contains(
                                newText
                            )
                        }
                        .updateAdapter()
                    return true
                }

            })
        }

        return true
    }


}
