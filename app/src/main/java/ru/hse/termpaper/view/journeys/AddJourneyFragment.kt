package ru.hse.termpaper.view.journeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.journey.JourneyService

class AddJourneyFragment(
    private val journeyService: JourneyService = JourneyService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val nextButton = view.findViewById<Button>(R.id.next)
        val title = view.findViewById<EditText>(R.id.journeyTitle)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backButton.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        nextButton.setOnClickListener {
            val journeyTitle = title.text.toString().trim()

            journeyService.saveJourney(journeyTitle, mainScreenActivity)
        }
    }
}