package id.bni46.zcstools

import java.util.Locale
import kotlin.reflect.KProperty1


object KeyId {
    /**
     * Main key.
     */
    @JvmStatic
    var mainKey = 1

    /**
     * MAC key.
     */
    @JvmStatic
    var macKey = 11

    /**
     * PIN key.
     */
    @JvmStatic
    var pinKey = 12

    /**
     * TDK key.
     */
    @JvmStatic
    var tdkKey = 13

    /**
     * DEK key.
     */
    @JvmStatic
    var dekKey = 14

    /**
     * CBC MAC key.
     */
    @JvmStatic
    var cbcKey = 15
}

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    val property = instance::class.members
        .first { it.name == propertyName } as KProperty1<Any, *>
    return property.get(instance) as R
}

val exampleKeyIdList = KeyId::class.members.filter { it.name.contains("Key") }
    .map {
        "${it.name.uppercase(Locale.getDefault())} = ${
            readInstanceProperty<Int>(
                KeyId,
                it.name
            )
        }"
    }