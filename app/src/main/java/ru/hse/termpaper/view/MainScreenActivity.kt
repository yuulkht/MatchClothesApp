package ru.hse.termpaper.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.termpaper.R

class MainScreenActivity : AppCompatActivity() {

    private val mainScreenFragment = MainScreenFragment()
    private val clothesFragment = ClothesFragment()
    private val outfitsFragment = OutfitsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        enableEdgeToEdge()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        // Установка обработчика нажатий на пункты BottomNavigationView
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homePage -> {
                    replaceFragment(mainScreenFragment)
                    true
                }
                R.id.clothesPage -> {
                    replaceFragment(clothesFragment)
                    true
                }
                R.id.outfitsPage -> {
                    replaceFragment(outfitsFragment)
                    true
                }
                else -> false
            }
        }
        replaceFragment(mainScreenFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_screen_container, fragment)
            .commit()
    }
}


