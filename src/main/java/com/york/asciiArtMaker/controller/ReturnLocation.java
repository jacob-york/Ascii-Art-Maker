package com.york.asciiArtMaker.controller;

public interface ReturnLocation<ResultType> {
    void acceptResult(ResultType result);
}
