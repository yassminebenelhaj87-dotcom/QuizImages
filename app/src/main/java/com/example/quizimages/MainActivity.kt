package com.example.quizimages

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class Question(
    val text: String,
    val imageResId: Int,
    val choices: Array<String>,
    val correctAnswer: Int,
    val imageDescription: String
)

class MainActivity : AppCompatActivity() {

    // Les cinq questions du quiz
    private val questions = arrayOf(
        Question(
            text = "Quelle est cette forme ?",
            imageResId = R.drawable.triangle,
            choices = arrayOf("Triangle", "Carré", "Cercle"),
            correctAnswer = 0,
            imageDescription = "Un triangle bleu"
        ),
        Question(
            text = "Combien de côtés égaux a cette forme ?",
            imageResId = R.drawable.square,
            choices = arrayOf("Trois", "Quatre", "Cinq"),
            correctAnswer = 1,
            imageDescription = "Un carré vert"
        ),
        Question(
            text = "Quelle forme ne possède aucun sommet ?",
            imageResId = R.drawable.circle,
            choices = arrayOf("Rectangle", "Triangle", "Cercle"),
            correctAnswer = 2,
            imageDescription = "Un cercle orange"
        ),
        Question(
            text = "Quelle forme ressemble à une étoile ?",
            imageResId = R.drawable.star,
            choices = arrayOf("Étoile", "Carré", "Rectangle"),
            correctAnswer = 0,
            imageDescription = "Une étoile jaune"
        ),
        Question(
            text = "Quelle forme possède quatre angles droits ?",
            imageResId = R.drawable.rectangle,
            choices = arrayOf("Cercle", "Rectangle", "Triangle"),
            correctAnswer = 1,
            imageDescription = "Un rectangle rose"
        )
    )

    private var currentQuestionIndex = 0
    private var score = 0
    private var hasAnswered = false

    private lateinit var txtScore: TextView
    private lateinit var txtProgress: TextView
    private lateinit var txtQuestionNumber: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var txtFeedback: TextView

    private lateinit var imgQuestion: ImageView
    private lateinit var progressQuiz: ProgressBar

    private lateinit var questionContainer: LinearLayout
    private lateinit var resultContainer: LinearLayout

    private lateinit var txtResult: TextView
    private lateinit var txtResultMessage: TextView

    private lateinit var btnSuivant: Button
    private lateinit var btnRejouer: Button

    private lateinit var choiceButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupérer les widgets du fichier XML
        txtScore = findViewById(R.id.txt_score)
        txtProgress = findViewById(R.id.txt_progress)
        txtQuestionNumber = findViewById(R.id.txt_question_number)
        txtQuestion = findViewById(R.id.txt_question)
        txtFeedback = findViewById(R.id.txt_feedback)

        imgQuestion = findViewById(R.id.img_question)
        progressQuiz = findViewById(R.id.progress_quiz)

        questionContainer = findViewById(R.id.question_container)
        resultContainer = findViewById(R.id.result_container)

        txtResult = findViewById(R.id.txt_result)
        txtResultMessage = findViewById(R.id.txt_result_message)

        btnSuivant = findViewById(R.id.btn_suivant)
        btnRejouer = findViewById(R.id.btn_rejouer)

        choiceButtons = listOf(
            findViewById(R.id.btn_choix1),
            findViewById(R.id.btn_choix2),
            findViewById(R.id.btn_choix3)
        )

