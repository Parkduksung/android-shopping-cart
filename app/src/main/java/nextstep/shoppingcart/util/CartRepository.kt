package nextstep.shoppingcart.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nextstep.shoppingcart.model.Cart
import nextstep.shoppingcart.model.Product

interface CartRepository {

    val items: StateFlow<List<Cart>>

    val totalPrice: Int

    fun addOne(product: Product)

    fun removeOne(product: Product)

    fun removeAll(product: Product)
}

class CartRepositoryImpl : CartRepository {
    private val _items = MutableStateFlow<List<Cart>>(emptyList())
    override val items: StateFlow<List<Cart>>
        get() = _items.asStateFlow()

    override val totalPrice: Int
        get() = _items.value.sumOf { it.totalPrice }

    override fun addOne(product: Product) {
        val item = _items.value.find { it.product == product }
        if (item == null) {
            _items.value += Cart(product, 1)
        } else {
            val index = _items.value.indexOf(item)
            val toMutableList = _items.value.toMutableList()
            toMutableList[index] = item.copy(count = item.count + 1)
            _items.update {
                toMutableList
            }
        }
    }
    override fun removeOne(product: Product) {
        val item = _items.value.find { it.product == product }
        if (item != null) {
            if (item.count > 1) {
                val index = _items.value.indexOf(item)
                val toMutableList = _items.value.toMutableList()
                toMutableList[index] = item.copy(count = item.count - 1)
                _items.update {
                    toMutableList
                }
            } else {
                _items.value = _items.value.filter { it.product != product }
            }
        }
    }
    override fun removeAll(product: Product) {
        _items.value = _items.value.filter { it.product != product }
    }

    companion object {
        private var INSTANCE: CartRepository? = null
        fun getInstance(): CartRepository {
            return INSTANCE ?: CartRepositoryImpl().also {
                INSTANCE = it
            }
        }
    }
}