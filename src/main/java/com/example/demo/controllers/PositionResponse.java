package com.example.demo.controllers;

public class PositionResponse {
    private final String position;

    public PositionResponse(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}