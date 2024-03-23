package si.uni_lj.fri.pbd.miniapp1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentNewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewFragment : Fragment() {
    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var context: Context

    private lateinit var imageView: ImageView
    private lateinit var buttonTakePicture: Button
    private lateinit var buttonSave: Button
    private lateinit var titleText: Editable
    private lateinit var messageText: Editable

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewBinding.inflate(inflater, container, false)
        val view = binding.root

        imageView = binding.imageView
        buttonTakePicture = binding.btnTakePhoto
        buttonSave = binding.btnSave
        titleText = binding.title.text
        messageText = binding.text.text

        context = requireContext()

        var imageBitmapData: Bitmap? = null

        // Initialize ActivityResultLauncher
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                imageBitmapData = imageBitmap
                imageView.setImageBitmap(imageBitmap)
            }
        }

        buttonTakePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        buttonSave.setOnClickListener {
            // *TAKEN FROM CHATGPT
            // Get current timestamp in milliseconds
            val currentTimeMillis = System.currentTimeMillis()
            // Convert timestamp to a readable date string
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTimeMillis))
            // *

            val newMemo = MemoModel(titleText.toString(), messageText.toString(), dateString, dateString)
                MemoModelStorage.saveMemo(context, newMemo, imageBitmapData)

            // navigate back to ListFragment
            findNavController().navigate(R.id.action_newFragment_to_listFragment)
        }

        return view
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}