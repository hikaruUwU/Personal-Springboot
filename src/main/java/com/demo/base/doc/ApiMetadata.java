package com.demo.base.doc;

import java.util.List;
import java.util.Map;

public record ApiMetadata(String Url,
                          String Method,
                          String Handler,
                          List<?> Parameter,
                          Object Return,
                          Map<?,?> AnnotationOfMethod) {
}