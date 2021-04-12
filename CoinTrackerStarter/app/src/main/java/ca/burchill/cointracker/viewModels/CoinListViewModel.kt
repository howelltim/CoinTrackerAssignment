package ca.burchill.cointracker.viewModels

import android.app.Application
import androidx.lifecycle.*
import ca.burchill.cointracker.database.DatabaseCoin
import ca.burchill.cointracker.network.CoinApi
import ca.burchill.cointracker.network.NetworkCoin
import ca.burchill.cointracker.repository.CoinsRepository
import ca.burchill.cointracker.database.getDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException


enum class CoinApiStatus { LOADING, ERROR, DONE }


class CoinListViewModel(application: Application) : AndroidViewModel(application) {

    private val coinsRepository = CoinsRepository(getDatabase(application))

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<CoinApiStatus>()
    val status: LiveData<CoinApiStatus>
        get() = _status

//     val coins = coinsRepository.coins
//     Tried implementing the above over the below but theres a type mismatch
//     The Observer in the fragment needs a NetworkCoin instead of a Coin
    
    private val _coins = MutableLiveData<List<NetworkCoin>>()
    val coins: LiveData<List<NetworkCoin>>
        get() = _coins

    // or use viewModelScope
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() {
        coroutineScope.launch {
            try {
//                coinsRepository.refreshCoins()
//                Tried implementing the above over the below but theres a type mismatch
//                The Observer in the fragment needs a NetworkCoin instead of a Coin

                var coinResult = CoinApi.retrofitService.getCoins()
                if (coinResult.coins.size > 0) {
                    _coins.value = coinResult.coins
                }
            } catch (t: Throwable) {
                if (coins.value.isNullOrEmpty())
                    _status.value = CoinApiStatus.ERROR
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}