package lk.badagini.core.ordermangement.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {


    @GetMapping
    fun healthCheck(): String {
        return "Order Service is alive!!!"
    }

}