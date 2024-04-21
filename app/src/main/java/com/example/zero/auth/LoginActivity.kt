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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val user = FirebaseManager.currentUser.currentUser

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //checkCurrentUser()

        binding.loginbtn.setOnClickListener {
            googleLogin()
        }
    }

    private fun checkCurrentUser() {
        if (user != null) {
            // User is signed in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // No user is signed in
            // Do nothing, user stays in this activity
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

        Log.d(TAG, "CALLED")

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