package com.hxl.desktop.loader.core

internal interface InstallStep<I, O> {
    fun execute(value: I): O
     
    fun <R> addSoftwareInstallStep(softwareInstallStep: InstallStep<O, R>): InstallStep<I, R> {
        return object : InstallStep<I, R> {
            override fun execute(value: I): R {
                return softwareInstallStep.execute(this@InstallStep.execute(value))
            }
        }
    }
    companion object {
        @kotlin.jvm.JvmStatic
        fun <I,O> of(source: InstallStep<I, O>): InstallStep<I, O> {
            return source
        }
    }
}