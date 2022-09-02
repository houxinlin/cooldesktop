package com.hxl.desktop.loader.core

internal interface InstallStep<I, O> {
    fun execute(value: I): O

    fun <R> addApplicationInstallStep(applicationInstallStep: InstallStep<O, R>): InstallStep<I, R> {
        return object : InstallStep<I, R> {
            override fun execute(value: I): R {
                return applicationInstallStep.execute(this@InstallStep.execute(value))
            }
        }
    }

    companion object {
        const val INSTALL_OK_STATE: Int = -3
        const val INSTALLING_STATE: Int = -4
        const val INSTALL_FAIL_STATE: Int = -5

        @kotlin.jvm.JvmStatic
        fun <I, O> of(source: InstallStep<I, O>): InstallStep<I, O> {
            return source
        }
    }


}