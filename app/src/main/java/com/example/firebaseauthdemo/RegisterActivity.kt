package com.example.firebaseauthdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister: Button = findViewById(R.id.btn_register)
        val emailInput: TextInputEditText = findViewById(R.id.email_register_input)
        val passwordInput: TextInputEditText = findViewById(R.id.password_register_input)
        val nameInput: TextInputEditText = findViewById(R.id.name_register_input)

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val btnLogin: TextView = findViewById(R.id.login_register)
        btnLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(emailInput.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(passwordInput.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }TextUtils.isEmpty(nameInput.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please enter your name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
                else -> {
                    val email: String = emailInput.text.toString().trim { it <= ' '}
                    val password: String = passwordInput.text.toString().trim { it <= ' '}
                    val name: String = nameInput.text.toString().trim { it <= ' ' }

                    // Register a user with email and password
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            // if the registration is successful
                            if (task.isSuccessful) {
                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "You were registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Save info on the database
                                val initialBalance: Double = 100.0
                                val userInfo: MutableMap<String, Any>  = HashMap()
                                userInfo["uid"] = firebaseUser.uid
                                userInfo["balance"] = initialBalance
                                userInfo["accountName"] = name
                                db.collection("users").document(firebaseUser.uid)
                                    .set(userInfo)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "User added to the database.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "User not added to the database.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                val intent =
                                    Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra(
                                    "user_accountName",
                                    name
                                )
                                intent.putExtra(
                                    "user_balance",
                                    initialBalance
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                // If registering is not successful then show an error message
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
        }
    }
}