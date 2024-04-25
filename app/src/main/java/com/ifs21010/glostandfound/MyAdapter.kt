package com.ifs21010.glostandfound

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifs21010.glostandfound.activity.DetailActivity
import com.ifs21010.glostandfound.activity.UpdateActivity
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.data.LostfoundViewModel
import com.ifs21010.glostandfound.models.DeleteResponse
import com.ifs21010.glostandfound.models.LostFound
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAdapter(
    private val dataset: ArrayList<LostFound>,
    private val context: Context?,
    private val apiService: Api,
    private val authToken: String,
    private val currentUserName: String,
    private val lifecycleOwner : LifecycleOwner,
    private val listSaved : LostfoundViewModel
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gambarItem: ImageView
        val judulItem: TextView
        val keterangan1: TextView
        val tombolDelete: Button
        val namaUploader: TextView
        val tombolUpdate: Button
        val tombolDetail: Button
        val tombolAddMark : Button
        val tombolRemoveMark : Button

        init {
            // Define click listener for the ViewHolder's View
            gambarItem = view.findViewById(R.id.image_of_lostfound)
            judulItem = view.findViewById(R.id.judul_lostfound1)
            keterangan1 = view.findViewById(R.id.keterangan_lostfound1)
            tombolDelete = view.findViewById(R.id.tombol_delete)
            namaUploader = view.findViewById(R.id.uploader)
            tombolUpdate = view.findViewById(R.id.tombol_update)
            tombolDetail = view.findViewById(R.id.detail_item)
            tombolAddMark = view.findViewById(R.id.tombol_save_add)
            tombolRemoveMark = view.findViewById(R.id.tombol_save_remove)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.lost_found_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Replace the contents of a view (invoked by the layout manager)
        val temp = dataset[(dataset.size - 1) - position].cover

        if (temp != null) {
            Glide.with(viewHolder.itemView.context)
                .load(dataset[(dataset.size - 1) - position].cover).into(viewHolder.gambarItem)
        }

            listSaved.allLostFoundId.observe(lifecycleOwner) { item ->
                try {

                    if (item.contains(dataset[(dataset.size - 1) - position].id)) {
                        viewHolder.tombolAddMark.visibility = View.GONE
                    }
                } catch (e : ArrayIndexOutOfBoundsException) {
                    // do nothin
                } catch (e : IndexOutOfBoundsException) {
                    // do nothin
                }
            }


        viewHolder.judulItem.text = dataset[(dataset.size - 1) - position].title
        viewHolder.keterangan1.text = dataset[(dataset.size - 1) - position].description
        viewHolder.namaUploader.text = dataset[(dataset.size - 1) - position].author.name

        viewHolder.tombolAddMark.setOnClickListener {
            val data = com.ifs21010.glostandfound.entity.LostFound(0,  dataset[(dataset.size - 1) - position].id)

            try {
                listSaved.addMarked(data)
                viewHolder.tombolAddMark.visibility = View.GONE
                Toast.makeText(context, "Berhasil menandai!", Toast.LENGTH_SHORT).show()
            } catch (e : SQLiteConstraintException) {
                Toast.makeText(context, "Gagal Menandai!. Duplikat!", Toast.LENGTH_SHORT).show()
            }

        }

        viewHolder.tombolRemoveMark.setOnClickListener {
            viewHolder.tombolAddMark.post {
                listSaved.removeMark(dataset[(dataset.size - 1) - position].id)
                viewHolder.tombolAddMark.visibility = View.VISIBLE
                viewHolder.tombolRemoveMark.visibility = View.GONE
                Toast.makeText(context, "Berhasil menghapus tanda!", Toast.LENGTH_SHORT).show()
            }
        }

        viewHolder.tombolDetail.setOnClickListener {
            context?.startActivity(
                Intent(context, DetailActivity::class.java).putExtra(
                    "id",
                    dataset[(dataset.size - 1) - position].id
                )
            )
        }

        if (currentUserName == dataset[(dataset.size - 1) - position].author.name) {
            viewHolder.tombolAddMark.visibility = View.GONE
            viewHolder.tombolRemoveMark.visibility = View.GONE

            viewHolder.tombolDelete.setOnClickListener {
                val call = apiService.deleteLostFound(
                    authToken,
                    "${dataset[(dataset.size - 1) - position].id}"
                )
                call.enqueue(object : Callback<DeleteResponse> {
                    override fun onResponse(
                        p0: Call<DeleteResponse>,
                        p1: Response<DeleteResponse>
                    ) {
                        if (p1.isSuccessful) {
                            Toast.makeText(context, "Berhasil menghapus!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(p0: Call<DeleteResponse>, p1: Throwable) {
                        Toast.makeText(context, "Gagal menghapus LostFound", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            }

            viewHolder.tombolUpdate.setOnClickListener {
                context?.startActivity(
                    Intent(context, UpdateActivity::class.java)
                        .putExtra("id", dataset[(dataset.size - 1) - position].id)
                        .putExtra("title", dataset[(dataset.size - 1) - position].title)
                        .putExtra("desc", dataset[(dataset.size - 1) - position].description)
                )
            }
        } else {
            viewHolder.tombolDelete.visibility = View.GONE
            viewHolder.tombolUpdate.visibility = View.GONE
        }

    }

    override fun getItemCount() = dataset.size
}