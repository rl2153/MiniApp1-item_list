package si.uni_lj.fri.pbd.miniapp1

import org.json.JSONObject
import java.sql.Timestamp


data class MemoModel(
    val title: String,
    val description: String,
    val timestamp: String,
    var imageReference: String
) {
    // converts memo model object to json
    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("description", description)
        jsonObject.put("imageRef", imageReference)
        jsonObject.put("timestamp", timestamp)
        return jsonObject
    }
    // converts JSON object to memo model object
    companion object {
        fun fromJson(json: JSONObject): MemoModel {
            val title = json.getString("title")
            val description = json.getString("description")
            val timestamp = json.getString("timestamp")
            val imageReference = json.getString("imageRef")
            return MemoModel(title, description, timestamp, imageReference)
        }
    }
}