package com.example.bitacorav1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class reportes : AppCompatActivity() {

    private lateinit var adapter: MesesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAtras = findViewById<Button>(R.id.btnAtrasReportes)
        btnAtras.setOnClickListener { finish() }

        val rvReportes = findViewById<RecyclerView>(R.id.rvTodosLosReportes)

        adapter = MesesAdapter(emptyList()) { textoSeleccionado ->
            val soloMes = textoSeleccionado.substringBefore(" -").trim()

            val intent = Intent(this, reporte_mensual::class.java)
            intent.putExtra("MES_ELEGIDO", soloMes)
            intent.putExtra("TITULO_COMPLETO", textoSeleccionado)
            startActivity(intent)
        }

        rvReportes.layoutManager = LinearLayoutManager(this)
        rvReportes.adapter = adapter

        val db = GastoDatabase.getDatabase(this)
        val gastoDao = db.gastoDao()

        lifecycleScope.launch {
            val mesesDelAnio = listOf(
                "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
            )

            val mesesConDatos = mutableListOf<String>()

            for (mes in mesesDelAnio) {
                val gastos = gastoDao.obtenerGastosPorMes(mes)
                if (gastos.isNotEmpty()) {
                    val fechaGuardada = gastos[0].fecha
                    val anioDelGasto = Regex("\\d{4}").find(fechaGuardada)?.value ?: "2026"
                    mesesConDatos.add("$mes - $anioDelGasto")
                }
            }
            val mesesOrdenados = mesesConDatos.sortedWith(compareBy<String> { texto ->
                Regex("\\d{4}").find(texto)?.value?.toIntOrNull() ?: 0
            }.thenBy { texto ->
                val mes = texto.substringBefore(" -").trim()
                mesesDelAnio.indexOf(mes)
            })
            adapter.actualizarLista(mesesOrdenados)
        }
    }
}