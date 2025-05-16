package com.example.tripai
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun showDatePickerDialog(context: Context, editText: EditText, calendar: Calendar){
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { view, year, month, day ->
                    val selectedDate =  "$day/${month+1}/$year"
                    editText.setText(selectedDate)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        //variables of work
        var msgForAPI: String
        var countryChoose = ""
        val editTextLocal = findViewById<EditText>(R.id.etLocal)
        var btnSearch: Button = findViewById(R.id.btnSearch)
        val editTextStartDate: EditText = findViewById(R.id.etStartDate)
        var startDate: String
        val editTextEndDate: EditText = findViewById(R.id.etEndDate)
        var endDate: String
        var txtReturnOfAPI: TextView = findViewById(R.id.txtItinerary)
        val fabTrash: View = findViewById(R.id.fabTrash)

        //call the API, send the message with the information and return the itinerary to the user
        fun callGemini(txtReturnOfAPI: TextView, msgForAPI: String){
            val generativeModel = GenerativeModel(
                // The Gemini 1.5 models are versatile and work with most use cases
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.API_KEY
            )

            val prompt = msgForAPI
            MainScope().launch{
                val response = generativeModel.generateContent(prompt)
                txtReturnOfAPI.text = response.text
            }
        }

        btnSearch.setOnClickListener {
            //get the typed text for the user
            var local: String = editTextLocal.text.toString()
            startDate = editTextStartDate.text.toString()
            endDate = editTextEndDate.text.toString()
            msgForAPI = "Favor elaborar uma roteiro touristic para $local, no país $countryChoose entre $startDate e $endDate"

            //show the typed text
            Toast.makeText(this, "Aguarde um momento", Toast.LENGTH_LONG).show()
            callGemini(txtReturnOfAPI, msgForAPI)
        }

        //clear the fields typed
        fabTrash.setOnClickListener {
            editTextEndDate.setText("")
            editTextStartDate.setText("")
            editTextLocal.setText("")
            txtReturnOfAPI.text = ""
        }

        //countries available
        var countries = arrayOf("Brasil", "Argentina", "Chile", "Uruguai", "Paraguai")

        //list of countries for the user select
        var listCountries: Spinner = findViewById(R.id.spinner)

        //connect the spinner with the list and show the list when user click on spinner
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listCountries.adapter = adapter

        //Treatment when item is selected
        listCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                //return the position of item selected
                countryChoose = countries[position]
                Toast.makeText(this@MainActivity, "País selecionado $countryChoose", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing selected")
            }
        }

        var calendar: Calendar = Calendar.getInstance()

        editTextStartDate.setOnClickListener {
            showDatePickerDialog(this, editTextStartDate, calendar)
        }
        editTextEndDate.setOnClickListener {
            showDatePickerDialog(this, editTextEndDate, calendar)
        }





    }
}
