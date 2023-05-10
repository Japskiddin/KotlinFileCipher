package io.github.japskiddin

import io.github.japskiddin.helper.CipherWorker

fun main(args: Array<String>) {
    // TODO: Add option for cipher only one file
    // TODO: Add git workflow
    val cipherWorker = CipherWorker()
    cipherWorker.checkArguments(args)
}