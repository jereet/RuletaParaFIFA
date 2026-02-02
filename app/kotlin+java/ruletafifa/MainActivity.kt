package com.example.ruletafifa

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.juegosdejere3.databinding.ActivityMainBinding
import kotlin.random.Random
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private var selectedGroup: String? = null

    private lateinit var binding: ActivityMainBinding
    private var selectedRouletteIndex = 1 // Inicia en la ruleta del medio (índice 1)

    // Lista de ruletas para fácil acceso
    private val roulettes by lazy {
        arrayOf(binding.smallRouletteLeft, binding.rouletteImage, binding.smallRouletteRight)
    }
    //Lista de frenadores
    private val frenadores by lazy {
        arrayOf(binding.frenadorSmallLeft, binding.frenadorCenter, binding.frenadorSmallRight)
    }
    // Nombres de sectores para cada ruleta
    private val rouletteSectors = arrayOf(
        arrayOf("Pérdida de puntos", "Inhibición de ganador", "Pérdida de puntos", "Inhibición de ganador", "Jere o nada", "Pérdida de puntos", "Inhibición de ganador", "Pérdida de puntos", "Inhibición de ganador", "Swinger extremo"),
        arrayOf("¿Quien?", "¿Quien?", "¿Quien?", "Pitonari", "Pitonari", "Pitonari", "Suerte", "Suerte", "¿Quien?", "¿Quien?"),
        arrayOf("DP", "Conexión con dios", "DP", "Jeredín", "Conexión con dios", "DP", "Conexión con dios", "DP", "Punisher/Jeresher/Jere Castle", "Conexión con dios"),
    )

    // Preguntas y respuestas para sectores específicos
    // Preguntas y respuestas para sectores específicos
    val specialQuestions = QuestionsData.sectorQuestions

    private val pendingQuestions: MutableMap<String, MutableMap<String, MutableList<Pair<String, String>>>> =
        specialQuestions.mapValues {
            it.value.mapValues { questions -> questions.value.toMutableList() }.toMutableMap()
        }.toMutableMap()

    private fun loadGameState() {
        val prefs = getSharedPreferences("GameCache", MODE_PRIVATE)
        val puntajesJson = prefs.getString("puntajes", null)
        val preguntasJson = prefs.getString("preguntas", null)

        // Restaurar puntajes
        if (!puntajesJson.isNullOrEmpty()) {
            puntajesJson.split(";").forEach {
                val (nombre, puntos) = it.split(":")
                GameData.puntajes[nombre] = puntos.toInt()
            }
        }

        // Restaurar preguntas restantes
        if (!preguntasJson.isNullOrEmpty()) {
            pendingQuestions.clear()
            preguntasJson.split(";").forEach {
                val parts = it.split("|")
                if (parts.size == 4) {
                    val (sector, grupo, pregunta, respuesta) = parts
                    val grupos = pendingQuestions.getOrPut(sector) { mutableMapOf() }
                    val lista = grupos.getOrPut(grupo) { mutableListOf() }
                    lista.add(pregunta to respuesta)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadGameState()
        // Configurar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar botón para hacer girar la ruleta seleccionada
        binding.spinButton.setOnClickListener {
            spinRoulette(selectedRouletteIndex)
        }
        binding.puntajes.setOnClickListener {
            val intent = Intent(this, PuntajesActivity::class.java)
            startActivity(intent)
        }


        // Inicializar el estado visual de las ruletas
        highlightSelectedRoulette()


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                selectedRouletteIndex = (selectedRouletteIndex + roulettes.size - 1) % roulettes.size
                highlightSelectedRoulette()
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                selectedRouletteIndex = (selectedRouletteIndex + 1) % roulettes.size
                highlightSelectedRoulette()
                return true
            }
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



    // Resaltar la ruleta seleccionada
    private fun highlightSelectedRoulette() {
        roulettes.forEachIndexed { index, imageView ->
            val targetAlpha = if (index == selectedRouletteIndex) 1.0f else 0.5f
            imageView.animate()
                .alpha(targetAlpha)
                .setDuration(300) // Duración de la animación de opacidad
                .start()
        }

        frenadores.forEachIndexed { index, imageView ->
            val targetAlpha = if (index == selectedRouletteIndex) 1.0f else 0.5f
            imageView.animate()
                .alpha(targetAlpha)
                .setDuration(300) // Duración de la animación de opacidad
                .start()
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





    private fun showSelectGroupDialog(selectedSector: String, sectorQuestions: MutableMap<String, MutableList<Pair<String, String>>>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_group, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.title).text = selectedSector
        if (selectedSector == "¿Quien?") {
            dialogView.findViewById<Button>(R.id.buttonGeneral).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.buttonGroupA).setOnClickListener {
                selectedGroup = "LOS DUROS"
                sectorQuestions["GRUPO A"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonGroupB).setOnClickListener {
                selectedGroup = "SEXO VIOLENTO"
                sectorQuestions["GRUPO B"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonGroupC).setOnClickListener {
                selectedGroup = "SALAAAAA"
                sectorQuestions["GRUPO C"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonCloseDialog).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        if (selectedSector == "Pitonari") {
            dialogView.findViewById<Button>(R.id.buttonGroupA).setOnClickListener {
                selectedGroup = "LOS DUROS"
                sectorQuestions["GRUPO A"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonGroupB).setOnClickListener {
                selectedGroup = "SEXO VIOLENTO"
                sectorQuestions["GRUPO B"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonGroupC).setOnClickListener {
                selectedGroup = "SALAAAAA"
                sectorQuestions["GRUPO C"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonGeneral).setOnClickListener {
                selectedGroup = "GENERAL"
                sectorQuestions["GENERAL"]?.let { it1 ->
                    handleGroupSelection(selectedSector, selectedGroup!!,
                        it1
                    )
                }
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.buttonCloseDialog).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        if (selectedSector == "Suerte") {
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




    private fun handleGroupSelection(selectedSector: String, group: String, sectorQuestions: MutableList<Pair<String, String>>) {
            if (sectorQuestions.isNotEmpty()) {
                val randomQuestion = sectorQuestions.random()
                showQuestionDialog(selectedSector, randomQuestion.first, randomQuestion.second)
                if(selectedSector != "Suerte"){
                    sectorQuestions.remove(randomQuestion)
                }
            } else {
                binding.resultText.text = "¡Todas las preguntas del grupo $group han sido vistas!"
            }


    }


    private fun showQuestionDialog(sector: String, question: String, answer: String) {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_question, null)
        dialog.setView(dialogView)

        val questionTextView = dialogView.findViewById<TextView>(R.id.questionTextView)
        val answerButton = dialogView.findViewById<Button>(R.id.showAnswerButton)
        val answerTextView = dialogView.findViewById<TextView>(R.id.answerTextView)
        val givePointsButton = dialogView.findViewById<Button>(R.id.showListPlayers)
        val givePointsButton2 = dialogView.findViewById<Button>(R.id.showListPlayers2)

        questionTextView.text = question
        answerTextView.visibility = View.GONE
        givePointsButton.visibility = View.GONE
        givePointsButton2.visibility = View.GONE

        if (answer.isBlank()) {
            answerButton.visibility = View.GONE
        }

        answerButton.setOnClickListener {
            answerTextView.text = answer
            answerTextView.visibility = View.VISIBLE
            answerButton.visibility = View.GONE
            if(sector != "Suerte"){
                givePointsButton.visibility = View.VISIBLE // Mostrar el botón para dar puntos
            }
            if(sector == "Pitonari"){
                givePointsButton2.visibility = View.VISIBLE // Mostrar el botón para dar puntos
            }

        }

        givePointsButton.setOnClickListener {
            showGivePointsDialog(sector)
        }
        givePointsButton2.setOnClickListener {
            showGivePointsDialog(sector)
        }

        dialog.setPositiveButton("Cerrar") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        dialog.create().show()
    }

    private fun showGivePointsDialog(sector: String) {
        val jugadores = GameData.puntajes.keys.toTypedArray()
        var puntos = 0
        AlertDialog.Builder(this)
            .setTitle("¿Quién respondió bien?")
            .setItems(jugadores) { _, which ->
                val jugador = jugadores[which]
                if(sector == "¿Quien?"){
                    puntos = 5
                }
                if(sector == "Pitonari"){
                    puntos = 10
                }
                GameData.puntajes[jugador] = GameData.puntajes[jugador]!! + puntos
                saveGameState()
                AlertDialog.Builder(this)
                    .setMessage("¡Se sumó $puntos puntos a $jugador!")
                    .setPositiveButton("OK", null)
                    .show()
            }
            .show()
    }
    private fun saveGameState() {
        val prefs = getSharedPreferences("GameCache", MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar puntajes
        val puntajesJson = GameData.puntajes.entries.joinToString(";") { "${it.key}:${it.value}" }
        editor.putString("puntajes", puntajesJson)

        // Guardar preguntas restantes
        val preguntasJson = pendingQuestions.flatMap { (sector, grupos) ->
            grupos.flatMap { (grupo, lista) ->
                lista.map { (pregunta, respuesta) ->
                    "$sector|$grupo|$pregunta|$respuesta"
                }
            }
        }.joinToString(";")
        editor.putString("preguntas", preguntasJson)

        editor.apply()
    }





}
