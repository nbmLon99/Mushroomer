package com.example.mushroomer.model

/**
 * @param mushroomImages    도감 대표 이미지들
 * @param foundedMushNo     찾은 버섯의 도감 넘버
 */
data class Dogam(
    val mushroomImages : ArrayList<String>,
    val foundedMushNo : ArrayList<Int>
)
