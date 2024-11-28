package com.yulianti.kodytest.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.FOCUSABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yulianti.kodytest.R
import com.yulianti.kodytest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                item.setVisible(false)
                binding.searchView.visibility = View.VISIBLE
                binding.searchView.focusable = FOCUSABLE
                return true
            }
            android.R.id.home -> {
//                performSearch("")
                onSupportNavigateUp()
                binding.searchView.setQuery("", false)
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                binding.toolbar.menu.findItem(R.id.action_search).setVisible(true)
                binding.searchView.visibility = View.GONE
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun performSearch(query: String) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Use SafeArgs to pass the search keyword
        val action = CharacterListFragmentDirections.actionListFragmentToSearch(query)
        navController.navigate(action)
    }
}