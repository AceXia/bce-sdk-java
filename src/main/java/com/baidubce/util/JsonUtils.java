/*
 * Copyright (c) 2014 Baidu.com, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.baidubce.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.baidubce.BceClientException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        JsonUtils.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonUtils.objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        JsonUtils.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonUtils.objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    private static final ObjectWriter writer = JsonUtils.objectMapper.writer();
    private static final ObjectWriter prettyWriter = JsonUtils.objectMapper.writerWithDefaultPrettyPrinter();

    public static String toJsonPrettyString(Object value) throws JsonProcessingException {
        return JsonUtils.prettyWriter.writeValueAsString(value);
    }

    public static String toJsonString(Object value) {
        try {
            return JsonUtils.writer.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the deserialized object from the given json string and target class; or null if the given json string is
     * null.
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return JsonUtils.objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new BceClientException("Unable to parse Json String.", e);
        }
    }

    public static JsonNode jsonNodeOf(String json) {
        return JsonUtils.fromJsonString(json, JsonNode.class);
    }

    public static JsonGenerator jsonGeneratorOf(Writer writer) throws IOException {
        return new JsonFactory().createGenerator(writer);
    }

    public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
        try {
            return JsonUtils.objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void load(InputStream input, Object obj) throws IOException, JsonProcessingException {
        objectMapper.readerForUpdating(obj).readValue(input);
    }

    public static <T> T loadFrom(InputStream input, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return JsonUtils.objectMapper.readValue(input, clazz);
    }

    public static ObjectMapper getObjectMapper() {
        return JsonUtils.objectMapper;
    }

    public static ObjectWriter getWriter() {
        return JsonUtils.writer;
    }

    public static ObjectWriter getPrettywriter() {
        return JsonUtils.prettyWriter;
    }
}
