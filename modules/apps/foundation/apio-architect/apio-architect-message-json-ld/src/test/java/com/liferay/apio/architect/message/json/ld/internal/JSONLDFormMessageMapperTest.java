/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.message.json.ld.internal;

import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.liferay.apio.architect.test.json.Conditions;
import com.liferay.apio.architect.test.writer.MockFormWriter;

import javax.ws.rs.core.HttpHeaders;

import org.hamcrest.Matcher;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
public class JSONLDFormMessageMapperTest {

	@Test
	public void testJSONLDSingleModelMessageMapper() {
		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		JsonObject jsonObject = MockFormWriter.write(
			httpHeaders, _formMessageMapper);

		Conditions.Builder builder = new Conditions.Builder();

		Conditions conditions = builder.where(
			"@context", _isALinkTo("http://www.w3.org/ns/hydra/context.jsonld")
		).where(
			"@id", _isALinkTo("localhost/f/f/s")
		).where(
			"@type", is(aJsonString(equalTo("Class")))
		).where(
			"description", is(aJsonString(equalTo("description")))
		).where(
			"supportedProperty", is(aJsonArrayThat(_containsTheProperties))
		).where(
			"title", is(aJsonString(equalTo("title")))
		).build();

		assertThat(jsonObject, is(aJsonObjectWith(conditions)));
	}

	@Test
	public void testMediaTypeIsCorrect() {
		String mediaType = _formMessageMapper.getMediaType();

		assertThat(mediaType, is("application/ld+json"));
	}

	private static Conditions _conditionsForField(
		String name, boolean required) {

		Conditions.Builder builder = new Conditions.Builder();

		return builder.where(
			"@type", is(aJsonString(equalTo("SupportedProperty")))
		).where(
			"property", is(aJsonString(equalTo("#" + name)))
		).where(
			"readable", is(aJsonBoolean(false))
		).where(
			"required", is(aJsonBoolean(required))
		).where(
			"writeable", is(aJsonBoolean(true))
		).build();
	}

	private static Matcher<? extends JsonElement> _isALinkTo(String url) {
		return is(aJsonString(equalTo(url)));
	}

	private static final Matcher<Iterable<? extends JsonElement>>
		_containsTheProperties = contains(
			aJsonObjectWith(_conditionsForField("boolean1", false)),
			aJsonObjectWith(_conditionsForField("date1", false)),
			aJsonObjectWith(_conditionsForField("double1", false)),
			aJsonObjectWith(_conditionsForField("long1", false)),
			aJsonObjectWith(_conditionsForField("string1", false)),
			aJsonObjectWith(_conditionsForField("boolean2", true)),
			aJsonObjectWith(_conditionsForField("date2", true)),
			aJsonObjectWith(_conditionsForField("double2", true)),
			aJsonObjectWith(_conditionsForField("long2", true)),
			aJsonObjectWith(_conditionsForField("string2", true)));

	private final JSONLDFormMessageMapper _formMessageMapper =
		new JSONLDFormMessageMapper();

}