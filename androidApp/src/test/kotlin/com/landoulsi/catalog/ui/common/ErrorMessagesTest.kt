package com.landoulsi.catalog.ui.common

import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMessagesTest {

    @Test
    fun `maps network error to the network string resource`() {
        val uiText = ErrorMessage.Network.toUiText()

        assertEquals(UiText.Res(R.string.error_network), uiText)
    }

    @Test
    fun `maps server error to the server string resource`() {
        val uiText = ErrorMessage.Server.toUiText()

        assertEquals(UiText.Res(R.string.error_server), uiText)
    }

    @Test
    fun `maps unknown error to the generic string resource`() {
        val uiText = ErrorMessage.Unknown.toUiText()

        assertEquals(UiText.Res(R.string.error_generic), uiText)
    }
}
