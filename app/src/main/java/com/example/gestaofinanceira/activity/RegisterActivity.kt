package com.example.gestaofinanceira.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gestaofinanceira.R
import com.example.gestaofinanceira.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mRegisterUserName:EditText
    private lateinit var mRegisterFullName:EditText
    private lateinit var mRegisterEmail:EditText
    private lateinit var mRegisterPhone:EditText
    private lateinit var mRegisterPassword:EditText
    private lateinit var mRegisterConfirmPassword:EditText
    private lateinit var mRegisterSignIn:Button

    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mRegisterUserName           = findViewById(R.id.register_edittext_username)
        mRegisterFullName           = findViewById(R.id.register_edittext_fullname)
        mRegisterEmail              = findViewById(R.id.register_edittext_email)
        mRegisterPhone              = findViewById(R.id.register_edittext_phone)
        mRegisterPassword           = findViewById(R.id.register_edittext_password)
        mRegisterConfirmPassword    = findViewById(R.id.register_edittext_confirmpassword)
        mRegisterSignIn             = findViewById(R.id.register_edittext_signin)
        mRegisterSignIn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            R.id.register_edittext_signin -> {
                val userName        = mRegisterUserName.text.toString()
                val fullName        = mRegisterFullName.text.toString()
                val email           = mRegisterEmail.text.toString()
                val phone           = mRegisterPhone.text.toString()
                val password        = mRegisterPassword.text.toString()
                val confirmPassword = mRegisterConfirmPassword.text.toString()

                var isFormRightFilled = true

                if( userName.isEmpty() ) {
                    mRegisterUserName.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( fullName.isEmpty() ) {
                    mRegisterFullName.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( email.isEmpty() ) {
                    mRegisterEmail.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( phone.isEmpty() ) {
                    mRegisterPhone.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( password.isEmpty() ) {
                    mRegisterPassword.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( confirmPassword.isEmpty() ) {
                    mRegisterConfirmPassword.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if( isFormRightFilled ) {

                    if( password != confirmPassword ) {
                        mRegisterConfirmPassword.error = "As senhas devem ser iguais"
                        return
                    }

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        val handler = Handler(Looper.getMainLooper())

                        if( it.isSuccessful ) {

                            val user = User(userName, fullName, email, phone)

                            val ref = mDatabase.getReference("users/${mAuth.uid}")
                            ref.setValue( user )


                            handler.post {
                                Toast.makeText(
                                    this,
                                    "Usuário $userName foi cadastrado com sucesso",
                                    Toast.LENGTH_LONG,
                                ).show()

                                finish()
                            }
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