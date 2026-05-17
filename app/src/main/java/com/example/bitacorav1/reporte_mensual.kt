package com.example.bitacorav1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
        btnAtras.setOnClickListener { finish() }

        val nombreMesActual = intent.getStringExtra("MES_ELEGIDO") ?: ""
        val tituloCompleto = intent.getStringExtra("TITULO_COMPLETO") ?: "MES - AÑO"

        val tvTitulo = findViewById<TextView>(R.id.tvTituloReporte)
        tvTitulo.text = "REPORTE DEL\n$tituloCompleto"

        val rvReporte = findViewById<RecyclerView>(R.id.rvDetalleMensual)
        adapter = ReporteMensualAdapter(emptyList())
        rvReporte.layoutManager = LinearLayoutManager(this)
        rvReporte.adapter = adapter

        val db = GastoDatabase.getDatabase(this)
        val gastoDao = db.gastoDao()

        lifecycleScope.launch {
            if (nombreMesActual.isNotEmpty()) {
                val gastosMes = gastoDao.obtenerGastosPorMes(nombreMesActual)
                val gastosOrdenados = gastosMes.sortedBy { gasto ->
                    Regex("\\d+").find(gasto.fecha)?.value?.toIntOrNull() ?: 0
                }

                adapter.actualizarLista(gastosOrdenados)

                var sumaTotal = 0.0
                for (gasto in gastosOrdenados) {
                    sumaTotal += gasto.monto
                }

                val tvMontoTotal = findViewById<TextView>(R.id.tvMontoTotal)
                tvMontoTotal.text = String.format("$%.2f", sumaTotal)
            } else {
                Toast.makeText(this@reporte_mensual, "Error al cargar el mes seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}