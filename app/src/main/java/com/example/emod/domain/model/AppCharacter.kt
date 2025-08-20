package com.example.emod.domain.model

import com.example.emod.R

enum class AppCharacter(
    val headRes: Int,
    val fullRes: Int
) {
    PARROT(R.drawable.ch_parrot_head_4x, R.drawable.ch_parrot_full_4x),
    ELEPHANT(R.drawable.ch_elephant_head_4x, R.drawable.ch_elephant_full_4x),
    TURTLE(R.drawable.ch_turtle_head_4x, R.drawable.ch_turtle_full_4x),
    LION(R.drawable.ch_lion_head_4x, R.drawable.ch_lion_full_4x),
    CAT(R.drawable.ch_cat_head_4x, R.drawable.ch_cat_full_4x),
    BEAR(R.drawable.ch_bear_head_4x, R.drawable.ch_bear_full_4x);
}