package ru.hse.termpaper.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.termpaper.R

class MainScreenActivity(
    val mainScreenFragment: MainScreenFragment = MainScreenFragment(),
    val clothesFragment: ClothesFragment = ClothesFragment(),
    val outfitsFragment: OutfitsFragment = OutfitsFragment(),
    val calendarFragment: CalendarFragment = CalendarFragment(),
    val journeyFragment: JourneyFragment = JourneyFragment(),
    val settingsFragment: SettingsFragment = SettingsFragment(),
    var bottomNavigation: BottomNavigationView? = null
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        enableEdgeToEdge()

        bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottomNavigation?.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homePage -> {
                    replaceFragment(mainScreenFragment, menuItem.itemId)
                    true
                }
                R.id.clothesPage -> {
                    replaceFragment(clothesFragment, menuItem.itemId)
                    true
                }
                R.id.outfitsPage -> {
                    replaceFragment(outfitsFragment, menuItem.itemId)
                    true
                }
                else -> false
            }
        }
        replaceFragment(mainScreenFragment, R.id.homePage)
    }

    fun replaceFragment(fragment: Fragment, menuItemId: Int) {
        bottomNavigation?.menu?.findItem(menuItemId)?.isChecked = true
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_screen_container, fragment)
            .commit()
    }
}


