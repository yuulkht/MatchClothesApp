package ru.hse.termpaper.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import ru.hse.termpaper.R

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
