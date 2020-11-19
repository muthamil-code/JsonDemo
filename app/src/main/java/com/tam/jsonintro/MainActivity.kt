package com.tam.jsonintro

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLConnection
import java.sql.Connection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallApiAsyncTask("tam","123").execute()
    }

    private inner class CallApiAsyncTask(val name : String,val password : String):AsyncTask<Any,Void,String>() {

        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgress()
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null

            try {

                var url = URL("https://run.mocky.io/v3/caa15db6-548c-4b67-ac07-6241a20c021b")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-type", "application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")
                connection.useCaches = false

                val writeDataOutPutStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("name",name)
                jsonRequest.put("password",password)
                writeDataOutPutStream.writeBytes(jsonRequest.toString())
                writeDataOutPutStream.flush()
                writeDataOutPutStream.close()


                val httpResult: Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {

                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?

                    try {
                        while (reader.readLine().also { line = it } != null) {

                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    result = connection.responseMessage
                    connection.disconnect()
                }
            } catch (e: SocketTimeoutException) {
                result = "connection error"
            } catch (e: Exception) {
                result = e.message + "Error"

            } finally {
              connection?.disconnect()
            }
            return result

        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgress()
            Log.i("JSON RESPONSE",result)


            val responseData = Gson().fromJson(result,ResponseData::class.java)
           Log.i("message",responseData.message)
            Log.i("profile","${responseData.profile_details.is_profile_completed}")

            for(item in responseData.data_list.indices){
                Log.i("id","${responseData.data_list[item].id}")
                Log.i("value","${responseData.data_list[item].value}")


            }




//            val jsonObject = JSONObject(result)
//            val message = jsonObject.optString("message")
//            Log.i("message",message)
//            val user_id = jsonObject.optInt("user_id")
//            Log.i("user","$user_id")
//            val name = jsonObject.optString("name")
//            Log.i("message",name)
//
//            val profileObject = jsonObject.optJSONObject("profile_details")
//            val is_profile_complete = profileObject.optBoolean("is_profile_completed")
//            Log.i("complete","$is_profile_complete")
//            val rating = profileObject.optDouble("rating")
//            Log.i("rate","$rating")
//
//            val datatListArray = jsonObject.getJSONArray("data_list")
//
//            Log.i("dataList","${datatListArray.length()}")
//
//           for(item in 0 until datatListArray.length()){
//               Log.i("dataMessage","${datatListArray[item]}")
//               val dataListObject : JSONObject = datatListArray[item] as JSONObject
//               val id = dataListObject.optInt("id")
//               Log.i("id","$id")
//               val value = dataListObject.optString("value")
//               Log.i("value","$value")
//
//           }

        }

        private fun showProgress(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_progress)
            customProgressDialog.show()
        }

        private fun cancelProgress(){
             customProgressDialog.dismiss()
        }


    }
}