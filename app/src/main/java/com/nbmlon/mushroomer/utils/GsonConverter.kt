package com.nbmlon.mushroomer.utils

import com.google.gson.*
import com.google.gson.JsonSerializer
import org.joda.time.DateTime
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GsonAdapter {

    //날짜 변환 클래스
    inner class DateSerializer : JsonSerializer<DateTime> {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

        override fun serialize(src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(dateFormat.format(src))
        }
    }
    inner class DateDeserializer : JsonDeserializer<DateTime> {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime {
            val dateString = json?.asString
            return  DateTime.parse(dateString)
        }
    }



    //Post 좋아요 변환 클래스
    inner class ThumbsUpSerializer{

    }

}

/*
// createdDate에 대한 어댑터 등록
    val createdDateType = Date::class.java.getDeclaredField("createdDate").genericType
    val createdDateSerializer = DateSerializer()
    val createdDateDeserializer = DateDeserializer()
    gsonBuilder.registerTypeAdapter(createdDateType, createdDateSerializer)
    gsonBuilder.registerTypeAdapter(createdDateType, createdDateDeserializer)

    val gson = gsonBuilder.create()

    val person = Person("John Doe", Date(), Date())
    val json = gson.toJson(person)
 */