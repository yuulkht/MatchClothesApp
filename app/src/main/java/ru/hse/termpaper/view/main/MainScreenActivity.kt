package ru.hse.termpaper.view.main

import ru.hse.termpaper.view.clothes.AddClothFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.termpaper.R
import ru.hse.termpaper.view.clothes.AddClothCategoryFragment
import ru.hse.termpaper.view.calendar.CalendarFragment
import ru.hse.termpaper.view.clothes.ClothesFragment
import ru.hse.termpaper.view.journeys.JourneyFragment
import ru.hse.termpaper.view.outfits.AddOutfitCategoryFragment
import ru.hse.termpaper.view.outfits.ChooseClothesForOutfit
import ru.hse.termpaper.view.outfits.OutfitsFragment

class MainScreenActivity(
    val mainScreenFragment: MainScreenFragment = MainScreenFragment(),
    val clothesFragment: ClothesFragment = ClothesFragment(),
    val addItemFragment: AddClothFragment = AddClothFragment(),
    val addClothCategoryFragment: AddClothCategoryFragment = AddClothCategoryFragment(),
    val outfitsFragment: OutfitsFragment = OutfitsFragment(),
    val chooseClothesForOutfit: ChooseClothesForOutfit = ChooseClothesForOutfit(),
    val addOutfitCategoryFragment: AddOutfitCategoryFragment = AddOutfitCategoryFragment(),
    val calendarFragment: CalendarFragment = CalendarFragment(),
    val journeyFragment: JourneyFragment = JourneyFragment(),
    val settingsFragment: SettingsFragment = SettingsFragment(),
    private var bottomNavigation: BottomNavigationView? = null
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        enableEdgeToEdge()

        bottomNavigation = findViewById(R.id.bottom_navigation_view)

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


