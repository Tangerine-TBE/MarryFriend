package com.twx.marryfriend.utils

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import java.io.File


/**
 * @author: Administrator
 * @date: 2022/7/19
 */
class DynamicFileProvider : FileProvider() {

    companion object {
        fun getUriForFile(
            context: Context, authority: String,
            file: File,
        ): Uri {
            return FileProvider.getUriForFile(context, authority, file)
        }
    }
}
