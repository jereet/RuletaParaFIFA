package com.example.rultafifa

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rultafifa.databinding.ActivityMainBinding
import kotlin.random.Random
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private var selectedGroup: String? = null

    private lateinit var binding: ActivityMainBinding
    private var selectedRouletteIndex = 0 // Inicia en la ruleta del medio

    // Lista de ruletas para fácil acceso
    private val roulettes by lazy {
        arrayOf(binding.rouletteImage)
    }
    //Lista de frenadores
    private val frenadores by lazy {
        arrayOf(binding.frenadorCenter)
    }
    // Nombres de sectores para cada ruleta
    private val rouletteSectors = arrayOf(
        arrayOf("Castigo", "Selecciones", "Categoria", "Equipo", "Equipo + Categoria", "Castigo", "Selecciones", "Categoria", "Equipo", "Equipo + Categoria"),
    )

    // Preguntas y respuestas para sectores específicos
    // Preguntas y respuestas para sectores específicos
    val specialQuestions = QuestionsData.sectorQuestions

    private val pendingQuestions: MutableMap<String, MutableMap<String, MutableList<String>>> =
        specialQuestions.mapValues {
            it.value.mapValues { questions -> questions.value.toMutableList() }.toMutableMap()
        }.toMutableMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar botón para hacer girar la ruleta seleccionada
        binding.spinButton.setOnClickListener {
            spinRoulette(selectedRouletteIndex)
        }



    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // Manejar entradas numéricas del 0 al 9 para seleccionar sectores
            KeyEvent.KEYCODE_0 -> {
                selectRouletteSector(0)
                return true
            }
            KeyEvent.KEYCODE_1 -> {
                selectRouletteSector(1)
                return true
            }
            KeyEvent.KEYCODE_2 -> {
                selectRouletteSector(2)
                return true
            }
            KeyEvent.KEYCODE_3 -> {
                selectRouletteSector(3)
                return true
            }
            KeyEvent.KEYCODE_4 -> {
                selectRouletteSector(4)
                return true
            }
            KeyEvent.KEYCODE_5 -> {
                selectRouletteSector(5)
                return true
            }
            KeyEvent.KEYCODE_6 -> {
                selectRouletteSector(6)
                return true
            }
            KeyEvent.KEYCODE_7 -> {
                selectRouletteSector(7)
                return true
            }
            KeyEvent.KEYCODE_8 -> {
                selectRouletteSector(8)
                return true
            }
            KeyEvent.KEYCODE_9 -> {
                selectRouletteSector(9)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun selectRouletteSector(sectorIndex: Int) {
        // Verificar si el índice está en el rango de sectores para la ruleta seleccionada
        if (sectorIndex in 0 until rouletteSectors[selectedRouletteIndex].size) {
            val selectedSector = rouletteSectors[selectedRouletteIndex][sectorIndex]
            binding.resultText.text = "$selectedSector"

            // Obtener las preguntas pendientes del sector
            val sectorQuestions = pendingQuestions[selectedSector]

            if (sectorQuestions != null) {
                // Si el sector tiene preguntas, muestra el diálogo de selección de grupo (si aplica)
                showSelectGroupDialog(selectedSector, sectorQuestions)
            }
        }
    }


    private fun spinRoulette(index: Int) {
        if (index !in rouletteSectors.indices) {
            binding.resultText.text = "Error: índice fuera de rango"
            return
        }

        val sectors = rouletteSectors[index]
        val result = Random.nextInt(sectors.size)

        val degreesPerSector = 325f / sectors.size
        val resultRotation = result * degreesPerSector

        roulettes[index].rotation = 0f
        roulettes[index].animate()
            .rotationBy(360f * 5 - resultRotation)
            .setDuration(4000)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                val selectedSector = sectors[result]
                binding.resultText.text = selectedSector

                val sectorQuestions = pendingQuestions[selectedSector]

                if (sectorQuestions != null) {
                    showSelectGroupDialog(selectedSector, sectorQuestions)
                }


                binding.resultText.text = selectedSector
            }
            .start()
    }





    private fun showSelectGroupDialog(selectedSector: String, sectorQuestions: MutableMap<String, MutableList<String>>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_group, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.title).text = selectedSector

        if (selectedSector == "Castigo") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupB).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupC).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            selectedGroup = "GENERAL"
            sectorQuestions["GENERAL"]?.let { it1 ->
                handleGroupSelection(selectedSector, selectedGroup!!,
                    it1
                )
            }
            dialog.dismiss()
        }
        if (selectedSector == "Equipo") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupB).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupC).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            selectedGroup = "GENERAL"
            sectorQuestions["GENERAL"]?.let { it1 ->
                handleGroupSelection(selectedSector, selectedGroup!!,
                    it1
                )
            }
            dialog.dismiss()
        }
        if (selectedSector == "Selecciones") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupB).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupC).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            selectedGroup = "GENERAL"
            sectorQuestions["GENERAL"]?.let { it1 ->
                handleGroupSelection(selectedSector, selectedGroup!!,
                    it1
                )
            }
            dialog.dismiss()
        }
        if (selectedSector == "Categoria") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupB).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupC).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            selectedGroup = "GENERAL"
            sectorQuestions["GENERAL"]?.let { it1 ->
                handleGroupSelection(selectedSector, selectedGroup!!,
                    it1
                )
            }
            dialog.dismiss()
        }
        if (selectedSector == "Equipo + Categoria") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupB).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupC).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            selectedGroup = "GENERAL"
            sectorQuestions["GENERAL"]?.let { it1 ->
                handleGroupSelection(selectedSector, selectedGroup!!,
                    it1
                )
            }
            dialog.dismiss()
        }
    }




    private fun handleGroupSelection(
        selectedSector: String,
        group: String,
        sectorQuestions: MutableList<String>
    ) {
        if (sectorQuestions.isNotEmpty()) {
            val randomText = sectorQuestions.random()
            showQuestionDialog(selectedSector, randomText)
            sectorQuestions.remove(randomText)

        } else {
            binding.resultText.text =
                "¡$selectedSector esta vacio!"
        }
    }



    private fun showQuestionDialog(sector: String, text: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_question, null)
        dialog.setView(dialogView)

        val questionTextView =
            dialogView.findViewById<TextView>(R.id.questionTextView)

        questionTextView.text = text

        dialog.setPositiveButton("Cerrar") { d, _ ->
            d.dismiss()
        }

        dialog.create().show()
    }






}
