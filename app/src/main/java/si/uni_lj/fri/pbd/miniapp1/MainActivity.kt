package si.uni_lj.fri.pbd.miniapp1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import si.uni_lj.fri.pbd.miniapp1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding variable represents the activity_main.xml file
        // all elements in the activity_main.xml can be accessed trough binding.elementId
        binding = ActivityMainBinding.inflate(layoutInflater)
        // sets the UI defined in activity_main.xml as the content of the MainActivity
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

    }
}