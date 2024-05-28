package com.example.zero.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.example.zero.MainActivity
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.ActivityLoginBinding
import com.example.zero.ui.onboarding.OnboardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val user = FirebaseManager.currentUser.currentUser

    private val badges = listOf(
        null,
        mapOf("id" to 1, "isUnlocked" to false),
        mapOf("id" to 2, "isUnlocked" to false),
        mapOf("id" to 3, "isUnlocked" to false),
        mapOf("id" to 4, "isUnlocked" to false),
        mapOf("id" to 5, "isUnlocked" to false),
        mapOf("id" to 6, "isUnlocked" to false)
    )

    private val materialsCompletion = listOf(
        mapOf("flashcardTaken" to 0, "id" to 0, "isCompleted" to false, "quizTaken" to 0, "readsTaken" to 0),
        mapOf("flashcardTaken" to 0, "id" to 1, "isCompleted" to false, "quizTaken" to 0, "readsTaken" to 0),
        mapOf("flashcardTaken" to 0, "id" to 2, "isCompleted" to false, "quizTaken" to 0, "readsTaken" to 0)
    )

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbtn.setOnClickListener {
            googleLogin()
        }
        binding.emailLoginbtn.setOnClickListener {
            emailLogin()
        }
    }

    private fun emailLogin() {
        val email = binding.loginEmailEdittext.text.toString()
        val password = binding.loginPasswordEdittext.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        handleUserAuthentication()
                    } else {
                        // Sign in failed, try to register the user
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { registerTask ->
                                if (registerTask.isSuccessful) {
                                    // Registration successful, sign in again to handle the user
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { signInAfterRegisterTask ->
                                            if (signInAfterRegisterTask.isSuccessful) {
                                                handleUserAuthentication()
                                            } else {
                                                // Sign in after registration failed
                                                Log.e(TAG, "ERROR: Authentication after registration failed")
                                                Toast.makeText(
                                                    this,
                                                    "Authentication after registration failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    // Registration failed
                                    Log.e(TAG, "ERROR: Registration failed")
                                    Toast.makeText(
                                        this,
                                        "Registration Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ERROR: ${e.message}")
                    Toast.makeText(
                        this,
                        "Authentication Failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                this,
                "Email and Password cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleUserAuthentication() {
        val user = FirebaseManager.currentUser.currentUser
        val uid = user?.uid

        if (uid != null) {
            // Create a reference to the Firebase Realtime Database
            val usersRef = FirebaseManager.database.getReference("users")

            // Check if the user already exists
            usersRef.child(uid).get().addOnCompleteListener { dataSnapshotTask ->
                if (dataSnapshotTask.isSuccessful) {
                    if (!dataSnapshotTask.result.exists()) {
                        // User does not exist, create a new record
                        val userObject = HashMap<String, Any>()
                        userObject["uid"] = uid
                        userObject["email"] = user.email ?: ""
                        userObject["avatarId"] = 1
                        userObject["userpoints"] = 0
                        userObject["isChatSent"]= false
                        userObject["isTop5"]= false
                        userObject["badges"] = badges
                        userObject["materialsCompletion"] = materialsCompletion

                        // Add more user data as needed

                        usersRef.child(uid).setValue(userObject)
                            .addOnSuccessListener {
                                // Database write successful
                                Toast.makeText(
                                    this,
                                    "SUCCESS LOGIN",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Do further processing with the authenticated user
                                finish()
                                startActivity(Intent(this, MainActivity::class.java))
                                Log.d(TAG, "success")
                            }
                            .addOnFailureListener { e ->
                                // Database write failed
                                Log.e(TAG, "Error writing user data to database: ${e.message}")
                            }
                    } else {
                        // User already exists, no need to create a new record
                        Toast.makeText(
                            this,
                            "SUCCESS LOGIN",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Do further processing with the authenticated user
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                        Log.d(TAG, "User already exists")
                    }
                } else {
                    // Failed to check if user exists
                    Log.e(TAG, "Error checking user existence: ${dataSnapshotTask.exception?.message}")
                }
            }
        } else {
            Log.e(TAG, "ERROR: User is null")
        }
    }

    private fun googleLogin() {
        try {
            // Configure Google Sign-In
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("507066497547-8c3hdgamfdbpr6sqo7qqvhaoo7obs4km.apps.googleusercontent.com")
                .requestEmail()
                .build()

            // Create a GoogleSignInClient
            val googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)

            // Launch the Google Sign-In intent
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Throwable) {
            // Handle the exception
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "CALLED, REQUEST CODE : $requestCode, RC 9001")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Get Google Sign-In account
                val account = task.getResult(ApiException::class.java)

                // Authenticate with Firebase using the Google credential
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Google Sign-In successful, handle the authenticated user
                            val uid = user?.uid

                            // Create a reference to the Firebase Realtime Database
                            val usersRef = FirebaseManager.database.getReference("users")

                            // Create a user object and store it in the database
                            uid?.let { userId ->
                                val userObject = HashMap<String, Any>()
                                userObject["uid"] = userId
                                userObject["email"] = user?.email ?: ""
                                userObject["username"] = user?.displayName.toString()
                                userObject["avatarId"] = 1
                                userObject["userpoints"] = 0
                                userObject["isChatSent"]= false
                                userObject["isTop5"]= false
                                userObject["badges"] = badges
                                userObject["materialsCompletion"] = materialsCompletion
                                // Add more user data as needed

                                usersRef.child(userId).setValue(userObject)
                                    .addOnSuccessListener {
                                        // Database write successful
                                        Toast.makeText(
                                            this,
                                            "SUCCESS LOGIN",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Do further processing with the authenticated user
                                        finish()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        Log.d(TAG, "success")
                                    }
                                    .addOnFailureListener { e ->
                                        // Database write failed
                                        Log.e(TAG, "Error writing user data to database: ${e.message}")
                                    }
                            }
                        } else {
                            Log.e(TAG, "ERROR not success")
                        }
                    }
            } catch (e: ApiException) {
                Log.e(TAG, "ERROR not success ${e.message}")
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
        private const val TAG = "LOGIN_ACTIVITY"
    }
}