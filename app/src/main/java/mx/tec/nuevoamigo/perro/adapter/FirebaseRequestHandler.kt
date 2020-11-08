package mx.tec.nuevoamigo.perro.adapter

import android.R.attr
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StreamDownloadTask
import com.squareup.picasso.Picasso.LoadedFrom.NETWORK
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutionException


class FirebaseRequestHandler: RequestHandler() {
    private val SCHEME_FIREBASE_STORAGE = "gs"
    override fun canHandleRequest(data: Request?): Boolean {
        val scheme: String? = data?.uri?.getScheme()
        return SCHEME_FIREBASE_STORAGE == scheme
    }

    @Throws(IOException::class)
    override fun load(request: Request, networkPolicy: Int): Result? {
        val gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(request.uri.toString())
        val mStreamTask: StreamDownloadTask
        val inputStream: InputStream
        mStreamTask = gsReference.stream
        return try {
            inputStream = Tasks.await(mStreamTask).stream
            Log.i("FireBaseRequestHandler", "Loaded " + gsReference.path)
            Result(BitmapFactory.decodeStream(inputStream), NETWORK)
        } catch (e: ExecutionException) {
            throw IOException()
        } catch (e: InterruptedException) {
            throw IOException()
        }
    }
}