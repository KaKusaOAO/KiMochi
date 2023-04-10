package com.kakaouo.mochi.i18n

import com.fasterxml.jackson.databind.JsonNode
import com.nfeld.jsonpathkt.extension.read

object JsonPathHelper {
    fun resolvePath(node: JsonNode, path: String): String? {
        return node.read<String>(path)
    }
}