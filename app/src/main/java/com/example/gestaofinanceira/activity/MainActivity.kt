package com.example.gestaofinanceira.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaofinanceira.R
import com.example.gestaofinanceira.adapter.FinancyAdapter
import com.example.gestaofinanceira.adapter.FinancyItemListener
import com.example.gestaofinanceira.model.Financy
import com.example.gestaofinanceira.model.User
import com.example.gestaofinanceira.model.UserWithFinancys
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), View.OnClickListener, FinancyItemListener {

    private lateinit var mRecycleView:RecyclerView
    private lateinit var mAddFinancy:FloatingActionButton

    private lateinit var mUserWithFinancys: UserWithFinancys
    private lateinit var financyAdapter: FinancyAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val mFinancyList = mutableListOf<Financy>()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private var mUserId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mRecycleView = findViewById(R.id.main_recycleview_financy)

        mAddFinancy = findViewById(R.id.main_floatingbutton_addtask)
        mAddFinancy.setOnClickListener(this)


    }

    override fun onStart() {
        super.onStart()

        val query = mDatabase.reference.child("users/${mAuth.uid}/tasks")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                mFinancyList.clear()
                var receita: Int = 0
                var despesa: Int = 0
                snapshot.children.reversed().forEach{

                    val financy = it.getValue(Financy::class.java)
                    if (financy != null) {
                        if(financy.receita) {
                            receita += 1
                        } else {
                            despesa += 1
                        }

                        if( financy.receita && receita < 6) {
                            mFinancyList.add(financy)
                        } else if( !financy.receita && despesa < 6 ) {
                            mFinancyList.add(financy)
                        }
                    }

                }

                financyAdapter = FinancyAdapter(mFinancyList)
                financyAdapter.setFinancyItemListener(this@MainActivity)
                val llm = LinearLayoutManager(applicationContext)

                handler.post {
                    mRecycleView.apply {
                        adapter = financyAdapter
                        layoutManager = llm
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onClick(v: View?) {

        when( v?.id) {
            R.id.main_floatingbutton_addtask -> {
                val it = Intent(applicationContext, FinancyFormActivity::class.java)
                startActivity( it )
            }
        }

    }

    override fun onFinancyItemClick(v: View, pos: Int) {
        val it = Intent( applicationContext, FinancyFormActivity::class.java )
        it.putExtra("financyId", mFinancyList[pos].fid)
        startActivity(it)
    }

    override fun onFinancyItemLongClick(v: View, pos: Int) {
        val alert = AlertDialog.Builder(this)
            .setTitle("Financy List")
            .setMessage("Você deseja excluir a receita ${mFinancyList[pos].name}?")
            .setPositiveButton("Sim") { dialog, _ ->
                dialog.dismiss()

                val ref = mDatabase.reference.child("users/${mAuth.uid}/tasks/${mFinancyList[pos].fid}")
                ref.removeValue().addOnCompleteListener{
                   handler.post {
                     financyAdapter.notifyItemRemoved(pos)
                   }
                }
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
            .create()

        alert.show()
    }
}