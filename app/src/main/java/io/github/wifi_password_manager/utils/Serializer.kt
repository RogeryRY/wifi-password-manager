package io.github.wifi_password_manager.utils

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(SealedSerializationApi::class)
class ImmutableSetSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<ImmutableSet<T>> {
    private class PersistentSetDescriptor : SerialDescriptor by serialDescriptor<Set<String>>() {
        override val serialName: String = "kotlinx.serialization.immutable.ImmutableSet"
    }

    override val descriptor: SerialDescriptor = PersistentSetDescriptor()

    override fun serialize(encoder: Encoder, value: ImmutableSet<T>) {
        return SetSerializer(dataSerializer).serialize(encoder, value.toSet())
    }

    override fun deserialize(decoder: Decoder): ImmutableSet<T> {
        return SetSerializer(dataSerializer).deserialize(decoder).toPersistentSet()
    }
}
