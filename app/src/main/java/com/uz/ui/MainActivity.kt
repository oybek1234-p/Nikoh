package com.uz.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.uz.nikoh.R
import com.uz.nikoh.databinding.ActivityMainBinding
import com.uz.nikoh.locale.LocaleController
import com.uz.nikoh.location.LocationHelper
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.fragments.location.SearchLocationSheet
import com.uz.ui.utils.getMaterialColor
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var navcontroller: NavController
    private lateinit var navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleController.init(this)
        AppTheme.setTheme(this)
        super.onCreate(savedInstanceState)
        themeChanged()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding.apply {
            navHost =
                (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            navcontroller = navHost.navController
        }
        showBottomSheet(false, animate = false)

        if (CurrentUser.userLogged().not()) {
            showOpenScreen()
        } else {
            showBaseScreen()
        }
    }

    fun showSnack(text: String) {
        Snackbar.make(window.decorView, text, Snackbar.LENGTH_SHORT).show()
    }

    fun requestSearchLocation() {
        if (BaseConfig.searchLocation.isEmpty().not()) return
        lifecycleScope.launch {
            val foundCity = LocationHelper.findSearchLocationByNetwork()
            LocationHelper.setSearchLocation(foundCity)
            lifecycleScope.launch(Dispatchers.Main) {
                SearchLocationSheet().show(supportFragmentManager, null)
            }
        }
    }

    private fun showOpenScreen() {
        navcontroller.setGraph(R.navigation.open_screen)
    }

    fun showBaseScreen(newUser: Boolean = false) {
        for (i in 1..3) {
            navcontroller.popBackStack()
        }
        navcontroller.setGraph(R.navigation.base_graph)
        binding?.bottomNavView?.setupWithNavController(navcontroller)
        if (newUser) {
            showSnack("${CurrentUser.user.name} ${getString(R.string.profilga_kirildi)}")
        }
    }

    private var bottomSetUp = false
    private var bottomNavShown = true

    fun showBottomSheet(show: Boolean, animate: Boolean) {
        if (bottomNavShown == show) return
        bottomNavShown = show
        binding?.bottomNavView?.apply {
            if (!bottomSetUp && show) {
                bottomSetUp = true
            }
            if (show && !isVisible) {
                visibleOrGone(true)
            }
            if (!show && !isVisible) return
            if (show) {
                translationY = 120.toFloat()
            }
            if (animate) {
                animate().setDuration(300).translationY(if (show) 0f else measuredHeight.toFloat())
                    .withEndAction {
                        if (!show) {
                            visibleOrGone(false)
                        }
                    }.start()
            } else {
                visibleOrGone(show)
            }
        }
    }

    fun localeChanged() {
        binding?.bottomNavView?.apply {
            menu.clear()
            inflateMenu(R.menu.bottom_menu)
            setupWithNavController(navcontroller)
        }
    }

    fun themeChanged() {
        window.statusBarColor = getMaterialColor(com.google.android.material.R.attr.colorSurface)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            !AppTheme.isNightMode
    }

    fun recreateMy() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent) // start same activity
            finish()
            overridePendingTransition(0, 0)
        }
    }

}
