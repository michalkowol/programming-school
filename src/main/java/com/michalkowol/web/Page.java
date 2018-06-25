package com.michalkowol.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Page<T> {

    private final List<T> data;
}
