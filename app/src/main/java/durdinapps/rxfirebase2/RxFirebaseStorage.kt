@file:JvmName("RxFirebaseStorage")

package durdinapps.rxfirebase2

import android.net.Uri
import android.support.annotation.NonNull
import com.google.firebase.storage.*
import io.reactivex.*
import java.io.File
import java.io.InputStream

/**
 * Asynchronously downloads the object from this [StorageReference] a byte array will be allocated large enough to hold the entire file in memory.
 *
 * @param storageRef           represents a reference to a Google Cloud Storage object.
 * @param maxDownloadSizeBytes the maximum allowed size in bytes that will be allocated. Set this parameter to prevent out of memory conditions from occurring.
 * If the download exceeds this limit, the task will fail and an IndexOutOfBoundsException will be returned.
 * @return a [Maybe] which emits an byte[] if success.
 */
@NonNull
fun getBytes(@NonNull storageRef: StorageReference,
             @NonNull maxDownloadSizeBytes: Long):
        Maybe<ByteArray> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, storageRef.getBytes(maxDownloadSizeBytes))

    }
}

/**
 * Asynchronously retrieves a long lived download URL with a revocable token.
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @return a [Maybe] which emits an [Uri] if success.
 */
@NonNull
fun getDownloadUrl(@NonNull storageRef: StorageReference):
        Maybe<Uri> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, storageRef.downloadUrl)

    }
}

/**
 * Asynchronously downloads the object at this [StorageReference] to a specified system filepath.
 *
 * @param storageRef      represents a reference to a Google Cloud Storage object.
 * @param destinationFile a File representing the path the object should be downloaded to.
 * @return a [Maybe] which emits an [FileDownloadTask.TaskSnapshot] if success.
 */

@NonNull
fun getFile(@NonNull storageRef: StorageReference,
            @NonNull destinationFile: File):
        Maybe<FileDownloadTask.TaskSnapshot> {
    return Maybe.create<FileDownloadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<FileDownloadTask.TaskSnapshot>(emitter, storageRef.getFile(destinationFile))
    }
}

/**
 * Asynchronously downloads the object at this [StorageReference] to a specified system filepath.
 *
 * @param storageRef     represents a reference to a Google Cloud Storage object.
 * @param destinationUri a file system URI representing the path the object should be downloaded to.
 * @return a [Maybe] which emits an [FileDownloadTask.TaskSnapshot] if success.
 */
@NonNull
fun getFile(@NonNull storageRef: StorageReference,
            @NonNull destinationUri: Uri):
        Maybe<FileDownloadTask.TaskSnapshot> {
    return Maybe.create<FileDownloadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<FileDownloadTask.TaskSnapshot>(emitter, storageRef.getFile(destinationUri))
    }
}

/**
 * Retrieves metadata associated with an object at this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @return a [Maybe] which emits an [StorageMetadata] if success.
 */
@NonNull
fun getMetadata(@NonNull storageRef: StorageReference):
        Maybe<StorageMetadata> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, storageRef.metadata)
    }
}

/**
 * Asynchronously downloads the object at this [StorageReference] via a InputStream.
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @return a [Maybe] which emits an [StreamDownloadTask.TaskSnapshot] if success.
 */
@NonNull
fun getStream(@NonNull storageRef: StorageReference):
        Maybe<StreamDownloadTask.TaskSnapshot> {
    return Maybe.create<StreamDownloadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<StreamDownloadTask.TaskSnapshot>(emitter, storageRef.stream)
    }
}

/**
 * Asynchronously downloads the object at this [StorageReference] via a InputStream.
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param processor  A StreamDownloadTask.StreamProcessor that is responsible for reading data from the InputStream.
 * The StreamDownloadTask.StreamProcessor is called on a background thread and checked exceptions thrown
 * from this object will be returned as a failure to the OnFailureListener registered on the StreamDownloadTask.
 * @return a [Maybe] which emits an [StreamDownloadTask.TaskSnapshot] if success.
 */
