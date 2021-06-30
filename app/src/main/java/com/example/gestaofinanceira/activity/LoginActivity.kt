package com.example.gestaofinanceira.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.gestaofinanceira.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginUserName: EditText
    private lateinit var mLoginPassword: EditText
    private lateinit var mLoginSigin: Button
    private lateinit var mRegister: TextView

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        mLoginUserName = findViewById(R.id.login_edittext_username)
        mLoginPassword = findViewById(R.id.login_edittext_password)

        mLoginSigin = findViewById(R.id.login_button_signin)
        mLoginSigin.setOnClickListener(this)

        mRegister = findViewById(R.id.login_textview_register)
        mRegister.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when( v?.id){

            R.id.login_textview_register -> {
                val it = Intent(this, RegisterActivity::class.java)
                startActivity( it )
            }

            R.id.login_button_signin -> {

                val email = mLoginUserName.text.toString()
                val password = mLoginPassword.text.toString()

                var isLoginFormFilled = true

                if( email.isEmpty() ) {
                    mLoginUserName.error = getString(R.string.form_is_required_error)
                    isLoginFormFilled = false
                }

                if( password.isEmpty() ) {
                    mLoginPassword.error = getString(R.string.form_is_required_error)
                    isLoginFormFilled = false
                }

                if( isLoginFormFilled ) {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        val handler = Handler(Looper.getMainLooper())

                        if(it.isSuccessful) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("userId", mAuth.uid)
                            startActivity(intent)
                            finish()

                        } else {
                            handler.post {
                                Toast.makeText(
                                    this,
                                    it.exception?.message,
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    }

                }

            }

        }

    }
}