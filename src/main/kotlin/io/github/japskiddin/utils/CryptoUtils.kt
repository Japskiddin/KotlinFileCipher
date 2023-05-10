package io.github.japskiddin.utils

import io.github.japskiddin.exception.CryptoException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec

private const val ALGORITHM = "AES"
private const val TRANSFORMATION = "AES"

/**
 * Encrypt or decrypt files.
 *
 * @param cipherMode Cipher mode, can be Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
 * @param key Cipher key
 * @param inputFile Source file
 * @param outputFile Destination file
 * @throws CryptoException
 */
@Throws(CryptoException::class)
fun doCrypto(
    cipherMode: Int,
    key: String,
    inputFile: File,
    outputFile: File
) {
    var inputStream: FileInputStream? = null
    var outputStream: FileOutputStream? = null
    try {
        val secretKey = SecretKeySpec(key.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(cipherMode, secretKey)
        inputStream = FileInputStream(inputFile)
        val inputBytes = ByteArray(inputFile.length().toInt())
        inputStream.read(inputBytes)
        val outputBytes = cipher.doFinal(inputBytes)
        outputStream = FileOutputStream(outputFile)
        outputStream.write(outputBytes)
    } catch (ex: Exception) {
        ex.printStackTrace()
        when (ex) {
            is InvalidKeyException,
            is BadPaddingException,
            is IllegalBlockSizeException,
            is IOException,
            is NoSuchAlgorithmException -> throw CryptoException("Error encrypting/decrypting file", ex)

            else -> throw ex
        }
    } finally {
        try {
            inputStream?.close()
            outputStream?.flush()
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}