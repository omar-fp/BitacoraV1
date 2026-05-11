package com.example.bitacorav1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class reporte_mensual : AppCompatActivity() {

    private lateinit var adapter: ReporteMensualAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte_mensual)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAtras = findViewById<Button>(R.id.btnAtrasReporte)
        btnAtras.setOnClickListener {
            finish()
        }

        val tvTitulo = findViewById<TextView>(R.id.tvTituloReporte)
        val calendario = Calendar.getInstance()
        val nombreMesActual =
            calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES"))
                ?.uppercase() ?: "MES"
        tvTitulo.text = "REPORTE DEL\nMES DE $nombreMesActual"

        val rvReporte = findViewById<RecyclerView>(R.id.rvDetalleMensual)
        adapter = ReporteMensualAdapter(emptyList()) // Inicia vacía
        rvReporte.layoutManager = LinearLayoutManager(this)
        rvReporte.adapter = adapter

        val db = GastoDatabase.getDatabase(this)
        val gastoDao = db.gastoDao()

        lifecycleScope.launch {
            val gastosMes = gastoDao.obtenerGastosPorMes(nombreMesActual)
            adapter.actualizarLista(gastosMes)

            var sumaTotal = 0.0
            for (gasto in gastosMes) {
                sumaTotal += gasto.monto
            }

            // Pinta la cantidad final en el recuadro gris
            val tvMontoTotal = findViewById<TextView>(R.id.tvMontoTotal)
            tvMontoTotal.text = String.format("$%.2f", sumaTotal)
        }
    }
}