package com.example.gestaofinanceira.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.example.gestaofinanceira.R
import com.example.gestaofinanceira.model.Financy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FinancyFormActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mFinancyFormTitle: TextView
    private lateinit var mFinancyFormName: EditText
    private lateinit var mFinancyFormPrice: EditText
    private lateinit var mFinancyFormIsReceita: Switch
    private lateinit var mFinancyFormSave: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var mFinancyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financy_form)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mFinancyId = intent.getStringExtra("financyId") ?: ""

        mFinancyFormTitle = findViewById(R.id.financy_form_textview_title)
        mFinancyFormName = findViewById(R.id.financy_form_plaintext_name)
        mFinancyFormPrice = findViewById(R.id.financy_form_plaintext_price)
        mFinancyFormIsReceita = findViewById(R.id.financy_form_switch_isreceita)

        mFinancyFormSave = findViewById(R.id.financy_form_button_create)
        mFinancyFormSave.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        if( mFinancyId.isNotEmpty()) {
            val query = mDatabase.reference.child("users/${mAuth.uid}/tasks/${mFinancyId}").orderByKey()
            query.addValueEventListener( object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val financy = snapshot.getValue(Financy::class.java)!!

                    handler.post {
                        mFinancyFormTitle.text =
                            Editable.Factory.getInstance().newEditable("Editar Tarefa")
                        mFinancyFormName.text =
                            Editable.Factory.getInstance().newEditable(financy.name)
                        mFinancyFormPrice.text =
                            Editable.Factory.getInstance().newEditable(financy.price.toString())
                        mFinancyFormIsReceita.isChecked = financy.receita
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.financy_form_button_create -> {
                //TODO Salvar uma tarefa

                val name = mFinancyFormName.text.toString()
                val price = mFinancyFormPrice.text.toString()
                val isReceita = mFinancyFormIsReceita.isChecked

                if (name.isEmpty()) {
                    mFinancyFormName.error = "Este campo não pode ser vazio"
                    return
                }

                if (price.isEmpty()) {
                    mFinancyFormName.error = "Este campo não pode ser vazio"
                    return
                }

                if(mFinancyId.isEmpty()) {
                    val fid = mDatabase.reference.child("users/${mAuth.uid}/tasks").push().key
                    val financy = Financy(
                        fid!!,
                        name = name,
                        price = price.toDouble(),
                        receita = isReceita
                    )

                    val ref = mDatabase
                        .getReference("users/${mAuth.uid}/tasks/$fid")
                    ref.setValue(financy)

                    finish()
                } else {

                    val financy = Financy(
                        mFinancyId,
                        name = name,
                        price = price.toDouble(),
                        receita = isReceita
                    )
                    val ref = mDatabase
                        .getReference("users/${mAuth.uid}/tasks/$mFinancyId")
                    ref.setValue(financy)

                    finish()
                }
            }
        }
    }
}