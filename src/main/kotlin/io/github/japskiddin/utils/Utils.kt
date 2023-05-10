package io.github.japskiddin.utils

import java.io.File
import java.io.IOException

/**
 * Delete folder with all files
 *
 * @param file Source folder
 * @throws IOException
 */
@Throws(IOException::class)
fun deleteDir(file: File) {
    if (file.isDirectory) {
        val entries = file.listFiles()
        if (entries != null) {
            for (entry in entries) {
                deleteDir(entry)
            }
        }
    }
    if (!file.delete()) {
        throw IOException("Failed to delete ${file.absolutePath}")
    }
}