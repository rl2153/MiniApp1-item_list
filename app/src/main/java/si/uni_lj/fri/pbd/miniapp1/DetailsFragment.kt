package si.uni_lj.fri.pbd.miniapp1

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import org.json.JSONObject
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentDetailsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        val jsonString = arguments?.getString("jsonObject")
        var timestamp: String? = null
        var title: String? = null
        var description: String? = null
        var imageBitmap: Bitmap? = null
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val memoObject = MemoModel.fromJson(jsonObject)
            // Implement UI setup
            title = memoObject.title
            binding.title.text = title
            description = memoObject.description
            binding.description.text = description
            timestamp = memoObject.timestamp
            binding.timestamp.text = timestamp
            imageBitmap = MemoModelStorage.loadImageFromSharedPreferences(requireContext(), memoObject.imageReference)
            binding.imageView.setImageBitmap(imageBitmap)
        }

        // Implement button click listeners
        binding.btnDelete.setOnClickListener {
            if (timestamp != null) {
                MemoModelStorage.deleteMemo(requireContext(), timestamp)
            }
            // navigate back to ListFragment
            findNavController().navigate(R.id.action_detailsFragment_to_listFragment)
        }

        binding.btnShare.setOnClickListener {
           sendEmail(title, description, timestamp, imageBitmap)
        }

        return view
    }

    fun sendEmail(title: String?, description: String?, timestamp: String?, bitmap: Bitmap?) {
        // KOPIRANO OD HANE
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "${description}\nTimestamp: ${timestamp}")

            if (bitmap != null) {
                val imageUri = Uri.parse(MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, "Attachment", null))
                putExtra(Intent.EXTRA_STREAM, imageUri)
            }
        }
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // KOPIRANO OD HANE KONEC
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {

        val context = requireContext().applicationContext

        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "image.jpg")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}