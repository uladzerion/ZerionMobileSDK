package io.zerion.kmm.api.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.zerion.kmm.api.api.ZerionApiConstants.DefaultValues
import io.zerion.kmm.api.api.ZerionApiConstants.Params
import io.zerion.kmm.api.models.PortfolioResponse
import io.zerion.kmm.api.models.PositionsResponse
import io.zerion.kmm.api.models.TransactionsResponse


internal interface ZerionAPI {
    suspend fun getWalletPortfolio(address: String): PortfolioResponse

    suspend fun getWalletPositions(
        address: String,
        positionsFilter: String = DefaultValues.POSITIONS_FILTER_SIMPLE,
        currency: String = DefaultValues.CURRENCY_USD,
        positionTypes: List<String>? = null,
        chainIds: List<String>? = null,
        fungibleIds: List<String>? = null,
        dappIds: List<String>? = null,
        trash: String = DefaultValues.TRASH_NON_TRASH,
        sort: String = DefaultValues.SORT_VALUE
    ): PositionsResponse

    suspend fun getWalletTransactions(
        address: String,
        currency: String = DefaultValues.CURRENCY_USD,
        pageSize: Int = DefaultValues.DEFAULT_PAGE_SIZE,
        pageAfter: String? = null,
        searchQuery: String? = null,
        operationTypes: List<String>? = null,
        assetTypes: List<String>? = null,
        chainIds: List<String>? = null,
        minMinedAt: Long? = null,
        maxMinedAt: Long? = null,
        trash: String = DefaultValues.TRASH_NO_FILTER,
        fungibleImplementations: List<String>? = null
    ): TransactionsResponse
}

internal class ZerionAPIImpl(private val client: HttpClient) : ZerionAPI {

    override suspend fun getWalletPortfolio(address: String): PortfolioResponse {
        return client.get("wallets/$address/portfolio").body()
    }

    override suspend fun getWalletPositions(
        address: String,
        positionsFilter: String,
        currency: String,
        positionTypes: List<String>?,
        chainIds: List<String>?,
        fungibleIds: List<String>?,
        dappIds: List<String>?,
        trash: String,
        sort: String
    ): PositionsResponse {
        return client.get("wallets/$address/positions") {
            parameter(Params.POSITIONS_FILTER, positionsFilter)
            parameter(Params.CURRENCY, currency)
            positionTypes?.let { parameter(Params.POSITION_TYPES, it.joinToString(",")) }
            chainIds?.let { parameter(Params.CHAIN_IDS, it.joinToString(",")) }
            fungibleIds?.let { parameter(Params.FUNGIBLE_IDS, it.joinToString(",")) }
            dappIds?.let { parameter(Params.DAPP_IDS, it.joinToString(",")) }
            parameter(Params.TRASH, trash)
            parameter(Params.SORT, sort)
        }.body()
    }

    override suspend fun getWalletTransactions(
        address: String,
        currency: String,
        pageSize: Int,
        pageAfter: String?,
        searchQuery: String?,
        operationTypes: List<String>?,
        assetTypes: List<String>?,
        chainIds: List<String>?,
        minMinedAt: Long?,
        maxMinedAt: Long?,
        trash: String,
        fungibleImplementations: List<String>?
    ): TransactionsResponse {
        return client.get("wallets/$address/transactions") {
            parameter(Params.CURRENCY, currency)
            parameter(Params.PAGE_SIZE, pageSize)
            pageAfter?.let { parameter(Params.PAGE_AFTER, it) }
            searchQuery?.let { parameter(Params.SEARCH_QUERY, it) }
            operationTypes?.let { parameter(Params.OPERATION_TYPES, it.joinToString(",")) }
            assetTypes?.let { parameter(Params.ASSET_TYPES, it.joinToString(",")) }
            chainIds?.let { parameter(Params.CHAIN_IDS, it.joinToString(",")) }
            minMinedAt?.let { parameter(Params.MIN_MINED_AT, it) }
            maxMinedAt?.let { parameter(Params.MAX_MINED_AT, it) }
            parameter(Params.TRASH, trash)
            fungibleImplementations?.let { parameter(Params.FUNGIBLE_IMPLEMENTATIONS, it.joinToString(",")) }
        }.body()
    }
}