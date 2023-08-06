package com.nbmlon.mushroomer.model

import retrofit2.http.Url

/**
 * @param mushroomImages    도감 대표 이미지들
 * @param foundedMushNo     찾은 버섯의 도감 넘버
 */
data class Dogam(
    val mushroomImages : ArrayList<Url>,
    val foundedMushNo : ArrayList<Int>
)
