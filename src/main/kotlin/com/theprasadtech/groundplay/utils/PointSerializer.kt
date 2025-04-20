package com.theprasadtech.groundplay.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.locationtech.jts.geom.Point

class PointSerializer : JsonSerializer<Point>() {
    override fun serialize(
        point: Point?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?,
    ) {
        if (gen != null && point != null) {
            gen.writeStartObject()
            gen.writeNumberField("lon", point.x)
            gen.writeNumberField("lat", point.y)
            gen.writeEndObject()
        }
    }
}
