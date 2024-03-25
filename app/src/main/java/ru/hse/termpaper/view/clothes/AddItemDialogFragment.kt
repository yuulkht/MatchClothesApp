package ru.hse.termpaper.view.clothes

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity

class AddItemDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_item, null)
        val dialog = Dialog(requireContext(), R.style.DialogStyle)
        dialog.setContentView(view)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        view.findViewById<LinearLayout>(R.id.addItem).setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.addItemFragment, R.id.clothesPage)
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.addItemCategory).setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.addClothCategoryFragment, R.id.clothesPage)
            dismiss()
        }

        return dialog
    }
}
