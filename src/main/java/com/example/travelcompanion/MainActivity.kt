package com.example.travelcompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var inputEt: EditText
    private lateinit var convertBtn: Button
    private lateinit var resultTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categorySpinner = findViewById(R.id.spinnerCategory)
        fromSpinner = findViewById(R.id.spinnerFrom)
        toSpinner = findViewById(R.id.spinnerTo)
        inputEt = findViewById(R.id.etInput)
        convertBtn = findViewById(R.id.btnConvert)
        resultTv = findViewById(R.id.tvResult)

        val categories = arrayOf("Currency", "Temperature", "Length")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val units = when (position) {
                    0 -> arrayOf("USD", "AUD", "EUR", "GBP")
                    1 -> arrayOf("°C", "°F", "K")
                    else -> arrayOf("m", "km", "mile", "ft")
                }
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, units)
                fromSpinner.adapter = adapter
                toSpinner.adapter = adapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        convertBtn.setOnClickListener {
            val input = inputEt.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val value = input.toDoubleOrNull()
            if (value == null) {
                Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cat = categorySpinner.selectedItemPosition
            val from = fromSpinner.selectedItem.toString()
            val to = toSpinner.selectedItem.toString()

            val res = when (cat) {
                0 -> convertCurrency(value, from, to)
                1 -> convertTemp(value, from, to)
                else -> convertLength(value, from, to)
            }

            resultTv.text = "Result: %.2f %s".format(res, to)
        }
    }

    private fun convertCurrency(v: Double, f: String, t: String): Double {
        val usd = when (f) {
            "USD" -> v
            "AUD" -> v / 1.52
            "EUR" -> v / 0.92
            "GBP" -> v / 0.79
            else -> v
        }
        return when (t) {
            "USD" -> usd
            "AUD" -> usd * 1.52
            "EUR" -> usd * 0.92
            "GBP" -> usd * 0.79
            else -> v
        }
    }

    private fun convertTemp(v: Double, f: String, t: String): Double {
        return when {
            f == "°C" && t == "°F" -> v * 1.8 + 32
            f == "°C" && t == "K" -> v + 273.15
            f == "°F" && t == "°C" -> (v - 32) / 1.8
            f == "°F" && t == "K" -> (v - 32) / 1.8 + 273.15
            f == "K" && t == "°C" -> v - 273.15
            f == "K" && t == "°F" -> (v - 273.15) * 1.8 + 32
            else -> v
        }
    }

    private fun convertLength(v: Double, f: String, t: String): Double {
        return when {
            f == "m" && t == "km" -> v / 1000
            f == "km" && t == "m" -> v * 1000
            f == "km" && t == "mile" -> v * 0.6214
            f == "mile" && t == "km" -> v / 0.6214
            f == "m" && t == "ft" -> v * 3.281
            f == "ft" && t == "m" -> v / 3.281
            else -> v
        }
    }
}