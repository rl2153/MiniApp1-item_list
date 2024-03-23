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

    fun saveMemo(context: Context, memo: MemoModel, imageBitmap: Bitmap) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val memoListJson = sharedPreferences.getString(PREF_KEY_MEMOS, "[]")

        val memoArray = JSONArray(memoListJson)

        saveImageToSharedPreferences(context, memo.timestamp, imageBitmap)

        val memoJson = memo.toJson()
        memoArray.put(memoJson)

        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_MEMOS, memoArray.toString())
        editor.apply()
    }

    fun saveMemoWithoutImage(context: Context, memo: MemoModel) {

    }


    fun deleteMemo(context: Context, memoTimestamp: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val memoListJson = sharedPreferences.getString(PREF_KEY_MEMOS, "[]")
        val memoArray = JSONArray(memoListJson)

        for (i in 0 until memoArray.length()) {
            val memoJson = memoArray.getJSONObject(i)
            val timestamp = memoJson.getString("timestamp")
            if (timestamp == memoTimestamp) {
                memoArray.remove(i)
                break
            }
        }
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_MEMOS, memoArray.toString())
        editor.apply()
    }

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

        if (encodedString != null) {
            // Decode the Base64 string into a ByteArray
            val byteArray = Base64.decode(encodedString, Base64.DEFAULT)

            // Convert the ByteArray back into a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            // Set the Bitmap to the ImageView
            return bitmap
        } else {
            // Handle case when no image is found in SharedPreferences
            // You can set a default image or take other appropriate action
            return BitmapFactory.decodeResource(context.resources, R.drawable.image_placeholder)
        }
    }
}