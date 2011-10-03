/*
 * Copyright 2010 CodeGist.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * ===================================================================
 *
 * More information at http://www.codegist.org.
 */

package at.sesame.fhooe.proxy.rest.google;

import java.util.Map;

import org.codegist.common.lang.Validate;
import org.codegist.common.marshal.Marshaller;
import org.codegist.common.reflect.Types;
import org.codegist.crest.CRestException;


import org.codegist.crest.ResponseContext;
import org.codegist.crest.handler.ResponseHandler;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import android.util.Log;
import at.sesame.fhooe.proxy.rest.google.model.Address;
import at.sesame.fhooe.proxy.rest.google.model.SearchResult;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class GoogleResponseHandler implements ResponseHandler {
	private static final String TAG = "GoogleResponseHandler";
	private Marshaller marshaller;
//	public final Object handle(org.codegist.crest. context) {
//		/* Marshall the response */
//		context.
////		Response res = context.to(Response.class, Types.newType(Response.class, context.getExpectedGenericType()));
//		Log.e(TAG,context.getExpectedGenericType().toString());
//		Object[] objects =context.getRequestContext().getArgs();
//		for(Object o:objects)
//		{
//			Log.e(TAG,o.toString());
//		}
//		return null;//new SearchResult<Address>();//Response<context.getExpectedGenericType().getClass()>(data, details, status)
//		/* Check for google OK status */
////		if (res.status == 200) {
////			return res.data; /* Returns the nested payload */
////		} else {
////			throw new CRestException(res.details + " (status=" + res.status + ")"); /* Throw exception with google error details */
////		}
//	}
	public GoogleResponseHandler(Map<String,Object> parameters) {
        this.marshaller = (Marshaller) parameters.get(Marshaller.class.getName());
        Validate.notNull(this.marshaller, "No marshaller set, please construct CRest using either JSON or XML expected return type.");
    }

    public final Object handle(ResponseContext context) {
        try {

            /* Marshall the response */
            Response<?> res = marshaller.marshall(context.getResponse().asStream(), Types.newType(Response.class, context.getExpectedGenericType()));
            /* Check for google OK status */
            if (res.status == 200) {
                return res.data; /* Returns the nested payload */
            } else {
                throw new CRestException(res.details + " (status=" + res.status + ")"); /* Throw exception with google error details */
            }
        } finally {
            context.getResponse().close();
        }
    }

	static class Response<T> {
		final T data;
		final String details;
		final int status;

		@JsonCreator
		Response(
				@JsonProperty("responseData") T data,
				@JsonProperty("responseDetails") String details,
				@JsonProperty("responseStatus") int status) {
			this.data = data;
			this.details = details;
			this.status = status;
		}
	}

}


