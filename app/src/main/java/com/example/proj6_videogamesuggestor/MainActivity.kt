package com.example.proj6_videogamesuggestor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.codepath.asynchttpclient.AsyncHttpClient
import okhttp3.Headers
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler

import com.codepath.asynchttpclient.RequestParams
import android.widget.Switch
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// A single game suggestion is composed of its title, image, description, and rating
data class GameSuggestion (
    val title: String,
    val imageURL: String,
    val rating: Double
)

class MainActivity : AppCompatActivity() {
    // This will contain a list of urls of the first 5 game suggestions
    private var gameList : MutableList<GameSuggestion> = mutableListOf()
    // This contains
    private lateinit var rvGames : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // First get the views by ID
        rvGames = findViewById(R.id.game_list)
        val genreSpinner = findViewById<Spinner>(R.id.genreSpinner)
        val platformSpinner = findViewById<Spinner>(R.id.platformSpinner)
        val multiplayerSwitch = findViewById<Switch>(R.id.multiplayerSwitch)

        // MAPS for Genres and Platforms
        //https://api.rawg.io/api/genres?key=MYKEYHERE
        val genreMap = mapOf(
            "Action" to "action",
            "RPG" to "role-playing-games-rpg",
            "Shooter" to "shooter",
            "Adventure" to "adventure",
            "Puzzle" to "puzzle"
        )
        //https://api.rawg.io/api/platforms?key=MYKEYHERE
        val platformMap = mapOf(
            "PC" to "4",
            "PlayStation 5" to "187",
            "PlayStation 4" to "18",
            "Xbox One" to "1",
            "Nintendo Switch" to "7"
        )

        // CONNECT to our list of choices to our SPINNERS
        // .keys will get the first part of the maps. So PC instead of 4.
        val genreList = listOf("Choose Genre") + genreMap.keys.toList()
        val platformList = listOf("Choose Platform") + platformMap.keys.toList()
        // We put these into a list so we can use an adapter to plug the list into the spinner.
        genreSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genreList)
        platformSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, platformList)

        // BUTTON SETUP
        val getGameButton = findViewById<Button>(R.id.button_getGame)
        getGameButton.setOnClickListener {
            // Get users choice as a string to check for error
            val selectedGenreString = genreSpinner.selectedItem.toString()
            val selectedPlatformString = platformSpinner.selectedItem.toString()
            val selectedPlayers = multiplayerSwitch.isChecked

            //Error if not chosen genre or platform
            if (selectedGenreString == "Choose Genre") {
                Toast.makeText(this, "Please choose genre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedPlatformString == "Choose Platform") {
                Toast.makeText(this, "Please choose platform", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //If there is no error we can find the appropriate names for the API to find
            val selectedGenre = genreMap[selectedGenreString]
            val selectedPlatform = platformMap[selectedPlatformString]

            // Get Game based on Users Input
            getGame(selectedGenre, selectedPlatform, selectedPlayers)
        }
    }

    private fun getGame(selectedGenre: String?, selectedPlatform: String?, selectedPlayers: Boolean) {
        // API Call to get game list
        val client = AsyncHttpClient()

        // Get stuff from games:
        val url = "https://api.rawg.io/api/games"
        // Filtering my search for games based on users input
        val params = RequestParams()
            params["key"] = "d44700847c5049c289439ccd92b9a8d7"
            params["genres"] = selectedGenre
            params["platforms"] = selectedPlatform
            if (selectedPlayers) {
                params["tags"] = "multiplayer"
            }


        // HANDLING API RESPONSE
        client[url, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {

                // Clear list so list wont show previous results to user
                gameList.clear()

                // JSON, getting the first FIVE game suggestions.
                val gameResults = json.jsonObject.getJSONArray("results")
                for (i in 0 until 5) {
                    // Getting items from API and adding them onto my list as data class GameSuggestions
                    val game = gameResults.getJSONObject(i)
                    val title = game.getString("name")
                    val imageURL = game.getString("background_image")
                    val rating = game.getDouble("rating")

                    val suggestion = GameSuggestion(title, imageURL, rating)
                    gameList.add(suggestion)

                    //getGameDescription(id, suggestion, adapter)
                }

                // Passing our list into our adapter
                val adapter = GameAdapter(gameList)
                // Attaching out adapter to RecyclerView
                rvGames.adapter = adapter
                // Arranging our layout into a horizontal list
                rvGames.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                rvGames.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.HORIZONTAL))

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.e("RAWG_Error", "Failed to load game name, rating, image")
            }
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
        }]
    }

//    private fun getGameDescription(gameId: Int, game: GameSuggestion, adapter: GameAdapter) {
//        val client = AsyncHttpClient()
//
//        // Get description from ID
//        val url = "https://api.rawg.io/api/games/$gameId"
//        val params = RequestParams()
//            params["key"] = "d44700847c5049c289439ccd92b9a8d7"
//
//        // API Connect
//        client[url, params, object : JsonHttpResponseHandler() {
//            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
//
//                val description = json.jsonObject.getString("description")
//
//                game.description = description
//                val index = gameList.indexOf(game)
//                adapter.notifyItemChanged(index) // Notify the game at this index has changed
//
//                //Update description and rating View
//                val gameDescription = findViewById<TextView>(R.id.text_game_description)
//                    gameDescription.text = description
//            }
//
//            override fun onFailure(
//                statusCode: Int,
//                headers: Headers?,
//                errorResponse: String,
//                throwable: Throwable?
//            ) {
//                Log.e("RAWG_Error", "Failed to load game description")
//            }
//        }]
//
//        return String
//    }

}