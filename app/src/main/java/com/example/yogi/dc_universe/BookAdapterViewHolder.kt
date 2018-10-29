package com.example.yogi.dc_universe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

class BookAdapterViewHolder : AppCompatActivity() {


    var key:String = ""
    var bookimage:String = ""
    var booktitle:String = ""
    var bookauthor:String = ""
    var bookgenre:String = ""
    var bookrating:Float = 0F
    var bookid:Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_adapter_view_holder)

        verifyUserLogin()
        fetchBooks()
    }

    private fun fetchBooks(){
        val ref = FirebaseDatabase.getInstance().getReference("/books")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val groupadapter = GroupAdapter<ViewHolder>().apply{
                    spanCount = 2
                }

                p0.children.forEach {
                    Log.d("Books", "fetchBooks: ${it.toString()}")
                    val book = it.getValue(Books::class.java)
                    if(book != null){
                        groupadapter.add(BookItem(book))
                    }

                }

                groupadapter.setOnItemClickListener { item, view ->
                    val bk = item as BookItem
                    val intent = Intent(view.context, BookDetails::class.java)
                    intent.putExtra("id",bk.books.id)
                    Log.d("passid","Bookid : ${bk.books.id}")
                    intent.putExtra("rating",bk.books.average_rating)
                    Log.d("passid","Rating : ${bk.books.average_rating}")
                    intent.putExtra("title",bk.books.title)
                    Log.d("passid","Title : ${bk.books.title}")
                    intent.putExtra("author",bk.books.authors)
                    Log.d("passid","Author : ${bk.books.authors}")
                    intent.putExtra("genre",bk.books.genre)
                    Log.d("passid","Genre : ${bk.books.genre}")
                    intent.putExtra("image",bk.books.image_url)
                    Log.d("passid","Image : ${bk.books.image_url}")
                    startActivity(intent)

                }

                recyclerview_book_adapter.apply{
                    layoutManager = GridLayoutManager(this@BookAdapterViewHolder, groupadapter.spanCount).apply{
                        spanSizeLookup = groupadapter.spanSizeLookup
                    }
                    adapter = groupadapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                //
            }
        })
    }

    private fun verifyUserLogin(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_bookmark ->{
                val intent = Intent(this, Bookmark::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

}

class BookItem(val books: Books): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Picasso.get().load(books.image_url).into(viewHolder.itemView.bookadapter_book_image)
    }

    override fun getLayout(): Int {
        return R.layout.bookadapter_row
    }

    override fun getSpanSize(spanCount: Int, position: Int)= spanCount/2
}

class Books(val authors:String, val average_rating:Float, val genre:String, val id:Long,
            val image_url:String, val original_publication_year:Long, val ratings_count:Long,
            val small_image_url:String, val title:String){
    constructor() : this("", 0F,"", 0L,"",
            0L, 0L,"","")

}