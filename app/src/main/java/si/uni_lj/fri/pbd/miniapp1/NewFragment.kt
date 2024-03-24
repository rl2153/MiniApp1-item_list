package si.uni_lj.fri.pbd.miniapp1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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
    private var imageBitmapData: Bitmap? = null

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

        // if there was a previous state, preserve the data
        if (savedInstanceState != null) {
            // Restore inputted data
            titleText = Editable.Factory.getInstance().newEditable(savedInstanceState.getCharSequence("titleText", ""))
            messageText = Editable.Factory.getInstance().newEditable(savedInstanceState.getCharSequence("messageText", ""))
            val imageBitmapData = savedInstanceState.getParcelable<Bitmap>("imageBitmapData")
            imageView.setImageBitmap(imageBitmapData)
        }

        // Initialize ActivityResultLauncher
        // callback happens when user makes the action
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // get the image bitmap
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                imageBitmapData = imageBitmap
                imageView.setImageBitmap(imageBitmap)
            }
        }
        // take photo button listener
        buttonTakePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }
        // save button listener
        buttonSave.setOnClickListener {
            // *TAKEN FROM CHATGPT
            // Get current timestamp in milliseconds
            val currentTimeMillis = System.currentTimeMillis()
            // Convert timestamp to a readable date string
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTimeMillis))
            // *

            Log.d("NewFragment", "titleText: "+titleText.toString())
            Log.d("NewFragment", "messageText: "+messageText.toString())

            // if title or description are empty, show a toast message to the user
            if ((titleText.toString() == "" || messageText.toString() == "")) {
                Toast.makeText(context, "Please provide title and description", Toast.LENGTH_SHORT).show()
            }
            // if required data is provided, create a new memo object and save it
            else {
                val newMemo = MemoModel(titleText.toString(), messageText.toString(), dateString, dateString)
                MemoModelStorage.saveMemo(context, newMemo, imageBitmapData)

                // navigate back to ListFragment
                findNavController().navigate(R.id.action_newFragment_to_listFragment)
            }
        }
        return view
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save bitmap when orientation changes
        outState.putParcelable("imageBitmapData", imageBitmapData)

        Log.d("NewFragment", "image bitmap data: "+imageBitmapData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}