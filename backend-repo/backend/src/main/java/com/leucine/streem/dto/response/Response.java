package com.leucine.streem.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.leucine.streem.constant.ObjectType;
import com.leucine.streem.constant.ResponseStatus;
import com.leucine.streem.util.DateTimeUtils;
import lombok.*;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
  private final long timestamp = DateTimeUtils.now();
  private ObjectType object;
  private ResponseStatus status;
  private String message;
  private T data;
  private PageMetadata pageable;
  private List<Error> errors;

  public static class ResponseBuilder<T> {
    private T data;
    private ObjectType object = ObjectType.OBJECT;
    private ResponseStatus status = ResponseStatus.OK;
    private String message = "success";

    @Override
    public String toString() {
      return "ResponseBuilder {" +
          "data=" + data +
          ", object=" + object +
          ", status=" + status +
          ", message='" + message + '\'' +
          ", pageable=" + pageable +
          ", errors=" + errors +
          '}';
    }

    public ResponseBuilder data(T data) {
      this.data = data;
      if (data instanceof PageImpl) {
        PageImpl<T> pageImpl = (PageImpl) data;
        PageMetadata pageMetadata = PageMetadata.builder()
            .page(pageImpl.getNumber())
            .pageSize(pageImpl.getSize())
            .numberOfElements(pageImpl.getNumberOfElements())
            .totalPages(pageImpl.getTotalPages())
            .totalElements(pageImpl.getTotalElements())
            .first(pageImpl.isFirst())
            .last(pageImpl.isLast())
            .empty(pageImpl.isEmpty()).build();
        pageable = pageMetadata;
        this.data = (T) pageImpl.getContent();
        object = ObjectType.LIST;
      }
      return this;
    }
  }

  @Builder
  @Getter
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class PageMetadata {
    private int page;
    private int pageSize;
    private long numberOfElements;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
    private boolean empty;
  }
}
