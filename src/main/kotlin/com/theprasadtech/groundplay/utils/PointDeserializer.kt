package com.theprasadtech.groundplay.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point

class PointDeserializer : JsonDeserializer<Point>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Point {
        val node: JsonNode = p.codec.readTree(p)
        val lat = node.get("lat").asDouble()
        val lon = node.get("lon").asDouble()
        return GeometryFactory().createPoint(Coordinate(lat, lon))
    }
}
