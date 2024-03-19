package ru.hse.termpaper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R

class MainScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        val clothesLink = view.findViewById<LinearLayout>(R.id.clothesLink)
        val outfitsLink = view.findViewById<LinearLayout>(R.id.outfitsLink)
        val calendarLink = view.findViewById<LinearLayout>(R.id.calendarLink)
        val journeyLink = view.findViewById<LinearLayout>(R.id.journeyLink)
        val settingsLink = view.findViewById<ImageView>(R.id.settings)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        clothesLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        outfitsLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        calendarLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        journeyLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        settingsLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.settingsFragment, R.id.homePage)
        }

        return view
    }
}