@NonNull
fun getStream(@NonNull storageRef: StorageReference,
              @NonNull processor: StreamDownloadTask.StreamProcessor):
        Maybe<StreamDownloadTask.TaskSnapshot> {
    return Maybe.create<StreamDownloadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<StreamDownloadTask.TaskSnapshot>(emitter, storageRef.getStream(processor)) }
}

/**
 * Asynchronously uploads byte data to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param bytes      The byte[] to upload.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putBytes(@NonNull storageRef: StorageReference,
             @NonNull bytes: ByteArray):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putBytes(bytes))
    }
}

/**
 * Asynchronously uploads byte data to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param bytes      The byte[] to upload.
 * @param metadata   [StorageMetadata] containing additional information (MIME type, etc.) about the object being uploaded.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putBytes(@NonNull storageRef: StorageReference,
             @NonNull bytes: ByteArray,
             metadata: StorageMetadata):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putBytes(bytes, metadata))
    }
}

/**
 * Asynchronously uploads from a content URI to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param uri        The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putFile(@NonNull storageRef: StorageReference,
            @NonNull uri: Uri):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putFile(uri))
    }
}

/**
 * Asynchronously uploads from a content URI to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param uri        The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
 * @param metadata   [StorageMetadata] containing additional information (MIME type, etc.) about the object being uploaded.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putFile(@NonNull storageRef: StorageReference,
            @NonNull uri: Uri,
            @NonNull metadata: StorageMetadata):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putFile(uri, metadata))
    }
}

/**
 * Asynchronously uploads from a content URI to this [StorageReference].
 *
 * @param storageRef        represents a reference to a Google Cloud Storage object.
 * @param uri               The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
 * @param metadata          [StorageMetadata] containing additional information (MIME type, etc.) about the object being uploaded.
 * @param existingUploadUri If set, an attempt is made to resume an existing upload session as defined by getUploadSessionUri().
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putFile(@NonNull storageRef: StorageReference,
            @NonNull uri: Uri,
            @NonNull metadata: StorageMetadata,
            @NonNull existingUploadUri: Uri):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putFile(uri, metadata, existingUploadUri))
    }
}

/**
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param stream     The InputStream to upload.
 * @param metadata   [StorageMetadata] containing additional information (MIME type, etc.) about the object being uploaded.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putStream(@NonNull storageRef: StorageReference,
              @NonNull stream: InputStream,
              @NonNull metadata: StorageMetadata):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putStream(stream, metadata))
    }
}

/**
 * Asynchronously uploads a stream of data to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param stream     The InputStream to upload.
 * @return a [Maybe] which emits an [UploadTask.TaskSnapshot] if success.
 */
@NonNull
fun putStream(@NonNull storageRef: StorageReference,
              @NonNull stream: InputStream):
        Maybe<UploadTask.TaskSnapshot> {
    return Maybe.create<UploadTask.TaskSnapshot> { emitter ->
        RxHandler.assignOnTask<UploadTask.TaskSnapshot>(emitter, storageRef.putStream(stream))
    }
}

/**
 * Asynchronously uploads a stream of data to this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @param metadata   [StorageMetadata] containing additional information (MIME type, etc.) about the object being uploaded.
 * @return a [Maybe] which emits an [StorageMetadata] if success.
 */
@NonNull
fun updateMetadata(@NonNull storageRef: StorageReference,
                   @NonNull metadata: StorageMetadata):
        Maybe<StorageMetadata> {
    return Maybe.create { emitter -> RxHandler.assignOnTask(emitter, storageRef.updateMetadata(metadata))
    }
}

/**
 * Deletes the object at this [StorageReference].
 *
 * @param storageRef represents a reference to a Google Cloud Storage object.
 * @return a [Completable] if the task is complete successfully.
 */
@NonNull
fun delete(@NonNull storageRef: StorageReference):
        Completable {
    return Completable.create { emitter ->
        RxCompletableHandler.assignOnTask(emitter, storageRef.delete())
    }
}