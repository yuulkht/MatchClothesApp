package ru.hse.termpaper.view.outfits

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity

class AddOutfitDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_outfit, null)
        val dialog = Dialog(requireContext(), R.style.DialogStyle)
        dialog.setContentView(view)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        view.findViewById<LinearLayout>(R.id.addOutfit).setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.chooseClothesForOutfit, R.id.outfitsPage)
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.addOutfitCategory).setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.addOutfitCategoryFragment, R.id.outfitsPage)
            dismiss()
        }

        return dialog
    }
}
