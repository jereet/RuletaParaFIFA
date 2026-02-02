package com.example.juegosdejere3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.juegosdejere3.GameData.puntajes

class PuntajesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntajes)

        // Ordenar por puntaje de mayor a menor
        val puntajesOrdenados = puntajes.toList().sortedByDescending { it.second }

        // Crear el texto con puestos y corona al primero
        val textoPuntajes = buildString {
            puntajesOrdenados.forEachIndexed { index, (nombre, puntaje) ->
                val puesto = "${index + 1}° $nombre: $puntaje"
                if (index == 0) append("$puesto 👑\n")
                else append("$puesto\n")
            }
        }

        // Mostrar en el TextView
        val textView = findViewById<TextView>(R.id.textViewPuntajes)
        textView.text = textoPuntajes

        val btnVolver = findViewById<Button>(R.id.back)
        btnVolver.setOnClickListener {
            finish()
        }
        val btnModificar = findViewById<Button>(R.id.modify)
        btnModificar.setOnClickListener {
            val opciones = arrayOf("Sumar 5 puntos", "Restar 5 puntos")
            val jugadores = puntajes.keys.toList() // Lista de jugadores actuales

            AlertDialog.Builder(this)
                .setTitle("¿Qué acción querés realizar?")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> { // SUMAR
                            AlertDialog.Builder(this)
                                .setTitle("Elegí un jugador para SUMAR 5 puntos")
                                .setItems(jugadores.toTypedArray()) { _, index ->
                                    val jugador = jugadores[index]
                                    puntajes[jugador] = (puntajes[jugador] ?: 0) + 5
                                    actualizarVistaPuntajes()
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }

                        1 -> { // RESTAR
                            AlertDialog.Builder(this)
                                .setTitle("Elegí un jugador para RESTAR 5 puntos")
                                .setItems(jugadores.toTypedArray()) { _, index ->
                                    val jugador = jugadores[index]
                                    puntajes[jugador] = (puntajes[jugador] ?: 0) - 5
                                    actualizarVistaPuntajes()
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
    private fun actualizarVistaPuntajes() {
        val textView = findViewById<TextView>(R.id.textViewPuntajes)

        // Ordenar los jugadores por puntaje de mayor a menor
        val jugadoresOrdenados = puntajes.entries.sortedByDescending { it.value }

        // Construir el texto con los puestos
        val texto = buildString {
            jugadoresOrdenados.forEachIndexed { index, (nombre, puntos) ->
                val puesto = "${index + 1}°) "
                val corona = if (index == 0) " 👑" else ""
                append("$puesto$nombre: $puntos $corona\n")
            }
        }

        textView.text = texto
    }

}