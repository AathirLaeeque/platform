package com.leucine.streem.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.model.helper.PrincipalUser;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

public final class Utility {
  public static final String SPACE = " ";
  public static final String FILE_EXTENSION_SEPARATOR = ".";
  public static final double MAX_PRECISION_LIMIT = Math.pow(10, 12); // upto 12 places is the max precision limit


  private Utility() {
  }

  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

  public static String normalizeFilePath(String originalFileName) {
    return StringUtils.cleanPath(originalFileName);
  }

  public static boolean containsText(String text) {
    return StringUtils.hasText(text);
  }

  public static String generateUnique() {
    return generateUuid().replace("-", "");
  }

  public static boolean isEmpty(String field) {
    return ObjectUtils.isEmpty(field);
  }

  public static boolean isEmpty(Object object) {
    return object instanceof JsonNode jsonNode ? jsonNode.isEmpty() : ObjectUtils.isEmpty(object);
  }

  public static boolean isEmpty(Collection<?> collection) {
    return CollectionUtils.isEmpty(collection);
  }

  public static boolean isNull(Object o) {
    return Objects.isNull(o);
  }

  public static boolean isCollection(Object o) {
    return o instanceof Collection<?>;
  }

  public static boolean isString(Object o) {
    return o instanceof String;
  }

  public static boolean trimAndCheckIfEmpty(String s) {
    return isEmpty(StringUtils.trimWhitespace(s));
  }

  public static boolean isNullOrZero(Integer o) {
    if (Objects.isNull(o)) {
      return true;
    }
    return o == 0;
  }

  public static boolean isNullOrZero(Long o) {
    if (Objects.isNull(o)) {
      return true;
    }
    return o == 0;
  }

  public static boolean isNotNull(Object o) {
    return Objects.nonNull(o);
  }

  public static String toUriString(String uri, String filters, Pageable pageable) {
    return toUriString(uri, Collections.singletonMap("filters", filters), pageable);
  }

  public static String toUriString(String uri, Map<String, Object> parameters) {
    return toUriString(uri, parameters, null);
  }

  public static String toUriString(String uri, Map<String, Object> parameters, Pageable pageable) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
    parameters.forEach((k, v) -> {
      if (v instanceof Collection) {
        ((Collection<?>) v).forEach(value -> builder.queryParam(k, value));
      } else {
        builder.queryParam(k, v);
      }
    });
    if (null != pageable) {
      String sort = pageable.getSort().stream().map(s -> s.getProperty() + "," + s.getDirection()).collect(Collectors.joining("&"));
      builder.queryParam("page", pageable.getPageNumber())
        .queryParam("size", pageable.getPageSize())
        .queryParam("sort", sort);
    }
    return builder.toUriString();
  }

  public static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean nullSafeEquals(String str1, String str2) {
    str1 = ObjectUtils.getDisplayString(str1);
    str2 = ObjectUtils.getDisplayString(str2);
    return ObjectUtils.nullSafeEquals(str1, str2);
  }

  public static String getFullNameFromPrincipalUser(PrincipalUser principalUser) {
    StringBuilder stringBuilder = new StringBuilder(principalUser.getFirstName());
    if (principalUser.getLastName() != null) {
      stringBuilder.append(SPACE).append(principalUser.getLastName());
    }
    return stringBuilder.toString();
  }

  public static double roundUpDecimalPlaces(double value) {
    return Math.round(value * MAX_PRECISION_LIMIT) / MAX_PRECISION_LIMIT;
  }

  public static String getFullName(String firstName, String lastName) {
    firstName = firstName == null ? "" : firstName;
    lastName = lastName == null ? "" : lastName;
    return firstName + " " + lastName;
  }
}