        // Gérer les clics sur les trois choix
        choiceButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                checkAnswer(index)
            }
        }

        // Passer à la question suivante ou afficher le résultat
        btnSuivant.setOnClickListener {
            if (!hasAnswered) return@setOnClickListener

            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                showResult()
            }
        }

        // Recommencer une nouvelle partie
        btnRejouer.setOnClickListener {
            restartQuiz()
        }

        // Afficher la première question
        displayQuestion()
    }

    // Afficher la question courante
    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]

        hasAnswered = false

        txtQuestionNumber.text =
            "Question ${currentQuestionIndex + 1} sur ${questions.size}"

        txtQuestion.text = question.text

        imgQuestion.setImageResource(question.imageResId)
        imgQuestion.contentDescription = question.imageDescription

        txtScore.text = "Score : $score/${questions.size}"

        // La progression correspond aux réponses déjà données
        progressQuiz.progress = currentQuestionIndex
        txtProgress.text =
            "$currentQuestionIndex réponse(s) sur ${questions.size}"

        txtFeedback.text = ""
        txtFeedback.visibility = View.GONE

        // Actualiser les trois boutons
        choiceButtons.forEachIndexed { index, button ->
            button.text = question.choices[index]
            button.isEnabled = true
            button.setBackgroundColor(Color.rgb(230, 237, 248))
            button.setTextColor(Color.rgb(23, 50, 92))
        }

        btnSuivant.isEnabled = false
        btnSuivant.text =
            if (currentQuestionIndex == questions.size - 1) {
                "Voir le résultat"
            } else {
                "Question suivante"
            }

        questionContainer.visibility = View.VISIBLE
        resultContainer.visibility = View.GONE
        btnSuivant.visibility = View.VISIBLE
    }

    // Vérifier la réponse choisie
    private fun checkAnswer(selectedIndex: Int) {

        // Empêcher plusieurs réponses pour la même question
        if (hasAnswered) return

        hasAnswered = true

        val question = questions[currentQuestionIndex]
        val isCorrect = selectedIndex == question.correctAnswer

        if (isCorrect) {
            score++
            txtFeedback.text = "Bonne réponse !"
            txtFeedback.setTextColor(Color.rgb(27, 130, 65))
        } else {
            txtFeedback.text =
                "Mauvaise réponse. La bonne réponse est : " +
                        question.choices[question.correctAnswer]

            txtFeedback.setTextColor(Color.rgb(198, 40, 40))
        }

        txtFeedback.visibility = View.VISIBLE

        // Mettre à jour le score
        txtScore.text = "Score : $score/${questions.size}"

        // Mettre à jour la progression après chaque réponse
        progressQuiz.progress = currentQuestionIndex + 1
        txtProgress.text =
            "${currentQuestionIndex + 1} réponse(s) sur ${questions.size}"

        // Désactiver les trois choix
        choiceButtons.forEachIndexed { index, button ->
            button.isEnabled = false

            // Mettre en évidence la bonne réponse
            if (index == question.correctAnswer) {
                button.setBackgroundColor(Color.rgb(200, 235, 210))
            } else if (index == selectedIndex) {
                button.setBackgroundColor(Color.rgb(255, 210, 210))
            }
        }

        // Autoriser la navigation uniquement après la réponse
        btnSuivant.isEnabled = true
    }

    // Afficher le résultat final dans la même activité
    private fun showResult() {

        questionContainer.visibility = View.GONE
        btnSuivant.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE

        txtResult.text = "Résultat final : $score/${questions.size}"

        txtResultMessage.text = when (score) {
            5 -> "Excellent ! Toutes les réponses sont correctes."
            4 -> "Très bien ! Presque un sans-faute."
            3 -> "Bien joué ! Continue à t'entraîner."
            2 -> "Pas mal ! Tu peux encore progresser."
            1 -> "Continue à apprendre et à pratiquer."
            else -> "Réessaie pour améliorer ton score !"
        }

        // La progression reste à 5/5 à la fin
        progressQuiz.progress = questions.size
        txtProgress.text =
            "${questions.size} réponses sur ${questions.size}"
    }

    // Réinitialiser le quiz
    private fun restartQuiz() {

        currentQuestionIndex = 0
        score = 0
        hasAnswered = false

        progressQuiz.progress = 0
        txtScore.text = "Score : 0/${questions.size}"

        resultContainer.visibility = View.GONE
        questionContainer.visibility = View.VISIBLE
        btnSuivant.visibility = View.VISIBLE

        displayQuestion()
    }
}
