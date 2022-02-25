package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.application.shared.ui.TransactionListItem
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.dto.SearchTransactionRequest
import com.wutsi.platform.payment.dto.TransactionSummary
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder,
    private val paymentApi: WutsiPaymentApi,
    private val securityContext: SecurityContext,
    private val tenantProvider: TenantProvider,
    private val togglesProvider: TogglesProvider,
    private val accountApi: WutsiAccountApi,
    private val sharedUIMapper: SharedUIMapper,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
) : AbstractQuery() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeScreen::class.java)
    }

    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val balance = getBalance(tenant)
        val me = securityContext.currentAccount()

        // Primary Buttons
        val children = mutableListOf<WidgetAware>(
            Container(
                alignment = Alignment.Center,
                background = Theme.COLOR_PRIMARY,
                child = Center(
                    child = Column(
                        mainAxisAlignment = MainAxisAlignment.center,
                        crossAxisAlignment = CrossAxisAlignment.center,
                        children = listOf(
                            Text(
                                getText("page.home.balance"),
                                color = Theme.COLOR_WHITE,
                            ),
                            MoneyText(
                                color = Theme.COLOR_WHITE,
                                value = balance.value,
                                currency = tenant.currencySymbol,
                                numberFormat = tenant.numberFormat,
                            )
                        ),
                    )
                ),
            ),
            Container(
                background = Theme.COLOR_PRIMARY,
                child = Row(
                    mainAxisAlignment = spaceAround,
                    children = primaryButtons(),
                )
            ),
        )
        val applications = applicationButtons(me)
        if (applications.isNotEmpty()) {
            children.addAll(
                toRows(applications, 4)
                    .map {
                        Container(
                            child = Row(
                                children = it,
                                mainAxisAlignment = spaceAround
                            )
                        )
                    }
            )
            children.add(Divider(color = Theme.COLOR_DIVIDER, height = 1.0))
        }

        // Recipients + Transactions
        val txs = findRecentTransactions(20)
        val userId = securityContext.currentAccountId()
        if (txs.isNotEmpty()) {

            // Recipients - for non business accounts
            if (!me.business) {
                val recipientIds = txs
                    .map { it.recipientId }
                    .filterNotNull()
                    .filter { it != userId }
                    .toSet()
                    .take(3)
                if (recipientIds.isNotEmpty()) {
                    val recipients = findRecipients(recipientIds.toList())
                    children.addAll(
                        listOf(
                            Container(
                                padding = 5.0,
                                child = Text(getText("page.home.send_to"), bold = true),
                            ),
                            recipientsWidget(recipients),
                            Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        )
                    )
                }
            }

            // Transactions
            children.addAll(
                listOf(
                    Container(
                        padding = 5.0,
                        child = Text(getText("page.home.transactions"), bold = true),
                    ),
                    transactionsWidget(txs.take(3), tenant)
                )
            )
        } else if (togglesProvider.isPaymentEnabled(me)) {
            val paymentMethods = findPaymentMethods()
            if (paymentMethods.isEmpty()) {
                children.add(linkFirstAccountWidget())
            }
        }

        return Screen(
            id = Page.HOME,
            appBar = AppBar(
                foregroundColor = Theme.COLOR_WHITE,
                backgroundColor = Theme.COLOR_PRIMARY,
                elevation = 0.0,
                automaticallyImplyLeading = false,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = Route,
                            url = urlBuilder.build("settings")
                        )
                    )
                ),
                leading = IconButton(
                    icon = Theme.ICON_HISTORY,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "history")
                    )
                )
            ),
            child = Column(children = children),
        ).toWidget()
    }

    // Buttons
    private fun primaryButtons(): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (togglesProvider.isScanEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.scan"),
                    icon = Theme.ICON_SCAN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("scan")
                    ),
                ),
            )
        }
        if (togglesProvider.isAccountEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.cashin"),
                    icon = Theme.ICON_CASHIN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                ),
            )
        }
        buttons.addAll(
            listOf(
                primaryButton(
                    caption = getText("page.home.button.send"),
                    icon = Theme.ICON_SEND,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "send")
                    )
                ),
                primaryButton(
                    caption = getText("page.home.button.qr-code"),
                    icon = Theme.ICON_QR_CODE,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("qr-code")
                    )
                )
            )
        )
        return buttons
    }

    private fun primaryButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_WHITE,
        iconColor = Theme.COLOR_WHITE,
        padding = 1.0,
        action = action
    )

    private fun applicationButtons(me: Account): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()

        if (togglesProvider.isStoreEnabled()) {
            buttons.addAll(
                listOf(
                    applicationButton(
                        caption = getText("page.home.button.store"),
                        icon = Theme.ICON_STORE,
                        action = Action(
                            type = Route,
                            url = storeUrl
                        )
                    ),
                    applicationButton(
                        caption = getText("page.home.button.orders"),
                        icon = Theme.ICON_ORDER,
                        action = Action(
                            type = Route,
                            url = "$storeUrl/orders"
                        )
                    )
                )
            )
        }

        if (togglesProvider.isPaymentEnabled(me))
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.payment"),
                    icon = Theme.ICON_PAYMENT,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "pay")
                    )
                )
            )

        if (togglesProvider.isContactEnabled())
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.contact"),
                    icon = Theme.ICON_CONTACT,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("contact")
                    )
                )
            )

        if (togglesProvider.isFeedbackEnabled())
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.feedback"),
                    icon = Theme.ICON_FEEDBACK,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("feedback")
                    )
                )
            )

        return buttons
    }

    private fun applicationButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_PRIMARY,
        iconColor = Theme.COLOR_PRIMARY,
        padding = 1.0,
        action = action
    )

    private fun getBalance(tenant: Tenant): Money {
        try {
            val userId = securityContext.currentAccountId()
            val balance = paymentApi.getBalance(userId).balance
            return Money(
                value = balance.amount,
                currency = balance.currency
            )
        } catch (ex: Throwable) {
            return Money(currency = tenant.currency)
        }
    }

    // Payment method
    private fun linkFirstAccountWidget() = Container(
        margin = 10.0,
        padding = 20.0,
        border = 1.0,
        borderRadius = 5.0,
        borderColor = Theme.COLOR_PRIMARY,
        background = Theme.COLOR_PRIMARY_LIGHT,
        alignment = Alignment.Center,
        child = Column(
            mainAxisAlignment = spaceAround,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOf(
                Text(getText("page.home.no-account-1"), bold = true),
                Text(getText("page.home.no-account-2")),
                Button(
                    caption = getText("page.home.button.link-account"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("/settings/accounts/link/mobile")
                    )
                ),
            )
        )
    )

    // Recipients
    private fun recipientsWidget(recipients: List<AccountSummary>): WidgetAware {
        return Row(
            mainAxisAlignment = MainAxisAlignment.spaceAround,
            children = recipients.map { recipientWidget(it) }
        )
    }

    private fun recipientWidget(recipient: AccountSummary): WidgetAware {
        val action = Action(
            type = Route,
            url = urlBuilder.build(cashUrl, "/send"),
            parameters = mapOf(
                "recipient-id" to recipient.id.toString()
            )
        )
        return Column(
            children = listOf(
                Avatar(
                    radius = 24.0,
                    model = sharedUIMapper.toAccountModel(recipient),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "/send"),
                        parameters = mapOf(
                            "recipient-id" to recipient.id.toString()
                        )
                    )
                ),
                Button(
                    type = ButtonType.Text,
                    caption = StringUtil.firstName(recipient.displayName),
                    action = action,
                    stretched = false,
                    padding = 1.0,
                )
            )
        )
    }

    // Transactions
    private fun transactionsWidget(txs: List<TransactionSummary>, tenant: Tenant): WidgetAware {
        if (txs.isEmpty())
            return Container()

        val accounts = findAccounts(txs)
        val paymentMethods = findPaymentMethods()
        val account = securityContext.currentAccount()
        return Flexible(
            child = ListView(
                separator = true,
                children = txs.map {
                    TransactionListItem(
                        model = sharedUIMapper.toTransactionModel(
                            obj = it,
                            tenant = tenant,
                            tenantProvider = tenantProvider,
                            paymentMethod = it.paymentMethodToken?.let { paymentMethods[it] },
                            currentUser = account,
                            accounts = accounts
                        ),
                        action = Action(
                            type = Route,
                            url = urlBuilder.build(cashUrl, "transaction?id=${it.id}")
                        )
                    )
                }
            )
        )
    }

    private fun findRecentTransactions(limit: Int): List<TransactionSummary> =
        try {
            paymentApi.searchTransaction(
                SearchTransactionRequest(
                    accountId = securityContext.currentAccountId(),
                    limit = limit,
                    offset = 0
                )
            ).transactions
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find the recent transactions", ex)
            emptyList()
        }

    private fun findRecipients(recipientIds: List<Long>): List<AccountSummary> =
        try {
            accountApi.searchAccount(
                SearchAccountRequest(
                    ids = recipientIds,
                    limit = recipientIds.size
                )
            ).accounts
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find the recipients", ex)
            emptyList()
        }

    private fun findPaymentMethods(): Map<String, PaymentMethodSummary> =
        accountApi.listPaymentMethods(securityContext.currentAccountId())
            .paymentMethods
            .map { it.token to it }.toMap()

    private fun findAccounts(txs: List<TransactionSummary>): Map<Long, AccountSummary> {
        if (txs.isEmpty())
            return emptyMap()

        val accountIds = txs.map { it.accountId }.toMutableSet()
        accountIds.addAll(txs.mapNotNull { it.recipientId })

        return accountApi.searchAccount(
            SearchAccountRequest(
                ids = accountIds.toList(),
                limit = accountIds.size
            )
        ).accounts.map { it.id to it }.toMap()
    }

    private fun toRows(products: List<WidgetAware>, size: Int): List<List<WidgetAware>> {
        val rows = mutableListOf<List<WidgetAware>>()
        var cur = mutableListOf<WidgetAware>()
        products.forEach {
            cur.add(it)
            if (cur.size == size) {
                rows.add(cur)
                cur = mutableListOf()
            }
        }
        if (cur.isNotEmpty())
            rows.add(cur)
        return rows
    }
}
