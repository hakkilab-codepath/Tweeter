package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "ComposeActivity"
class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharacterCount = findViewById(R.id.tvCharacterCount)

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable) {
                val tweetLength = text.length
                tvCharacterCount.text = tweetLength.toString() + "/280"
                if (tweetLength > 280 || tweetLength == 0) {
                    btnTweet.isEnabled = false
                    tvCharacterCount.setTextColor(getResources().getColor((R.color.red)))
                } else {
                    btnTweet.isEnabled = true
                    tvCharacterCount.setTextColor(getResources().getColor((R.color.black)))
                }
            }
        })

        btnTweet.setOnClickListener {
            val tweetContent = etCompose.text.toString()
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed", Toast.LENGTH_SHORT).show()
            }
            else if (tweetContent.length > 140) {
                Toast.makeText(this, "Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT).show()
            }
            else {
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published tweet")
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }
                })
            }
        }
    }
}