package com.example.gestaofinanceira.model

data class UserWithFinancys(
    val user: User,
    val financys: List<Financy>
)
