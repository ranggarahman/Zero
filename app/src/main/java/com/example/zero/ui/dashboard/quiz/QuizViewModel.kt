package com.example.zero.ui.dashboard.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zero.R
import com.example.zero.data.FirebaseManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class QuizViewModel(): ViewModel() {

//    private val authRepository: AuthRepository,
//    private val quizRepository: QuizRepository

    private val _questionList = MutableLiveData<List<Question>>()
    val questionList: LiveData<List<Question>> = _questionList

    private val _correctAnswerCount = MutableLiveData<Int>()
    val correctAnswerCount: LiveData<Int> = _correctAnswerCount

    fun getQuiz(id: Int) {
        val database = FirebaseManager.database.reference
        val reference = database.child("materials").child("$id").child("quiz")

        try {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childCount = snapshot.childrenCount.toInt()
                    val drawables = getRandomDrawables(childCount)
                    val questions = mutableListOf<Question>()
                    snapshot.children.forEachIndexed { index, questionSnapshot ->
                        val type = questionSnapshot.child("type").getValue(String::class.java)
                        val questionText = questionSnapshot.child("questionText").getValue(String::class.java)
                        val mcqOptionsSnapshot = questionSnapshot.child("mcqOptions")
                        val tfOptionsSnapshot = questionSnapshot.child("tfOptions")
                        val correctAnswer = questionSnapshot.child("correctAnswer").getValue(String::class.java)

                        // Based on type, construct the Question object appropriately
                        when (type) {
                            "MCQ" -> {
                                val mcqOptions = mcqOptionsSnapshot.getValue(McqOption::class.java)
                                val question = Question(type,
                                    questionText!!, mcqOptions, null, correctAnswer ?: "", drawables[index])
                                questions.add(question)
                            }
                            "TF" -> {
                                val tfOptions = tfOptionsSnapshot.getValue(TfOption::class.java)
                                val question = Question(type, questionText!!, null, tfOptions, correctAnswer ?: "", drawables[index])
                                questions.add(question)
                            }
                            "FIB" -> {
                                val question = Question(type, questionText!!, null, null, correctAnswer ?: "", drawables[index])
                                questions.add(question)
                            }
                        }
                    }
                    _questionList.postValue(questions)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.d(TAG, "EXCEPTION ${error.message}")
                }
            })
        } catch (e: Exception){
            Log.d(TAG, "EXCEPTION ${e.message}")
        }
    }

    fun correctAnswerIterator() {
        _correctAnswerCount.value = (_correctAnswerCount.value ?: 0) + 1

        //Log.d(TAG, "COUNT: ${_correctAnswerCount.value}")
    }

    fun generateRandomQuestions() {
        val questionList = mutableListOf<Question>()
        val numQuestions = 5

        val drawables = getRandomDrawables(numQuestions)

        //Log.d(TAG, "$drawables")

        repeat(numQuestions) {
            val question: Question = when (listOf("MCQ", "TF", "FIB").random()) {
                "MCQ" -> generateMCQQuestion(drawables[it])
                "TF" -> generateTFQuestion(drawables[it])
                "FIB" -> generateFIBQuestion(drawables[it])
                else -> throw IllegalArgumentException("Invalid Type")
            }

            questionList.add(question)
        }

        _questionList.value = questionList
    }


    fun generateMCQSpaced(){
        //_questionList.value = listOf(generateMCQQuestion(), generateMCQQuestion(), generateTFQuestion())
    }

    private fun generateMCQQuestion(i: Int): Question {
        val questionText = "This is a multiple-choice question."
        val options = McqOption("Option A", "Option B", "Option C", "Option D")
        val correctAnswer = "Option A"

        return Question("MCQ", questionText, options, null, correctAnswer, i)
    }

    private fun generateTFQuestion(i: Int): Question {
        val questionText = "This is a true or false question."
        val options = TfOption("True", "False")
        val correctAnswer = "True"

        return Question("TF",questionText, null, options, correctAnswer, i)
    }

    private fun generateFIBQuestion(i: Int): Question {
        val questionText = "This is a fill in the blank question."
        val correctAnswer = "Correct Answer"

        return Question("FIB", questionText, null, null, correctAnswer, i)
    }

    fun getRandomDrawables(numDrawables: Int): List<Int> {
        val randomDrawable = listOf(
            R.drawable.img_quiz_1,
            R.drawable.img_quiz_2,
            R.drawable.img_quiz_3,
            R.drawable.img_quiz_4,
            R.drawable.img_quiz_5,
            R.drawable.img_quiz_6,
            R.drawable.img_quiz_7,
            R.drawable.img_quiz_8,
            R.drawable.img_quiz_9,
            R.drawable.img_quiz_10,
        )

        val shuffledList = randomDrawable.shuffled()
        return shuffledList.take(numDrawables).distinct().take(numDrawables)

    }

    companion object {
        private const val TAG = "QuizViewModel"
    }
}

data class Question(
    val type: String,
    val questionText: String,
    val mcqOptions: McqOption?,
    val tfOptions: TfOption?,
    val correctAnswer: String = "",
    val imgInt: Int
)

data class TfOption(
    val option1: String = "",
    val option2: String = "",
)

data class McqOption(
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = ""
)