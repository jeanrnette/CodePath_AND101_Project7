package com.example.proj6_videogamesuggestor;

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameAdapter(private val gameList: List<GameSuggestion>) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {


    // Class constructor so we can change the item sin out viewholder. (so game image, title, rating)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView
        val gameTitle: TextView
        val gameRating: TextView

        // init: An initializer block which is automatically called when an instance of a class is created.
        init {
            gameImage = view.findViewById(R.id.image_game)
            gameTitle = view.findViewById(R.id.text_game_title)
            gameRating = view.findViewById(R.id.text_game_rating)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = gameList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get game at current spot
        val game = gameList[position]

        // Change Game Title
        holder.gameTitle.text = game.title

        // Change Game Image
        Glide.with(holder.itemView)
            .load(game.imageURL)
            .centerCrop()
            .into(holder.gameImage)

        // Change Game Rating
        holder.gameRating.text = "Rating: ${game.rating}"

        // Make a toast pop up
        holder.gameImage.setOnClickListener {
            // Its message depends on its rating! so create if else
            val message = if (game.rating >= 4) {
                "${game.title} is an AMAZING game!"
            } else if (game.rating >= 3) {
                "${game.title} is a Pretty Good game!"
            } else if (game.rating >= 2) {
                "${game.title} is an okay game..."
            } else if (game.rating >= 1) {
                "${game.title}? Reconsider playing this..."
            } else {
                "${game.title} STINKS!!"
            }
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }
}



//// Updating Views
//val gameTitle = findViewById<TextView>(R.id.text_game_title)
//gameTitle.text = name
//val gameRating = findViewById<TextView>(R.id.text_game_rating)
//gameRating.text = "Rating: $rating"
//
//// Updating Game ImageView
//val gameImageView = findViewById<ImageView>(R.id.image_game)
//Glide.with(this@MainActivity)
//.load(image)
//.fitCenter()
//.into(gameImageView)