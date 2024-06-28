package com.example.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.example.whatsapp.R
import com.example.whatsapp.adapters.ViewPagerAdapter
import com.example.whatsapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        initializeToolbar()
        initializeTabNavigation()
    }

    private fun initializeTabNavigation() {
        val tabLayout = binding.tabLayoutMain
        val viewPager = binding.viewPagerMain

        val tabs = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(
            tabs, supportFragmentManager, lifecycle
        )

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun initializeToolbar() {
        val toolbar = binding.IncludeMainToolbar.tbMain
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
                title = "WhatsApp"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when( menuItem.itemId ){
                        R.id.item_profile -> {
                            startActivity(
                                Intent(applicationContext, ProfileActivity::class.java )
                            )
                        }
                        R.id.item_logout -> {
                            logoutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun logoutUser() {

        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não"){dialog, position -> }
            .setPositiveButton("Sim"){dialog, position ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()
    }
}