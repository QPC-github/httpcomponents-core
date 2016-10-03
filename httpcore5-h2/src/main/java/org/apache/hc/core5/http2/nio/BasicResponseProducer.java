/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.hc.core5.http2.nio;

import java.io.IOException;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.entity.ContentType;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http2.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.util.Args;

/**
 * @since 5.0
 */
public class BasicResponseProducer implements AsyncResponseProducer {

    private final HttpResponse response;
    private final AsyncEntityProducer dataProducer;

    public BasicResponseProducer(final HttpResponse response, final AsyncEntityProducer dataProducer) {
        this.response = Args.notNull(response, "Response");
        this.dataProducer = dataProducer;
    }

    public BasicResponseProducer(final int code, final AsyncEntityProducer dataProducer) {
        this(new BasicHttpResponse(code), dataProducer);
    }

    public BasicResponseProducer(final int code, final String message) {
        this(new BasicHttpResponse(code), new StringAsyncEntityProducer(message, ContentType.TEXT_PLAIN));
    }

    public BasicResponseProducer(final AsyncEntityProducer dataProducer) {
        this(HttpStatus.SC_OK, dataProducer);
    }

    @Override
    public HttpResponse produceResponse() {
        return response;
    }

    @Override
    public EntityDetails getEntityDetails() {
        return dataProducer;
    }

    @Override
    public void dataStart(final DataStreamChannel channel) throws IOException {
        if (dataProducer != null) {
            dataProducer.streamStart(channel);
        }
    }

    @Override
    public int available() {
        return dataProducer != null ? dataProducer.available() : 0;
    }

    @Override
    public void produce(final DataStreamChannel channel) throws IOException {
        if (dataProducer != null) {
            dataProducer.produce(channel);
        }
    }

    @Override
    public void failed(final Exception cause) {
        releaseResources();
    }

    @Override
    public void releaseResources() {
        if (dataProducer != null) {
            dataProducer.releaseResources();
        }
    }

}