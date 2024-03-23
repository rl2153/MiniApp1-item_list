package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import si.uni_lj.fri.pbd.miniapp1.RecyclerAdapter.CardViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerAdapter(
    private val context: Context,
    private val memos: List<MemoModel>,
    private val onItemClick: (Int) -> Unit) // Callback function to handle item click
    : RecyclerView.Adapter<CardViewHolder?>() {

    inner class CardViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var itemImage: ImageView? = null
        var itemTitle: TextView? = null
        var itemTimestamp: TextView? = null

        init {
            itemImage = itemView?.findViewById(R.id.item_image)
            itemTitle = itemView?.findViewById(R.id.item_title)
            itemTimestamp = itemView?.findViewById(R.id.item_timestamp)

            itemView?.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_item_memo_model, viewGroup, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, i: Int) {
        val memo = memos[i]
        viewHolder.itemTitle?.text = memo.title
        viewHolder.itemTimestamp?.text = memo.timestamp
        val imageBitmap: Bitmap = MemoModelStorage.loadImageFromSharedPreferences(context, memo.imageReference)
        viewHolder.itemImage?.setImageBitmap(imageBitmap)
    }

    override fun getItemCount(): Int {
        return memos.size
    }
}