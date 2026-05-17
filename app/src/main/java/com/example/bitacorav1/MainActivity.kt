package com.example.bitacorav1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog

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
        val btnSalir = findViewById<Button>(R.id.btnSalir)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val btnVerReportes = findViewById<Button>(R.id.btnTodosLosReportes)
        var fechaSeleccionada = ""

        btnVerReportes.setOnClickListener {
            val intent = Intent(this, reportes::class.java)
            startActivity(intent)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val meses = arrayOf(
                "ENERO",
                "FEBRERO",
                "MARZO",
                "ABRIL",
                "MAYO",
                "JUNIO",
                "JULIO",
                "AGOSTO",
                "SEPTIEMBRE",
                "OCTUBRE",
                "NOVIEMBRE",
                "DICIEMBRE")
            val mesNombre = meses[month]
            val diaFormateado = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

            fechaSeleccionada = "$diaFormateado - $mesNombre -$year"

            val intent = Intent(this, detalle_dia::class.java)
            intent.putExtra("FECHA_ELEGIDA", fechaSeleccionada)
            startActivity(intent)
        }

        btnSalir.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Salir")
            builder.setMessage("¿Seguro que quieres salir?")

            builder.setPositiveButton("Sí") { dialog, _ ->
                finishAffinity()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
}