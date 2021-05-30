package com.amazon.tv.leanbacklauncher.core

import java.lang.RuntimeException

class LaunchException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}