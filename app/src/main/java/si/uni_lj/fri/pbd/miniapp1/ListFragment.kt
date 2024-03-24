package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentListBinding
import java.io.Serializable

class ListFragment : Fragment() {
    // var -> mutable variable
    private var _binding: FragmentListBinding? = null
    // val -> read-only, when we use the property binding, it returns a value
    // of _binding that is not null (if it is null, we get an exception)
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    //private var adapter: RecyclerAdapter? = null

    override fun onCreateView(
        // inflater is used to inflate xml layouts into View objects
        // container (not sure)
        // savedInstanceState contains fragment's previous state
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // _binding will now hold the layout of this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        // Implement RecyclerView setup and item click listener
        recyclerView = binding.recyclerView
        layoutManager = LinearLayoutManager(view.context)
        recyclerView?.layoutManager = layoutManager

        val navController = findNavController()

        val memoList = MemoModelStorage.loadMemos(requireContext())

        // callback used when an item of recycler view is clicked
        val adapter = RecyclerAdapter(requireContext(), memoList) { position ->
            val clickedItem = memoList[position]
            val memoJson = clickedItem.toJson()
            val bundle = Bundle()
            // bundle the clicked item data so it can be sent
            bundle.putString("jsonObject", memoJson.toString())
            // navigate to details fragment
            navController.navigate(R.id.action_listFragment_to_detailsFragment, bundle)
        }
        recyclerView?.adapter = adapter

        // + button click listener for creating new memos
        binding.button.setOnClickListener {
            // Navigate to the new fragment
            navController.navigate(R.id.action_listFragment_to_newFragment)
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}