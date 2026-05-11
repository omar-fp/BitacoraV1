package com.example.bitacorav1

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class detalle_dia : AppCompatActivity() {

    private lateinit var adapter: GastoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_dia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvFechaDia = findViewById<TextView>(R.id.tvFechaDia)
        val fechaRecibida = intent.getStringExtra("FECHA_ELEGIDA") ?: "Sin fecha"
        tvFechaDia.text = fechaRecibida

        val btnAtras = findViewById<Button>(R.id.btnAtras)
        btnAtras.setOnClickListener { finish() }

        val etConcepto = findViewById<EditText>(R.id.etNuevoGasto)
        val etMonto = findViewById<EditText>(R.id.etNuevoMonto)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        val db = GastoDatabase.getDatabase(this)
        val gastoDao = db.gastoDao()

        fun cargarGastosDelDia() {
            lifecycleScope.launch {
                val gastos = gastoDao.obtenerGastosPorDia(fechaRecibida)
                adapter.actualizarLista(gastos)
            }
        }

        fun alertaEliminar(gasto: Gasto) {
            val builder = AlertDialog.Builder(this@detalle_dia)
            builder.setTitle("Eliminar Gasto")
            builder.setMessage("¿Estás seguro de que quieres eliminar \"${gasto.concepto}\"?")

            builder.setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    gastoDao.eliminarGasto(gasto)
                    cargarGastosDelDia()
                    Toast.makeText(this@detalle_dia, "Gasto eliminado", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancelar", null)
            builder.create().show()
        }

        val rvGastos = findViewById<RecyclerView>(R.id.rvGastos)

        adapter = GastoAdapter(emptyList()) { gastoClickeado ->
            alertaEliminar(gastoClickeado)
        }

        rvGastos.layoutManager = LinearLayoutManager(this)
        rvGastos.adapter = adapter

        cargarGastosDelDia()

        btnAgregar.setOnClickListener {
            val conceptoTexto = etConcepto.text.toString()
            val montoTexto = etMonto.text.toString()

            if (conceptoTexto.isNotEmpty() && montoTexto.isNotEmpty()) {
                val montoDouble = montoTexto.toDoubleOrNull() ?: 0.0

                val nuevoGasto = Gasto(
                    fecha = fechaRecibida,
                    concepto = conceptoTexto,
                    monto = montoDouble
                )

                lifecycleScope.launch {
                    gastoDao.insertarGasto(nuevoGasto)

                    cargarGastosDelDia()

                    Toast.makeText(this@detalle_dia, "Gasto guardado con éxito", Toast.LENGTH_SHORT).show()

                    etConcepto.text.clear()
                    etMonto.text.clear()
                }
            } else {
                Toast.makeText(this, "Tienes que llenar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}