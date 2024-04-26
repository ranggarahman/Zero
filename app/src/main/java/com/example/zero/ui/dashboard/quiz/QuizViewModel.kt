package com.example.zero.ui.dashboard.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zero.R
import kotlinx.coroutines.launch

class QuizViewModel(): ViewModel() {

//    private val authRepository: AuthRepository,
//    private val quizRepository: QuizRepository

    private val _questionList = MutableLiveData<List<Question>>()
    val questionList: LiveData<List<Question>> = _questionList

    private val _correctAnswerCount = MutableLiveData<Int>()
    val correctAnswerCount: LiveData<Int> = _correctAnswerCount

//    fun getQuiz(categoryId: Int, levelId : Int) {
//
//        val jwtToken = "Bearer ${authRepository.getToken()}"
//
//        Log.d(TAG, "CREDENTIALS : $categoryId, $levelId, $jwtToken")
//        Log.d(TAG, "QUIZ CALLED")
//
//        try {
//            viewModelScope.launch {
//                val result = quizRepository.getQuiz(
//                    categoryId = categoryId,
//                    levelId = levelId,
//                    token = jwtToken
//                )
//
//                Log.d(TAG, "RESULT $result")
//
//                if (result is Result.Success){
//                    val drawables = getRandomDrawables(result.data.quizResponse!!.size)
//
//                    val questions: List<Question> = result.data.quizResponse.mapIndexed { index, quizResponseItem ->
//                        quizResponseItem.let {
//                            val mcqOptions = if (it!!.type == "MC") {
//                                McqOption(
//                                    option1 = it.a!!,
//                                    option2 = it.b!!,
//                                    option3 = it.c!!,
//                                    option4 = it.d!!
//                                )
//                            } else {
//                                null
//                            }
//
//                            val tfOptions = if (it.type == "TF") {
//
//                                Log.d(TAG, "${it.answer}")
//
//                                TfOption(
//                                    option1 = "Benar",
//                                    option2 = "Salah"
//                                )
//                            } else {
//                                null
//                            }
//
//                            Question(
//                                type = it.type!!,
//                                questionText = it.question!!,
//                                mcqOptions = mcqOptions,
//                                tfOptions = tfOptions,
//                                correctAnswer = it.answer!!,
//                                imgInt = drawables[index]
//                            )
//                        }
//                    }
//
//                    _questionList.value = questions
//
//                }
//            }
//        } catch (e: Exception){
//            Log.d(TAG, "EXCEPTION ${e.message}")
//        }
//    }

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
    val option1: String,
    val option2: String
)

data class McqOption(
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
)