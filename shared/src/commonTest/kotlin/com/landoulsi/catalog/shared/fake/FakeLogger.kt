package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.core.Logger

class FakeLogger : Logger {
    override fun error(throwable: Throwable, message: String) = Unit
    override fun debug(message: String) = Unit
}
