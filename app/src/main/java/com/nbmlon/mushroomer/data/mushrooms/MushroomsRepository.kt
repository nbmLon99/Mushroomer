package com.nbmlon.mushroomer.data.mushrooms

interface MushroomsRepository {
}

fun MushroomsRepository() : MushroomsRepository = MushroomsRepositoryImpl()

private class MushroomsRepositoryImpl : MushroomsRepository {
}