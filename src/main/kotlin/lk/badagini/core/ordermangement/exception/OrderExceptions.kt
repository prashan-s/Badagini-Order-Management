package lk.badagini.core.ordermangement.exception

import org.springframework.http.HttpStatus

class OrderNotFoundException(message: String) : RuntimeException(message)

class InvalidOrderStateException(message: String) : RuntimeException(message)

class PaymentFailedException(message: String) : RuntimeException(message)

class SLABreachException(message: String) : RuntimeException(message)

class MerchantNotFoundException(message: String) : RuntimeException(message)

class CustomerNotFoundException(message: String) : RuntimeException(message)

class RiderNotFoundException(message: String) : RuntimeException(message) 