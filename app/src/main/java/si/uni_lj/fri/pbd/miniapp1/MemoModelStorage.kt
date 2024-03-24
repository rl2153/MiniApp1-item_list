package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object MemoModelStorage {
    private const val PREF_KEY_MEMOS = "memos"

    // gets memos from shared preferences
    fun loadMemos(context: Context): List<MemoModel>{
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val memoListJson = sharedPreferences.getString(PREF_KEY_MEMOS, "[]")
        //Log.d("MemoModelStorage", memoListJson)
        val memoArray = JSONArray(memoListJson)

        val memoList = mutableListOf<MemoModel>()
        // iterates over every element in the memoArray and converts each JSON object to a memo object
        for (i in 0 until memoArray.length()) {
            val memoJson = memoArray[i] as? JSONObject
            if (memoJson != null) {
                memoList.add(i, MemoModel.fromJson(memoJson))
            }
        }
        return memoList
    }

    // saves a memo to shared preferences
    fun saveMemo(context: Context, memo: MemoModel, imageBitmap: Bitmap?) {
        // first we get the list of all the memos
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val memoListJson = sharedPreferences.getString(PREF_KEY_MEMOS, "[]")
        val memoArray = JSONArray(memoListJson)

        // save the image bitmap to shared preferences
        if (imageBitmap != null) {
            saveImageToSharedPreferences(context, memo.timestamp, imageBitmap)
        }
        // if no image was taken, set image reference to the default image
        else {
            memo.imageReference = "image_placeholder.jpg"
        }

        // convert memo object to json
        val memoJson = memo.toJson()
        // add it to array of all memos
        memoArray.put(memoJson)
        // save the modified array to shared preferences
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_MEMOS, memoArray.toString())
        editor.apply()
    }


    // deletes the memo
    fun deleteMemo(context: Context, memoTimestamp: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val memoListJson = sharedPreferences.getString(PREF_KEY_MEMOS, "[]")
        val memoArray = JSONArray(memoListJson)
        // find and remove the deleted memo from memo array
        for (i in 0 until memoArray.length()) {
            val memoJson = memoArray.getJSONObject(i)
            val timestamp = memoJson.getString("timestamp")
            if (timestamp == memoTimestamp) {
                memoArray.remove(i)
                break
            }
        }
        // saves the modified memo array back to shared preferences
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_MEMOS, memoArray.toString())
        editor.apply()
    }

    // saves image bitmap to shared preferences
    private fun saveImageToSharedPreferences(context: Context, key: String, bitmap: Bitmap) {
        // Convert the Bitmap to a Base64 encoded string
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // Save the encoded string to SharedPreferences
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, encodedString)
        editor.apply()
    }

    fun loadImageFromSharedPreferences(context: Context, key: String): Bitmap {
        // Retrieve the Base64 encoded string from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val encodedString = sharedPreferences.getString(key, null)
        Log.d("MemoModelStorage", "encoded string: " +encodedString)
        if (encodedString != null) {
            // Decode the Base64 string into a ByteArray
            val byteArray = Base64.decode(encodedString, Base64.DEFAULT)

            // Convert the ByteArray back into a Bitmap
            var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return bitmap
        } else {
            // Handle case when no image is found in SharedPreferences
            Log.d("MemoModelStorage", "no photo was taken")
            return BitmapFactory.decodeResource(context.resources, R.drawable.image_placeholder)

        }
    }
}