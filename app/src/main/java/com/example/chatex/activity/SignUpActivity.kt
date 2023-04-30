package com.example.chatex.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatex.R
import com.example.chatex.models.SignUpModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)

        auth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            val userName = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(applicationContext, "username is required!", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "email is required!", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "password is required!", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(applicationContext, "confirm_password is required!", Toast.LENGTH_SHORT).show()
            }

            if (password != confirmPassword) {
                Toast.makeText(applicationContext, "password not match", Toast.LENGTH_SHORT).show()
            }

            registerUser(userName, email, password)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email!!, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users")

                    var signUpModel = SignUpModel(userId, userName)

                    databaseReference.child(userId).setValue(signUpModel)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this@SignUpActivity, UsersActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener { err ->
                            Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }

    }
}