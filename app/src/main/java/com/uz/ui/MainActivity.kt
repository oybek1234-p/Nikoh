package com.uz.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.ActivityMainBinding
import com.uz.nikoh.locale.LocaleController
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.utils.visibleOrGone

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navcontroller: NavController
    private lateinit var navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleController.init(this)
        AppTheme.setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun showOpenScreen() {
        navcontroller.setGraph(R.navigation.open_screen)
    }

    fun showBaseScreen() {
        for (i in 1..2) {
            navcontroller.popBackStack()
        }
        navcontroller.setGraph(R.navigation.base_graph)
        binding.bottomNavView.setupWithNavController(navcontroller)
    }

    private var bottomSetUp = false
    private var bottomNavShown = true

    fun showBottomSheet(show: Boolean, animate: Boolean) {
        if (bottomNavShown == show) return
        bottomNavShown = show
        binding.bottomNavView.apply {
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

}