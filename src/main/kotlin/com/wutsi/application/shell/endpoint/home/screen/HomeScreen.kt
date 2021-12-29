package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.TenantProvider
import com.wutsi.application.shell.service.TogglesProvider
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
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
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.WutsiAccountApi
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
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder,
    private val paymentApi: WutsiPaymentApi,
    private val userProvider: UserProvider,
    private val tenantProvider: TenantProvider,
    private val togglesProvider: TogglesProvider,
    private val accountApi: WutsiAccountApi,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeScreen::class.java)
    }

    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val balance = getBalance(tenant)

        // Primary Buttons
        val children = mutableListOf<WidgetAware>(
            Container(
                alignment = Alignment.Center,
                background = Theme.COLOR_PRIMARY,
                child = Row(
                    mainAxisAlignment = spaceAround,
                    children = listOf(
                        Column(
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
                                    currency = balance.currency,
                                    numberFormat = tenant.numberFormat,
                                )
                            ),
                        )
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
            Container(
                child = Row(
                    mainAxisAlignment = spaceAround,
                    children = applicationButton()
                )
            ),
        )

        // Transactions
        val txs = findRecentTransactions(20)
        if (txs.isNotEmpty()) {
            val recipientIds = txs.map { it.recipientId }.filterNotNull()
            if (recipientIds.isNotEmpty()) {
                val recipients = findReecipients(recipientIds.toList())
                children.addAll(
                    listOf(
                        Divider(color = Theme.COLOR_DIVIDER),
                        Container(
                            padding = 5.0,
                            child = Text(getText("page.home.send_to"), bold = true),
                        ),
                        recipientsWidget(recipients.take(3))
                    )
                )
            }

            children.addAll(
                listOf(
                    Divider(color = Theme.COLOR_DIVIDER),
                    Text(getText("page.home.transactions"), bold = true),
                    transactionsWidget(txs.take(3), tenant)
                )
            )
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
        buttons.addAll(
            listOf(
//                primaryButton(
//                    caption = getText("page.home.button.scan"),
//                    icon = Theme.ICON_SCAN,
//                    action = Action(
//                        type = Route,
//                        url = urlBuilder.build("pay/scan")
//                    ),
//                ),
                primaryButton(
                    caption = getText("page.home.button.cashin"),
                    icon = Theme.ICON_CASHIN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                ),
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
        action = action
    )

    private fun applicationButton(): List<WidgetAware> {
        val toggles = togglesProvider.get()
        val buttons = mutableListOf<WidgetAware>()

        if (toggles.payment) {
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
        }

        return buttons
    }

    private fun applicationButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_BLACK,
        iconColor = Theme.COLOR_PRIMARY,
        action = action
    )

    private fun getBalance(tenant: Tenant): Money {
        try {
            val userId = userProvider.id()
            val balance = paymentApi.getBalance(userId).balance
            return Money(
                value = balance.amount,
                currency = balance.currency
            )
        } catch (ex: Throwable) {
            return Money(currency = tenant.currency)
        }
    }

    // Recipients
    private fun recipientsWidget(recipients: List<AccountSummary>): WidgetAware {
        return Row(
            mainAxisAlignment = MainAxisAlignment.spaceAround,
            children = recipients.map { recipientWidget(it) }
        )
    }

    private fun recipientWidget(recipient: AccountSummary): WidgetAware =
        Column(
            children = listOf(
                CircleAvatar(
                    radius = 24.0,
                    child = recipient.pictureUrl?.let { Image(width = 48.0, height = 48.0, url = it) }
                        ?: Text(
                            caption = StringUtil.initials(recipient.displayName),
                            size = Theme.TEXT_SIZE_X_LARGE,
                            bold = true
                        )
                ),
                Container(
                    padding = 5.0,
                    child = Text(firstName(recipient.displayName), size = Theme.TEXT_SIZE_SMALL)
                )
            )
        )

    private fun firstName(displayName: String?): String {
        if (displayName == null)
            return ""

        val i = displayName.indexOf(' ')
        return if (i > 0) displayName.substring(0, i) else displayName
    }

    // Transactions
    private fun transactionsWidget(txs: List<TransactionSummary>, tenant: Tenant): WidgetAware {
        if (txs.isEmpty())
            return Container()

        val accounts = findAccounts(txs)
        val paymentMethods = findPaymentMethods()
        return Flexible(
            child = ListView(
                separator = true,
                children = txs.map { toListItem(it, accounts, paymentMethods, tenant) }
            )
        )
    }

    private fun findRecentTransactions(limit: Int): List<TransactionSummary> =
        try {
            paymentApi.searchTransaction(
                SearchTransactionRequest(
                    accountId = userProvider.id(),
                    limit = limit,
                    offset = 0
                )
            ).transactions
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find the recent transactions", ex)
            emptyList()
        }

    private fun findReecipients(recipientIds: List<Long>): List<AccountSummary> =
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
        accountApi.listPaymentMethods(userProvider.id())
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

    private fun toListItem(
        tx: TransactionSummary,
        accounts: Map<Long, AccountSummary>,
        paymentMethods: Map<String, PaymentMethodSummary>,
        tenant: Tenant
    ): WidgetAware =
        Container(
            padding = 10.0,
            child = Row(
                crossAxisAlignment = CrossAxisAlignment.start,
                mainAxisAlignment = MainAxisAlignment.start,
                children = listOf(
                    Flexible(
                        flex = 8,
                        child = Container(
                            alignment = Alignment.TopLeft,
                            child = caption(tx, accounts, paymentMethods),
                            padding = 5.0,
                        ),
                    ),
                    Flexible(
                        flex = 4,
                        child = Container(
                            alignment = Alignment.TopRight,
                            child = amount(tx, tenant),
                            padding = 5.0,
                        ),
                    ),
                )
            )
        )

    private fun caption(
        tx: TransactionSummary,
        accounts: Map<Long, AccountSummary>,
        paymentMethods: Map<String, PaymentMethodSummary>
    ): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            Text(
                caption = toCaption1(tx)
            ),
        )

        val caption2 = toCaption2(tx, accounts, paymentMethods)
        if (caption2 != null) {
            children.add(
                Text(caption = caption2, color = Theme.COLOR_GRAY)
            )
        }

        if (tx.status != "SUCCESSFUL") {
            children.add(
                Text(
                    caption = getText("transaction.status.${tx.status}"),
                    bold = true,
                    color = toColor(tx),
                    size = Theme.TEXT_SIZE_SMALL
                )
            )
        }
        return Column(
            mainAxisAlignment = MainAxisAlignment.spaceBetween,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = children,
        )
    }

    private fun amount(tx: TransactionSummary, tenant: Tenant): WidgetAware {
        val moneyFormat = DecimalFormat(tenant.monetaryFormat)
        val locale = LocaleContextHolder.getLocale()
        val dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
        return Column(
            mainAxisAlignment = MainAxisAlignment.spaceBetween,
            crossAxisAlignment = CrossAxisAlignment.end,
            children = listOf(
                Text(
                    caption = moneyFormat.format(toDisplayAmount(tx)),
                    bold = true,
                    color = toColor(tx),
                    alignment = TextAlignment.Right
                ),
                Text(
                    caption = tx.created.format(dateFormat),
                    size = Theme.TEXT_SIZE_SMALL,
                    alignment = TextAlignment.Right
                )
            ),
        )
    }

    private fun toDisplayAmount(tx: TransactionSummary): Double =
        when (tx.type.uppercase()) {
            "CASHOUT" -> -tx.amount
            "CASHIN" -> tx.amount
            else -> if (tx.recipientId == userProvider.id())
                tx.amount
            else
                -tx.amount
        }

    private fun toColor(tx: TransactionSummary): String =
        when (tx.status.uppercase()) {
            "FAILED" -> Theme.COLOR_DANGER
            "PENDING" -> Theme.COLOR_WARNING
            else -> when (tx.type.uppercase()) {
                "CASHIN" -> Theme.COLOR_SUCCESS
                "CASHOUT" -> Theme.COLOR_DANGER
                else -> if (tx.recipientId == userProvider.id())
                    Theme.COLOR_SUCCESS
                else
                    Theme.COLOR_DANGER
            }
        }

    private fun toCaption1(
        tx: TransactionSummary
    ): String {
        if (tx.type == "CASHIN") {
            return getText("page.home.cashin.caption")
        } else if (tx.type == "CASHOUT") {
            return getText("page.home.cashout.caption")
        } else {
            return if (tx.accountId == userProvider.id())
                getText("page.home.transfer.to.caption")
            else
                getText("page.home.transfer.from.caption")
        }
    }

    private fun toCaption2(
        tx: TransactionSummary,
        accounts: Map<Long, AccountSummary>,
        paymentMethods: Map<String, PaymentMethodSummary>
    ): String? {
        if (tx.type == "CASHIN" || tx.type == "CASHOUT") {
            val paymentMethod = paymentMethods[tx.paymentMethodToken]
            return getPhoneNumber(paymentMethod)
        } else {
            val account = getAccount(tx, accounts)
            return account?.displayName
        }
    }

    private fun getPhoneNumber(paymentMethod: PaymentMethodSummary?): String =
        formattedPhoneNumber(paymentMethod?.phone?.number, paymentMethod?.phone?.country)
            ?: paymentMethod?.maskedNumber
            ?: ""

    private fun getAccount(tx: TransactionSummary, accounts: Map<Long, AccountSummary>): AccountSummary? =
        if (tx.accountId == userProvider.id())
            accounts[tx.recipientId]
        else
            accounts[tx.accountId]
}
