package io.github.japskiddin.helper

import io.github.japskiddin.exception.CryptoException
import io.github.japskiddin.model.Option
import io.github.japskiddin.utils.deleteDir
import io.github.japskiddin.utils.doCrypto
import java.io.File
import java.io.IOException
import javax.crypto.Cipher

/**
 * Main worker class.
 */
class CipherWorker {
    /**
     * Checks parameters and decides what to do next.
     *
     * @param args Entered parameters.
     */
    fun checkArguments(args: Array<String>) {
        if (args.isEmpty()) {
            throw IllegalArgumentException("No parameters found! Print --help for more information")
        }
        
        val optsList: MutableList<Option> = ArrayList()
        val doubleOptsList: MutableList<String> = ArrayList()

        parseArguments(args, optsList, doubleOptsList)

        var key: String? = null
        var dst: String? = null
        var src: String? = null

        optsList.forEach { option ->
            when (option.flag) {
                "key" -> key = option.opt
                "dst" -> dst = option.opt
                "src" -> src = option.opt
            }
        }

        doubleOptsList.forEach { opt ->
            when (opt) {
                "version" -> println("Version: ${javaClass.`package`.implementationVersion}")
                "help" -> showHelp()
                "decrypt" -> prepareCipher(Cipher.DECRYPT_MODE, src, dst, key)
                "encrypt" -> prepareCipher(Cipher.ENCRYPT_MODE, src, dst, key)
            }
        }
    }

    /**
     * Parsing arguments from command line.
     *
     * @param args Entered parameters.
     * @param optsList -opt parameters
     * @param doubleOptsList --opt parameters
     */
    private fun parseArguments(
        args: Array<String>,
        optsList: MutableList<Option>,
        doubleOptsList: MutableList<String>
    ) {
        var i = 0
        while (i < args.size) {
            if (args[i][0] == '-') {
                require(args[i].length >= 2) { "Not a valid argument: ${args[i]}" }
                if (args[i][1] == '-') {
                    require(args[i].length >= 3) { "Not a valid argument: ${args[i]}" }
                    // --opt
                    doubleOptsList.add(args[i].substring(2))
                } else {
                    require(args.size - 1 != i) { "Expected arg after: ${args[i]}" }
                    // -opt
                    optsList.add(Option(args[i].substring(1), args[i + 1]))
                    i++
                }
            }
            i++
        }
    }

    /**
     * Checks all parameters and prepares files before crypt operations.
     *
     * @param type Type of operation (encrypt / decrypt).
     * @param src Path to folder with source files.
     * @param dst Path to folder with output files.
     * @param key Cipher key.
     */
    private fun prepareCipher(type: Int, src: String?, dst: String?, key: String?) {
        requireNotNull(src) { "Expected arg \"-src\"" }
        requireNotNull(dst) { "Expected arg \"-dst\"" }
        requireNotNull(key) { "Expected arg \"-key\"" }

        val srcDir = File(src)
        if (!srcDir.exists()) {
            throw NullPointerException("Folder doesn't exists.")
        }

        if (!srcDir.isDirectory) {
            throw NullPointerException("File isn't directory!")
        }

        val files = srcDir.listFiles()
        if (files == null || files.isEmpty()) {
            throw NullPointerException("Folder is empty.")
        }

        val isEncrypt = type == Cipher.ENCRYPT_MODE
        val typeName = if (isEncrypt) "_encrypted" else "_decrypted"
        val dstPathName = dst + File.separator + "outputs" + typeName + File.separator + srcDir.name
        val dstDir = File(dstPathName)
        if (dstDir.exists()) {
            try {
                deleteDir(dstDir)
            } catch (e: IOException) {
                error(e.message.toString())
            }
        }

        val created = dstDir.mkdirs()
        if (!created) {
            throw NullPointerException("Can't create output folder.")
        }

        cipherFiles(files, dstDir, type, key)

        println(
            (if (isEncrypt) "Encryption" else "Decryption") + " done successfully!"
        )
    }

    /**
     * Cipher files.
     *
     * @param files List of files
     * @param dir Source directory
     * @param type Type of operation (encrypt / decrypt).
     * @param key Cipher key.
     */
    private fun cipherFiles(files: Array<File>, dir: File, type: Int, key: String) {
        for (srcFile in files) {
            if (srcFile.isDirectory) {
                val list = srcFile.listFiles()
                if (list != null && list.isNotEmpty()) {
                    val dstDir = File(dir, srcFile.name)
                    val created = dstDir.mkdirs()
                    if (created) {
                        cipherFiles(list, dstDir, type, key)
                    }
                }
            } else {
                val dstFile = File(dir, srcFile.name)
                try {
                    doCrypto(type, key, srcFile, dstFile)
                } catch (ex: CryptoException) {
                    error(ex.message.toString())
                }
            }
        }
    }

    /**
     * Shows help information.
     */
    private fun showHelp() {
        println("Usage: [--encrypt | --decrypt | --help] -src <path> -dst <path> -key <key>")
        println("\n--help - Show help information")
        println("--version - Show library version")
        println("--encrypt - Encrypt files")
        println("--decrypt - Decrypt files")
        println("-src <path> - Path to folder with source files")
        println("-dst <path> - Path to folder with output files")
        println("-key <key> - Cipher key")
    }
}