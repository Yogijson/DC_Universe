package com.example.yogi.dc_universe

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            performLogin()

        }

        back_to_register.setOnClickListener {
            finish()
        }
    }

    private fun performLogin(){
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Login", "Attempt login with email: $email")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    val intent = Intent(this, BookAdapterViewHolder::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Log.d("Main", "Logged in with user: ${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to login: ${it.message}")
                }
    }

}