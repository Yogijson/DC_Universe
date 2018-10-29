package com.example.yogi.dc_universe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_book_details.*

class BookDetails : AppCompatActivity() {



    var key:String = ""
    var bookimage:String = ""
    var booktitle:String = ""
    var bookauthor:String = ""
    var bookgenre:String = ""
    var bookrating:Float = 0F
    var bookid:Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        val bundle = intent.extras
        if(bundle!=null){
            bookid = bundle.getLong("id")
            Log.d("details","bookid: $bookid")
            bookrating = bundle.getFloat("rating")
            bookauthor = bundle.getString("author")
            booktitle = bundle.getString("title")
            bookgenre = bundle.getString("genre")
            bookimage = bundle.getString("image")
        }

        supportActionBar?.title = booktitle
        fetchDetails()
    }

    private fun fetchDetails(){

        var bktitle:TextView = findViewById(R.id.book_title)
        var bkauthor:TextView = findViewById(R.id.book_author)
        var bkgenre:TextView = findViewById(R.id.book_genre)
        var bkrating:TextView = findViewById(R.id.book_rating)
        var bkimage:ImageView = findViewById(R.id.book_detail_image)
        var bookmark:ImageView = findViewById(R.id.bookmark)

        val auth = FirebaseAuth.getInstance()

        val key = bookid - 1
        bktitle.text = booktitle
        bkauthor.text = bookauthor
        bkgenre.text = bookgenre
        bkrating.text = bookrating.toString()
        Picasso.get().load(bookimage).into(bkimage)

        val dbref = FirebaseDatabase.getInstance().getReference("bookmarks").child(auth.uid!!).child(key.toString())
        dbref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp)
                }
            }

        })


        bookmark.setOnClickListener {
                it.setOnClickListener {
                    dbref.addListenerForSingleValueEvent (object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()){
                                p0.ref.removeValue()
                                bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                            }else{
                                dbref.setValue(true)
                                bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp)
                            }
                        }

                    })
                }
        }

    }
}
