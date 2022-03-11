package com.wutsi.application.shell.endpoint.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.ecommerce.cart.WutsiCartApi
import com.wutsi.ecommerce.cart.dto.Cart
import com.wutsi.ecommerce.cart.dto.GetCartResponse
import com.wutsi.ecommerce.cart.dto.Product
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ProfileScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var contactApi: WutsiContactApi

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @MockBean
    private lateinit var cartApi: WutsiCartApi

    @Test
    fun personal() {
        // GIVEN
        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val account = createAccount(5555, false, null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/personal.json", response.body)
    }

    @Test
    fun storeEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isBusinessAccountEnabled()
        doReturn(true).whenever(togglesProvider).isStoreEnabled()

        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val account = createAccount(5555, true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/store-enabled.json", response.body)
    }

    @Test
    fun cartEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isBusinessAccountEnabled()
        doReturn(true).whenever(togglesProvider).isStoreEnabled()
        doReturn(true).whenever(togglesProvider).isCartEnabled()

        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val cart = Cart(
            products = listOf(Product(), Product())
        )
        doReturn(GetCartResponse(cart)).whenever(cartApi).getCart(any())

        val account = createAccount(5555, true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/cart-enabled.json", response.body)
    }

    @Test
    fun business() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isBusinessAccountEnabled()
        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val account = createAccount(5555, true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/business.json", response.body)
    }

    private fun createAccount(
        id: Long,
        business: Boolean,
        pictureUrl: String? = "https://img.com/1.png",
        hasStore: Boolean = false
    ) = Account(
        id = id,
        displayName = "Ray Sponsible",
        country = "CM",
        language = "en",
        status = "ACTIVE",
        phone = Phone(
            id = 1,
            number = "+1237666666666",
            country = "CM"
        ),
        pictureUrl = pictureUrl,
        business = business,
        retail = business,
        biography = "This is my bio",
        category = Category(
            id = 1000,
            title = "Marketing",
        ),
        website = "https://my.business.com/12432",
        whatsapp = "+23500000000",
        hasStore = hasStore
    )
}
