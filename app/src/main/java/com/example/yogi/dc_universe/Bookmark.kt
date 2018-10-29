package com.example.yogi.dc_universe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.yogi.dc_universe.R.id.recyclerview_bookmark
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_book_adapter_view_holder.*
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.bookadapter_row.view.*
import kotlinx.android.synthetic.main.bookmark_row.view.*

class Bookmark : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        supportActionBar?.title = "Bookmarks"
        Log.d("Bookmarks","reached Bookmarks")
        fetchBookmark()
        Log.d("Bookmarks","After fetchBookmark")
    }

    private fun fetchBookmark(){
        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/bookmarks/${auth.uid}")
        ref.keepSynced(true)


        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val groupadapter = GroupAdapter<ViewHolder>().apply{
                    spanCount = 2
                }

                p0.children.forEach {
                    Log.d("Bookmarks", "fetchBookmarks: ${it.toString()}")
                    val key = it.ref.key
                    Log.d("Bookmarks","key : $key")
                    val bookmarkRef = FirebaseDatabase.getInstance().getReference("books").child(key!!)
                    bookmarkRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            //
                            Log.d("Bookmarks","error: ${p0.message}")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            //
                            val book = p0.getValue(Books::class.java)
                            groupadapter.add(BookmarkItem(book!!))
                            Log.d("Bookmarks","inner data change:")


                        }
                    })
                }

                recyclerview_bookmark.apply {
                    layoutManager = GridLayoutManager(this@Bookmark,groupadapter.spanCount).apply {
                        spanSizeLookup = groupadapter.spanSizeLookup
                    }
                    adapter = groupadapter
                }


            }
            override fun onCancelled(p0: DatabaseError) {
                //
                Log.d("Bookmarks","outer error: ${p0.message}")
            }

        })



    }
}



class BookmarkItem(val books:Books): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){
    Picasso.get().load(books.image_url).into(viewHolder.itemView.bookmark_book_image)
    }

    override fun getLayout(): Int {
        return R.layout.bookmark_row
    }

    override fun getSpanSize(spanCount: Int, position: Int)= spanCount/2
}

class Bookmarks(val id:Long, val value:Boolean){
    constructor() : this(0L,false)
}